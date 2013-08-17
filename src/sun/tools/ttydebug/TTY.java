/*
 * @(#)TTY.java	1.81 98/12/02
 *
 * Copyright 1995-1998 by Sun Microsystems, Inc.,
 * 901 San Antonio Road, Palo Alto, California, 94303, U.S.A.
 * All rights reserved.
 * 
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
 */

package sun.tools.ttydebug;
import sun.tools.debug.*;
import java.util.*;
import java.io.*;
import java.net.*;

public class TTY implements DebuggerCallback {
    RemoteDebugger debugger;
    RemoteThread currentThread;
    RemoteThreadGroup currentThreadGroup;
    PrintStream out = null;
    PrintStream console = null;

    private static final String progname = "jdb";
    private static final String version = "98/12/02";

    private String lastArgs = null;
    
    private boolean hasRun = false;
    
    private RemoteThread indexToThread(int index) throws Exception {
	setDefaultThreadGroup();
        RemoteThread list[] = currentThreadGroup.listThreads(true);
	if (index == 0 || index > list.length) {
	    return null;
	}
	return list[index-1];
    }

    private int parseThreadId(String idToken) throws Exception {
	if (idToken.startsWith("t@")) {
	    idToken = idToken.substring(2);
	}

	int threadId;
	try {
	    threadId = Integer.valueOf(idToken).intValue();
	} catch (NumberFormatException e) {
	    threadId = 0;
	}
	if (indexToThread(threadId) == null) {
	    out.println("\"" + idToken +
			       "\" is not a valid thread id.");
	    return 0;
	}
	return threadId;
    }

    private void printPrompt() throws Exception {
        if (currentThread == null) {
            out.print("> ");
        } else {
            out.print(currentThread.getName() + "[" +
                      (currentThread.getCurrentFrameIndex() + 1)
                      + "] ");
        }
        out.flush();
    }

    public synchronized void printToConsole(String text) throws Exception {
        console.print(text);
        console.flush();
    }

    public void breakpointEvent(RemoteThread t) throws Exception {
	out.print("\nBreakpoint hit: ");

	RemoteStackFrame[] stack = t.dumpStack();
	if (stack.length > 0) {
	    out.println(stack[0].toString());
            currentThread = t;
	} else {
	    out.println("Invalid thread specified in breakpoint.");
	}
        printPrompt();
    }

    public void exceptionEvent(RemoteThread t, String errorText) 
      throws Exception {
	out.println("\n" + errorText);
	t.setCurrentFrameIndex(0);
	currentThread = t;
        printPrompt();
    }

    public void threadDeathEvent(RemoteThread t) throws Exception {
	out.println("\n" + t.getName() + " died.");
        if (t == currentThread) {
            currentThread = null;
        }
        printPrompt();
    }

    public void quitEvent() throws Exception {
        String msg = null;
        if (lastArgs != null) {
            StringTokenizer t = new StringTokenizer(lastArgs);
            if (t.hasMoreTokens()) {
                msg = new String("\n" + t.nextToken() + " exited");
            }
        }
        if (msg == null) {
            msg = new String("\nThe application exited");
        }
        out.println(msg);
        currentThread = null;
        System.exit(0);
    }

    void classes() throws Exception {
	RemoteClass list[] = debugger.listClasses();

	out.println("** classes list **");
	for (int i = 0 ; i < list.length ; i++) {
	    out.println(list[i].description());
	}
    }

    void methods(StringTokenizer t) throws Exception {
	if (!t.hasMoreTokens()) {
	    out.println("No class specified.");
	    return;
	}

	String idClass = t.nextToken();
	try {
	    RemoteClass cls = getClassFromToken(idClass);

	    RemoteField methods[] = cls.getMethods();
	    for (int i = 0; i < methods.length; i++) {
		out.println(methods[i].getTypedName());
	    }
	} catch (IllegalArgumentException e) {
	    out.println("\"" + idClass +
			       "\" is not a valid id or class name.");
	}
    }

    int printThreadGroup(RemoteThreadGroup tg, int iThread) throws Exception {
	out.println("Group " + tg.getName() + ":");
	RemoteThread tlist[] = tg.listThreads(false);

	int maxId = 0;
	int maxName = 0;
	for (int i = 0 ; i < tlist.length ; i++) {
	    int len = tlist[i].description().length();
	    if (len > maxId)
		maxId = len;
	    String name = tlist[i].getName();
	    int iDot = name.lastIndexOf('.');
	    if (iDot >= 0 && name.length() > iDot) {
		name = name.substring(iDot + 1);
	    }
	    if (name.length() > maxName)
		maxName = name.length();
	}

        String maxNumString = String.valueOf(iThread + tlist.length);
        int maxNumDigits = maxNumString.length();

	for (int i = 0 ; i < tlist.length ; i++) {
	    char buf[] = new char[80];
	    for (int j = 0; j < 79; j++) {
		buf[j] = ' ';
	    }
	    buf[79] = '\0';
	    StringBuffer sbOut = new StringBuffer();
	    sbOut.append(buf);

            // Right-justify the thread number at start of output string
            String numString = String.valueOf(iThread + i + 1);
	    sbOut.insert(maxNumDigits - numString.length(),
                         numString);
	    sbOut.insert(maxNumDigits, ".");

	    int iBuf = maxNumDigits + 2;
	    sbOut.insert(iBuf, tlist[i].description());
	    iBuf += maxId + 1;
	    String name = tlist[i].getName();
	    int iDot = name.lastIndexOf('.');
	    if (iDot >= 0 && name.length() > iDot) {
		name = name.substring(iDot + 1);
	    }
	    sbOut.insert(iBuf, name);
	    iBuf += maxName + 1;
	    sbOut.insert(iBuf, tlist[i].getStatus());
	    sbOut.setLength(79);
	    out.println(sbOut.toString());
	}

	RemoteThreadGroup tglist[] = debugger.listThreadGroups(tg);
	for (int ig = 0; ig < tglist.length; ig++) {
	    if (tg != tglist[ig]) {
		iThread += printThreadGroup(tglist[ig], iThread + tlist.length);
	    }
	}
	return tlist.length;
    }

    private void setDefaultThreadGroup() throws Exception {
	if (currentThreadGroup == null) {
	    RemoteThreadGroup tglist[] = debugger.listThreadGroups(null);
	    currentThreadGroup = tglist[0];	// system threadgroup
	}
    }
    
    void threads(StringTokenizer t) throws Exception {
	if (!t.hasMoreTokens()) {
	    setDefaultThreadGroup();
	    printThreadGroup(currentThreadGroup, 0);
	    return;
	}
	String name = t.nextToken();
	RemoteThreadGroup tglist[] = debugger.listThreadGroups(null);
	for (int i = 0; i < tglist.length; i++) {
	    if (name.equals(tglist[i].getName())) {
		printThreadGroup(tglist[i], 0);
		return;
	    }
	}
	out.println(name + " is not a valid threadgroup name.");
    }

    void threadGroups() throws Exception {
	RemoteThreadGroup tglist[] = debugger.listThreadGroups(null);
	for (int i = 0; i < tglist.length; i++) {
	    out.println(new Integer(i+1).toString() + ". " +
			       tglist[i].description() + " " +
			       tglist[i].getName());
	}
    }

    void setThread(int threadId) throws Exception {
	setDefaultThreadGroup();
	RemoteThread thread = indexToThread(threadId);
	if (thread == null) {
	    out.println("\"" + threadId +
			       "\" is not a valid thread id.");
	    return;
	}
	currentThread = thread;
    }
    
    void thread(StringTokenizer t) throws Exception {
	if (!t.hasMoreTokens()) {
	    out.println("Thread number not specified.");
	    return;
	}
	int threadId = parseThreadId(t.nextToken());
	if (threadId == 0) {
	    return;
	}
	setThread(threadId);
    }
    
    void threadGroup(StringTokenizer t) throws Exception {
	if (!t.hasMoreTokens()) {
	    out.println("Threadgroup name not specified.");
	    return;
	}
	String name = t.nextToken();
	RemoteThreadGroup tglist[] = debugger.listThreadGroups(null);
	for (int i = 0; i < tglist.length; i++) {
	    if (name.equals(tglist[i].getName())) {
		currentThreadGroup = tglist[i];
		return;
	    }
	}
	out.println(name + " is not a valid threadgroup name.");
    }
    
    void run(StringTokenizer t) throws Exception {
	String argv[] = new String[100];
	int argc = 0;

        if ( hasRun ) {
            out.println("Cannot restart program in this session");
            return;
        }

	if (!t.hasMoreTokens() && lastArgs != null) {
	    t = new StringTokenizer(lastArgs);
	    out.println("run " + lastArgs);
	}
	while (t.hasMoreTokens()) {
	    argv[argc++] = t.nextToken();
            if (argc == 1) {
                // Expand name, if necessary.
                RemoteClass cls = debugger.findClass(argv[0]);
                if (cls == null) {
                    out.println("Could not load the " + argv[0] + " class.");
                    return;
                }
                argv[0] = cls.getName();
            }
	}

	if (argc > 0) {
	    RemoteThreadGroup newGroup = debugger.run(argc, argv);
	    if (newGroup != null) {
		currentThreadGroup = newGroup;
		setThread(1);
		out.println("running ...");
	    } else {
		out.println(argv[0] + " failed.");
	    }
	} else {
	    out.println("No class name specified.");
	}

        hasRun = true;
    }

    void load(StringTokenizer t) throws Exception {
	if (!t.hasMoreTokens()) {
	    out.println("Class name not specified.");
	    return;
	}
	String idToken = t.nextToken();
	RemoteClass cls = debugger.findClass(idToken);
	if (cls == null) {
	    out.print(idToken + " not found");
	    out.println((idToken.indexOf('.') > 0) ?
			       " (try the full name)" : "");
	} else {
	    out.println(cls.toString());
	}
    }

    void suspend(StringTokenizer t) throws Exception {
	if (!t.hasMoreTokens()) {
	    setDefaultThreadGroup();
	    RemoteThread list[] = currentThreadGroup.listThreads(true);
	    for (int i = 0; i < list.length; i++) {
		list[i].suspend();
	    }
	    out.println("All (non-system) threads suspended.");
	} else {
	    while (t.hasMoreTokens()) {
		String idToken = t.nextToken();
		int threadId;
		try {
		    threadId = Integer.valueOf(idToken).intValue();
		} catch (NumberFormatException e) {
		    threadId = 0;
		}
		RemoteThread thread = indexToThread(threadId);
		if (thread == null) {
		    out.println("\"" + idToken +
				       "\" is not a valid thread id.");
		    continue;
		}
		thread.suspend();
	    }
	}
    }

    void resume(StringTokenizer t) throws Exception {
 	if (!t.hasMoreTokens()) {
	    setDefaultThreadGroup();
	    RemoteThread list[] = currentThreadGroup.listThreads(true);
	    for (int i = 0; i < list.length; i++) {
		list[i].resume();
	    }
	    if (currentThread != null) {
		currentThread.resetCurrentFrameIndex();
	    }
 	    out.println("All threads resumed.");
 	} else {
 	    while (t.hasMoreTokens()) {
 		String idToken = t.nextToken();
 		int threadId;
 		try {
 		    threadId = Integer.valueOf(idToken).intValue();
 		} catch (NumberFormatException e) {
 		    threadId = 0;
 		}
		RemoteThread thread = indexToThread(threadId);
		if (thread == null) {
 		    out.println("\"" + idToken +
				       "\" is not a valid thread id.");
 		    continue;
 		}
 		thread.resume();
 		if (thread == currentThread) {
 		    currentThread.resetCurrentFrameIndex();
 		}
  	    }
	}
    }

    void cont() throws Exception {
        if (currentThread == null) {
            out.println("Nothing suspended.");
            return;
        }
	debugger.cont();
    }

    /* step
     *
     * step up (out of a function).
     * Courtesy of Gordon Hirsch of SAS.
     */
    void step(StringTokenizer t) throws Exception {
	if (currentThread == null) {
	    out.println("Nothing suspended.");
	    return;
	}
	try {
	    if (t.hasMoreTokens()) {
		String nt = t.nextToken().toLowerCase();
		if (nt.equals("up")) {
		    currentThread.stepOut();               
		} else {
		    currentThread.step(true);
		}
	    } else {
		currentThread.step(true);
	    }
	} catch (IllegalAccessError e) {
	    out.println("Current thread is not suspended.");
	}
    }

    /* stepi
     * step instruction.
     * Courtesy of Gordon Hirsch of SAS.
     */
    void stepi() throws Exception {
	if (currentThread == null) {
	    out.println("Nothing suspended.");
	    return;
	}
	try {
	    currentThread.step(false);
	} catch (IllegalAccessError e) {
	    out.println("Current thread is not suspended.");
	}
    }

    void next() throws Exception {
	if (currentThread == null) {
	    out.println("Nothing suspended.");
	    return;
	}
	try {
	    currentThread.next();
	} catch (IllegalAccessError e) {
	    out.println("Current thread is not suspended.");
	}
    }

    void kill(StringTokenizer t) throws Exception {
 	if (!t.hasMoreTokens()) {
	    out.println("Usage: kill <threadgroup name> or <thread id>");
	    return;
	}
	while (t.hasMoreTokens()) {
	    String idToken = t.nextToken();
	    int threadId;
	    try {
		threadId = Integer.valueOf(idToken).intValue();
	    } catch (NumberFormatException e) {
		threadId = 0;
	    }
	    RemoteThread thread = indexToThread(threadId);
	    if (thread != null) {
                out.println("killing thread: " + thread.getName());
		thread.stop();
                return;
	    } else {
		/* Check for threadgroup name, skipping "system". */
		RemoteThreadGroup tglist[] = debugger.listThreadGroups(null);
		tglist = debugger.listThreadGroups(tglist[0]);
		for (int i = 0; i < tglist.length; i++) {
		    if (tglist[i].getName().equals(idToken)) {
                        out.println("killing threadgroup: " + idToken);
			tglist[i].stop();
			return;
		    }
		}
		
		out.println("\"" + idToken +
				   "\" is not a valid threadgroup or id.");
	    }
	}
    }

    void catchException(StringTokenizer t) throws Exception {
 	if (!t.hasMoreTokens()) {
	    String exceptionList[] = debugger.getExceptionCatchList();
	    for (int i = 0; i < exceptionList.length; i++) {
		out.print("  " + exceptionList[i]);
		if ((i & 4) == 3 || (i == exceptionList.length - 1)) {
		    out.println();
		}
	    }
	} else {
	    String idClass = t.nextToken();
	    try {
		RemoteClass cls = getClassFromToken(idClass);
		cls.catchExceptions();
	    } catch (Exception e) {
		out.println("Invalid exception class name: " + idClass);
	    }
	}
    }
    
    void ignoreException(StringTokenizer t) throws Exception {
 	if (!t.hasMoreTokens()) {
	    String exceptionList[] = debugger.getExceptionCatchList();
	    for (int i = 0; i < exceptionList.length; i++) {
		out.print("  " + exceptionList[i]);
		if ((i & 4) == 3 || (i == exceptionList.length - 1)) {
		    out.println();
		}
	    }
	} else {
	    String idClass = t.nextToken();
	    try {
		RemoteClass cls = getClassFromToken(idClass);
		cls.ignoreExceptions();
	    } catch (Exception e) {
		out.println("Invalid exception class name: " + idClass);
	    }
	}
    }
    
    void up(StringTokenizer t) throws Exception {
	if (currentThread == null) {
	    out.println("Current thread not set.");
	    return;
	}

	int nLevels = 1;
	if (t.hasMoreTokens()) {
	    String idToken = t.nextToken();
	    int n;
	    try {
		n = Integer.valueOf(idToken).intValue();
	    } catch (NumberFormatException e) {
		n = 0;
	    }
	    if (n == 0) {
		out.println("Usage: up [n frames]");
		return;
	    }
	    nLevels = n;
	}

	try {
	    currentThread.up(nLevels);
	} catch (IllegalAccessError e) {
	    out.println("Thread isn't suspended.");
	} catch (ArrayIndexOutOfBoundsException e) {
	    out.println("End of stack.");
	}
    }

    void down(StringTokenizer t) throws Exception {
	if (currentThread == null) {
	    out.println("Current thread not set.");
	    return;
	}

	int nLevels = 1;
	if (t.hasMoreTokens()) {
	    String idToken = t.nextToken();
	    int n;
	    try {
		n = Integer.valueOf(idToken).intValue();
	    } catch (NumberFormatException e) {
		n = 0;
	    }
	    if (n == 0) {
		out.println("usage: down [n frames]");
		return;
	    }
	    nLevels = n;
	}

	try {
	    currentThread.down(nLevels);
	} catch (IllegalAccessError e) {
	    out.println("Thread isn't suspended.");
	} catch (ArrayIndexOutOfBoundsException e) {
	    out.println("End of stack.");
	}
    }

    void dumpStack(RemoteThread thread, boolean showPC) throws Exception {
	RemoteStackFrame[] stack = thread.dumpStack();
	if (stack.length == 0) {
	    out.println("Thread is not running (no stack).");
	} else {
	    int nFrames = stack.length;
	    for (int i = thread.getCurrentFrameIndex(); i < nFrames; i++) {
		out.print("  [" + (i + 1) + "] ");
		out.print(stack[i].toString());
		if (showPC) {
		    out.print(", pc = " + stack[i].getPC());
		}
		out.println();
	    }
	}
    }

    void where(StringTokenizer t, boolean showPC) throws Exception {
	if (!t.hasMoreTokens()) {
	    if (currentThread == null) {
		out.println("No thread specified.");
		return;
	    }
	    dumpStack(currentThread, showPC);
	} else {
	    String token = t.nextToken();
	    if (token.toLowerCase().equals("all")) {
		setDefaultThreadGroup();
		RemoteThread list[] = currentThreadGroup.listThreads(true);
		for (int i = 0; i < list.length; i++) {
		    out.println(list[i].getName() + ": ");
		    dumpStack(list[i], showPC);
		}
	    } else {
		int threadId = parseThreadId(token);
		if (threadId == 0) {
		    return;
		}
		dumpStack(indexToThread(threadId), showPC);
	    }
	}
    }

    void trace(String cmd, StringTokenizer t) throws Exception {
	if (!t.hasMoreTokens()) {
	    out.println("(i)trace < \"on\" | \"off\" >");
	    return;
	}
	
	String v = t.nextToken();
	boolean traceOn;
	if (v.equals("on")) {
	    traceOn = true;
	} else if (v.equals("off")) {
	    traceOn = false;
	} else {
	    out.println("(i)trace < \"on\" | \"off\" >");
	    return;
	}

	if (cmd.equals("trace")) {
	    debugger.trace(traceOn);
	} else {
	    debugger.itrace(traceOn);
	}
    }

    void memory() throws Exception {
	out.println("Free: " + debugger.freeMemory() + ", total: " +
			   debugger.totalMemory());
    }

    void gc() throws Exception {
        RemoteObject[] save_list = new RemoteObject[2];
        save_list[0] = currentThread;
        save_list[1] = currentThreadGroup;
        debugger.gc(save_list);
    }

    private RemoteClass getClassFromToken(String idToken) throws Exception {
	RemoteObject obj;
	if (idToken.startsWith("0x") ||
	    Character.isDigit(idToken.charAt(0))) {
	    /* It's an object id. */
	    int id;
	    try {
		id = RemoteObject.fromHex(idToken);
	    } catch (NumberFormatException e) {
		id = 0;
	    }
	    if (id == 0 || (obj = debugger.get(new Integer(id))) == null) {
		throw new IllegalArgumentException();
	    } else if (!(obj instanceof RemoteClass)) {
		throw new IllegalArgumentException();
	    }
	} else {
	    /* It's a class */
	    obj = debugger.findClass(idToken);
	    if (obj == null) {
		throw new IllegalArgumentException();
	    }
	}
	return (RemoteClass)obj;
    }

    void listBreakpoints() throws Exception {
        String bkptList[] = debugger.listBreakpoints();
	if (bkptList.length > 0) {
            out.println("Current breakpoints set:");
            for(int i = 0; i < bkptList.length; i++) {
                out.println("\t" + bkptList[i]);
            }
	} else {
	    out.println("No breakpoints set.");
	}
    }

    void stop(StringTokenizer t) throws Exception {
	if (!t.hasMoreTokens()) {
	    listBreakpoints();
	    return;
	}
	
	String idClass = null;
	try {
	    String modifier = t.nextToken();
	    boolean stopAt;
	    if (modifier.equals("at")) {
		stopAt = true;
	    } else if (modifier.equals("in")) {
		stopAt = false;
	    } else {
		out.println("Usage: stop at <class>:<line_number> or");
		out.println("       stop in <class>.<method_name>");
		return;
	    }

	    if (modifier.equals("at")) {
		idClass = t.nextToken(": \t\n\r");
		RemoteClass cls = getClassFromToken(idClass);

		String idLine = t.nextToken();
		int lineno = Integer.valueOf(idLine).intValue();

		String err = cls.setBreakpointLine(lineno);
		if (err.length() > 0) {
		    out.println(err);
		} else {
		    out.println("Breakpoint set at " + cls.getName() +
				       ":" + lineno);
		}
	    } else {
		idClass = t.nextToken(": \t\n\r");
                RemoteClass cls = null;
                String idMethod = null;

                try {
                    cls = getClassFromToken(idClass);
                } catch (IllegalArgumentException e) {
                    // Try stripping method from class.method token.
                    int idot = idClass.lastIndexOf(".");
                    if (idot == -1) {
                        out.println("\"" + idClass +
                            "\" is not a valid id or class name.");
                        return;
                    }
                    idMethod = idClass.substring(idot + 1);
                    idClass = idClass.substring(0, idot);
                    cls = getClassFromToken(idClass);
                }

                if (idMethod == null) {
                    idMethod = t.nextToken();
                }
                RemoteField method;
                try {
                    method = cls.getMethod(idMethod);

                    /*
                     * Prevent a breakpoint on overloaded method, since there
                     * is, currently,  no way to choose among the overloads.
                     */
                    RemoteField[] allMethods = cls.getMethods();
                    for (int i = 0; i < allMethods.length; i++) {
                        if (allMethods[i].getName().equals(idMethod)
                                        && (allMethods[i] != method)) {
                            out.println(cls.getName() + "." + idMethod 
                                + " is overloaded. Use the 'stop at' command to " 
                                + "set a breakpoint in one of the overloads");
                            return;
                            
                        }
                    }


                } catch (NoSuchMethodException nsme) {
		    out.println("Class " + cls.getName() +
				       " doesn't have a method " + idMethod);
		    return;
		}
		String err = cls.setBreakpointMethod(method);
		if (err.length() > 0) {
		    out.println(err);
		} else {
		    out.println("Breakpoint set in " + cls.getName() +
				       "." + idMethod);
		}
	    }
	} catch (NoSuchElementException e) {
		out.println("Usage: stop at <class>:<line_number> or");
		out.println("       stop in <class>.<method_name>");
	} catch (NumberFormatException e) {
	    out.println("Invalid line number.");
	} catch (IllegalArgumentException e) {
	    out.println("\"" + idClass +
			       "\" is not a valid id or class name.");
	}
    }

    void clear(StringTokenizer t) throws Exception {
	if (!t.hasMoreTokens()) {
	    listBreakpoints();
	    return;
	}
	
	String idClass = null;
	String idMethod = null;
	RemoteClass cls = null;
	try {
	    idClass = t.nextToken(": \t\n\r");
	    try {
	        cls = getClassFromToken(idClass);
            } catch (IllegalArgumentException e) {
                // Try stripping method from class.method token.
                int idot = idClass.lastIndexOf(".");
                if (idot == -1) {
                    out.println("\"" + idClass +
                        "\" is not a valid id or class name.");
                    return;
                }
                idMethod = idClass.substring(idot + 1);
                idClass = idClass.substring(0, idot);
                cls = getClassFromToken(idClass);
                RemoteField method;
                try {
                    method = cls.getMethod(idMethod);
                } catch (NoSuchMethodException nsme) {
		    out.println("\"" + idMethod + 
				"\" is not a valid method name of class " +
				cls.getName());
		    return;
		}
		String err = cls.clearBreakpointMethod(method);
	        if (err.length() > 0) {
		    out.println(err);
	        } else {
		    out.println("Breakpoint cleared at " + 
				cls.getName() + "." + idMethod);
		}
		return;
            }

	    String idLine = t.nextToken();
	    int lineno = Integer.valueOf(idLine).intValue();

	    String err = cls.clearBreakpointLine(lineno);
	    if (err.length() > 0) {
		out.println(err);
	    } else {
		out.println("Breakpoint cleared at " + cls.getName() +
				   ": " + lineno);
	    }
	} catch (NoSuchElementException e) {
	    out.println("Usage: clear <class>:<line_number>");
	    out.println("   or: clear <class>.<method>");
	} catch (NumberFormatException e) {
	    out.println("Usage: clear <class>:<line_number>");
	    out.println("   or: clear <class>.<method>");
	} catch (IllegalArgumentException e) {
	    out.println("\"" + idClass +
			       "\" is not a valid id or class name.");
	}
    }

    void list(StringTokenizer t) throws Exception {
	RemoteStackFrame frame = null;
	if (currentThread == null) {
	    out.println("No thread specified.");
	    return;
	}
	try {
	    frame = currentThread.getCurrentFrame();
	} catch (IllegalAccessError e) {
	    out.println("Current thread isn't suspended.");
	    return;
	} catch (ArrayIndexOutOfBoundsException e) {
	    out.println("Thread is not running (no stack).");
	    return;
	}
	
	int lineno;
	if (t.hasMoreTokens()) {
	    String id = t.nextToken();

            // See if token is a line number.
            try {
                lineno = Integer.valueOf(id).intValue();
            } catch (NumberFormatException nfe) {
                // It isn't -- see if it's a method name.
                try {
                    lineno = frame.getRemoteClass().getMethodLineNumber(id);
                } catch (NoSuchMethodException iobe) {
                    out.println(id + " is not a valid line number or " +
                                "method name for class " + 
                                frame.getRemoteClass().getName());
                    return;
                } catch (NoSuchLineNumberException nse) {
                    out.println("Line number information not found in " +
                                frame.getRemoteClass().getName());
                    return;
                }
            }
	} else {
	    lineno = frame.getLineNumber();
	}
	int startLine = (lineno > 4) ? lineno - 4 : 1;
	int endLine = startLine + 9;

	InputStream rawSourceFile = frame.getRemoteClass().getSourceFile();
	if (rawSourceFile == null) {
	    out.println("Unable to find " + 
                        frame.getRemoteClass().getSourceFileName());
	    return;
	}

	DataInputStream sourceFile = new DataInputStream(rawSourceFile);
	String sourceLine = null;

	/* Skip through file to desired window. */
	for (int i = 1; i <= startLine; i++) {
	    sourceLine = sourceFile.readLine();
	}
	if (sourceLine == null) {
	    out.println(new Integer(lineno).toString() +
                        " is an invalid line number for the file " +
                        frame.getRemoteClass().getSourceFileName());
	}

	/* Print lines */
	for (int i = startLine; i < endLine && sourceLine != null; i++) {
	    out.print(new Integer(i).toString() + "\t" +
			     ((i == lineno) ? "=> " : "   "));
	    out.println(sourceLine);
	    sourceLine = sourceFile.readLine();
	}
	    
    }

    /* Get or set the source file path list. */
    void use(StringTokenizer t) throws Exception {
	if (!t.hasMoreTokens()) {
	    out.println(debugger.getSourcePath());
	} else {
	    debugger.setSourcePath(t.nextToken());
	}
    }

    /* Print a stack variable */
    private void printVar(RemoteStackVariable var) {
        out.print("  " + var.getName());
        if (var.inScope()) {
            RemoteValue val = var.getValue();
            out.println(" = " + (val == null? "null" : val.toString()) );
        } else {
            out.println(" is not in scope");
        }
    }

    /* Print all local variables in current stack frame. */
    void locals() throws Exception {
	if (currentThread == null) {
	    out.println("No default thread specified: " +
			       "use the \"thread\" command first.");
	    return;
	}
        if (!currentThread.isSuspended()) {
            out.println("Thread isn't suspended.");
            return;
        }
	RemoteStackVariable rsv[] = currentThread.getStackVariables();
	if (rsv == null || rsv.length == 0) {
	    out.println("No local variables: try compiling with -g");
	    return;
	}
	out.println("Method arguments:");
	for (int i = 0; i < rsv.length; i++) {
	    if (rsv[i].methodArgument()) {
		printVar(rsv[i]);
	    }
	}
	out.println("Local variables:");
	for (int i = 0; i < rsv.length; i++) {
	    if (!rsv[i].methodArgument()) {
		printVar(rsv[i]);
            }
	}
	return;
    }

    static final String printDelimiters = ".[(";

    /* Print a specified reference. 
     * New print() implementation courtesy of S. Blackheath of IBM
     */
    void print(StringTokenizer t, boolean dumpObject) throws Exception {
	if (!t.hasMoreTokens()) {
	    out.println("No objects specified.");
            return;
	}

	int id;
	RemoteValue obj = null;

        while (t.hasMoreTokens()) {
	    String expr = t.nextToken();
	    StringTokenizer pieces =
	       new StringTokenizer(expr, printDelimiters, true);

	    String idToken = pieces.nextToken(); // There will be at least one.
	    if (idToken.startsWith("t@")) {
	        /* It's a thread */
	        setDefaultThreadGroup();
	        RemoteThread tlist[] = currentThreadGroup.listThreads(true);
	        try {
	            id = Integer.valueOf(idToken.substring(2)).intValue();
	        } catch (NumberFormatException e) {
		    id = 0;
	        }
	        if (id <= 0 || id > tlist.length) {
	            out.println("\"" + idToken +
		           "\" is not a valid thread id.");
                    continue;
	        }
	        obj = tlist[id - 1];

	    } else if (idToken.startsWith("$s")) {
	        int slotnum;
	        try {
	            slotnum = Integer.valueOf(idToken.substring(2)).intValue();
	        } catch (NumberFormatException e) {

	            out.println("\"" + idToken + "\" is not a valid slot.");
                    continue;
	        }
	        if (currentThread != null) {
	            RemoteStackVariable rsv[] = currentThread.getStackVariables();
		    if (rsv == null || slotnum >= rsv.length) {
		        out.println("\"" + idToken + "\" is not a valid slot.");
                        continue;
		    }
		    obj = rsv[slotnum].getValue();
	        }
		
	    } else if (idToken.startsWith("0x") ||
		       Character.isDigit(idToken.charAt(0))) {
	        /* It's an object id. */
	        try {
	            id = RemoteObject.fromHex(idToken);
	        } catch (NumberFormatException e) {
	            id = 0;
	        }
                if (id == 0 || (obj = debugger.get(new Integer(id))) == null) {
	            out.println("\"" + idToken + "\" is not a valid id.");
                    continue;
	        }
	    } else {
	        /* See if it's a local stack variable */
                RemoteStackVariable rsv = null;
	        if (currentThread != null) {
		    rsv = currentThread.getStackVariable(idToken);
		    if (rsv != null && !rsv.inScope()) {
		        out.println(idToken + " is not in scope.");
                        continue;
		    }
		    obj = (rsv == null) ? null : rsv.getValue();
	        }
	        if (rsv == null) {
                    String error = null;
                    /* See if it's an instance variable */
                    String instanceStr = idToken;
                    try {
                        instanceStr = instanceStr + pieces.nextToken("");
                    }
                    catch (NoSuchElementException e) {}

                    if (currentThread != null)
                        rsv = currentThread.getStackVariable("this");
                    if (rsv != null && rsv.inScope()) {
                        obj = rsv.getValue();

                        error = printModifiers(expr,
                              new StringTokenizer("."+instanceStr, printDelimiters, true),
                              dumpObject, obj, true);
                        if (error == null)
                            continue;
                    }

                    // If the above failed, then re-construct the same
                    // string tokenizer we had before.
                    pieces = new StringTokenizer(instanceStr, printDelimiters, true);
                    idToken = pieces.nextToken();

		    /* Try interpreting it as a class */
                    while (true) {
		        obj = debugger.findClass(idToken);
		        if (obj != null)             // break if this is a valid class name
                            break;
                        if (!pieces.hasMoreTokens()) // break if we run out of input
                            break;
                        String dot = pieces.nextToken();
                        if (!dot.equals("."))        // break if this token is not a dot
                            break;
                        if (!pieces.hasMoreTokens())
                            break;
                        // If it is a dot, then add the next token, and loop
                        idToken = idToken + dot + pieces.nextToken();
                    }
                    if (obj == null) {
                        if (error == null)
		            error = "\"" + expr + "\" is not a " + "valid local or class name.";
                    }
                    else {
                        String error2 = printModifiers(expr, pieces, dumpObject, obj, false);
                        if (error2 == null)
                            continue;
                        if (error == null)
                            error = error2;
                    }
                    out.println(error);
                    continue;
	        }
	    }
            String error = printModifiers(expr, pieces, dumpObject, obj, false);
            if (error != null)
                out.println(error);
        }
    }

    String printModifiers(String expr, StringTokenizer pieces, boolean dumpObject, RemoteValue obj,
        boolean could_be_local_or_class)
        throws Exception
    {
        RemoteInt noValue = new RemoteInt(-1);
        RemoteValue rv = noValue;

        // If the object is null, or a non-object type (integer, array, etc...)
        // then the value must be in rv.
        if (obj == null)
            rv = null;
        else
        if (!obj.isObject())
            rv = obj;

	String lastField = "";
	String idToken = pieces.hasMoreTokens() ? pieces.nextToken() : null;
	while (idToken != null) {

	    if (idToken.equals(".")) {
	        if (pieces.hasMoreTokens() == false) {
		    return "\"" + expr + "\" is not a valid expression.";
		}
		idToken = pieces.nextToken();

		if (rv != noValue) {
		    /* attempt made to get a field on a non-object */
		    return "\"" + lastField + "\" is not an object.";
		}
		lastField = idToken;

                /* Rather than calling RemoteObject.getFieldValue(), we do this so that
                 * we can report an error if the field doesn't exist. */
                {
	            RemoteField fields[] = ((RemoteObject)obj).getFields();
                    boolean found = false;
                    for (int i = fields.length-1; i >= 0; i--)
                        if (idToken.equals(fields[i].getName())) {
                            rv = ((RemoteObject)obj).getFieldValue(i);
                            found = true;
                            break;
                        }

                    if (!found) {
                        if (could_be_local_or_class)
                            /* expr is used here instead of idToken, because:
                             *   1. we know that we're processing the first token in the line,
                             *   2. if the user specified a class name with dots in it, 'idToken'
                             *      will only give the first token. */
                            return "\"" + expr + "\" is not a valid local, class name, or field of "
                                + obj.description();
                        else
                            return "\"" + idToken + "\" is not a valid field of "
                                + obj.description();
                    }
                }

                  // don't give long error message next time round the loop
                could_be_local_or_class = false;

		if (rv != null && rv.isObject()) {
		    obj = rv;
		    rv = noValue;
		}
		idToken =
		    pieces.hasMoreTokens() ? pieces.nextToken() : null;

	    } else if (idToken.equals("[")) {
		if (pieces.hasMoreTokens() == false) {
		    return "\"" + expr +
					"\" is not a valid expression.";
		}
		idToken = pieces.nextToken("]");
		try {
		    int index = Integer.valueOf(idToken).intValue();
		    rv = ((RemoteArray)obj).getElement(index);
		} catch (NumberFormatException e) {
		    return "\"" + idToken +
					   "\" is not a valid decimal number.";
		} catch (ArrayIndexOutOfBoundsException e) {
		    return idToken + " is out of bounds for " +
				obj.description();
		}
		if (rv != null && rv.isObject()) {
		    obj = rv;
		    rv = noValue;
		}
		if (pieces.hasMoreTokens() == false ||
		    (idToken = pieces.nextToken()).equals("]") == false) {
		    return "\"" + expr +
				        "\" is not a valid expression.";
		}
		idToken = pieces.hasMoreTokens() ?
		    pieces.nextToken(printDelimiters) : null;

	    } else if (idToken.equals("(")) {
	        return "print <method> not supported yet.";
	    } else {
		/* Should never get here. */
		return "invalid expression";
	    }
	}

	out.print(expr + " = ");
	if (rv != noValue) {
	    out.println((rv == null) ? "null" : rv.description());
	} else if (dumpObject && obj instanceof RemoteObject) {
	    out.println(obj.description() + " {");

	    if (obj instanceof RemoteClass) {
		RemoteClass cls = (RemoteClass)obj;

		out.print("    superclass = ");
		RemoteClass superClass = cls.getSuperclass();
		out.println((superClass == null) ?
				   "null" : superClass.description());

		out.print("    loader = ");
		RemoteObject loader = cls.getClassLoader();
		out.println((loader == null) ?
				   "null" : loader.description());

		RemoteClass interfaces[] = cls.getInterfaces();
		if (interfaces != null && interfaces.length > 0) {
		    out.println("    interfaces:");
		    for (int i = 0; i < interfaces.length; i++) {
		        out.println("        " + interfaces[i]);
		    }
		}
	    }

	    RemoteField fields[] = ((RemoteObject)obj).getFields();
	    if (obj instanceof RemoteClass && fields.length > 0) {
		out.println();
	    }
	    for (int i = 0; i < fields.length; i++) {
		String name = fields[i].getTypedName();
		String modifiers = fields[i].getModifiers();
		out.print("    " + modifiers + name + " = ");
		RemoteValue v = ((RemoteObject)obj).getFieldValue(i);
		out.println((v == null) ? "null" : v.description());
	    }
	    out.println("}");
	} else {
            out.println(obj.toString());
        }
        return null;
    }

    void help() {
	    out.println("** command list **");
	    out.println("threads [threadgroup]     -- list threads");
	    out.println("thread <thread id>        -- set default thread");
	    out.println("suspend [thread id(s)]    -- suspend threads (default: all)");
	    out.println("resume [thread id(s)]     -- resume threads (default: all)");
	    out.println("where [thread id] | all   -- dump a thread's stack");
	    out.println("wherei [thread id] | all  -- dump a thread's stack, with pc info");
	    out.println("threadgroups              -- list threadgroups");
	    out.println("threadgroup <name>        -- set current threadgroup\n");
	    out.println("print <id> [id(s)]        -- print object or field");
	    out.println("dump <id> [id(s)]         -- print all object information\n");
	    out.println("locals                    -- print all local variables in current stack frame\n");
	    out.println("classes                   -- list currently known classes");
	    out.println("methods <class id>        -- list a class's methods\n");
	    out.println("stop in <class id>.<method> -- set a breakpoint in a method");
	    out.println("stop at <class id>:<line> -- set a breakpoint at a line");
	    out.println("up [n frames]             -- move up a thread's stack");
	    out.println("down [n frames]           -- move down a thread's stack");
	    out.println("clear <class id>:<line>   -- clear a breakpoint");
	    out.println("step                      -- execute current line");
	    out.println("step up                   -- execute until the current method returns to its caller");  // SAS GVH step out
	    out.println("stepi                     -- execute current instruction");
	    out.println("next                      -- step one line (step OVER calls)");
	    out.println("cont                      -- continue execution from breakpoint\n");
	    out.println("catch <class id>          -- break for the specified exception");
	    out.println("ignore <class id>         -- ignore when the specified exception\n");
	    out.println("list [line number|method] -- print source code");
	    out.println("use [source file path]    -- display or change the source path\n");
	    out.println("memory                    -- report memory usage");
	    out.println("gc                        -- free unused objects\n");
	    out.println("load classname            -- load Java class to be debugged");
	    out.println("run <class> [args]        -- start execution of a loaded Java class");
//	    out.println("kill <thread(group)>      -- kill a thread or threadgroup\n");
	    out.println("!!                        -- repeat last command");
	    out.println("help (or ?)               -- list commands");
	    out.println("exit (or quit)            -- exit debugger");
	}

    void executeCommand(StringTokenizer t) {
	String cmd = t.nextToken().toLowerCase();

	try {
	    if (cmd.equals("print")) {
		print(t, false);
	    } else if (cmd.equals("dump")) {
		print(t, true);
	    } else if (cmd.equals("locals")) {
		locals();
	    } else if (cmd.equals("classes")) {
		classes();
	    } else if (cmd.equals("methods")) {
		methods(t);
	    } else if (cmd.equals("threads")) {
		threads(t);
	    } else if (cmd.equals("thread")) {
		thread(t);
	    } else if (cmd.equals("suspend")) {
		suspend(t);
	    } else if (cmd.equals("resume")) {
		resume(t);
	    } else if (cmd.equals("threadgroups")) {
		threadGroups();
	    } else if (cmd.equals("threadgroup")) {
		threadGroup(t);
	    } else if (cmd.equals("catch")) {
		catchException(t);
	    } else if (cmd.equals("ignore")) {
		ignoreException(t);
	    } else if (cmd.equals("cont")) {
		cont();
	    } else if (cmd.equals("step")) {
		step(t);
	    } else if (cmd.equals("stepi")) {
		stepi();
	    } else if (cmd.equals("next")) {
		next();
            } else if (cmd.equals("kill")) {
                kill(t);
	    } else if (cmd.equals("where")) {
		where(t, false);
	    } else if (cmd.equals("wherei")) {
		where(t, true);
	    } else if (cmd.equals("up")) {
		up(t);
	    } else if (cmd.equals("down")) {
		down(t);
	    } else if (cmd.equals("load")) {
		load(t);
	    } else if (cmd.equals("run")) {
		run(t);
	    } else if (cmd.equals("memory")) {
		memory();
            } else if (cmd.equals("gc")) {
                gc();
//                   This cannot reasonably work
//	    } else if (cmd.equals("trace") || cmd.equals("itrace")) {
//		trace(cmd, t);
	    } else if (cmd.equals("stop")) {
		stop(t);
	    } else if (cmd.equals("clear")) {
		clear(t);
	    } else if (cmd.equals("list")) {
		list(t);
	    } else if (cmd.equals("use")) {
		use(t);
	    } else if (cmd.equals("help") || cmd.equals("?")) {
		help();
	    } else if (cmd.equals("quit") || cmd.equals("exit")) {
		debugger.close();
		System.exit(0);
	    } else {
		out.println("huh? Try help...");
	    }
	} catch (Exception e) {
	    out.println("Internal exception:");
	    out.flush();
	    e.printStackTrace();
	}
    }

    void readCommandFile(File f) {
	try {
	    if (f.canRead()) {
		// Process initial commands.
		DataInputStream inFile = 
		    new DataInputStream(new FileInputStream(f));
		String ln;
		while ((ln = inFile.readLine()) != null) {
		    StringTokenizer t = new StringTokenizer(ln);
		    if (t.hasMoreTokens()) {
			executeCommand(t);
		    }
		}
	    }
	} catch (IOException e) {}
    }

    public TTY(String host, String password, String javaArgs, String args, 
               PrintStream outStream, PrintStream consoleStream,
               boolean verbose) throws Exception {
        System.out.println("Initializing " + progname + "...");
	out = outStream;
	console = consoleStream;
        if (password == null) {
            debugger = new RemoteDebugger(javaArgs, this, verbose);
        } else {
            debugger = new RemoteDebugger(host, password, this, verbose);
        }
	DataInputStream in = new DataInputStream(System.in);
	String lastLine = null;

	if (args != null && args.length() > 0) {
	    StringTokenizer t = new StringTokenizer(args);
	    load(t);
	    lastArgs = args;
	}

	Thread.currentThread().setPriority(Thread.NORM_PRIORITY);

	// Try reading user's startup file.
	File f = new File(System.getProperty("user.home") + 
	    System.getProperty("file.separator") + "jdb.ini");
        if (!f.canRead()) {
            // Try opening $HOME/jdb.ini
            f = new File(System.getProperty("user.home") + 
                         System.getProperty("file.separator") + ".jdbrc");
        }
        readCommandFile(f);

	// Try opening local jdb.ini
	f = new File(System.getProperty("user.dir") + 
	    System.getProperty("file.separator") + "startup.jdb");
        readCommandFile(f);

	// Process interactive commands.
	while (true) {
            printPrompt();
	    String ln = in.readLine();
	    if (ln == null) {
		out.println("Input stream closed.");
		return;
	    }

	    if (ln.startsWith("!!") && lastLine != null) {
		ln = lastLine + ln.substring(2);
		out.println(ln);
	    }

	    StringTokenizer t = new StringTokenizer(ln);
	    if (t.hasMoreTokens()) {
		lastLine = ln;
		executeCommand(t);
	    }
	}
    }

    private static void usage() {
        System.out.println("Usage: " + progname + " <options> <classes>");
        System.out.println();
        System.out.println("where options include:");
        System.out.println("    -help             print out this message and exit");
        System.out.println("    -version          print out the build version and exit");
        System.out.println("    -host <hostname>  host machine of interpreter to attach to");
        System.out.println("    -password <psswd> password of interpreter to attach to (from -debug)");
        System.out.println("options forwarded to debuggee process:");
        System.out.println("    -v -verbose       turn on verbose mode");
        System.out.println("    -debug            enable remote JAVA debugging");
        System.out.println("    -noasyncgc        don't allow asynchronous garbage collection");
        System.out.println("    -verbosegc        print a message when garbage collection occurs");
        System.out.println("    -noclassgc        disable class garbage collection");
        System.out.println("    -cs -checksource  check if source is newer when loading classes");
        System.out.println("    -ss<number>       set the maximum native stack size for any thread");
        System.out.println("    -oss<number>      set the maximum Java stack size for any thread");
        System.out.println("    -ms<number>       set the initial Java heap size");
        System.out.println("    -mx<number>       set the maximum Java heap size");
        System.out.println("    -D<name>=<value>  set a system property");
        System.out.println("    -classpath <directories separated by colons>");
        System.out.println("                      list directories in which to look for classes");
        System.out.println("    -prof[:<file>]    output profiling data to ./java.prof or ./<file>");
        System.out.println("    -verify           verify all classes when read in");
        System.out.println("    -verifyremote     verify classes read in over the network [default]");
        System.out.println("    -noverify         do not verify any class");
        System.out.println("    -dbgtrace         print info for debugging " + progname);
        System.out.println();
        System.out.println("For command help type 'help' at " + progname + " prompt");
    }

    public static void main(String argv[]) {
	// Get host attribute, if any.
	String localhost;
	try {
	    localhost = InetAddress.getLocalHost().getHostName();
	} catch (Exception ex) {
	    localhost = null;
	}	
	if (localhost == null) {
	    localhost = "localhost";
	}
	String host = null;
	String password = null;
	String cmdLine = "";
	String javaArgs = "";
        boolean verbose = false;
	
	for (int i = 0; i < argv.length; i++) {
	    String token = argv[i];
	    if (token.equals("-dbgtrace")) {
		verbose = true;
	    } else if (token.equals("-cs") || token.equals("-checksource") ||
		       token.equals("-noasyncgc") || token.equals("-prof") ||
		       token.equals("-v") || token.equals("-verbose") ||
		       token.equals("-verify") || token.equals("-noverify") ||
		       token.equals("-verifyremote") ||
		       token.equals("-verbosegc") ||
		       token.startsWith("-ms") || token.startsWith("-mx") ||
		       token.startsWith("-ss") || token.startsWith("-oss") ||
		       token.startsWith("-D")) {
		javaArgs += token + " ";
	    } else if (token.equals("-classpath")) {
		if (i == (argv.length - 1)) {
		    System.out.println("No classpath specified.");
		    usage();
		    System.exit(1);
		}
		javaArgs += token + " " + argv[++i] + " ";
	    } else if (token.equals("-host")) {
		if (i == (argv.length - 1)) {
		    System.out.println("No host specified.");
		    usage();
		    System.exit(1);
		}
		host = argv[++i];
	    } else if (token.equals("-password")) {
		if (i == (argv.length - 1)) {
		    System.out.println("No password specified.");
		    usage();
		    System.exit(1);
		}
		password = argv[++i];
	    } else if (token.equals("-help")) {
		usage();
		System.exit(0);
	    } else if (token.equals("-version")) {
		System.out.println(progname + " version " + version);
		System.exit(0);
	    } else if (token.startsWith("-")) {
		System.out.println("invalid option: " + token);
		usage();
		System.exit(1);
	    } else {
                // Everything from here is part of the command line
                cmdLine = token + " ";
                for (i++; i < argv.length; i++) {
                    cmdLine += argv[i] + " ";
                }
                break;
	    }
	}
	if (host != null && password == null) {
	    System.out.println("A debug password must be specified for " +
			       "remote debugging.");
	    System.exit(1);
	}
	if (host == null) {
	    host = localhost;
	}

	try {
	    if (!host.equals(localhost) && password.length() == 0) {
		System.out.println(
		    "No password supplied for accessing remote " +
		    "Java interpreter.");
		System.out.println(
		    "The password is reported by the remote interpreter" +
		    "when it is started.");
                System.exit(1);
            }
            new TTY(host, password, javaArgs, cmdLine, 
                    System.out, System.out, verbose);
	} catch(SocketException se) {
	    System.out.println("Failed accessing debugging session on " +
			       host + ": invalid password.");
	} catch(NumberFormatException ne) {
	    System.out.println("Failed accessing debugging session on " +
			       host + ": invalid password.");
	} catch(Exception e) {		
	    System.out.print("Internal exception:  ");
	    System.out.flush();
	    e.printStackTrace();
	}
    }
}
