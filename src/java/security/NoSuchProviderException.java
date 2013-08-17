/*
 * @(#)NoSuchProviderException.java	1.12 99/02/09
 *
 * Copyright 1995-1999 by Sun Microsystems, Inc.,
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
 * This exception is thrown when a particular security provider is
 * requested but is not available in the environment.
 *
 * @version 1.12 99/02/09
 * @author Benjamin Renaud 
 */

public class NoSuchProviderException extends Exception {

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
