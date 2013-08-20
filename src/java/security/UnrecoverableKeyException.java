/*
 * @(#)UnrecoverableKeyException.java	1.10 03/12/19
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.security;

/**
 * This exception is thrown if a key in the keystore cannot be recovered.
 *
 * @version 1.10, 12/19/03
 *
 * @since 1.2
 */

public class UnrecoverableKeyException extends GeneralSecurityException {

    private static final long serialVersionUID = 7275063078190151277L;

    /**
     * Constructs an UnrecoverableKeyException with no detail message.
     */
    public UnrecoverableKeyException() {
	super();
    }

    /**
     * Constructs an UnrecoverableKeyException with the specified detail
     * message, which provides more information about why this exception
     * has been thrown.
     *
     * @param msg the detail message.
     */
   public UnrecoverableKeyException(String msg) {
       super(msg);
    }
}
