/*
 * @(#)CertificateParsingException.java	1.9 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.security.cert;

/**
 * Certificate Parsing Exception. This is thrown whenever an
 * invalid DER-encoded certificate is parsed or unsupported DER features
 * are found in the Certificate.
 *
 * @author Hemma Prafullchandra
 * 1.9
 */
public class CertificateParsingException extends CertificateException {

    /**
     * Constructs a CertificateParsingException with no detail message. A
     * detail message is a String that describes this particular
     * exception.
     */
    public CertificateParsingException() {
        super();
    }

    /**
     * Constructs a CertificateParsingException with the specified detail
     * message. A detail message is a String that describes this
     * particular exception.
     *   
     * @param message the detail message.
     */
    public CertificateParsingException(String message) {
        super(message);
    }
}
