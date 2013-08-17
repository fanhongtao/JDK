/*
 * @(#)ExceptionInInitializerError.java	1.9 98/08/25
 *
 * Copyright 1996-1998 by Sun Microsystems, Inc.,
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

import java.io.PrintStream;
import java.io.PrintWriter;

/**
 * Signals that an unexpected exception has occurred in a static initializer. 
 * An <code>ExceptionInInitializerError</code> is thrown to indicate that an 
 * exception occurred during evaluation of a static initializer or the 
 * initializer for a static variable.
 *
 * @author  Frank Yellin
 * @version 1.9, 08/25/98
 *
 * @since   JDK1.1
 */
public
class ExceptionInInitializerError extends LinkageError {

    /**
     * Use serialVersionUID from JDK 1.1.X for interoperability
     */
    private static final long serialVersionUID = 1521711792217232256L;

    /**
     * This field holds the exception if the 
     * ExceptionInInitializerError(Throwable thrown) constructor was
     * used to instantiate the object
     * 
     * @serial 
     * 
     */
    private Throwable exception;

    /**
     * Constructs an <code>ExceptionInInitializerError</code> with 
     * <code>null</code> as its detail message string and with no saved 
     * thowable object. 
     * A detail message is a String that describes this particular exception.
     */
    public ExceptionInInitializerError() {
	super();
    }

    /**
     * Constructs a new <code>ExceptionInInitializerError</code> class by 
     * saving a reference to the <code>Throwable</code> object thrown for 
     * later retrieval by the {@link #getException()} method. The detail 
     * message string is set to <code>null</code>.
     *
     * @param thrown The exception thrown
     */
    public ExceptionInInitializerError(Throwable thrown) {
	this.exception = thrown;
    }

    /**
     * Constructs an ExceptionInInitializerError with the specified detail 
     * message string.  A detail message is a String that describes this 
     * particular exception. The detail message string is saved for later 
     * retrieval by the {@link Throwable#getMessage()} method. There is no 
     * saved throwable object. 
     *
     *
     * @param s the detail message
     */
    public ExceptionInInitializerError(String s) {
	super(s);
    }

    /**
     * Returns the exception that occurred during a static initialization that
     * caused this Error to be created.
     * 
     * @return the saved throwable object of this 
     *         <code>ExceptionInInitializerError</code>, or <code>null</code> 
     *         if this <code>ExceptionInInitializerError</code> has no saved 
     *         throwable object. 
     */
    public Throwable getException() {
	return exception;
    }

    /**
     * Prints the stack trace of the exception that occurred.
     *
     * @see     java.lang.System#err
     */
    public void printStackTrace() {
	printStackTrace(System.err);
    }

    /**
     * Prints the stack trace of the exception that occurred to the
     * specified print stream.
     */
    public void printStackTrace(PrintStream ps) {
	synchronized (ps) {
	    if (exception != null) {
		ps.print("java.lang.ExceptionInInitializerError: ");
		exception.printStackTrace(ps);
	    } else {
		super.printStackTrace(ps);
	    }
	}
    }

    /**
     * Prints the stack trace of the exception that occurred to the
     * specified print writer.
     */
    public void printStackTrace(PrintWriter pw) {
	synchronized (pw) {
	    if (exception != null) {
		pw.print("java.lang.ExceptionInInitializerError: ");
		exception.printStackTrace(pw);
	    } else {
		super.printStackTrace(pw);
	    }
	}
    }

}
