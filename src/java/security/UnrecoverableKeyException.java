/*
 * @(#)UnrecoverableKeyException.java	1.6 00/02/02
 *
 * Copyright 1997-2000 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */

package java.security;

/**
 * This exception is thrown if a key in the keystore cannot be recovered.
 *
 * @version 1.6, 02/02/00
 *
 * @since 1.2
 */

public class UnrecoverableKeyException extends GeneralSecurityException {

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
