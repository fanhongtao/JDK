/*
 * @(#)NoSuchAlgorithmException.java	1.14 98/07/01
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
 * This exception is thrown when a particular cryptographic algorithm is
 * requested but is not available in the environment.
 * 
 * @version 1.12 96/12/11
 * @author Benjamin Renaud 
 */

public class NoSuchAlgorithmException extends Exception {

    /** 
     * Constructs a NoSuchAlgorithmException with no detail
     * message. A detail message is a String that describes this
     * particular exception.
     */
    public NoSuchAlgorithmException() {
	super();
    }

    /**
     * Constructs a NoSuchAlgorithmException with the specified
     * detail message. A detail message is a String that describes
     * this particular exception, which may, for example, specify which
     * algorithm is not available.  
     *
     * @param msg the detail message.  
     */
    public NoSuchAlgorithmException(String msg) {
	super(msg);
    }
}
