/*
 * @(#)NoSuchProviderException.java	1.16 00/02/02
 *
 * Copyright 1996-2000 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */

package java.security;

/**
 * This exception is thrown when a particular security provider is
 * requested but is not available in the environment.
 *
 * @version 1.16 00/02/02
 * @author Benjamin Renaud 
 */

public class NoSuchProviderException extends GeneralSecurityException {

    /**
     * Constructs a NoSuchProviderException with no detail message. A
     * detail message is a String that describes this particular
     * exception.
     */
    public NoSuchProviderException() {
	super();
    }

    /**
     * Constructs a NoSuchProviderException with the specified detail
     * message. A detail message is a String that describes this
     * particular exception.  
     *
     * @param msg the detail message.  
     */
    public NoSuchProviderException(String msg) {
	super(msg);
    }
}
