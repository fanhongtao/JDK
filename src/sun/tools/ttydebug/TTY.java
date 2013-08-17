/*
 * @(#)TTY.java	1.70 97/05/08
 * 
 * Copyright (c) 1995, 1996 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the confidential and proprietary information of Sun
 * Microsystems, Inc. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Sun.
 * 
 * SUN MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF THE
 * SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE, OR NON-INFRINGEMENT. SUN SHALL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING
 * THIS SOFTWARE OR ITS DERIVATIVES.
 * 
 * CopyrightVersion 1.1_beta
 * 
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

    private String lastArgs = null;
    
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

	for (int i = 0 ; i < tlist.length ; i++) {
	    char buf[] = new char[80];
	    for (int j = 0; j < 79; j++) {
		buf[j] = ' ';
	    }
	    buf[79] = '\0';
	    StringBuffer sbOut = new StringBuffer();
	    sbOut.append(buf);

	    sbOut.insert(((i + iThread + 1) < 10) ? 1 : 0, (i + iThread + 1));
	    sbOut.insert(2, ".");
	    int iBuf = 4;
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

    void step() throws Exception {
	if (currentThread == null) {
	    out.println("Nothing suspended.");
	    return;
	}
	try {
	    currentThread.step(true);
	} catch (IllegalAccessError e) {
	    out.println("Current thread is not at breakpoint.");
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
	    out.println("Current thread is not at breakpoint.");
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

    void dumpStack(RemoteThread thread) throws Exception {
	RemoteStackFrame[] stack = thread.dumpStack();
	if (stack.length == 0) {
	    out.println("Thread is not running (no stack).");
	} else {
	    int nFrames = stack.length;
	    for (int i = thread.getCurrentFrameIndex(); i < nFrames; i++) {
		out.print("  [" + (i + 1) + "] ");
		out.println(stack[i].toString());
	    }
	}
    }

    void where(StringTokenizer t) throws Exception {
	if (!t.hasMoreTokens()) {
	    if (currentThread == null) {
		out.println("No thread specified.");
		return;
	    }
	    dumpStack(currentThread);
	} else {
	    String token = t.nextToken();
	    if (token.toLowerCase().equals("all")) {
		setDefaultThreadGroup();
		RemoteThread list[] = currentThreadGroup.listThreads(true);
		for (int i = 0; i < list.length; i++) {
		    out.println(list[i].getName() + ": ");
		    dumpStack(list[i]);
		}
	    } else {
		int threadId = parseThreadId(token);
		if (threadId == 0) {
		    return;
		}
		dumpStack(indexToThread(threadId));
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

    /* Print a specified reference.  Returns success in resolving reference. */
    boolean print(StringTokenizer t, boolean dumpObject, boolean recursing) throws Exception {
	if (!t.hasMoreTokens()) {
	    out.println("No objects specified.");
	    return false;
	}

	while (t.hasMoreTokens()) {
	    int id;
	    RemoteValue obj = null;

	    String delimiters = ".[(";
	    String expr = t.nextToken();
	    StringTokenizer pieces =
		new StringTokenizer(expr, delimiters, true);

	    String idToken = pieces.nextToken(); // There will be at least one.
	    String varName = expr;
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
		    return false;
		}
		obj = tlist[id - 1];

	    } else if (idToken.startsWith("$s")) {
		int slotnum;
		try {
		    slotnum = Integer.valueOf(idToken.substring(2)).intValue();
		} catch (NumberFormatException e) {
		    out.println("\"" + idToken +
				       "\" is not a valid slot.");
		    return false;
		}
		if (currentThread != null) {
		    RemoteStackVariable rsv[] = currentThread.getStackVariables();
		    if (rsv == null || slotnum >= rsv.length) {
			out.println("\"" + idToken +
					   "\" is not a valid slot.");
			return false;
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
		    out.println("\"" + idToken +
				       "\" is not a valid id.");
		    return false;
		}
	    } else {
		/* See if it's a local stack variable */
		if (currentThread != null) {
		    RemoteStackVariable rsv =
			currentThread.getStackVariable(idToken);
		    if (rsv != null && !rsv.inScope()) {
		        out.println(idToken + " is not in scope.");
			return false;
		    }
		    obj = (rsv == null) ? null : rsv.getValue();
		}
		if (obj == null) {
		    if (idToken.equals("this") == false) {
			/* See if it's an instance variable */
			String instanceStr = "this." + idToken;
			if (print(new StringTokenizer(instanceStr),
				  dumpObject, true))
			    return true;
		    }
		    
		    /* It's a class */
		    obj = debugger.findClass(idToken);
		    if (obj == null) {
			if (!recursing) {
			    out.println("\"" + expr + "\" is not a " +
					       "valid id or class name.");
			}
			return false;
		    }
		}
	    }

	    RemoteInt noValue = new RemoteInt(-1);
	    RemoteValue rv = noValue;
	    String lastField = "";
	    idToken = pieces.hasMoreTokens() ? pieces.nextToken() : null;
	    while (idToken != null) {

		if (idToken.equals(".")) {
		    if (pieces.hasMoreTokens() == false) {
			out.println("\"" + expr +
					   "\" is not a valid expression.");
			return false;
		    }
		    idToken = pieces.nextToken();

		    if (rv != noValue) {
			/* attempt made to get a field on a non-object */
			out.println("\"" + lastField +
					   "\" is not an object.");
			return false;
		    }
		    lastField = idToken;
			
		    rv = ((RemoteObject)obj).getFieldValue(idToken);
		    if (rv == null) {
			out.println("\"" + idToken +
					   "\" is not a valid field of " +
					   obj.description());
			return false;
		    }
		    if (rv.isObject()) {
			obj = rv;
			rv = noValue;
		    }
		    idToken =
			pieces.hasMoreTokens() ? pieces.nextToken() : null;

		} else if (idToken.equals("[")) {
		    if (pieces.hasMoreTokens() == false) {
			out.println("\"" + expr +
					   "\" is not a valid expression.");
			return false;
		    }
		    idToken = pieces.nextToken("]");
		    try {
			int index = Integer.valueOf(idToken).intValue();
			rv = ((RemoteArray)rv).getElement(index);
		    } catch (NumberFormatException e) {
			out.println("\"" + idToken +
					   "\" is not a valid decimal number.");
			return false;
		    } catch (ArrayIndexOutOfBoundsException e) {
			out.println(idToken + " is out of bounds for " +
					   obj.description());
			return false;
		    }
		    if (rv != null && rv.isObject()) {
			obj = rv;
			rv = noValue;
		    }
		    if (pieces.hasMoreTokens() == false ||
			(idToken = pieces.nextToken()).equals("]") == false) {
			out.println("\"" + expr +
					   "\" is not a valid expression.");
			return false;
		    }
		    idToken = pieces.hasMoreTokens() ?
			pieces.nextToken(delimiters) : null;

		} else if (idToken.equals("(")) {
		    out.println("print <method> not supported yet.");
		    return false;
		} else {
		    /* Should never get here. */
		    out.println("invalid expression");
		    return false;
		}
	    }

	    out.print(varName + " = ");
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
	}
	return true;
    }

    void help
	() {
	    out.println("** command list **");
	    out.println("threads [threadgroup]     -- list threads");
	    out.println("thread <thread id>        -- set default thread");
	    out.println("suspend [thread id(s)]    -- suspend threads (default: all)");
	    out.println("resume [thread id(s)]     -- resume threads (default: all)");
	    out.println("where [thread id] | all   -- dump a thread's stack");
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
		print(t, false, false);
	    } else if (cmd.equals("dump")) {
		print(t, true, false);
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
		step();
	    } else if (cmd.equals("next")) {
		next();
            } else if (cmd.equals("kill")) {
                kill(t);
	    } else if (cmd.equals("where")) {
		where(t);
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
	    } else if (cmd.equals("trace") || cmd.equals("itrace")) {
		trace(cmd, t);
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
        System.out.println("Initializing jdb...");
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
	String classArgs = "";
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
		    System.exit(1);
		}
		javaArgs += token + " " + argv[++i] + " ";
	    } else if (token.equals("-host")) {
		if (i == (argv.length - 1)) {
		    System.out.println("No host specified.");
		    System.exit(1);
		}
		host = argv[++i];
	    } else if (token.equals("-password")) {
		if (i == (argv.length - 1)) {
		    System.out.println("No password specified.");
		    System.exit(1);
		}
		password = argv[++i];
	    } else {
		classArgs += token + " ";
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
            new TTY(host, password, javaArgs, classArgs, 
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
