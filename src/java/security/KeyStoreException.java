/*
 * @(#)KeyStoreException.java	1.9 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.security;

/**
 * This is the generic KeyStore exception. 
 * 
 * @author Jan Luehe
 *
 * @version 1.9, 01/23/03
 *
 * @since 1.2
 */

public class KeyStoreException extends GeneralSecurityException {

    /** 
     * Constructs a KeyStoreException with no detail message.  (A
     * detail message is a String that describes this particular
     * exception.)  
     */
    public KeyStoreException() {
	super();
    }

    /** 
     * Constructs a KeyStoreException with the specified detail
     * message.  (A detail message is a String that describes this
     * particular exception.)
     *
     * @param msg the detail message.  
     */
   public KeyStoreException(String msg) {
       super(msg);
    }
}
