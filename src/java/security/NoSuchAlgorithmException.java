/*
 * @(#)NoSuchAlgorithmException.java	1.20 00/02/02
 *
 * Copyright 1996-2000 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */

package java.security;

/**
 * This exception is thrown when a particular cryptographic algorithm is
 * requested but is not available in the environment.
 * 
 * @version 1.20, 00/02/02
 * @author Benjamin Renaud 
 */

public class NoSuchAlgorithmException extends GeneralSecurityException {

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
