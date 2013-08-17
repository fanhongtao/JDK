/*
 * @(#)KeyException.java	1.12 98/12/03
 *
 * Copyright 1996-1998 by Sun Microsystems, Inc.,
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
 * This is the basic key exception.
 *
 * @see Key
 * @see InvalidKeyException
 * @see KeyManagementException
 *
 * @version 1.12 98/12/03
 * @author Benjamin Renaud
 */

public class KeyException extends GeneralSecurityException {

    /**
     * Constructs a KeyException with no detail message. A detail
     * message is a String that describes this particular exception.
     */
    public KeyException() {
	super();
    }

    /**
     * Constructs a KeyException with the specified detail message.
     * A detail message is a String that describes this particular
     * exception.
     *
     * @param msg the detail message.  
     */
    public KeyException(String msg) {
	super(msg);
    }
}
