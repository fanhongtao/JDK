/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.security.cert;

/**
 * Certificate Expired Exception. This is thrown whenever the current
 * <code>Date</code> or the specified <code>Date</code> is after the
 * <code>notAfter</code> date/time specified in the validity period
 * of the certificate.
 *
 * @author Hemma Prafullchandra
 * 1.7
 */
public class CertificateExpiredException extends CertificateException {

    /**
     * Constructs a CertificateExpiredException with no detail message. A
     * detail message is a String that describes this particular
     * exception.
     */
    public CertificateExpiredException() {
        super();
    }

    /**
     * Constructs a CertificateExpiredException with the specified detail
     * message. A detail message is a String that describes this
     * particular exception.
     *   
     * @param message the detail message.
     */
    public CertificateExpiredException(String message) {
        super(message);
    }
}
