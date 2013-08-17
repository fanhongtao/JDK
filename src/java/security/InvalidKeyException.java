/*
 * @(#)InvalidKeyException.java	1.2 00/01/12
 *
 * Copyright 1996-2000 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */


package java.security;

/**
 * This is the exception for invalid Keys (invalid encoding, wrong
 * length, uninitialized, etc).
 *
 * @version 1.6 08/01/96
 * @author Benjamin Renaud 
 */

public class InvalidKeyException extends KeyException {

    /**
     * Constructs an InvalidKeyException with no detail message. A
     * detail message is a String that describes this particular
     * exception.
     */
    public InvalidKeyException() {
	super();
    }

    /**
     * Constructs an InvalidKeyException with the specified detail
     * message. A detail message is a String that describes this
     * particular exception.  
     *
     * @param msg the detail message.  
     */
    public InvalidKeyException(String msg) {
	super(msg);
    }
}
