/*
 * @(#)KeyStoreException.java	1.2 00/01/12
 *
 * Copyright 1997-2000 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */

package java.security;

/**
 * This is the generic KeyStore exception. 
 * 
 * @author Jan Luehe
 *
 * @version 1.3, 09/21/98
 *
 * @since JDK1.2
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
