/*
 * @(#)SignatureException.java	1.2 00/01/12
 *
 * Copyright 1996-2000 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */

package java.security;

/**
 * This is the generic Signature exception. 
 * 
 * @version 1.9 09/21/98
 * @author Benjamin Renaud 
 */

public class SignatureException extends GeneralSecurityException {

    /**
     * Constructs a SignatureException with no detail message. A
     * detail message is a String that describes this particular
     * exception.
     */
    public SignatureException() {
	super();
    }

    /**
     * Constructs a SignatureException with the specified detail
     * message.  A detail message is a String that describes this
     * particular exception.
     *
     * @param msg the detail message.  
     */
    public SignatureException(String msg) {
	super(msg);
    }
}

