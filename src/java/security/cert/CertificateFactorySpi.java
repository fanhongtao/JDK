/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.security.cert;

import java.io.InputStream;
import java.util.Collection;
import java.security.Provider;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

/**
 * This class defines the <i>Service Provider Interface</i> (<b>SPI</b>)
 * for the <code>CertificateFactory</code> class.
 * All the abstract methods in this class must be implemented by each
 * cryptographic service provider who wishes to supply the implementation
 * of a certificate factory for a particular certificate type, e.g., X.509.
 *
 * <p>Certificate factories are used to generate certificate and certificate
 * revocation list (CRL) objects from their encoding.
 *
 * <p>A certificate factory for X.509 must return certificates that are an
 * instance of <code>java.security.cert.X509Certificate</code>, and CRLs
 * that are an instance of <code>java.security.cert.X509CRL</code>.
 *
 * @author Hemma Prafullchandra
 * @author Jan Luehe
 *
 * @version 1.10, 02/06/02
 *
 * @see CertificateFactory
 * @see Certificate
 * @see X509Certificate
 * @see CRL
 * @see X509CRL
 *
 * @since 1.2
 */

public abstract class CertificateFactorySpi {

    /**
     * Generates a certificate object and initializes it with
     * the data read from the input stream <code>inStream</code>.
     *
     * <p>The given input stream <code>inStream</code> must contain a single
     * certificate.
     *
     * <p>In order to take advantage of the specialized certificate format
     * supported by this certificate factory,
     * the returned certificate object can be typecast to the corresponding
     * certificate class. For example, if this certificate
     * factory implements X.509 certificates, the returned certificate object
     * can be typecast to the <code>X509Certificate</code> class.
     *
     * <p>In the case of a certificate factory for X.509 certificates, the
     * certificate provided in <code>inStream</code> must be DER-encoded and
     * may be supplied in binary or printable (Base64) encoding. If the
     * certificate is provided in Base64 encoding, it must be bounded at
     * the beginning by -----BEGIN CERTIFICATE-----, and must be bounded at
     * the end by -----END CERTIFICATE-----.
     *
     * <p>Note that if the given input stream does not support
     * {@link java.io.InputStream#mark(int) mark} and
     * {@link java.io.InputStream#reset() reset}, this method will
     * consume the entire input stream.
     *
     * @param inStream an input stream with the certificate data.
     *
     * @return a certificate object initialized with the data
     * from the input stream.
     *
     * @exception CertificateException on parsing errors.
     */
    public abstract Certificate engineGenerateCertificate(InputStream inStream)
        throws CertificateException;

    /**
     * Returns a (possibly empty) collection view of the certificates read
     * from the given input stream <code>inStream</code>.
     *
     * <p>In order to take advantage of the specialized certificate format
     * supported by this certificate factory, each element in
     * the returned collection view can be typecast to the corresponding
     * certificate class. For example, if this certificate
     * factory implements X.509 certificates, the elements in the returned
     * collection can be typecast to the <code>X509Certificate</code> class.
     *
     * <p>In the case of a certificate factory for X.509 certificates,
     * <code>inStream</code> may contain a single DER-encoded certificate
     * in the formats described for 
     * {@link CertificateFactory#generateCertificate(java.io.InputStream) 
     * generateCertificate}.
     * In addition, <code>inStream</code> may contain a PKCS#7 certificate
     * chain. This is a PKCS#7 <i>SignedData</i> object, with the only
     * significant field being <i>certificates</i>. In particular, the
     * signature and the contents are ignored. This format allows multiple
     * certificates to be downloaded at once. If no certificates are present,
     * an empty collection is returned.
     *
     * <p>Note that if the given input stream does not support
     * {@link java.io.InputStream#mark(int) mark} and
     * {@link java.io.InputStream#reset() reset}, this method will
     * consume the entire input stream.
     *
     * @param inStream the input stream with the certificates.
     *
     * @return a (possibly empty) collection view of
     * java.security.cert.Certificate objects
     * initialized with the data from the input stream.
     *
     * @exception CertificateException on parsing errors.
     */
    public abstract Collection engineGenerateCertificates(InputStream inStream)
        throws CertificateException;

    /**
     * Generates a certificate revocation list (CRL) object and initializes it
     * with the data read from the input stream <code>inStream</code>.
     *
     * <p>In order to take advantage of the specialized CRL format
     * supported by this certificate factory,
     * the returned CRL object can be typecast to the corresponding
     * CRL class. For example, if this certificate
     * factory implements X.509 CRLs, the returned CRL object
     * can be typecast to the <code>X509CRL</code> class.
     *
     * <p>Note that if the given input stream does not support
     * {@link java.io.InputStream#mark(int) mark} and
     * {@link java.io.InputStream#reset() reset}, this method will
     * consume the entire input stream.
     *
     * @param inStream an input stream with the CRL data.
     *
     * @return a CRL object initialized with the data
     * from the input stream.
     *
     * @exception CRLException on parsing errors.
     */
    public abstract CRL engineGenerateCRL(InputStream inStream)
        throws CRLException;

    /**
     * Returns a (possibly empty) collection view of the CRLs read
     * from the given input stream <code>inStream</code>.
     *
     * <p>In order to take advantage of the specialized CRL format
     * supported by this certificate factory, each element in
     * the returned collection view can be typecast to the corresponding
     * CRL class. For example, if this certificate
     * factory implements X.509 CRLs, the elements in the returned
     * collection can be typecast to the <code>X509CRL</code> class.
     *
     * <p>In the case of a certificate factory for X.509 CRLs,
     * <code>inStream</code> may contain a single DER-encoded CRL.
     * In addition, <code>inStream</code> may contain a PKCS#7 CRL
     * set. This is a PKCS#7 <i>SignedData</i> object, with the only
     * significant field being <i>crls</i>. In particular, the
     * signature and the contents are ignored. This format allows multiple
     * CRLs to be downloaded at once. If no CRLs are present,
     * an empty collection is returned.
     *
     * <p>Note that if the given input stream does not support
     * {@link java.io.InputStream#mark(int) mark} and
     * {@link java.io.InputStream#reset() reset}, this method will
     * consume the entire input stream.
     *
     * @param inStream the input stream with the CRLs.
     *
     * @return a (possibly empty) collection view of
     * java.security.cert.CRL objects initialized with the data from the input
     * stream.
     *
     * @exception CRLException on parsing errors.
     */
    public abstract Collection engineGenerateCRLs(InputStream inStream)
        throws CRLException;
}
