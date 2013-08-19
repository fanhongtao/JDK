/*
 * @(#)ServerCloneException.java	1.18 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.rmi.server;

/**
 * A <code>ServerCloneException</code> is thrown if a remote exception occurs
 * during the cloning of a <code>UnicastRemoteObject</code>.
 *
 * <p>As of release 1.4, this exception has been retrofitted to conform to
 * the general purpose exception-chaining mechanism.  The "nested exception"
 * that may be provided at construction time and accessed via the public
 * {@link #detail} field is now known as the <i>cause</i>, and may be
 * accessed via the {@link Throwable#getCause()} method, as well as
 * the aforementioned "legacy field."
 *
 * @version 1.18, 01/23/03
 * @author  Ann Wollrath
 * @since   JDK1.1
 * @see     java.rmi.server.UnicastRemoteObject#clone()
 */
public class ServerCloneException extends CloneNotSupportedException {

    /**
     * Nested exception for ServerCloneException.
     *
     * <p>This field predates the general-purpose exception chaining facility.
     * The {@link Throwable#getCause()} method is now the preferred means of
     * obtaining this information.
     *
     * @serial
     * @since JDK1.1
     */
    public Exception detail;

    /* indicate compatibility with JDK 1.1.x version of class */
    private static final long serialVersionUID = 6617456357664815945L;

    /**
     * Constructs an <code>ServerCloneException</code> with the specified
     * detail message.
     *
     * @param s the detail message.
     * @since JDK1.1
     */
    public ServerCloneException(String s) {
	super(s);
        initCause(null);  // Disallow subsequent initCause
    }

    /**
     * Constructs an <code>ServerCloneException</code> with the specified
     * detail message and nested exception.
     *
     * @param s the detail message.
     * @param ex the nested exception
     * @since JDK1.1
     */
    public ServerCloneException(String s, Exception ex) {
	super(s);
        initCause(null);  // Disallow subsequent initCause
	detail = ex;
    }

    /**
     * Returns the detail message, including the message from the nested
     * exception if there is one.
     *
     * @return	the detail message, including nested exception message if any
     */
    public String getMessage() {
	if (detail == null)
	    return super.getMessage();
	else
	    return super.getMessage() +
		"; nested exception is: \n\t" +
		detail.toString();
    }

    /**
     * Returns the nested exception (the <i>cause</i>).
     *
     * @return  the nested exception, which may be <tt>null</tt>.
     * @since   1.4
     */
    public Throwable getCause() {
        return detail;
    }
}
