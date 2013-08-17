/*
 * @(#)KeyException.java	1.14 00/02/02
 *
 * Copyright 1996-2000 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */

package java.security;

/**
 * This is the basic key exception.
 *
 * @see Key
 * @see InvalidKeyException
 * @see KeyManagementException
 *
 * @version 1.14 00/02/02
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
