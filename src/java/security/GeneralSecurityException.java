/*
 * @(#)GeneralSecurityException.java	1.9 00/02/02
 *
 * Copyright 1997-2000 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
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
 * @version 1.9, 00/02/02
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
