/*
 * @(#)SignatureException.java	1.6 98/07/01
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
 * This is the generic Signature exception. 
 * 
 * @version 1.6 07/01/98
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

