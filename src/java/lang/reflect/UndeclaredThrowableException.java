/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.lang.reflect;

import java.io.PrintStream;
import java.io.PrintWriter;

/**
 * Thrown by a method invocation on a proxy instance if its invocation
 * handler's {@link InvocationHandler#invoke invoke} method throws a
 * checked exception (a <code>Throwable</code> that is not assignable
 * to <code>RuntimeException</code> or <code>Error</code>) that
 * is not assignable to any of the exception types declared in the
 * <code>throws</code> clause of the method that was invoked on the
 * proxy instance and dispatched to the invocation handler.
 *
 * <p>An <code>UndeclaredThrowableException</code> instance contains
 * the undeclared checked exception that was thrown by the invocation
 * handler, and it can be retrieved with the
 * <code>getUndeclaredThrowable()</code> method.
 * <code>UndeclaredThrowableException</code> extends
 * <code>RuntimeException</code>, so it is an unchecked exception
 * that wraps a checked exception.
 *
 * @author	Peter Jones
 * @version	1.7, 02/02/06
 * @see		InvocationHandler
 * @since	JDK1.3
 */
public class UndeclaredThrowableException extends RuntimeException {

    /**
     * the undeclared checked exception that was thrown
     * @serial
     */
    private Throwable undeclaredThrowable;

    /**
     * Constructs an <code>UndeclaredThrowableException</code> with the
     * specifed <code>Throwable</code>.
     *
     * @param	undeclaredThrowable the undeclared checked exception
     *		that was thrown
     */
    public UndeclaredThrowableException(Throwable undeclaredThrowable) {
	super();
	this.undeclaredThrowable = undeclaredThrowable;
    }

    /**
     * Constructs an <code>UndeclaredThrowableException</code> with the
     * specified <code>Throwable</code> and a detail message.
     *
     * @param	undeclaredThrowable the undeclared checked exception
     *		that was thrown
     * @param	s the detail message
     */
    public UndeclaredThrowableException(Throwable undeclaredThrowable,
					String s)
    {
	super(s);
	this.undeclaredThrowable = undeclaredThrowable;
    }

    /**
     * Returns the <code>Throwable</code> instance wrapped in this
     * <code>UndeclaredThrowableException</code>.
     *
     * @return the undeclared checked exception that was thrown
     */
    public Throwable getUndeclaredThrowable() {
	return undeclaredThrowable;
    }

    /**
     * Prints this <code>UndeclaredThrowableException</code> and its 
     * backtrace to the standard error stream. 
     */
    public void printStackTrace() {
	printStackTrace(System.err);
    }

    /**
     * Prints this <code>UndeclaredThrowableException</code> and its 
     * backtrace to the specified <code>PrintStream</code>.
     */
    public void printStackTrace(PrintStream ps) {
	synchronized (ps) {
	    if (undeclaredThrowable != null) {
		ps.print("java.lang.reflect.UndeclaredThrowableException: ");
		undeclaredThrowable.printStackTrace(ps);
	    } else {
		super.printStackTrace(ps);
	    }
	}
    }

    /**
     * Prints this <code>UndeclaredThrowableException</code> and its 
     * backtrace to the specified <code>PrintWriter</code>.
     */
    public void printStackTrace(PrintWriter pw) {
	synchronized (pw) {
	    if (undeclaredThrowable != null) {
		pw.print("java.lang.reflect.UndeclaredThrowableException: ");
		undeclaredThrowable.printStackTrace(pw);
	    } else {
		super.printStackTrace(pw);
	    }
	}
    }
}
