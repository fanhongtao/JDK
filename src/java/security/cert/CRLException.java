/*
 * @(#)CRLException.java	1.2 00/01/12
 *
 * Copyright 1997-2000 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */

package java.security.cert;

import java.security.GeneralSecurityException;

/**
 * CRL (Certificate Revocation List) Exception
 *
 * @author Hemma Prafullchandra
 * 1.4
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
