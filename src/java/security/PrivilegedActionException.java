/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.security;

import java.io.PrintWriter;
import java.io.PrintStream;


/**
 * This exception is thrown by
 * <code>doPrivileged(PrivilegedExceptionAction)</code> and
 * <code>doPrivileged(PrivilegedExceptionAction,
 * AccessControlContext context)</code> to indicate
 * that the action being performed threw a checked exception.  The exception
 * thrown by the action can be obtained by calling the
 * <code>getException</code> method.  In effect, an
 * <code>PrivilegedActionException</code> is a "wrapper"
 * for an exception thrown by a privileged action.
 *
 * @see PrivilegedExceptionAction
 * @see AccessController#doPrivileged(PrivilegedExceptionAction)
 * @see AccessController#doPrivileged(PrivilegedExceptionAction,AccessControlContext)
 */
public
class PrivilegedActionException extends Exception {
    // use serialVersionUID from JDK 1.2.2 for interoperability
    private static final long serialVersionUID = 4724086851538908602L;

    /**
     * @serial
     */
    private Exception exception;

    /**
     * Constructs a new PrivilegedActionException &quot;wrapping&quot;
     * the specific Exception.
     *
     * @param exception The exception thrown
     */
    public PrivilegedActionException(Exception exception) {
	this.exception = exception;
    }

    /**
     * Returns the exception thrown by the privileged computation that
     * resulted in this <code>PrivilegedActionException</code>.
     *
     * @return the exception thrown by the privileged computation that
     *         resulted in this <code>PrivilegedActionException</code>.
     * @see PrivilegedExceptionAction
     * @see AccessController#doPrivileged(PrivilegedExceptionAction)
     * @see AccessController#doPrivileged(PrivilegedExceptionAction,
     *                                            AccessControlContext)
     */
    public Exception getException() {
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
		ps.print("java.security.PrivilegedActionException: ");
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
		pw.print("java.security.PrivilegedActionException: ");
		exception.printStackTrace(pw);
	    } else {
		super.printStackTrace(pw);
	    }
	}
    }

    /**
     * Returns a string describing this exception, including a description
     * of the exception it wraps.
     *
     * @return a string representation of this
     * <code>PrivilegedActionException</code>
     */
    public String toString() {
	return getClass().getName() + " <<" + this.exception.toString() + ">>";
    }
}
