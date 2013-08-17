/*
 * @(#)Signature.java	1.49 98/07/01
 *
 * Copyright 1995-1998 by Sun Microsystems, Inc.,
 * 901 San Antonio Road, Palo Alto, California, 94303, U.S.A.
 * All rights reserved.
 * 
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
 */
 
 
package java.security;

import java.util.*;
import java.io.*;
/**
 * This Signature class is used to provide the functionality of a
 * digital signature algorithm, such as <tt>RSA with MD5</tt> or
 * <tt>DSA</tt>. Digital signatures are used for authentication and
 * integrity assurance of digital data.
 *
 * </dl> 
 *
 * <p>Like other algorithm-based classes in Java Security, the
 * Signature class has two major components:
 *
 * <dl>
 *
 * <dt><b>Digital Signature API</b> (Application Program Interface)
 *
 * <dd>This is the interface of methods called by applications needing
 * digital signature services. The API consists of all public methods.
 *
 * <dt><b>Digital Signature SPI</b> (Service Provider Interface)
 *
 * <dd>This is the interface implemented by providers that supply
 * specific algorithms. It consists of all methods whose names are
 * prefixed by <code>engine</code>. Each such method is called by a
 * correspondingly-named public API method. For example, the
 * <code>engineSign</code> method is called by the
 * <code>sign</code> method.  The SPI methods are abstract;
 * providers must supply a concrete implementation.
 *
 * </dl>
 *
 * <p>Also like other algorithm-based classes in Java Security, Signature 
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
 * there is a preferred one.
 * 
 * <li>If both an algorithm name and a package provider are specified,
 * the system will determine if there is an implementation of the
 * algorithm in the package requested, and throw an exception if there
 * is not.
 *
 * </ul>

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
 *     verification (see <a href = "#initVerify">initVerify</a>), or
 *
 *     <li>a private key, which initializes the signature for
 *     signing (see <a href = "#initSign">initSign</a>).
 *
 *     </ul><p>
 *
 * <li>Updating<p>
 *
 * <p>Depending on the type of initialization, this will update the
 * bytes to be signed or verified. See the <a href =
 * "#update(byte)">update</a> methods.<p>
 *
 * <li>Signing or Verifying 
 *
 * <p>a signature on all updated bytes. See <a
 * href = "#sign">sign</a> and <a href = "#verify">verify</a>.
 *
 * </ol>
 *
 * @version 1.44 97/02/03
 * @author Benjamin Renaud 
 */
public abstract class Signature {

    /*  Are we in debugging mode? */
    private static boolean debug = false;

    /* The algorithm for this signature object. */
    private String algorithm;

    /** 
     * Possible <a href = "#state ">state </a> value, signifying that       
     * this signature object has not yet been initialized.
     */      
    protected final static int UNINITIALIZED = 0;       
       
    /** 
     * Possible <a href = "#state ">state </a> value, signifying that       
     * this signature object has been initialized for signing.
     */      
    protected final static int SIGN = 2;
       
    /** 
     * Possible <a href = "#state ">state </a> value, signifying that       
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
     * "../guide/security/CryptoSpec.html#AppA">
     * Java Cryptography Architecture API Specification &amp; Reference </a> 
     * for information about standard algorithm names.
     */
    protected Signature(String algorithm) {
	this.algorithm = algorithm;
    }

    /**
     * Generates a Signature object that implements the specified 
     * algorithm. If the default provider package contains a Signature
     * subclass implementing the algorithm, an instance of that subclass
     * is returned. If the algorithm is not available in the default 
     * package, other packages are searched.
     *
     * @param algorithm the standard name of the algorithm requested. 
     * See Appendix A in the <a href=
     * "../guide/security/CryptoSpec.html#AppA">
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
	    return (Signature)Security.getImpl(algorithm, "Signature", null);
	} catch(NoSuchProviderException e) {
	    throw new InternalError("please send a bug report via " +
				    System.getProperty("java.vendor.url.bug"));
	}
    }

    /** 
     * Generates a Signature object implementing the specified
     * algorithm, as supplied from the specified provider, if such an 
     * algorithm is available from the provider.
     *
     * @param algorithm the name of the algorithm requested.
     * See Appendix A in the <a href=
     * "../guide/security/CryptoSpec.html#AppA">
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
     * @see Provider 
     */
    public static Signature getInstance(String algorithm, String provider) 
    throws NoSuchAlgorithmException, NoSuchProviderException {

	return (Signature)Security.getImpl(algorithm, "Signature", provider);
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
     * Returns the signature bytes of all the data updated.  The 
     * signature returned is X.509-encoded. 
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
				     "signing.");
    }

    /**
     * Verifies the passed-in signature. The signature bytes are expected 
     * to be X.509-encoded. 
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
    public final boolean verify(byte[] signature) 
	    throws SignatureException {
	if (state == VERIFY) {
	    return engineVerify(signature);
	}
	throw new SignatureException("object not initialized for " +
				     "verification.");
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
	    throw new SignatureException("object not initialized for signature " +
					 "or verification.");
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
	    throw new SignatureException("object not initialized for signature " +
					 "or verification.");
	}
    }

    /** 
     * Returns the name of the algorithm for this signature object.
     * 
     * @return the name of the algorithm for this signature object.
     */
    public final String getAlgorithm() {
	return algorithm;
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
     */
    public final void setParameter(String param, Object value) 
	throws InvalidParameterException {
	engineSetParameter(param, value);
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
     */
    public final Object getParameter(String param) 
	throws InvalidParameterException {
	    return engineGetParameter(param);
    }

    /**
     * <b>SPI</b>: Initializes this signature object with the specified
     * public key for verification operations.
     *
     * @param publicKey the public key of the identity whose signature is
     * going to be verified.
     * 
     * @exception InvalidKeyException if the key is improperly
     * encoded, parameters are missing, and so on.  
     */
    protected abstract void engineInitVerify(PublicKey publicKey)
    throws InvalidKeyException;

    /**
     * <b>SPI</b>: Initializes this signature object with the specified
     * private key for signing operations.
     *
     * @param privateKey the private key of the identity whose signature
     * will be generated.
     *
     * @exception InvalidKeyException if the key is improperly
     * encoded, parameters are missing, and so on. 
     */
    protected abstract void engineInitSign(PrivateKey privateKey)
    throws InvalidKeyException;

   /**
     * <b>SPI</b>: Updates the data to be signed or verified
     * using the specified byte.
     *
     * @param b the byte to use for the update.
     *
     * @exception SignatureException if the engine is not initialized
     * properly.
     */
    protected abstract void engineUpdate(byte b) throws SignatureException;

    /**
     * <b>SPI</b>: Updates the data to be signed or verified, using the 
     * specified array of bytes, starting at the specified offset.
     *
     * @param data the array of bytes.  
     * @param off the offset to start from in the array of bytes.  
     * @param len the number of bytes to use, starting at offset.
     *
     * @exception SignatureException if the engine is not initialized 
     * properly.
     */
    protected abstract void engineUpdate(byte[] b, int off, int len) 
        throws SignatureException;

    /** 
     * <b>SPI</b>: Returns the signature bytes of all the data
     * updated so far. The signature returned is X.509-encoded.    
     * For more information about the X.509 encoding, see    
     * <a href = "../guide/security/cert2.html">X.509 certificates</a>.   
     *
     * @return the signature bytes of the signing operation's result.
     *
     * @exception SignatureException if the engine is not
     * initialized properly.  
     */
    protected abstract byte[] engineSign() throws SignatureException;

    /** 
     * <b>SPI</b>: Verifies the passed-in signature. The signature bytes 
     * are expected to be X.509-encoded. For more information about the 
     * X.509 encoding, see <a href = "../guide/security/cert2.html">X.509 
     * certificates</a>.   
     * 
     * @param sigBytes the signature bytes to be verified.
     *
     * @return true if the signature was verified, false if not. 
     *
     * @exception SignatureException if the engine is not initialized 
     * properly, or the passed-in signature is improperly encoded or 
     * of the wrong type, etc.  
     */
    protected abstract boolean engineVerify(byte[] sigBytes) 
	throws SignatureException;

    /**
     * <b>SPI</b>: Sets the specified algorithm parameter to the specified
     * value. This method supplies a general-purpose mechanism through
     * which it is possible to set the various parameters of this object. 
     * A parameter may be any settable parameter for the algorithm, such as 
     * a parameter size, or a source of random bits for signature generation 
     * (if appropriate), or an indication of whether or not to perform
     * a specific but optional computation. A uniform algorithm-specific 
     * naming scheme for each parameter is desirable but left unspecified 
     * at this time.
     *
     * @param param the string identifier of the parameter.
     *
     * @param value the parameter value.
     *
     * @exception InvalidParameterException if <code>param</code> is an
     * invalid parameter for this signature algorithm engine,
     * the parameter is already set
     * and cannot be set again, a security exception occurs, and so on. 
     */
    protected abstract void engineSetParameter(String param, Object value) 
	throws InvalidParameterException;

    /**
     * <b>SPI</b>: Gets the value of the specified algorithm parameter. 
     * This method supplies a general-purpose mechanism through which it 
     * is possible to get the various parameters of this object. A parameter
     * may be any settable parameter for the algorithm, such as a parameter 
     * size, or  a source of random bits for signature generation (if 
     * appropriate), or an indication of whether or not to perform a 
     * specific but optional computation. A uniform algorithm-specific 
     * naming scheme for each parameter is desirable but left unspecified 
     * at this time.
     *
     * @param param the string name of the parameter.
     *
     * @return the object that represents the parameter value, or null if
     * there is none.
     *
     * @exception InvalidParameterException if <code>param</code> is an 
     * invalid parameter for this engine, or another exception occurs while
     * trying to get this parameter.
     */
    protected abstract Object engineGetParameter(String param)
	throws InvalidParameterException;

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

}
    
	    



	    
	    
	
