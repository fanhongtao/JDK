/*
 * @(#)Runtime.java	1.3 00/03/24
 *
 * Copyright 1995-2000 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */

package java.lang;

import java.io.*;
import java.util.StringTokenizer;

/**
 * Every Java application has a single instance of class 
 * <code>Runtime</code> that allows the application to interface with 
 * the environment in which the application is running. The current 
 * runtime can be obtained from the <code>getRuntime</code> method. 
 * <p>
 * An application cannot create its own instance of this class. 
 *
 * @author  unascribed
 * @version 1.47, 10/17/98
 * @see     java.lang.Runtime#getRuntime()
 * @since   JDK1.0
 */

public class Runtime {
    private static Runtime currentRuntime = new Runtime();
      
    /**
     * Returns the runtime object associated with the current Java application.
     * Most of the methods of class <code>Runtime</code> are instance 
     * methods and must be invoked with respect to the current runtime object. 
     * 
     * @return  the <code>Runtime</code> object associated with the current
     *          Java application.
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
     * First, if there is a security manager, its <code>checkExit</code> 
     * method is called with the status as its argument. This may result 
     * in a security exception. 
     * <p>
     * The argument serves as a status code; by convention, a nonzero 
     * status code indicates abnormal termination. 
     * <p>
     * The method {@link System#exit(int)} is the conventional and 
     * convenient means of invoking this method.
     *
     * @param      status   exit status. By convention, a nonzero status 
     *             code indicates abnormal termination.
     * @throws  SecurityException
     *        if a security manager exists and its <code>checkExit</code> 
     *        method doesn't allow exit with the specified status.
     * @see        java.lang.SecurityException
     * @see        java.lang.SecurityManager#checkExit(int)
     */
    public void exit(int status) {
	SecurityManager security = System.getSecurityManager();
	if (security != null) {
	    security.checkExit(status);
	}
	exitInternal(status);
    }

    /* Wormhole for calling java.lang.ref.Finalizer.setRunFinalizersOnExit */
    private static native void runFinalizersOnExit0(boolean value);

    /**
     * Enable or disable finalization on exit; doing so specifies that the
     * finalizers of all objects that have finalizers that have not yet been
     * automatically invoked are to be run before the Java runtime exits.
     * By default, finalization on exit is disabled.
     * 
     * <p>If there is a security manager, 
     * its <code>checkExit</code> method is first called
     * with 0 as its argument to ensure the exit is allowed. 
     * This could result in a SecurityException.
     *
     * @deprecated  This method is inherently unsafe.  It may result in
     * 	    finalizers being called on live objects while other threads are
     *      concurrently manipulating those objects, resulting in erratic
     *	    behavior or deadlock.
     * 
     * @throws  SecurityException
     *        if a security manager exists and its <code>checkExit</code> 
     *        method doesn't allow the exit.
     *
     * @see     java.lang.Runtime#exit(int)
     * @see     java.lang.Runtime#gc()
     * @see     java.lang.SecurityManager#checkExit(int)
     * @since   JDK1.1
     */
    public static void runFinalizersOnExit(boolean value) {
	SecurityManager security = System.getSecurityManager();
	if (security != null) {
	    try {
		security.checkExit(0); 
	    } catch (SecurityException e) {
		throw new SecurityException("runFinalizersOnExit");
	    }
	}
	runFinalizersOnExit0(value);
    }

    /* Helper for exec
     */
    private native Process execInternal(String cmdarray[], String envp[]) 
	 throws IOException;

    /**
     * Executes the specified string command in a separate process. 
     * <p>
     * The <code>command</code> argument is parsed into tokens and then 
     * executed as a command in a separate process. The token parsing is 
     * done by a {@link java.util.StringTokenizer} created by the call:
     * <blockquote><pre>
     * new StringTokenizer(command)
     * </pre></blockquote> 
     * with no further modifications of the character categories. 
     * This method has exactly the same effect as 
     * <code>exec(command, null)</code>. 
     *
     * @param      command   a specified system command.
     * @return     a <code>Process</code> object for managing the subprocess.
     * @exception  SecurityException  if a security manager exists and its  
     *             <code>checkExec</code> method doesn't allow creation of a subprocess.
     * @see        java.lang.Runtime#exec(java.lang.String, java.lang.String[])
     * @see     java.lang.SecurityManager#checkExec(java.lang.String)
     */
    public Process exec(String command) throws IOException {
	return exec(command, null);
    }

    /**
     * Executes the specified string command in a separate process with the 
     * specified environment. 
     * <p>
     * This method breaks the <code>command</code> string into tokens and 
     * creates a new array <code>cmdarray</code> containing the tokens in the 
     * order that they were produced by the string tokenizer; it 
     * then performs the call <code>exec(cmdarray, envp)</code>. The token
     * parsing is done by a {@link java.util.StringTokenizer} created by 
     * the call: 
     * <blockquote><pre>
     * new StringTokenizer(command)
     * </pre></blockquote>
     * with no further modification of the character categories. 
     *
     * @param      command   a specified system command.
     * @param      envp      array of strings, each element of which 
     *                       has environment variable settings in format
     *                       <i>name</i>=<i>value</i>.
     * @return     a <code>Process</code> object for managing the subprocess.
     * @exception  SecurityException  if a security manager exists and its  
     *             <code>checkExec</code> method doesn't allow creation of a subprocess.
     * @see        java.lang.Runtime#exec(java.lang.String[])
     * @see        java.lang.Runtime#exec(java.lang.String[], java.lang.String[])
     * @see        java.lang.SecurityManager#checkExec(java.lang.String)
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
	return exec(cmdarray, envp);
    }

    /**
     * Executes the specified command and arguments in a separate process.
     * <p>
     * The command specified by the tokens in <code>cmdarray</code> is 
     * executed as a command in a separate process. This has exactly the 
     * same effect as <code>exec(cmdarray, null)</code>. 
     * <p>
     * If there is a security manager, its <code>checkExec</code> 
     * method is called with the first component of the array 
     * <code>cmdarray</code> as its argument. This may result in a security 
     * exception. 
     *
     * @param      cmdarray   array containing the command to call and
     *                        its arguments.
     * @return     a <code>Process</code> object for managing the subprocess.
     * @exception  SecurityException  if a security manager exists and its  
     *             <code>checkExec</code> method doesn't allow creation of a subprocess.
     * @see        java.lang.Runtime#exec(java.lang.String[], java.lang.String[])
     * @see        java.lang.SecurityManager#checkExec(java.lang.String)
     */
    public Process exec(String cmdarray[]) throws IOException {
	return exec(cmdarray, null);
    }

    /**
     * Executes the specified command and arguments in a separate process
     * with the specified environment. 
     * <p>
     * If there is a security manager, its <code>checkExec</code> 
     * method is called with the first component of the array 
     * <code>cmdarray</code> as its argument. This may result in a security 
     * exception. 
     * <p>
     * Given an array of strings <code>cmdarray</code>, representing the 
     * tokens of a command line, and an array of strings <code>envp</code>, 
     * representing "environment" variable settings, this method creates 
     * a new process in which to execute the specified command. 
     *
     * @param      cmdarray   array containing the command to call and
     *                        its arguments.
     * @param      envp       array of strings, each element of which 
     *                        has environment variable settings in format
     *                        <i>name</i>=<i>value</i>.
     * @return     a <code>Process</code> object for managing the subprocess.
     * @exception  SecurityException  if a security manager exists and its  
     *             <code>checkExec</code> method doesn't allow creation of a subprocess.
     * @exception  NullPointerException if <code>cmdarray</code> is 
     *             <code>null</code>.
     * @exception  IndexOutOfBoundsException if <code>cmdarray</code> is an 
     *             empty array (has length <code>0</code>).
     * @see     java.lang.Process
     * @see     java.lang.SecurityException
     * @see     java.lang.SecurityManager#checkExec(java.lang.String)
     */
    public Process exec(String cmdarray[], String envp[]) throws IOException {
        cmdarray = (String[])cmdarray.clone();
        envp = (envp != null ? (String[])envp.clone() : null);

        if (cmdarray.length == 0) {
            throw new IndexOutOfBoundsException();            
        }
        for (int i = 0; i < cmdarray.length; i++) {
            if (cmdarray[i] == null) {
                throw new NullPointerException();
            }
        }
        if (envp != null) {
            for (int i = 0; i < envp.length; i++) {
                if (envp[i] == null) {
                    throw new NullPointerException();
                }
            }
        }
	SecurityManager security = System.getSecurityManager();
	if (security != null) {
	    security.checkExec(cmdarray[0]);
	}
	return execInternal(cmdarray, envp);
    }

    /**
     * Returns the amount of free memory in the system. Calling the 
     * <code>gc</code> method may result in increasing the value returned 
     * by <code>freeMemory.</code>
     *
     * @return  an approximation to the total amount of memory currently
     *          available for future allocated objects, measured in bytes.
     */
    public native long freeMemory();

    /**
     * Returns the total amount of memory in the Java Virtual Machine. 
     * The value returned by this method may vary over time, depending on 
     * the host environment.
     * <p>
     * Note that the amount of memory required to hold an object of any 
     * given type may be implementation-dependent.
     * 
     * @return  the total amount of memory currently available for current 
     *          and future objects, measured in bytes.
     */
    public native long totalMemory();

    /**
     * Runs the garbage collector.
     * Calling this method suggests that the Java Virtual Machine expend 
     * effort toward recycling unused objects in order to make the memory 
     * they currently occupy available for quick reuse. When control 
     * returns from the method call, the Java Virtual Machine has made 
     * its best effort to recycle all discarded objects. 
     * <p>
     * The name <code>gc</code> stands for "garbage 
     * collector". The Java Virtual Machine performs this recycling 
     * process automatically as needed, in a separate thread, even if the 
     * <code>gc</code> method is not invoked explicitly.
     * <p>
     * The method {@link System#gc()} is hte conventional and convenient 
     * means of invoking this method. 
     */
    public native void gc();

    /* Wormhole for calling java.lang.ref.Finalizer.runFinalization */
    private static native void runFinalization0();

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
     * automatically as needed, in a separate thread, if the 
     * <code>runFinalization</code> method is not invoked explicitly. 
     * <p>
     * The method {@link System#runFinalization()} is the conventional 
     * and convenient means of invoking this method.
     *
     * @see     java.lang.Object#finalize()
     */
    public void runFinalization() {
	runFinalization0();
    }

    /**
     * Enables/Disables tracing of instructions.
     * If the <code>boolean</code> argument is <code>true</code>, this 
     * method suggests that the Java Virtual Machine emit debugging 
     * information for each instruction in the Java Virtual Machine as it 
     * is executed. The format of this information, and the file or other 
     * output stream to which it is emitted, depends on the host environment. 
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
     */
    public native void traceInstructions(boolean on);

    /**
     * Enables/Disables tracing of method calls.
     * If the <code>boolean</code> argument is <code>true</code>, this 
     * method suggests that the Java Virtual Machine emit debugging 
     * information for each method in the Java Virtual Machine as it is 
     * called. The format of this information, and the file or other output 
     * stream to which it is emitted, depends on the host environment. The 
     * virtual machine may ignore this request if it does not support 
     * this feature.  
     * <p>
     * Calling this method with argument false suggests that the Java 
     * Virtual Machine cease emitting per-call debugging information.
     *
     * @param   on   <code>true</code> to enable instruction tracing;
     *               <code>false</code> to disable this feature.
     */
    public native void traceMethodCalls(boolean on);

    /**
     * Loads the specified filename as a dynamic library. The filename 
     * argument must be a complete pathname. 
     * From <code>java_g</code> it will automagically insert "_g" before the
     * ".so" (for example
     * <code>Runtime.getRuntime().load("/home/avh/lib/libX11.so");</code>).
     * <p>
     * First, if there is a security manager, its <code>checkLink</code> 
     * method is called with the <code>filename</code> as its argument. 
     * This may result in a security exception. 
     * <p>
     * This is similar to the method {@link #loadLibrary(String)}, but it 
     * accepts a general file name as an argument rathan than just a library 
     * name, allowing any file of native code to be loaded.
     * <p>
     * The method {@link System#load(String)} is the conventional and 
     * convenient means of invoking this method.
     *
     * @param      filename   the file to load.
     * @exception  SecurityException  if a security manager exists and its  
     *             <code>checkLink</code> method doesn't allow 
     *             loading of the specified dynamic library
     * @exception  UnsatisfiedLinkError  if the file does not exist.
     * @see        java.lang.Runtime#getRuntime()
     * @see        java.lang.SecurityException
     * @see        java.lang.SecurityManager#checkLink(java.lang.String)
     */
    public void load(String filename) {
        load0(System.getCallerClass(), filename);
    }

    synchronized void load0(Class fromClass, String filename) {
	SecurityManager security = System.getSecurityManager();
	if (security != null) {
	    security.checkLink(filename);
	}
	if (!(new File(filename).isAbsolute())) {
	    throw new UnsatisfiedLinkError(
	        "Expecting an absolute path of the library: " + filename);
	}
	ClassLoader.loadLibrary(fromClass, filename, true);
    }

    /**
     * Loads the dynamic library with the specified library name. 
     * A file containing native code is loaded from the local file system 
     * from a place where library files are conventionally obtained. The 
     * details of this process are implementation-dependent. The 
     * mapping from a library name to a specific filename is done in a 
     * system-specific manner. 
     * <p>
     * First, if there is a security manager, its <code>checkLink</code> 
     * method is called with the <code>libname</code> as its argument. 
     * This may result in a security exception. 
     * <p>
     * The method {@link System#loadLibrary(String)} is the conventional 
     * and convenient means of invoking this method. If native
     * methods are to be used in the implementation of a class, a standard 
     * strategy is to put the native code in a library file (call it 
     * <code>LibFile</code>) and then to put a static initializer:
     * <blockquote><pre>
     * static { System.loadLibrary("LibFile"); }
     * </pre></blockquote>
     * within the class declaration. When the class is loaded and 
     * initialized, the necessary native code implementation for the native 
     * methods will then be loaded as well. 
     * <p>
     * If this method is called more than once with the same library 
     * name, the second and subsequent calls are ignored. 
     *
     * @param      libname   the name of the library.
     * @exception  SecurityException  if a security manager exists and its  
     *             <code>checkLink</code> method doesn't allow 
     *             loading of the specified dynamic library
     * @exception  UnsatisfiedLinkError  if the library does not exist.
     * @see        java.lang.SecurityException
     * @see        java.lang.SecurityManager#checkLink(java.lang.String)
     */
    public void loadLibrary(String libname) {
        loadLibrary0(System.getCallerClass(), libname); 
    }

    synchronized void loadLibrary0(Class fromClass, String libname) {
	SecurityManager security = System.getSecurityManager();
	if (security != null) {
	    security.checkLink(libname);
	}
	if (libname.indexOf((int)File.separatorChar) != -1) {
	    throw new UnsatisfiedLinkError(
    "Directory separator should not appear in library name: " + libname);
	}
	ClassLoader.loadLibrary(fromClass, libname, false);
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
