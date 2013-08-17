/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
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
 * @version 1.12 02/02/06
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
