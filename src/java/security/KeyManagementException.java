/*
 * @(#)KeyManagementException.java	1.11 00/02/02
 *
 * Copyright 1996-2000 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */

package java.security;

/**
 * This is the general key management exception, for all operations
 * dealing with key management. Subclasses 
 * could include:
 *
 * <ul>
 * <li>KeyIDConflict
 * <li>KeyAuthorizationFailureException
 * <li>ExpiredKeyException
 * </ul>
 *
 * @version 1.11 00/02/02
 * @author Benjamin Renaud
 *
 * @see Key
 * @see KeyException
 */

public class KeyManagementException extends KeyException {

    /**
     * Constructs a KeyManagementException with no detail message. A
     * detail message is a String that describes this particular
     * exception.
     */
    public KeyManagementException() {
	super();
    }

     /**
     * Constructs a KeyManagementException with the specified detail
     * message. A detail message is a String that describes this
     * particular exception.  
     *
     * @param msg the detail message.  
     */
   public KeyManagementException(String msg) {
	super(msg);
    }
}
