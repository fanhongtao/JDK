/*
 * @(#)PrivilegedActionException.java	1.3 98/06/29
 *
 * Copyright 1998 by Sun Microsystems, Inc.,
 * 901 San Antonio Road, Palo Alto, California, 94303, U.S.A.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
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
    /**
     * @serial
     */
    private Exception exception;

    /**
     * Constructs a new PrivilegedActionException "wrapping"
     * the specific Exception
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
}
