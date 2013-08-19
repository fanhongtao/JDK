/*
 * @(#)KeyFactory.java	1.28 02/05/07
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.security;

import java.security.spec.KeySpec;
import java.security.spec.InvalidKeySpecException;

/**
 * Key factories are used to convert <I>keys</I> (opaque
 * cryptographic keys of type <code>Key</code>) into <I>key specifications</I>
 * (transparent representations of the underlying key material), and vice
 * versa.
 *
 * <P> Key factories are bi-directional. That is, they allow you to build an
 * opaque key object from a given key specification (key material), or to
 * retrieve the underlying key material of a key object in a suitable format.
 *
 * <P> Multiple compatible key specifications may exist for the same key.
 * For example, a DSA public key may be specified using
 * <code>DSAPublicKeySpec</code> or
 * <code>X509EncodedKeySpec</code>. A key factory can be used to translate
 * between compatible key specifications.
 *
 * <P> The following is an example of how to use a key factory in order to
 * instantiate a DSA public key from its encoding.
 * Assume Alice has received a digital signature from Bob.
 * Bob also sent her his public key (in encoded format) to verify
 * his signature. Alice then performs the following actions:
 *
 * <pre>
 * X509EncodedKeySpec bobPubKeySpec = new X509EncodedKeySpec(bobEncodedPubKey);
 * KeyFactory keyFactory = KeyFactory.getInstance("DSA");
 * PublicKey bobPubKey = keyFactory.generatePublic(bobPubKeySpec);
 * Signature sig = Signature.getInstance("DSA");
 * sig.initVerify(bobPubKey);
 * sig.update(data);
 * sig.verify(signature);
 * </pre>
 *
 * @author Jan Luehe
 *
 * @version 1.28, 05/07/02
 *
 * @see Key
 * @see PublicKey
 * @see PrivateKey
 * @see java.security.spec.KeySpec
 * @see java.security.spec.DSAPublicKeySpec
 * @see java.security.spec.X509EncodedKeySpec
 *
 * @since 1.2
 */

public class KeyFactory {

    // The algorithm associated with this key factory
    private String algorithm;

    // The provider
    private Provider provider;

    // The provider implementation (delegate)
    private KeyFactorySpi keyFacSpi;

    /**
     * Creates a KeyFactory object.
     *
     * @param keyFacSpi the delegate
     * @param provider the provider
     * @param algorithm the name of the algorithm
     * to associate with this <tt>KeyFactory</tt>
     */
    protected KeyFactory(KeyFactorySpi keyFacSpi, Provider provider,
			 String algorithm) {
	this.keyFacSpi = keyFacSpi;
	this.provider = provider;
	this.algorithm = algorithm;
    }

    /**
     * Generates a KeyFactory object that implements the specified 
     * algorithm. If the default provider package
     * provides an implementation of the requested algorithm,
     * an instance of KeyFactory containing that implementation is returned.
     * If the algorithm is not available in the default 
     * package, other packages are searched.
     *
     * @param algorithm the name of the requested key algorithm. 
     * See Appendix A in the <a href=
     * "../../../guide/security/CryptoSpec.html#AppA">
     * Java Cryptography Architecture API Specification &amp; Reference </a> 
     * for information about standard algorithm names.
     *
     * @return a KeyFactory object for the specified algorithm.
     *
     * @exception NoSuchAlgorithmException if the requested algorithm is
     * not available in the default provider package or any of the other
     * provider packages that were searched.  
     */
    public static KeyFactory getInstance(String algorithm) 
	throws NoSuchAlgorithmException { 
	    try {
		Object[] objs = Security.getImpl(algorithm, "KeyFactory",
						 (String)null);
		return new KeyFactory((KeyFactorySpi)objs[0],
				      (Provider)objs[1],
				      algorithm);
	    } catch(NoSuchProviderException e) {
		throw new NoSuchAlgorithmException(algorithm + " not found");
	    }
    }

    /**
     * Generates a KeyFactory object for the specified algorithm from the
     * specified provider.
     *
     * @param algorithm the name of the requested key algorithm. 
     * See Appendix A in the <a href=
     * "../../../guide/security/CryptoSpec.html#AppA">
     * Java Cryptography Architecture API Specification &amp; Reference </a> 
     * for information about standard algorithm names.
     *
     * @param provider the name of the provider.
     *
     * @return a KeyFactory object for the specified algorithm.
     *
     * @exception NoSuchAlgorithmException if the algorithm is
     * not available from the specified provider.
     *
     * @exception NoSuchProviderException if the provider has not been 
     * configured.
     *
     * @exception IllegalArgumentException if the provider name is null
     * or empty. 
     * 
     * @see Provider 
     */
    public static KeyFactory getInstance(String algorithm, String provider)
	throws NoSuchAlgorithmException, NoSuchProviderException
    {
	if (provider == null || provider.length() == 0)
	    throw new IllegalArgumentException("missing provider");
	Object[] objs = Security.getImpl(algorithm, "KeyFactory", provider);
	return new KeyFactory((KeyFactorySpi)objs[0], (Provider)objs[1],
			      algorithm);
    }

    /**
     * Generates a KeyFactory object for the specified algorithm from the
     * specified provider. Note: the <code>provider</code> doesn't have 
     * to be registered. 
     *
     * @param algorithm the name of the requested key algorithm. 
     * See Appendix A in the <a href=
     * "../../../guide/security/CryptoSpec.html#AppA">
     * Java Cryptography Architecture API Specification &amp; Reference </a> 
     * for information about standard algorithm names.
     *
     * @param provider the provider.
     *
     * @return a KeyFactory object for the specified algorithm.
     *
     * @exception NoSuchAlgorithmException if the algorithm is
     * not available from the specified provider.
     *
     * @exception IllegalArgumentException if the <code>provider</code> is
     * null.
     * 
     * @see Provider
     *
     * @since 1.4
     */
    public static KeyFactory getInstance(String algorithm, Provider provider)
	throws NoSuchAlgorithmException
    {
	if (provider == null)
	    throw new IllegalArgumentException("missing provider");
	Object[] objs = Security.getImpl(algorithm, "KeyFactory", provider);
	return new KeyFactory((KeyFactorySpi)objs[0], (Provider)objs[1],
			      algorithm);
    }

    /** 
     * Returns the provider of this key factory object.
     * 
     * @return the provider of this key factory object
     */
    public final Provider getProvider() {
	return this.provider;
    }

    /**
     * Gets the name of the algorithm 
     * associated with this <tt>KeyFactory</tt>.
     *
     * @return the name of the algorithm associated with this
     * <tt>KeyFactory</tt>
     */
    public final String getAlgorithm() {
	return this.algorithm;
    }

    /**
     * Generates a public key object from the provided key specification
     * (key material).
     *
     * @param keySpec the specification (key material) of the public key.
     *
     * @return the public key.
     *
     * @exception InvalidKeySpecException if the given key specification
     * is inappropriate for this key factory to produce a public key.
     */
    public final PublicKey generatePublic(KeySpec keySpec)
        throws InvalidKeySpecException {
	    return keyFacSpi.engineGeneratePublic(keySpec);
    }

    /**
     * Generates a private key object from the provided key specification
     * (key material).
     *
     * @param keySpec the specification (key material) of the private key.
     *
     * @return the private key.
     *
     * @exception InvalidKeySpecException if the given key specification
     * is inappropriate for this key factory to produce a private key.
     */
    public final PrivateKey generatePrivate(KeySpec keySpec)
        throws InvalidKeySpecException {
	    return keyFacSpi.engineGeneratePrivate(keySpec);
    }

    /**
     * Returns a specification (key material) of the given key object.
     * <code>keySpec</code> identifies the specification class in which 
     * the key material should be returned. It could, for example, be
     * <code>DSAPublicKeySpec.class</code>, to indicate that the
     * key material should be returned in an instance of the 
     * <code>DSAPublicKeySpec</code> class.
     *
     * @param key the key.
     *
     * @param keySpec the specification class in which 
     * the key material should be returned.
     *
     * @return the underlying key specification (key material) in an instance
     * of the requested specification class.
     *
     * @exception InvalidKeySpecException if the requested key specification is
     * inappropriate for the given key, or the given key cannot be processed
     * (e.g., the given key has an unrecognized algorithm or format).
     */
    public final KeySpec getKeySpec(Key key, Class keySpec)
	throws InvalidKeySpecException {
	    return keyFacSpi.engineGetKeySpec(key, keySpec);
    }

    /**
     * Translates a key object, whose provider may be unknown or potentially
     * untrusted, into a corresponding key object of this key factory.
     *
     * @param key the key whose provider is unknown or untrusted.
     *
     * @return the translated key.
     *
     * @exception InvalidKeyException if the given key cannot be processed
     * by this key factory.
     */
    public final Key translateKey(Key key) throws InvalidKeyException {
	return keyFacSpi.engineTranslateKey(key);
    }
}
