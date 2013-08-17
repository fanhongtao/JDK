/*
 * @(#)GeneralSecurityException.java	1.6 98/09/21
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
 * This is the general security exception class, which serves to group all
 * the exception classes of the <code>java.security</code> package that
 * extend from it.
 * (Exceptions are AccessControlException and CertificateException,
 * which subclass from <code>java.lang.SecurityException</code>, and ProviderException
 * and InvalidParameterException, which subclass from 
 * <code>java.lang.RuntimeException</code>.)
 * 
 * @version 1.6, 00/05/10
 * @author Jan Luehe
 */

public class GeneralSecurityException extends Exception {

    /** 
     * Constructs a GeneralSecurityException with no detail message.  
     */
    public GeneralSecurityException() {
        super();
    }

    /**
     * Constructs a GeneralSecurityException with the specified detail
     * message.
     * A detail message is a String that describes this particular
     * exception.
     *
     * @param msg the detail message.  
     */
    public GeneralSecurityException(String msg) {
        super(msg);
    }
}
