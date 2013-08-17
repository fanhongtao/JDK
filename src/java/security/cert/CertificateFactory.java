/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.security.cert;

import java.io.InputStream;
import java.util.Collection;
import java.security.Provider;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;

/**
 * This class defines the functionality of a certificate factory, which is
 * used to generate certificate and certificate revocation list (CRL) objects
 * from their encodings.
 *
 * <p>A certificate factory for X.509 must return certificates that are an
 * instance of <code>java.security.cert.X509Certificate</code>, and CRLs
 * that are an instance of <code>java.security.cert.X509CRL</code>.
 *
 * <p>The following example reads a file with Base64 encoded certificates,
 * which are each bounded at the beginning by -----BEGIN CERTIFICATE-----, and
 * bounded at the end by -----END CERTIFICATE-----. We convert the
 * <code>FileInputStream</code> (which does not support <code>mark</code>
 * and <code>reset</code>) to a <code>ByteArrayInputStream</code> (which
 * supports those methods), so that each call to
 * <code>generateCertificate</code> consumes only one certificate, and the
 * read position of the input stream is positioned to the next certificate in
 * the file:<p>
 *
 * <pre>
 * FileInputStream fis = new FileInputStream(filename);
 * DataInputStream dis = new DataInputStream(fis);
 *
 * CertificateFactory cf = CertificateFactory.getInstance("X.509");
 *
 * byte[] bytes = new byte[dis.available()];
 * dis.readFully(bytes);
 * ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
 *
 * while (bais.available() > 0) {
 *    Certificate cert = cf.generateCertificate(bais);
 *    System.out.println(cert.toString());
 * }
 * </pre>
 *
 * <p>The following example parses a PKCS#7-formatted certificate reply stored
 * in a file and extracts all the certificates from it:<p>
 *
 * <pre>
 * FileInputStream fis = new FileInputStream(filename);
 * CertificateFactory cf = CertificateFactory.getInstance("X.509");
 * Collection c = cf.generateCertificates(fis);
 * Iterator i = c.iterator();
 * while (i.hasNext()) {
 *    Certificate cert = (Certificate)i.next();
 *    System.out.println(cert);
 * }
 * </pre>
 *
 * @author Hemma Prafullchandra
 * @author Jan Luehe
 *
 * @version 1.16, 02/06/02
 *
 * @see Certificate
 * @see X509Certificate
 * @see CRL
 * @see X509CRL
 *
 * @since 1.2
 */

public class CertificateFactory {
    // for use with the reflection API
    private static final Class cl = java.security.Security.class;
    private static final Class[] GET_IMPL_PARAMS = { String.class,
						     String.class,
						     String.class };
    private static Method implMethod;

    static {
	implMethod = (Method)
	    AccessController.doPrivileged(new PrivilegedAction() {
	    public Object run() {
		Method m = null;
		try {
		    m = cl.getDeclaredMethod("getImpl", GET_IMPL_PARAMS);
		    if (m != null)
			m.setAccessible(true);
		} catch (NoSuchMethodException nsme) {
		}
		return m;
	    }
	});
    }

    // The certificate type
    private String type;

    // The provider
    private Provider provider;

    // The provider implementation
    private CertificateFactorySpi certFacSpi;

    /**
     * Creates a CertificateFactory object of the given type, and encapsulates
     * the given provider implementation (SPI object) in it.
     *
     * @param certFacSpi the provider implementation.
     * @param provider the provider.
     * @param type the certificate type.
     */
    protected CertificateFactory(CertificateFactorySpi certFacSpi,
				 Provider provider, String type)
    {
	this.certFacSpi = certFacSpi;
	this.provider = provider;
	this.type = type;
    }

    /**
     * Generates a certificate factory object that implements the
     * specified certificate type. If the default provider package
     * provides an implementation of the requested certificate type,
     * an instance of certificate factory containing that
     * implementation is returned.
     * If the type is not available in the default
     * package, other packages are searched.
     *
     * @param type the name of the requested certificate type.
     * See Appendix A in the <a href=
     * "../../../../guide/security/CryptoSpec.html#AppA">
     * Java Cryptography Architecture API Specification &amp; Reference </a>
     * for information about standard certificate types.
     *
     * @return a certificate factory object for the specified type.
     *
     * @exception CertificateException if the requested certificate type is
     * not available in the default provider package or any of the other
     * provider packages that were searched.
     */
    public static final CertificateFactory getInstance(String type)
	throws CertificateException
    {
	try {
	    if (implMethod == null) {
		throw new CertificateException(type + " not found");
	    }

	    // The underlying method is static, so we set the object
	    // argument to null.
	    Object[] objs = (Object[])implMethod.invoke(null,
					       new Object[]
					       { type,
						 "CertificateFactory",
						 null
					       } );
	    return new CertificateFactory((CertificateFactorySpi)objs[0],
					  (Provider)objs[1], type);
	} catch (IllegalAccessException iae) {
	    throw new CertificateException(type + " not found");
	} catch (InvocationTargetException ite) {
	    throw new CertificateException(type + " not found");
	}
    }

    /**
     * Generates a certificate factory object for the specified
     * certificate type from the specified provider.
     *
     * @param type the certificate type
     * @param provider the name of the provider.
     *
     * @return a certificate factory object for the specified type.
     *
     * @exception CertificateException if the certificate type is
     * not available from the specified provider.
     *
     * @exception NoSuchProviderException if the provider has not been
     * configured.
     *
     * @see Provider
     */
    public static final CertificateFactory getInstance(String type,
					  	       String provider)
	throws CertificateException, NoSuchProviderException
    {
	if (provider == null || provider.length() == 0)
	    throw new IllegalArgumentException("missing provider");
	try {
	    if (implMethod == null) {
		throw new CertificateException(type + " not found");
	    }

	    // The underlying method is static, so we set the object
	    // argument to null.
	    Object[] objs = (Object[])implMethod.invoke(null,
					       new Object[]
					       { type,
						 "CertificateFactory",
						 provider
					       } );
	    return new CertificateFactory((CertificateFactorySpi)objs[0],
					  (Provider)objs[1], type);
	} catch (IllegalAccessException iae) {
	    throw new CertificateException(type + " not found");
	} catch (InvocationTargetException ite) {
	    Throwable t = ite.getTargetException();
	    if (t!=null && t instanceof NoSuchProviderException) {
		throw (NoSuchProviderException)t;
	    } else {
		throw new CertificateException(type + " not found");
	    }
	}
    }

    /**
     * Returns the provider of this certificate factory.
     *
     * @return the provider of this certificate factory.
     */
    public final Provider getProvider() {
	return this.provider;
    }

    /**
     * Returns the name of the certificate type associated with this
     * certificate factory.
     *
     * @return the name of the certificate type associated with this
     * certificate factory.
     */
    public final String getType() {
	return this.type;
    }

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
    public final Certificate generateCertificate(InputStream inStream)
        throws CertificateException
    {
	return certFacSpi.engineGenerateCertificate(inStream);
    }

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
     * <code>inStream</code> may contain a sequence of DER-encoded certificates
     * in the formats described for
     * {@link #generateCertificate(java.io.InputStream) generateCertificate}.
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
    public final Collection generateCertificates(InputStream inStream)
        throws CertificateException
    {
	return certFacSpi.engineGenerateCertificates(inStream);
    }

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
    public final CRL generateCRL(InputStream inStream)
        throws CRLException
    {
	return certFacSpi.engineGenerateCRL(inStream);
    }

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
     * <code>inStream</code> may contain a sequence of DER-encoded CRLs.
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
    public final Collection generateCRLs(InputStream inStream)
        throws CRLException
    {
	return certFacSpi.engineGenerateCRLs(inStream);
    }
}
