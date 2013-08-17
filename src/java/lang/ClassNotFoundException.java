/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.lang;

import java.io.PrintStream;
import java.io.PrintWriter;

/**
 * Thrown when an application tries to load in a class through its 
 * string name using:
 * <ul>
 * <li>The <code>forName</code> method in class <code>Class</code>.
 * <li>The <code>findSystemClass</code> method in class
 *     <code>ClassLoader</code> .
 * <li>The <code>loadClass</code> method in class <code>ClassLoader</code>.
 * </ul>
 * <p>
 * but no definition for the class with the specifed name could be found. 
 *
 * @author  unascribed
 * @version 1.14, 02/06/02
 * @see     java.lang.Class#forName(java.lang.String)
 * @see     java.lang.ClassLoader#findSystemClass(java.lang.String)
 * @see     java.lang.ClassLoader#loadClass(java.lang.String, boolean)
 * @since   JDK1.0
 */
public
class ClassNotFoundException extends Exception {
    /**
     * use serialVersionUID from JDK 1.1.X for interoperability
     */
     private static final long serialVersionUID = 9176873029745254542L;

    /**
     * This field holds the exception ex if the 
     * ClassNotFoundException(String s, Throwable ex) constructor was
     * used to instantiate the object
     * @serial 
     * @since 1.2
     */
    private Throwable ex;

    /**
     * Constructs a <code>ClassNotFoundException</code> with no detail message.
     */
    public ClassNotFoundException() {
	super();
    }

    /**
     * Constructs a <code>ClassNotFoundException</code> with the 
     * specified detail message. 
     *
     * @param   s   the detail message.
     */
    public ClassNotFoundException(String s) {
	super(s);
    }

    /**
     * Constructs a <code>ClassNotFoundException</code> with the
     * specified detail message and optional exception that was
     * raised while loading the class.
     *
     * @param s the detail message
     * @param ex the exception that was raised while loading the class
     * @since 1.2
     */
    public ClassNotFoundException(String s, Throwable ex) {
	super(s);
	this.ex = ex;
    }

    /**
     * Returns the exception that was raised if an error occurred while
     * attempting to load the class. Otherwise, returns null.
     *
     * @return the <code>Exception</code> that was raised while loading a class
     * @since 1.2
     */
    public Throwable getException() {
	return ex;
    }

    /**
     * Prints the stack backtrace. 
     * 
     * If an exception occurred during class loading it prints that
     * exception's stack trace, or else prints the stack backtrace of
     * this exception.
     *
     * @see java.lang.System#err
     */
    public void printStackTrace() { 
	printStackTrace(System.err);
    }
    
    /**
     * Prints the stack backtrace to the specified print stream.
     *
     * If an exception occurred during class loading it prints that
     * exception's stack trace, or else prints the stack backtrace of
     * this exception.
     */
    public void printStackTrace(PrintStream ps) { 
	synchronized (ps) {
	    if (ex != null) {
		ps.print("java.lang.ClassNotFoundException: ");
		ex.printStackTrace(ps);
	    } else {
		super.printStackTrace(ps);
	    }
	}
    }
    
    /**
     * Prints the stack backtrace to the specified print writer.
     *
     * If an exception occurred during class loading it prints that
     * exception's stack trace, or else prints the stack backtrace of
     * this exception.
     */
    public void printStackTrace(PrintWriter pw) { 
	synchronized (pw) {
	    if (ex != null) {
		pw.print("java.lang.ClassNotFoundException: ");
		ex.printStackTrace(pw);
	    } else {
		super.printStackTrace(pw);
	    }
	}
    }

}
