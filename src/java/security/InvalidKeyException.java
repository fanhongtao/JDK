/*
 * @(#)InvalidKeyException.java	1.6 98/07/01
 *
 * Copyright 1995-1998 by Sun Microsystems, Inc.,
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
