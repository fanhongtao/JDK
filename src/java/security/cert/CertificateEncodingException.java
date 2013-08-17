/*
 * @(#)CertificateEncodingException.java	1.2 00/01/12
 *
 * Copyright 1997-2000 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */

package java.security.cert;

/**
 * Certificate Encoding Exception. This is thrown whenever an error
 * occurs while attempting to encode a certificate.
 *
 * @author Hemma Prafullchandra
 * 1.5
 */
public class CertificateEncodingException extends CertificateException {

    /**
     * Constructs a CertificateEncodingException with no detail message. A
     * detail message is a String that describes this particular
     * exception.
     */
    public CertificateEncodingException() {
        super();
    }

    /**
     * Constructs a CertificateEncodingException with the specified detail
     * message. A detail message is a String that describes this
     * particular exception.
     *   
     * @param message the detail message.
     */
    public CertificateEncodingException(String message) {
        super(message);
    }
}
