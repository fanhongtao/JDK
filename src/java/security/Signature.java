/*
 * @(#)Signature.java	1.91 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
  
package java.security;

import java.security.spec.AlgorithmParameterSpec;
import java.util.*;
import java.io.*;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;

/**
 * This Signature class is used to provide applications the functionality
 * of a digital signature algorithm. Digital signatures are used for
 * authentication and integrity assurance of digital data.
 *
 * <p> The signature algorithm can be, among others, the NIST standard
 * DSA, using DSA and SHA-1. The DSA algorithm using the
 * SHA-1 message digest algorithm can be specified as <tt>SHA1withDSA</tt>.
 * In the case of RSA, there are multiple choices for the message digest
 * algorithm, so the signing algorithm could be specified as, for example,
 * <tt>MD2withRSA</tt>, <tt>MD5withRSA</tt>, or <tt>SHA1withRSA</tt>.
 * The algorithm name must be specified, as there is no default.
 *
 * <p>Like other algorithm-based classes in Java Security, Signature 
 * provides implementation-independent algorithms, whereby a caller 
 * (application code) requests a particular signature algorithm
 * and is handed back a properly initialized Signature object. It is
 * also possible, if desired, to request a particular algorithm from a
 * particular provider. See the <code>getInstance </code> methods.
 *
 * <p>Thus, there are two ways to request a Signature algorithm object: by
 * specifying either just an algorithm name, or both an algorithm name
 * and a package provider. <ul>
 *
 * <li>If just an algorithm name is specified, the system will
 * determine if there is an implementation of the algorithm requested
 * available in the environment, and if there is more than one, if
 * there is a preferred one.<p>
 * 
 * <li>If both an algorithm name and a package provider are specified,
 * the system will determine if there is an implementation of the
 * algorithm in the package requested, and throw an exception if there
 * is not.
 *
 * </ul>
 *
 * <p>A Signature object can be used to generate and verify digital
 * signatures.
 *
 * <p>There are three phases to the use of a Signature object for
 * either signing data or verifying a signature:<ol>
 *
 * <li>Initialization, with either 
 *
 *     <ul>
 *
 *     <li>a public key, which initializes the signature for
 *     verification (see {@link #initVerify(PublicKey) initVerify}), or
 *
 *     <li>a private key (and optionally a Secure Random Number Generator),
 *     which initializes the signature for signing
 *     (see {@link #initSign(PrivateKey)}
 *     and {@link #initSign(PrivateKey, SecureRandom)}).
 *
 *     </ul><p>
 *
 * <li>Updating<p>
 *
 * <p>Depending on the type of initialization, this will update the
 * bytes to be signed or verified. See the 
 * {@link #update(byte) update} methods.<p>
 *
 * <li>Signing or Verifying a signature on all updated bytes. See the 
 * {@link #sign() sign} methods and the {@link #verify(byte[]) verify}
 * method.
 *
 * </ol>
 *
 * <p>Note that this class is abstract and extends from
 * <code>SignatureSpi</code> for historical reasons.
 * Application developers should only take notice of the methods defined in
 * this <code>Signature</code> class; all the methods in
 * the superclass are intended for cryptographic service providers who wish to
 * supply their own implementations of digital signature algorithms.
 *
 * @author Benjamin Renaud 
 *
 * @version 1.91, 01/23/03
 */

public abstract class Signature extends SignatureSpi {

    /*  Are we in debugging mode? */
    private static final boolean debug = false;

    /*
     * The algorithm for this signature object.
     * This value is used to map an OID to the particular algorithm.
     * The mapping is done in AlgorithmObject.algOID(String algorithm)
     */
    private String algorithm;

    // The provider
    private Provider provider;

    /** 
     * Possible {@link #state} value, signifying that       
     * this signature object has not yet been initialized.
     */      
    protected final static int UNINITIALIZED = 0;       
       
    /** 
     * Possible {@link #state} value, signifying that       
     * this signature object has been initialized for signing.
     */      
    protected final static int SIGN = 2;
       
    /** 
     * Possible {@link #state} value, signifying that       
     * this signature object has been initialized for verification.
     */      
    protected final static int VERIFY = 3;

    /** 
     * Current state of this signature object.
     */      
    protected int state = UNINITIALIZED;

    /**
     * Creates a Signature object for the specified algorithm.
     *
     * @param algorithm the standard string name of the algorithm. 
     * See Appendix A in the <a href=
     * "../../../guide/security/CryptoSpec.html#AppA">
     * Java Cryptography Architecture API Specification &amp; Reference </a> 
     * for information about standard algorithm names.
     */
    protected Signature(String algorithm) {
	this.algorithm = algorithm;
    }

    /**
     * Generates a Signature object that implements the specified digest
     * algorithm. If the default provider package
     * provides an implementation of the requested digest algorithm,
     * an instance of Signature containing that implementation is returned.
     * If the algorithm is not available in the default 
     * package, other packages are searched.
     *
     * @param algorithm the standard name of the algorithm requested. 
     * See Appendix A in the <a href=
     * "../../../guide/security/CryptoSpec.html#AppA">
     * Java Cryptography Architecture API Specification &amp; Reference </a> 
     * for information about standard algorithm names.
     *
     * @return the new Signature object.
     *
     * @exception NoSuchAlgorithmException if the algorithm is
     * not available in the environment.
     */
    public static Signature getInstance(String algorithm) 
    throws NoSuchAlgorithmException {
	try {
	    Object[] objs = Security.getImpl(algorithm, "Signature", 
					     (String)null);
	    if (objs[0] instanceof Signature) {
		Signature sig = (Signature)objs[0];
		sig.provider = (Provider)objs[1];
		return sig;
	    } else {
		Signature delegate =
		    new Delegate((SignatureSpi)objs[0], algorithm);
		delegate.provider = (Provider)objs[1];
		return delegate;
	    }
	} catch(NoSuchProviderException e) {
	    throw new NoSuchAlgorithmException(algorithm + " not found");
	}
    }

    /** 
     * Generates a Signature object implementing the specified
     * algorithm, as supplied from the specified provider, if such an 
     * algorithm is available from the provider.
     *
     * @param algorithm the name of the algorithm requested.
     * See Appendix A in the <a href=
     * "../../../guide/security/CryptoSpec.html#AppA">
     * Java Cryptography Architecture API Specification &amp; Reference </a> 
     * for information about standard algorithm names.
     *
     * @param provider the name of the provider.
     *
     * @return the new Signature object.
     *
     * @exception NoSuchAlgorithmException if the algorithm is
     * not available in the package supplied by the requested
     * provider.
     *
     * @exception NoSuchProviderException if the provider is not
     * available in the environment.
     *
     * @exception IllegalArgumentException if the provider name is null
     * or empty.
     * 
     * @see Provider 
     */
    public static Signature getInstance(String algorithm, String provider) 
	throws NoSuchAlgorithmException, NoSuchProviderException
    {
	if (provider == null || provider.length() == 0)
	    throw new IllegalArgumentException("missing provider");
	Object[] objs = Security.getImpl(algorithm, "Signature", provider);
	if (objs[0] instanceof Signature) {
	    Signature sig = (Signature)objs[0];
	    sig.provider = (Provider)objs[1];
	    return sig;
	} else {
	    Signature delegate =
		new Delegate((SignatureSpi)objs[0], algorithm);
	    delegate.provider = (Provider)objs[1];
	    return delegate;
	}
    }

    /** 
     * Generates a Signature object implementing the specified
     * algorithm, as supplied from the specified provider, if such an 
     * algorithm is available from the provider. Note: the 
     * <code>provider</code> doesn't have to be registered. 
     *
     * @param algorithm the name of the algorithm requested.
     * See Appendix A in the <a href=
     * "../../../guide/security/CryptoSpec.html#AppA">
     * Java Cryptography Architecture API Specification &amp; Reference </a> 
     * for information about standard algorithm names.
     *
     * @param provider the provider.
     *
     * @return the new Signature object.
     *
     * @exception NoSuchAlgorithmException if the algorithm is
     * not available in the package supplied by the requested
     * provider.
     *
     * @exception IllegalArgumentException if the <code>provider</code> is
     * null.
     * 
     * @see Provider
     *
     * @since 1.4
     */
    public static Signature getInstance(String algorithm, Provider provider) 
	throws NoSuchAlgorithmException
    {
	if (provider == null)
	    throw new IllegalArgumentException("missing provider");
	Object[] objs = Security.getImpl(algorithm, "Signature", provider);
	if (objs[0] instanceof Signature) {
	    Signature sig = (Signature)objs[0];
	    sig.provider = (Provider)objs[1];
	    return sig;
	} else {
	    Signature delegate =
		new Delegate((SignatureSpi)objs[0], algorithm);
	    delegate.provider = (Provider)objs[1];
	    return delegate;
	}
    }

    /** 
     * Returns the provider of this signature object.
     * 
     * @return the provider of this signature object
     */
    public final Provider getProvider() {
	return this.provider;
    }

    /**
     * Initializes this object for verification. If this method is called
     * again with a different argument, it negates the effect
     * of this call.
     *
     * @param publicKey the public key of the identity whose signature is
     * going to be verified.
     *
     * @exception InvalidKeyException if the key is invalid.
     */
    public final void initVerify(PublicKey publicKey) 
	throws InvalidKeyException {
	    engineInitVerify(publicKey);
	    state = VERIFY;
    }

    /**
      * Initializes this object for verification, using the public key from
      * the given certificate.
      * <p>If the certificate is of type X.509 and has a <i>key usage</i>
     * extension field marked as critical, and the value of the <i>key usage</i>
     * extension field implies that the public key in
     * the certificate and its corresponding private key are not
     * supposed to be used for digital signatures, an <code>InvalidKeyException</code>
     * is thrown.
     *
     * @param certificate the certificate of the identity whose signature is
     * going to be verified.
     *
     * @exception InvalidKeyException  if the public key in the certificate
     * is not encoded properly or does not include required  parameter
     * information or cannot be used for digital signature purposes.
     */
    public final void initVerify(Certificate certificate)
	throws InvalidKeyException {
	    // If the certificate is of type X509Certificate,
	    // we should check whether it has a Key Usage
	    // extension marked as critical.
	    if (certificate instanceof java.security.cert.X509Certificate) {
		// Check whether the cert has a key usage extension
		// marked as a critical extension.
		// The OID for KeyUsage extension is 2.5.29.15.
		X509Certificate cert = (X509Certificate)certificate;
		Set critSet = cert.getCriticalExtensionOIDs();

		if (critSet != null && !critSet.isEmpty()
		    && critSet.contains(new String("2.5.29.15"))) {
		    boolean[] keyUsageInfo = cert.getKeyUsage();
		    // keyUsageInfo[0] is for digitalSignature.
		    if ((keyUsageInfo != null) && (keyUsageInfo[0] == false))
			throw new InvalidKeyException("Wrong key usage");
		}
	    }
		
	    PublicKey publicKey = certificate.getPublicKey();
	    engineInitVerify(publicKey);
	    state = VERIFY;
    }

    /**
     * Initialize this object for signing. If this method is called
     * again with a different argument, it negates the effect
     * of this call.
     *
     * @param privateKey the private key of the identity whose signature
     * is going to be generated.
     * 
     * @exception InvalidKeyException if the key is invalid.  
     */
    public final void initSign(PrivateKey privateKey) 
	throws InvalidKeyException {
	    engineInitSign(privateKey);
	    state = SIGN;
    }

    /**
     * Initialize this object for signing. If this method is called
     * again with a different argument, it negates the effect
     * of this call.
     *
     * @param privateKey the private key of the identity whose signature
     * is going to be generated.
     * 
     * @param random the source of randomness for this signature.
     * 
     * @exception InvalidKeyException if the key is invalid.  
     */
    public final void initSign(PrivateKey privateKey, SecureRandom random) 
	throws InvalidKeyException {
	    engineInitSign(privateKey, random);
	    state = SIGN;
    }

    /**
     * Returns the signature bytes of all the data updated.
     * The format of the signature depends on the underlying 
     * signature scheme.
     * 
     * <p>A call to this method resets this signature object to the state 
     * it was in when previously initialized for signing via a
     * call to <code>initSign(PrivateKey)</code>. That is, the object is 
     * reset and available to generate another signature from the same 
     * signer, if desired, via new calls to <code>update</code> and 
     * <code>sign</code>.     
     *
     * @return the signature bytes of the signing operation's result.
     *
     * @exception SignatureException if this signature object is not
     * initialized properly.
     */
    public final byte[] sign() throws SignatureException {
	if (state == SIGN) {
	    return engineSign();
	}
	throw new SignatureException("object not initialized for " +
				     "signing");
    }

    /**
     * Finishes the signature operation and stores the resulting signature
     * bytes in the provided buffer <code>outbuf</code>, starting at
     * <code>offset</code>. 
     * The format of the signature depends on the underlying 
     * signature scheme.
     * 
     * <p>This signature object is reset to its initial state (the state it
     * was in after a call to one of the <code>initSign</code> methods) and
     * can be reused to generate further signatures with the same private key.
     *
     * @param outbuf buffer for the signature result.
     *
     * @param offset offset into <code>outbuf</code> where the signature is
     * stored.
     *
     * @param len number of bytes within <code>outbuf</code> allotted for the
     * signature.
     *
     * @return the number of bytes placed into <code>outbuf</code>.
     *
     * @exception SignatureException if an error occurs or <code>len</code>
     * is less than the actual signature length.
     *
     * @since 1.2
     */
    public final int sign(byte[] outbuf, int offset, int len)
	throws SignatureException {
	if (outbuf == null) {
	    throw new IllegalArgumentException("No output buffer given");
	}
	if (outbuf.length - offset < len) {
	    throw new IllegalArgumentException
		("Output buffer too small for specified offset and length");
	}
	if (state != SIGN) {
	    throw new SignatureException("object not initialized for " +
					 "signing");
	}
	return engineSign(outbuf, offset, len);
    }

    /**
     * Verifies the passed-in signature. 
     * 
     * <p>A call to this method resets this signature object to the state 
     * it was in when previously initialized for verification via a
     * call to <code>initVerify(PublicKey)</code>. That is, the object is 
     * reset and available to verify another signature from the identity
     * whose public key was specified in the call to <code>initVerify</code>.
     *      
     * @param signature the signature bytes to be verified.
     *
     * @return true if the signature was verified, false if not. 
     *
     * @exception SignatureException if this signature object is not 
     * initialized properly, or the passed-in signature is improperly 
     * encoded or of the wrong type, etc.
     */
    public final boolean verify(byte[] signature) throws SignatureException {
	if (state == VERIFY) {
	    return engineVerify(signature);
	}
	throw new SignatureException("object not initialized for " +
				     "verification");
    }

    /**
     * Verifies the passed-in signature in the specified array
     * of bytes, starting at the specified offset.
     * 
     * <p>A call to this method resets this signature object to the state 
     * it was in when previously initialized for verification via a
     * call to <code>initVerify(PublicKey)</code>. That is, the object is 
     * reset and available to verify another signature from the identity
     * whose public key was specified in the call to <code>initVerify</code>.
     *
     *      
     * @param signature the signature bytes to be verified.
     * @param offset the offset to start from in the array of bytes.
     * @param length the number of bytes to use, starting at offset.
     *
     * @return true if the signature was verified, false if not. 
     *
     * @exception SignatureException if this signature object is not 
     * initialized properly, or the passed-in signature is improperly 
     * encoded or of the wrong type, etc.
     * @exception IllegalArgumentException if the <code>signature</code>
     * byte array is null, or the <code>offset</code> or <code>length</code>
     * is less than 0, or the sum of the <code>offset</code> and 
     * <code>length</code> is greater than the length of the
     * <code>signature</code> byte array.
     */
    public final boolean verify(byte[] signature, int offset, int length)
	throws SignatureException {
	if (state == VERIFY) {
	    if ((signature == null) || (offset < 0) || (length < 0) ||
		(offset + length > signature.length)) {
		throw new IllegalArgumentException("Bad arguments");
	    }

	    return engineVerify(signature, offset, length);
	}
	throw new SignatureException("object not initialized for " +
				     "verification");
    }

    /**
     * Updates the data to be signed or verified by a byte.
     *
     * @param b the byte to use for the update.
     * 
     * @exception SignatureException if this signature object is not 
     * initialized properly.     
     */
    public final void update(byte b) throws SignatureException {
	if (state == VERIFY || state == SIGN) {
	    engineUpdate(b);
	} else {
	    throw new SignatureException("object not initialized for "
					 + "signature or verification");
	}
    }

    /**
     * Updates the data to be signed or verified, using the specified
     * array of bytes.
     *
     * @param data the byte array to use for the update.       
     * 
     * @exception SignatureException if this signature object is not 
     * initialized properly.          
     */
    public final void update(byte[] data) throws SignatureException {
	update(data, 0, data.length);
    }

    /**
     * Updates the data to be signed or verified, using the specified
     * array of bytes, starting at the specified offset.  
     *
     * @param data the array of bytes.  
     * @param off the offset to start from in the array of bytes.  
     * @param len the number of bytes to use, starting at offset.
     *  
     * @exception SignatureException if this signature object is not 
     * initialized properly.          
     */
    public final void update(byte[] data, int off, int len) 
	throws SignatureException {
	    if (state == SIGN || state == VERIFY) {
		engineUpdate(data, off, len);
	    } else {
		throw new SignatureException("object not initialized for "
					     + "signature or verification");
	    }
    }

    /** 
     * Returns the name of the algorithm for this signature object.
     * 
     * @return the name of the algorithm for this signature object.
     */
    public final String getAlgorithm() {
	return this.algorithm;
    }

    /**
     * Returns a string representation of this signature object,       
     * providing information that includes the state of the object       
     * and the name of the algorithm used.       
     * 
     * @return a string representation of this signature object.
     */
    public String toString() {
	String initState = "";
	switch (state) {
	case UNINITIALIZED:
	    initState = "<not initialized>";
	    break;
	  case VERIFY:
	    initState = "<initialized for verifying>";
	    break;	      
	  case SIGN:
	    initState = "<initialized for signing>";
	    break;	      
	}
	return "Signature object: " + getAlgorithm() + initState;
    }

    /**
     * Sets the specified algorithm parameter to the specified value.
     * This method supplies a general-purpose mechanism through
     * which it is possible to set the various parameters of this object. 
     * A parameter may be any settable parameter for the algorithm, such as 
     * a parameter size, or a source of random bits for signature generation 
     * (if appropriate), or an indication of whether or not to perform
     * a specific but optional computation. A uniform algorithm-specific 
     * naming scheme for each parameter is desirable but left unspecified 
     * at this time.
     *
     * @param param the string identifier of the parameter.
     * @param value the parameter value.
     *
     * @exception InvalidParameterException if <code>param</code> is an
     * invalid parameter for this signature algorithm engine,
     * the parameter is already set
     * and cannot be set again, a security exception occurs, and so on.
     *
     * @see #getParameter
     *
     * @deprecated Use 
     * {@link #setParameter(java.security.spec.AlgorithmParameterSpec)
     * setParameter}.
     */
    public final void setParameter(String param, Object value) 
	throws InvalidParameterException {
	    engineSetParameter(param, value);
    }

    /**
     * Initializes this signature engine with the specified parameter set.
     *
     * @param params the parameters
     *
     * @exception InvalidAlgorithmParameterException if the given parameters
     * are inappropriate for this signature engine
     *
     * @see #getParameters
     */
    public final void setParameter(AlgorithmParameterSpec params)
	throws InvalidAlgorithmParameterException {
	    engineSetParameter(params);
    }

    /**
     * Returns the parameters used with this signature object.
     *
     * <p>The returned parameters may be the same that were used to initialize
     * this signature, or may contain a combination of default and randomly
     * generated parameter values used by the underlying signature 
     * implementation if this signature requires algorithm parameters but 
     * was not initialized with any.
     *
     * @return the parameters used with this signature, or null if this
     * signature does not use any parameters.
     *
     * @see #setParameter(AlgorithmParameterSpec)
     */
    public final AlgorithmParameters getParameters() {
	return engineGetParameters();
    }    

    /**
     * Gets the value of the specified algorithm parameter. This method 
     * supplies a general-purpose mechanism through which it is possible to 
     * get the various parameters of this object. A parameter may be any 
     * settable parameter for the algorithm, such as a parameter size, or 
     * a source of random bits for signature generation (if appropriate), 
     * or an indication of whether or not to perform a specific but optional 
     * computation. A uniform algorithm-specific naming scheme for each 
     * parameter is desirable but left unspecified at this time.
     *
     * @param param the string name of the parameter.
     *
     * @return the object that represents the parameter value, or null if
     * there is none.
     *
     * @exception InvalidParameterException if <code>param</code> is an invalid
     * parameter for this engine, or another exception occurs while
     * trying to get this parameter.
     *
     * @see #setParameter(String, Object)
     *
     * @deprecated
     */
    public final Object getParameter(String param) 
	throws InvalidParameterException {
	    return engineGetParameter(param);
    }

    /**
     * Returns a clone if the implementation is cloneable.
     * 
     * @return a clone if the implementation is cloneable.
     *
     * @exception CloneNotSupportedException if this is called
     * on an implementation that does not support <code>Cloneable</code>.
     */
    public Object clone() throws CloneNotSupportedException {
	if (this instanceof Cloneable) {
	    return super.clone();
	} else {
	    throw new CloneNotSupportedException();
	}
    }

    // private debugging method.
    private static void debug(String statement) {
	if (debug) {
	    System.err.println(statement);
	}
    }

    // private debugging method.
    private static void debug(Exception e) {
	if (debug) {
	    e.printStackTrace();
	}
    }




    /*
     * The following class allows providers to extend from SignatureSpi
     * rather than from Signature. It represents a Signature with an
     * encapsulated, provider-supplied SPI object (of type SignatureSpi).
     * If the provider implementation is an instance of SignatureSpi, the
     * getInstance() methods above return an instance of this class, with
     * the SPI object encapsulated.
     *
     * Note: All SPI methods from the original Signature class have been
     * moved up the hierarchy into a new class (SignatureSpi), which has
     * been interposed in the hierarchy between the API (Signature)
     * and its original parent (Object).
     */

    static class Delegate extends Signature {

	// The provider implementation (delegate)
	private SignatureSpi sigSpi;

	// constructor
	public Delegate(SignatureSpi sigSpi, String algorithm) {
	    super(algorithm);
	    this.sigSpi = sigSpi;
	}

	/*
	 * Returns a clone if the delegate is cloneable.    
	 * 
	 * @return a clone if the delegate is cloneable.
	 *
	 * @exception CloneNotSupportedException if this is called on a
	 * delegate that does not support <code>Cloneable</code>.
	 */
	public Object clone() throws CloneNotSupportedException {
	    if (sigSpi instanceof Cloneable) {
		SignatureSpi sigSpiClone = (SignatureSpi)sigSpi.clone();
		// Because 'algorithm' and 'provider' are private
		// members of our supertype, we must perform a cast to
		// access them.
		Signature that =
		    new Delegate(sigSpiClone, ((Signature)this).algorithm);
		that.provider = ((Signature)this).provider;
		return that;
	    } else {
		throw new CloneNotSupportedException();
	    }
	}

	protected void engineInitVerify(PublicKey publicKey)
	    throws InvalidKeyException {
		sigSpi.engineInitVerify(publicKey);
	}

	protected void engineInitSign(PrivateKey privateKey)
	    throws InvalidKeyException {
		sigSpi.engineInitSign(privateKey);
	}

        protected void engineInitSign(PrivateKey privateKey, SecureRandom sr)
            throws InvalidKeyException {
                sigSpi.engineInitSign(privateKey, sr);
        }

	protected void engineUpdate(byte b) throws SignatureException {
	    sigSpi.engineUpdate(b);
	}

	protected void engineUpdate(byte[] b, int off, int len) 
	    throws SignatureException {
		sigSpi.engineUpdate(b, off, len);
	}
	
	protected byte[] engineSign() throws SignatureException {
	    return sigSpi.engineSign();
	}

	protected int engineSign(byte[] outbuf, int offset, int len)
	    throws SignatureException {
		return sigSpi.engineSign(outbuf, offset, len);
	}

	protected boolean engineVerify(byte[] sigBytes) 
	    throws SignatureException {
		return sigSpi.engineVerify(sigBytes);
	}

	protected boolean engineVerify(byte[] sigBytes, int offset, int length)
	    throws SignatureException {
	    return sigSpi.engineVerify(sigBytes, offset, length);
	}

	protected void engineSetParameter(String param, Object value) 
	    throws InvalidParameterException {
		sigSpi.engineSetParameter(param, value);
	}

	protected void engineSetParameter(AlgorithmParameterSpec params)
	    throws InvalidAlgorithmParameterException {
		sigSpi.engineSetParameter(params);
	}

	protected Object engineGetParameter(String param)
	    throws InvalidParameterException {
		return sigSpi.engineGetParameter(param);
	}

	protected AlgorithmParameters engineGetParameters() {
	    return sigSpi.engineGetParameters();
	}
    }
}
    
	    



	    
	    
	
