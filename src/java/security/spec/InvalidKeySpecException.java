/*
 * @(#)InvalidKeySpecException.java	1.16 03/12/19
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.security.spec;

import java.security.GeneralSecurityException;

/**
 * This is the exception for invalid key specifications.
 *
 * @author Jan Luehe
 *
 * @version 1.16, 12/19/03
 *
 * @see KeySpec
 *
 * @since 1.2
 */

public class InvalidKeySpecException extends GeneralSecurityException {

    private static final long serialVersionUID = 3546139293998810778L;

    /**
     * Constructs an InvalidKeySpecException with no detail message. A
     * detail message is a String that describes this particular
     * exception.
     */
    public InvalidKeySpecException() {
	super();
    }

    /**
     * Constructs an InvalidKeySpecException with the specified detail
     * message. A detail message is a String that describes this
     * particular exception.  
     *
     * @param msg the detail message.  
     */
    public InvalidKeySpecException(String msg) {
	super(msg);
    }

    /**
     * Creates a <code>InvalidKeySpecException</code> with the specified
     * detail message and cause.
     *
     * @param message the detail message (which is saved for later retrieval
     *        by the {@link #getMessage()} method).
     * @param cause the cause (which is saved for later retrieval by the
     *        {@link #getCause()} method).  (A <tt>null</tt> value is permitted,
     *        and indicates that the cause is nonexistent or unknown.)
     * @since 1.5
     */
    public InvalidKeySpecException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Creates a <code>InvalidKeySpecException</code> with the specified cause
     * and a detail message of <tt>(cause==null ? null : cause.toString())</tt>
     * (which typically contains the class and detail message of
     * <tt>cause</tt>).
     *
     * @param cause the cause (which is saved for later retrieval by the
     *        {@link #getCause()} method).  (A <tt>null</tt> value is permitted,
     *        and indicates that the cause is nonexistent or unknown.)
     * @since 1.5
     */
    public InvalidKeySpecException(Throwable cause) {
        super(cause);
    }
}
