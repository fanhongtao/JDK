/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
 
package java.security.cert;

import java.math.BigInteger;
import java.security.Principal;
import java.security.PublicKey;
import java.util.Date;

/**
 * <p>
 * Abstract class for X.509 certificates. This provides a standard
 * way to access all the attributes of an X.509 certificate.
 * <p>
 * In June of 1996, the basic X.509 v3 format was completed by
 * ISO/IEC and ANSI X9, which is described below in ASN.1:
 * <pre>
 * Certificate  ::=  SEQUENCE  {
 *     tbsCertificate       TBSCertificate,
 *     signatureAlgorithm   AlgorithmIdentifier,
 *     signature            BIT STRING  }
 * </pre>
 * <p>
 * These certificates are widely used to support authentication and
 * other functionality in Internet security systems. Common applications
 * include Privacy Enhanced Mail (PEM), Transport Layer Security (SSL),
 * code signing for trusted software distribution, and Secure Electronic
 * Transactions (SET).
 * <p>
 * These certificates are managed and vouched for by <em>Certificate
 * Authorities</em> (CAs). CAs are services which create certificates by
 * placing data in the X.509 standard format and then digitally signing
 * that data. CAs act as trusted third parties, making introductions
 * between principals who have no direct knowledge of each other.
 * CA certificates are either signed by themselves, or by some other
 * CA such as a "root" CA.
 * <p>
 * More information can be found in RFC 2459,
 * "Internet X.509 Public Key Infrastructure Certificate and CRL
 * Profile" at <A HREF="http://www.ietf.org/rfc/rfc2459.txt"> 
 * http://www.ietf.org/rfc/rfc2459.txt </A>.
 * <p>
 * The ASN.1 definition of <code>tbsCertificate</code> is:
 * <pre>
 * TBSCertificate  ::=  SEQUENCE  {
 *     version         [0]  EXPLICIT Version DEFAULT v1,
 *     serialNumber         CertificateSerialNumber,
 *     signature            AlgorithmIdentifier,
 *     issuer               Name,
 *     validity             Validity,
 *     subject              Name,
 *     subjectPublicKeyInfo SubjectPublicKeyInfo,
 *     issuerUniqueID  [1]  IMPLICIT UniqueIdentifier OPTIONAL,
 *                          -- If present, version must be v2 or v3
 *     subjectUniqueID [2]  IMPLICIT UniqueIdentifier OPTIONAL,
 *                          -- If present, version must be v2 or v3
 *     extensions      [3]  EXPLICIT Extensions OPTIONAL
 *                          -- If present, version must be v3
 *     }
 * </pre>
 * <p>
 * Certificates are instantiated using a certificate factory. The following is
 * an example of how to instantiate an X.509 certificate:
 * <pre> 
 * InputStream inStream = new FileInputStream("fileName-of-cert");
 * CertificateFactory cf = CertificateFactory.getInstance("X.509");
 * X509Certificate cert = (X509Certificate)cf.generateCertificate(inStream);
 * inStream.close();
 * </pre>
 *
 * @author Hemma Prafullchandra
 *
 * @version 1.28
 *
 * @see Certificate
 * @see CertificateFactory
 * @see X509Extension
 */

public abstract class X509Certificate extends Certificate
implements X509Extension {

    /**
     * Constructor for X.509 certificates.
     */
    protected X509Certificate() {
	super("X.509");
    }

    /**
     * Checks that the certificate is currently valid. It is if
     * the current date and time are within the validity period given in the
     * certificate.
     * <p>
     * The validity period consists of two date/time values: 
     * the first and last dates (and times) on which the certificate 
     * is valid. It is defined in
     * ASN.1 as:
     * <pre>
     * validity             Validity<p>
     * Validity ::= SEQUENCE {
     *     notBefore      CertificateValidityDate,
     *     notAfter       CertificateValidityDate }<p>
     * CertificateValidityDate ::= CHOICE {
     *     utcTime        UTCTime,
     *     generalTime    GeneralizedTime }
     * </pre>
     * 
     * @exception CertificateExpiredException if the certificate has expired.
     * @exception CertificateNotYetValidException if the certificate is not
     * yet valid.
     */
    public abstract void checkValidity()
        throws CertificateExpiredException, CertificateNotYetValidException;

    /**
     * Checks that the given date is within the certificate's
     * validity period. In other words, this determines whether the 
     * certificate would be valid at the given date/time.
     *
     * @param date the Date to check against to see if this certificate
     *        is valid at that date/time.
     *
     * @exception CertificateExpiredException if the certificate has expired
     * with respect to the <code>date</code> supplied.
     * @exception CertificateNotYetValidException if the certificate is not
     * yet valid with respect to the <code>date</code> supplied.
     * 
     * @see #checkValidity()
     */
    public abstract void checkValidity(Date date)
        throws CertificateExpiredException, CertificateNotYetValidException;

    /**
     * Gets the <code>version</code> (version number) value from the
     * certificate.
     * The ASN.1 definition for this is:
     * <pre>
     * version  [0] EXPLICIT Version DEFAULT v1<p>
     * Version ::=  INTEGER  {  v1(0), v2(1), v3(2)  }
     * </pre>
     * @return the version number, i.e. 1, 2 or 3.
     */
    public abstract int getVersion();

    /**
     * Gets the <code>serialNumber</code> value from the certificate.
     * The serial number is an integer assigned by the certification
     * authority to each certificate. It must be unique for each
     * certificate issued by a given CA (i.e., the issuer name and
     * serial number identify a unique certificate).
     * The ASN.1 definition for this is:
     * <pre>
     * serialNumber     CertificateSerialNumber<p>
     * 
     * CertificateSerialNumber  ::=  INTEGER
     * </pre>
     *
     * @return the serial number.
     */
    public abstract BigInteger getSerialNumber();

    /**
     * Gets the <code>issuer</code> (issuer distinguished name) value from 
     * the certificate. The issuer name identifies the entity that signed (and
     * issued) the certificate. 
     * 
     * <p>The issuer name field contains an
     * X.500 distinguished name (DN).
     * The ASN.1 definition for this is:
     * <pre>
     * issuer    Name<p>
     *
     * Name ::= CHOICE { RDNSequence }
     * RDNSequence ::= SEQUENCE OF RelativeDistinguishedName
     * RelativeDistinguishedName ::=
     *     SET OF AttributeValueAssertion
     *
     * AttributeValueAssertion ::= SEQUENCE {
     *                               AttributeType,
     *                               AttributeValue }
     * AttributeType ::= OBJECT IDENTIFIER
     * AttributeValue ::= ANY
     * </pre>
     * The <code>Name</code> describes a hierarchical name composed of
     * attributes,
     * such as country name, and corresponding values, such as US.
     * The type of the <code>AttributeValue</code> component is determined by
     * the <code>AttributeType</code>; in general it will be a 
     * <code>directoryString</code>. A <code>directoryString</code> is usually 
     * one of <code>PrintableString</code>,
     * <code>TeletexString</code> or <code>UniversalString</code>.
     * 
     * @return a Principal whose name is the issuer distinguished name.
     */
    public abstract Principal getIssuerDN();

    /**
     * Gets the <code>subject</code> (subject distinguished name) value 
     * from the certificate.
     * The ASN.1 definition for this is:
     * <pre>
     * subject    Name
     * </pre>
     * 
     * <p>See {@link #getIssuerDN() getIssuerDN} for <code>Name</code> 
     * and other relevant definitions.
     * 
     * @return a Principal whose name is the subject name.
     */
    public abstract Principal getSubjectDN();

    /**
     * Gets the <code>notBefore</code> date from the validity period of 
     * the certificate.
     * The relevant ASN.1 definitions are:
     * <pre>
     * validity             Validity<p>
     * 
     * Validity ::= SEQUENCE {
     *     notBefore      CertificateValidityDate,
     *     notAfter       CertificateValidityDate }<p>
     * CertificateValidityDate ::= CHOICE {
     *     utcTime        UTCTime,
     *     generalTime    GeneralizedTime }
     * </pre>
     *
     * @return the start date of the validity period.
     * @see #checkValidity
     */
    public abstract Date getNotBefore();

    /**
     * Gets the <code>notAfter</code> date from the validity period of 
     * the certificate. See {@link #getNotBefore() getNotBefore}
     * for relevant ASN.1 definitions.
     *
     * @return the end date of the validity period.
     * @see #checkValidity
     */
    public abstract Date getNotAfter();

    /**
     * Gets the DER-encoded certificate information, the
     * <code>tbsCertificate</code> from this certificate.
     * This can be used to verify the signature independently.
     *
     * @return the DER-encoded certificate information.
     * @exception CertificateEncodingException if an encoding error occurs.
     */
    public abstract byte[] getTBSCertificate()
        throws CertificateEncodingException;

    /**
     * Gets the <code>signature</code> value (the raw signature bits) from 
     * the certificate.
     * The ASN.1 definition for this is:
     * <pre>
     * signature     BIT STRING  
     * </pre>
     *
     * @return the signature.
     */
    public abstract byte[] getSignature();

    /**
     * Gets the signature algorithm name for the certificate
     * signature algorithm. An example is the string "SHA-1/DSA".
     * The ASN.1 definition for this is:
     * <pre>
     * signatureAlgorithm   AlgorithmIdentifier<p>
     * AlgorithmIdentifier  ::=  SEQUENCE  {
     *     algorithm               OBJECT IDENTIFIER,
     *     parameters              ANY DEFINED BY algorithm OPTIONAL  }
     *                             -- contains a value of the type
     *                             -- registered for use with the
     *                             -- algorithm object identifier value
     * </pre>
     * 
     * <p>The algorithm name is determined from the <code>algorithm</code>
     * OID string.
     *
     * @return the signature algorithm name.
     */
    public abstract String getSigAlgName();

    /**
     * Gets the signature algorithm OID string from the certificate.
     * An OID is represented by a set of positive whole numbers separated
     * by periods.
     * For example, the string "1.2.840.10040.4.3" identifies the SHA-1
     * with DSA signature algorithm, as per RFC 2459.
     * 
     * <p>See {@link #getSigAlgName() getSigAlgName} for 
     * relevant ASN.1 definitions.
     *
     * @return the signature algorithm OID string.
     */
    public abstract String getSigAlgOID();

    /**
     * Gets the DER-encoded signature algorithm parameters from this
     * certificate's signature algorithm. In most cases, the signature
     * algorithm parameters are null; the parameters are usually
     * supplied with the certificate's public key.
     * If access to individual parameter values is needed then use
     * {@link java.security.AlgorithmParameters AlgorithmParameters}
     * and instantiate with the name returned by
     * {@link #getSigAlgName() getSigAlgName}.
     * 
     * <p>See {@link #getSigAlgName() getSigAlgName} for 
     * relevant ASN.1 definitions.
     *
     * @return the DER-encoded signature algorithm parameters, or
     *         null if no parameters are present.
     */
    public abstract byte[] getSigAlgParams();

    /**
     * Gets the <code>issuerUniqueID</code> value from the certificate.
     * The issuer unique identifier is present in the certificate
     * to handle the possibility of reuse of issuer names over time.
     * RFC 2459 recommends that names not be reused and that
     * conforming certificates not make use of unique identifiers.
     * Applications conforming to that profile should be capable of
     * parsing unique identifiers and making comparisons.
     * 
     * <p>The ASN.1 definition for this is:
     * <pre>
     * issuerUniqueID  [1]  IMPLICIT UniqueIdentifier OPTIONAL<p>
     * UniqueIdentifier  ::=  BIT STRING
     * </pre>
     *
     * @return the issuer unique identifier or null if it is not
     * present in the certificate.
     */
    public abstract boolean[] getIssuerUniqueID();

    /**
     * Gets the <code>subjectUniqueID</code> value from the certificate.
     * 
     * <p>The ASN.1 definition for this is:
     * <pre>
     * subjectUniqueID  [2]  IMPLICIT UniqueIdentifier OPTIONAL<p>
     * UniqueIdentifier  ::=  BIT STRING
     * </pre>
     *
     * @return the subject unique identifier or null if it is not
     * present in the certificate.
     */
    public abstract boolean[] getSubjectUniqueID();
   
    /**
     * Gets a boolean array representing bits of
     * the <code>KeyUsage</code> extension, (OID = 2.5.29.15).
     * The key usage extension defines the purpose (e.g., encipherment,
     * signature, certificate signing) of the key contained in the
     * certificate.
     * The ASN.1 definition for this is:
     * <pre>
     * KeyUsage ::= BIT STRING {
     *     digitalSignature        (0),
     *     nonRepudiation          (1),
     *     keyEncipherment         (2),
     *     dataEncipherment        (3),
     *     keyAgreement            (4),
     *     keyCertSign             (5),
     *     cRLSign                 (6),
     *     encipherOnly            (7),
     *     decipherOnly            (8) }
     * </pre>
     * RFC 2459 recommends that when used, this be marked
     * as a critical extension.
     *
     * @return the KeyUsage extension of this certificate, represented as
     * an array of booleans. The order of KeyUsage values in the array is
     * the same as in the above ASN.1 definition. The array will contain a
     * value for each KeyUsage defined above. If the KeyUsage list encoded
     * in the certificate is longer than the above list, it will not be
     * truncated. Returns null if this certificate does not
     * contain a KeyUsage extension.
     */
    public abstract boolean[] getKeyUsage();

    /**
     * Gets the certificate constraints path length from the
     * critical <code>BasicConstraints</code> extension, (OID = 2.5.29.19).
     * <p>
     * The basic constraints extension identifies whether the subject
     * of the certificate is a Certificate Authority (CA) and 
     * how deep a certification path may exist through that CA. The 
     * <code>pathLenConstraint</code> field (see below) is meaningful
     * only if <code>cA</code> is set to TRUE. In this case, it gives the
     * maximum number of CA certificates that may follow this certificate in a
     * certification path. A value of zero indicates that only an end-entity
     * certificate may follow in the path.
     * <p>
     * Note that for RFC 2459 this extension is always marked
     * critical if <code>cA</code> is TRUE, meaning this certificate belongs
     * to a Certificate Authority.
     * <p>
     * The ASN.1 definition for this is:
     * <pre>
     * BasicConstraints ::= SEQUENCE {
     *     cA                  BOOLEAN DEFAULT FALSE,
     *     pathLenConstraint   INTEGER (0..MAX) OPTIONAL }
     * </pre>
     *
     * @return the value of <code>pathLenConstraint</code> if the
     * BasicConstraints extension is present in the certificate and the
     * subject of the certificate is a CA, otherwise -1.
     * If the subject of the certificate is a CA and
     * <code>pathLenConstraint</code> does not appear,
     * <code>Integer.MAX_VALUE</code> is returned to indicate that there is no
     * limit to the allowed length of the certification path.
     */
    public abstract int getBasicConstraints();
}
