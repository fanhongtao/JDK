/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.lang.reflect;

import java.io.PrintStream;
import java.io.PrintWriter;

/**
 * InvocationTargetException is a checked exception that wraps
 * an exception thrown by an invoked method or constructor.
 *
 * @see Method
 * @see Constructor
 *
 */
public
class InvocationTargetException extends Exception {

    /**
     * Use serialVersionUID from JDK 1.1.X for interoperability
     */
    private static final long serialVersionUID = 4085088731926701167L;

     /**
     * This field holds the target if the 
     * InvocationTargetException(Throwable target) constructor was
     * used to instantiate the object
     * 
     * @serial 
     * 
     */
    private Throwable target;

    /**
     * Constructs an <code>InvocationTargetException</code> with 
     * <code>null</code> as the target exception.
     */
    protected InvocationTargetException() {
	super();
    }

    /**
     * Constructs a InvocationTargetException with a target exception.
     */
    public InvocationTargetException(Throwable target) {
	super();
	this.target = target;
    }

    /**
     * Constructs a InvocationTargetException with a target exception
     * and a detail message.
     */
    public InvocationTargetException(Throwable target, String s) {
	super(s);
	this.target = target;
    }

    /**
     * Get the thrown target exception.
     */
    public Throwable getTargetException() {
	return target;
    }

    /**
     * Prints the stack trace of the thrown target exception.
     *
     * @see     java.lang.System#err
     */
    public void printStackTrace() {
	printStackTrace(System.err);
    }

    /**
     * Prints the stack trace of the thrown target exception to the specified
     * print stream.
     */
    public void printStackTrace(PrintStream ps) {
	synchronized (ps) {
	    if (target != null) {
		ps.print("java.lang.reflect.InvocationTargetException: ");
		target.printStackTrace(ps);
	    } else {
		super.printStackTrace(ps);
	    }
	}
    }

    /**
     * Prints the stack trace of the thrown target exception to the
     * specified print writer.
     */
    public void printStackTrace(PrintWriter pw) {
	synchronized (pw) {
	    if (target != null) {
		pw.print("java.lang.reflect.InvocationTargetException: ");
		target.printStackTrace(pw);
	    } else {
		super.printStackTrace(pw);
	    }
	}
    }

}
