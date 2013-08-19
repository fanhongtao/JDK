/*
 * @(#)CRLException.java	1.9 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.security.cert;

import java.security.GeneralSecurityException;

/**
 * CRL (Certificate Revocation List) Exception.
 *
 * @author Hemma Prafullchandra
 * 1.9
 */
public class CRLException extends GeneralSecurityException {

   /**
     * Constructs a CRLException with no detail message. A
     * detail message is a String that describes this particular
     * exception.
     */
    public CRLException() {
        super();
    }

    /**
     * Constructs a CRLException with the specified detail
     * message. A detail message is a String that describes this
     * particular exception. 
     *
     * @param message the detail message.
     */
    public CRLException(String message) {
        super(message);
    }
}
