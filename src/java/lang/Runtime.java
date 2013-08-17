/*
 * @(#)Runtime.java	1.28 00/04/06
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

package java.lang;

import java.io.*;
import java.util.StringTokenizer;

/*
 * Every Java application has a single instance of class 
 * <code>Runtime</code> that allows the application to interface with 
 * the environment in which the application is running. The current 
 * runtime can be obtained from the <code>getRuntime</code> method. 
 * <p>
 * An application cannot create its own instance of this class. 
 *
 * @author  unascribed
 * @version 1.28, 04/06/00
 * @see     java.lang.Runtime#getRuntime()
 * @since   JDK1.0
 */
public class Runtime {
    private static Runtime currentRuntime = new Runtime();
      
    /**
     * Returns the runtime object associated with the current Java application.
     *
     * @return  the <code>Runtime</code> object associated with the current
     *          Java application.
     * @since   JDK1.0
     */
    public static Runtime getRuntime() { 
	return currentRuntime;
    }
    
    /** Don't let anyone else instantiate this class */
    private Runtime() {}

    /* Helper for exit
     */
    private native void exitInternal(int status);

    /**
     * Terminates the currently running Java Virtual Machine. This 
     * method never returns normally. 
     * <p>
     * If there is a security manager, its <code>checkExit</code> method 
     * is called with the status as its argument. This may result in a 
     * security exception. 
     * <p>
     * The argument serves as a status code; by convention, a nonzero 
     * status code indicates abnormal termination. 
     *
     * @param      status   exit status.
     * @exception  SecurityException  if the current thread cannot exit with
     *               the specified status.
     * @see        java.lang.SecurityException
     * @see        java.lang.SecurityManager#checkExit(int)
     * @since      JDK1.0
     */
    public void exit(int status) {
	SecurityManager security = System.getSecurityManager();
	if (security != null) {
	    security.checkExit(status);
	}
	exitInternal(status);
    }

    /**
     * Enable or disable finalization on exit; doing so specifies that the
     * finalizers of all objects that have finalizers that have not yet been
     * automatically invoked are to be run before the Java runtime exits.
     * By default, finalization on exit is disabled.  An invocation of
     * the runFinalizersOnExit method is permitted only if the caller is
     * allowed to exit, and is otherwise rejected by the security manager.
     * @see Runtime#gc
     * @see Runtime#exit
     * @since   JDK1.1
     */
    public static void runFinalizersOnExit(boolean value) {
	SecurityManager security = System.getSecurityManager();
	if (security != null) {
	    try { security.checkExit(0); }
	    catch (SecurityException e) {
		throw new SecurityException("runFinalizersOnExit");
	    }
	}
	runFinalizersOnExit0(value);
    }

    /*
     * Private variable holding the boolean determining whether to finalize
     * on exit.  The default value of the variable is false.  See the comment
     * on Runtime.runFinalizersOnExit for constraints on modifying this.
     */
    private static native void runFinalizersOnExit0(boolean value);

    /* Helper for exec
     */
    private native Process execInternal(String cmdarray[], String envp[]) 
	 throws IOException;

    /**
     * Executes the specified string command in a separate process. 
     * <p>
     * The <code>command</code> argument is parsed into tokens and then 
     * executed as a command in a separate process. This method has 
     * exactly the same effect as <code>exec(command, null)</code>. 
     *
     * @param      command   a specified system command.
     * @return     a <code>Process</code> object for managing the subprocess.
     * @exception  SecurityException  if the current thread cannot create a
     *             subprocess.
     * @see        java.lang.Runtime#exec(java.lang.String, java.lang.String[])
     * @since      JDK1.0
     */
    public Process exec(String command) throws IOException {
	return exec(command, null);
    }

    /**
     * Executes the specified string command in a separate process with the 
     * specified environment. 
     * <p>
     * This method breaks the <code>command</code> string into tokens and 
     * creates a new array <code>cmdarray</code> containing the tokens; it 
     * then performs the call <code>exec(cmdarray, envp)</code>. 
     *
     * @param      command   a specified system command.
     * @param      envp      array containing environment in format
     *                       <i>name</i>=<i>value</i>
     * @return     a <code>Process</code> object for managing the subprocess.
     * @exception  SecurityException  if the current thread cannot create a
     *               subprocess.
     * @see        java.lang.Runtime#exec(java.lang.String[], java.lang.String[])
     * @since      JDK1.0
     */
    public Process exec(String command, String envp[]) throws IOException {
	int count = 0;
	String cmdarray[];
 	StringTokenizer st;

	st = new StringTokenizer(command);
 	count = st.countTokens();

	cmdarray = new String[count];
	st = new StringTokenizer(command);
	count = 0;
 	while (st.hasMoreTokens()) {
 		cmdarray[count++] = st.nextToken();
 	}
	SecurityManager security = System.getSecurityManager();
	if (security != null) {
	    security.checkExec(cmdarray[0]);
	}
	return execInternal(cmdarray, envp);
    }

    /**
     * Executes the specified command and arguments in a separate process.
     * <p>
     * The command specified by the tokens in <code>cmdarray</code> is 
     * executed as a command in a separate process. This has exactly the 
     * same effect as <code>exec(cmdarray, null)</code>. 
     *
     * @param      cmdarray   array containing the command to call and
     *                        its arguments.
     * @return     a <code>Process</code> object for managing the subprocess.
     * @exception  SecurityException  if the current thread cannot create a
     *               subprocess.
     * @see        java.lang.Runtime#exec(java.lang.String[], java.lang.String[])
     * @since      JDK1.0
     */
    public Process exec(String cmdarray[]) throws IOException {
	return exec(cmdarray, null);
    }

    /**
     * Executes the specified command and arguments in a separate process
     * with the specified environment. 
     * <p>
     * If there is a security manager, its <code>checkExec</code> method 
     * is called with the first component of the array 
     * <code>cmdarray</code> as its argument. This may result in a security 
     * exception. 
     * <p>
     * Given an array of strings <code>cmdarray</code>, representing the 
     * tokens of a command line, and an array of strings <code>envp</code>, 
     * representing an "environment" that defines system 
     * properties, this method creates a new process in which to execute 
     * the specified command. 
     *
     * @param      cmdarray   array containing the command to call and
     *                        its arguments.
     * @param      envp       array containing environment in format
     *                        <i>name</i>=<i>value</i>.
     * @return     a <code>Process</code> object for managing the subprocess.
     * @exception  SecurityException  if the current thread cannot create a
     *               subprocess.
     * @see     java.lang.SecurityException
     * @see     java.lang.SecurityManager#checkExec(java.lang.String)
     * @since   JDK1.0
     */
    public Process exec(String cmdarray[], String envp[]) throws IOException {
        cmdarray = (String[])cmdarray.clone();
        envp = (envp != null ? (String[])envp.clone() : null);
 
	SecurityManager security = System.getSecurityManager();
	if (security != null) {
	    security.checkExec(cmdarray[0]);
	}
	return execInternal(cmdarray, envp);
    }

    /**
     * Returns the amount of free memory in the system. The value returned
     * by this method is always less than the value returned by the
     * <code>totalMemory</code> method. Calling the <code>gc</code> method may
     * result in increasing the value returned by <code>freeMemory.</code>
     *
     * @return  an approximation to the total amount of memory currently
     *          available for future allocated objects, measured in bytes.
     * @since   JDK1.0
     */
    public native long freeMemory();

    /**
     * Returns the total amount of memory in the Java Virtual Machine. 
     *
     * @return  the total amount of memory currently available for allocating
     *          objects, measured in bytes.
     * @since   JDK1.0
     */
    public native long totalMemory();

    /**
     * Runs the garbage collector.
     * Calling this method suggests that the Java Virtual Machine expend 
     * effort toward recycling unused objects in order to make the memory 
     * they currently occupy available for quick reuse. When control 
     * returns from the method call, the Java Virtual Machine has made 
     * its best effort to recycle all unused objects. 
     * <p>
     * The name <code>gc</code> stands for "garbage 
     * collector". The Java Virtual Machine performs this recycling 
     * process automatically as needed even if the <code>gc</code> method 
     * is not invoked explicitly. 
     *
     * @since   JDK1.0
     */
    public native void gc();

    /**
     * Runs the finalization methods of any objects pending finalization.
     * Calling this method suggests that the Java Virtual Machine expend 
     * effort toward running the <code>finalize</code> methods of objects 
     * that have been found to be discarded but whose <code>finalize</code> 
     * methods have not yet been run. When control returns from the 
     * method call, the Java Virtual Machine has made a best effort to 
     * complete all outstanding finalizations. 
     * <p>
     * The Java Virtual Machine performs the finalization process 
     * automatically as needed if the <code>runFinalization</code> method 
     * is not invoked explicitly. 
     *
     * @see     java.lang.Object#finalize()
     * @since   JDK1.0
     */
    public native void runFinalization();

    /**
     * Enables/Disables tracing of instructions.
     * If the <code>boolean</code> argument is <code>true</code>, this 
     * method asks the Java Virtual Machine to print out a detailed trace 
     * of each instruction in the Java Virtual Machine as it is executed. 
     * The virtual machine may ignore this request if it does not support 
     * this feature. The destination of the trace output is system 
     * dependent. 
     * <p>
     * If the <code>boolean</code> argument is <code>false</code>, this 
     * method causes the Java Virtual Machine to stop performing the 
     * detailed instruction trace it is performing. 
     *
     * @param   on   <code>true</code> to enable instruction tracing;
     *               <code>false</code> to disable this feature.
     * @since   JDK1.0
     */
    public native void traceInstructions(boolean on);

    /**
     * Enables/Disables tracing of method calls.
     * If the <code>boolean</code> argument is <code>true</code>, this 
     * method asks the Java Virtual Machine to print out a detailed trace 
     * of each method in the Java Virtual Machine as it is called. The 
     * virtual machine may ignore this request if it does not support 
     * this feature. The destination of the trace output is system dependent. 
     * <p>
     * If the <code>boolean</code> argument is <code>false</code>, this 
     * method causes the Java Virtual Machine to stop performing the 
     * detailed method trace it is performing. 
     *
     * @param   on   <code>true</code> to enable instruction tracing;
     *               <code>false</code> to disable this feature.
     * @since   JDK1.0
     */
    public native void traceMethodCalls(boolean on);

    /**
     * Initializes the linker and returns the search path for shared libraries.
     */
    private synchronized native String initializeLinkerInternal();
    private native String buildLibName(String pathname, String filename);

    /* Helper for load and loadLibrary */
    private native int loadFileInternal(String filename);

    /** The paths searched for libraries */
    private String paths[];

    private void initializeLinker() {
	String ldpath = initializeLinkerInternal();
	char c = System.getProperty("path.separator").charAt(0);
	int ldlen = ldpath.length();
	int i, j, n;
	// Count the separators in the path
	i = ldpath.indexOf(c);
	n = 0;
	while (i >= 0) {
	    n++;
	    i = ldpath.indexOf(c, i+1);
	}

	// allocate the array of paths - n :'s = n + 1 path elements
	paths = new String[n + 1];

	// Fill the array with paths from the ldpath
	n = i = 0;
	j = ldpath.indexOf(c);
	while (j >= 0) {
	    if (j - i > 0) {
		paths[n++] = ldpath.substring(i, j);
	    } else if (j - i == 0) { 
		paths[n++] = ".";
	    }
	    i = j + 1;
	    j = ldpath.indexOf(c, i);
	}
	paths[n] = ldpath.substring(i, ldlen);
    }

    /**
     * Loads the specified filename as a dynamic library. The filename 
     * argument must be a complete pathname. 
     * From <code>java_g</code> it will automagically insert "_g" before the
     * ".so" (for example
     * <code>Runtime.getRuntime().load("/home/avh/lib/libX11.so");</code>).
     * <p>
     * If there is a security manager, its <code>checkLink</code> method 
     * is called with the <code>filename</code> as its argument. This may 
     * result in a security exception. 
     *
     * @param      filename   the file to load.
     * @exception  SecurityException     if the current thread cannot load the
     *               specified dynamic library.
     * @exception  UnsatisfiedLinkError  if the file does not exist.
     * @see        java.lang.Runtime#getRuntime()
     * @see        java.lang.SecurityException
     * @see        java.lang.SecurityManager#checkLink(java.lang.String)
     * @since      JDK1.0
     */
    public synchronized void load(String filename) {
	SecurityManager security = System.getSecurityManager();
	int ret;
	if (security != null) {
	    security.checkLink(filename);
	}
	ret = loadFileInternal(filename);
	if (ret == -1) {
	    throw new OutOfMemoryError();
	} else if (ret == 0) {
	    throw new UnsatisfiedLinkError(filename);
	}   /* else load was successful; return */
    }

    /**
     * Loads the dynamic library with the specified library name. The 
     * mapping from a library name to a specific filename is done in a 
     * system-specific manner. 
     * <p>
     * First, if there is a security manager, its <code>checkLink</code> 
     * method is called with the <code>filename</code> as its argument. 
     * This may result in a security exception. 
     * <p>
     * If this method is called more than once with the same library 
     * name, the second and subsequent calls are ignored. 
     *
     * @param      libname   the name of the library.
     * @exception  SecurityException     if the current thread cannot load the
     *               specified dynamic library.
     * @exception  UnsatisfiedLinkError  if the library does not exist.
     * @see        java.lang.SecurityException
     * @see        java.lang.SecurityManager#checkLink(java.lang.String)
     * @since      JDK1.0
     */
    public synchronized void loadLibrary(String libname) {
	SecurityManager security = System.getSecurityManager();
	if (security != null) {
	    security.checkLink(libname);
	}
        if (paths == null) {
            initializeLinker();
	}
	for (int i = 0 ; i < paths.length ; i++) {
	    int ret;
	    String tempname = buildLibName(paths[i], libname);
	    ret = loadFileInternal(tempname);
	    if (ret == -1) {
		throw new OutOfMemoryError();
	    } else if (ret == 1) {	// Loaded or found it already loaded
		return;
	    }
	}
	// Oops, it failed
        throw new UnsatisfiedLinkError("no " + libname + 
					   " in shared library path");
    }

    /**
     * Creates a localized version of an input stream. This method takes 
     * an <code>InputStream</code> and returns an <code>InputStream</code> 
     * equivalent to the argument in all respects except that it is 
     * localized: as characters in the local character set are read from 
     * the stream, they are automatically converted from the local 
     * character set to Unicode. 
     * <p>
     * If the argument is already a localized stream, it may be returned 
     * as the result. 
     *
     * @deprecated As of JDK&nbsp;1.1, the preferred way translate a byte
     * stream in the local encoding into a character stream in Unicode is via
     * the <code>InputStreamReader</code> and <code>BufferedReader</code>
     * classes.
     *
     * @return     a localized input stream.
     * @see        java.io.InputStream
     * @see        java.io.BufferedReader#BufferedReader(java.io.Reader)
     * @see        java.io.InputStreamReader#InputStreamReader(java.io.InputStream)
     */
    public InputStream getLocalizedInputStream(InputStream in) {
	return in;
    }

    /**
     * Creates a localized version of an output stream. This method 
     * takes an <code>OutputStream</code> and returns an 
     * <code>OutputStream</code> equivalent to the argument in all respects 
     * except that it is localized: as Unicode characters are written to 
     * the stream, they are automatically converted to the local 
     * character set. 
     * <p>
     * If the argument is already a localized stream, it may be returned 
     * as the result. 
     *
     * @deprecated As of JDK&nbsp;1.1, the preferred way to translate a
     * Unicode character stream into a byte stream in the local encoding is via
     * the <code>OutputStreamWriter</code>, <code>BufferedWriter</code>, and
     * <code>PrintWriter</code> classes.
     *
     * @return     a localized output stream.
     * @see        java.io.OutputStream
     * @see        java.io.BufferedWriter#BufferedWriter(java.io.Writer)
     * @see        java.io.OutputStreamWriter#OutputStreamWriter(java.io.OutputStream)
     * @see        java.io.PrintWriter#PrintWriter(java.io.OutputStream)
     */
    public OutputStream getLocalizedOutputStream(OutputStream out) {
	return out;
    }

}
