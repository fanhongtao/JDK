/*
 * @(#)ActivationException.java	1.22 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.rmi.activation;

/**
 * General exception used by the activation interfaces.
 *
 * <p>As of release 1.4, this exception has been retrofitted to conform to
 * the general purpose exception-chaining mechanism.  The "detail exception"
 * that may be provided at construction time and accessed via the public
 * {@link #detail} field is now known as the <i>cause</i>, and may be
 * accessed via the {@link Throwable#getCause()} method, as well as
 * the aforementioned "legacy field."
 *
 * @author 	Ann Wollrath
 * @version	1.22, 01/23/03
 * @since 	1.2
 */
public class ActivationException extends Exception {

    /**
     * Nested Exception to hold wrapped remote exceptions.
     *
     * <p>This field predates the general-purpose exception chaining facility.
     * The {@link Throwable#getCause()} method is now the preferred means of
     * obtaining this information.
     *
     * @serial 
     */
    public Throwable detail;

    /** indicate compatibility with the Java 2 SDK v1.2 version of class */
    private static final long serialVersionUID = -4320118837291406071L;

    /**
     * Constructs an <code>ActivationException</code> with no specified
     * detail message.
     * @since 1.2
     */
    public ActivationException() {
        initCause(null);  // Disallow subsequent initCause
    }

    /**
     * Constructs an <code>ActivationException</code> with detail
     * message, <code>s</code>.
     * @param s the detail message
     * @since 1.2
     */
    public ActivationException(String s) {
	super(s);
        initCause(null);  // Disallow subsequent initCause
    }

    /**
     * Constructs an <code>ActivationException</code> with detail message,
     * <code>s</code>, and detail exception <code>ex</code>.
     *
     * @param s detail message
     * @param ex detail exception
     * @since 1.2
     */
    public ActivationException(String s, Throwable ex) {
	super(s);
        initCause(null);  // Disallow subsequent initCause
	detail = ex;
    }

    /**
     * Returns the detail message, including the message from the detail
     * exception if there is one.
     *
     * @return	the detail message, including detail exception message if any
     * @since 1.2
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
     * Returns the detail exception (the <i>cause</i>).
     *
     * @return  the detail exception, which may be <tt>null</tt>.
     * @since   1.4
     */
    public Throwable getCause() {
        return detail;
    }
}
