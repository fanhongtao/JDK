/*
 * @(#)KeyStoreException.java	1.3 98/09/21
 *
 * Copyright 1997, 1998 by Sun Microsystems, Inc.,
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
