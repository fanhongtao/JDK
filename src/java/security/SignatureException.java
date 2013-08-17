/*
 * @(#)SignatureException.java	1.7 01/12/10
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.security;

/**
 * This is the generic Signature exception. 
 * 
 * @version 1.7 12/10/01
 * @author Benjamin Renaud 
 */

public class SignatureException extends Exception {

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

