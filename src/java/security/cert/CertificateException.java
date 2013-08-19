/*
 * @(#)CertificateException.java	1.30 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.security.cert;

import java.security.GeneralSecurityException;

/**
 * This exception indicates one of a variety of certificate problems.
 *
 * @author Hemma Prafullchandra
 * @version 1.30
 * @see Certificate
 */
public class CertificateException extends GeneralSecurityException {

    /**
     * Constructs a certificate exception with no detail message. A detail
     * message is a String that describes this particular exception. 
     */
    public CertificateException() {
        super();
    }

    /**
     * Constructs a certificate exception with the given detail
     * message. A detail message is a String that describes this
     * particular exception.
     *
     * @param msg the detail message.  
     */
    public CertificateException(String msg) {
        super(msg);
    }
}
