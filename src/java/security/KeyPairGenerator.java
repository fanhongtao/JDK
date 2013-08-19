/*
 * @(#)KeyPairGenerator.java	1.52 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
 
package java.security;

import java.security.spec.AlgorithmParameterSpec;

/**
 * The KeyPairGenerator class is used to generate pairs of
 * public and private keys. Key pair generators are constructed using the
 * <code>getInstance</code> factory methods (static methods that
 * return instances of a given class).
 *
 * <p>A Key pair generator for a particular algorithm creates a public/private
 * key pair that can be used with this algorithm. It also associates
 * algorithm-specific parameters with each of the generated keys.
 * 
 * <p>There are two ways to generate a key pair: in an algorithm-independent
 * manner, and in an algorithm-specific manner.
 * The only difference between the two is the initialization of the object:
 * 
 * <ul>
 * <li><b>Algorithm-Independent Initialization</b>
 * <p>All key pair generators share the concepts of a keysize and a
 * source of randomness. The keysize is interpreted differently for different
 * algorithms (e.g., in the case of the <i>DSA</i> algorithm, the keysize 
 * corresponds to the length of the modulus).
 * There is an 
 * {@link #initialize(int, java.security.SecureRandom) initialize}
 * method in this KeyPairGenerator class that takes these two universally
 * shared types of arguments. There is also one that takes just a
 * <code>keysize</code> argument, and uses the <code>SecureRandom</code>
 * implementation of the highest-priority installed provider as the source
 * of randomness. (If none of the installed providers supply an implementation
 * of <code>SecureRandom</code>, a system-provided source of randomness is
 * used.)
 * 
 * <p>Since no other parameters are specified when you call the above
 * algorithm-independent <code>initialize</code> methods, it is up to the
 * provider what to do about the algorithm-specific parameters (if any) to be
 * associated with each of the keys.
 * 
 * <p>If the algorithm is the <i>DSA</i> algorithm, and the keysize (modulus
 * size) is 512, 768, or 1024, then the <i>Sun</i> provider uses a set of
 * precomputed values for the <code>p</code>, <code>q</code>, and
 * <code>g</code> parameters. If the modulus size is not one of the above
 * values, the <i>Sun</i> provider creates a new set of parameters. Other
 * providers might have precomputed parameter sets for more than just the
 * three modulus sizes mentioned above. Still others might not have a list of
 * precomputed parameters at all and instead always create new parameter sets.
 * <p>
 *
 * <li><b>Algorithm-Specific Initialization</b>
 * <p>For situations where a set of algorithm-specific parameters already
 * exists (e.g., so-called <i>community parameters</i> in DSA), there are two
 * {@link #initialize(java.security.spec.AlgorithmParameterSpec)
 * initialize} methods that have an <code>AlgorithmParameterSpec</code>
 * argument. One also has a <code>SecureRandom</code> argument, while the
 * the other uses the <code>SecureRandom</code>
 * implementation of the highest-priority installed provider as the source
 * of randomness. (If none of the installed providers supply an implementation
 * of <code>SecureRandom</code>, a system-provided source of randomness is
 * used.)
 * </ul>
 *
 * <p>In case the client does not explicitly initialize the KeyPairGenerator
 * (via a call to an <code>initialize</code> method), each provider must
 * supply (and document) a default initialization.
 * For example, the <i>Sun</i> provider uses a default modulus size (keysize)
 * of 1024 bits.
 *
 * <p>Note that this class is abstract and extends from
 * <code>KeyPairGeneratorSpi</code> for historical reasons.
 * Application developers should only take notice of the methods defined in
 * this <code>KeyPairGenerator</code> class; all the methods in
 * the superclass are intended for cryptographic service providers who wish to
 * supply their own implementations of key pair generators.
 *
 * @author Benjamin Renaud
 *
 * @version 1.52, 01/23/03
 *
 * @see java.security.spec.AlgorithmParameterSpec
 */

public abstract class KeyPairGenerator extends KeyPairGeneratorSpi {

    private String algorithm;

    // The provider
    private Provider provider;

    /**
     * Creates a KeyPairGenerator object for the specified algorithm.
     *
     * @param algorithm the standard string name of the algorithm. 
     * See Appendix A in the <a href=
     * "../../../guide/security/CryptoSpec.html#AppA">
     * Java Cryptography Architecture API Specification &amp; Reference </a> 
     * for information about standard algorithm names.
     */
    protected KeyPairGenerator(String algorithm) {
	this.algorithm = algorithm;
    }
    
    /**
     * Returns the standard name of the algorithm for this key pair generator.
     * See Appendix A in the <a href=
     * "../../../guide/security/CryptoSpec.html#AppA">
     * Java Cryptography Architecture API Specification &amp; Reference </a> 
     * for information about standard algorithm names.
     * 
     * @return the standard string name of the algorithm. 
     */
    public String getAlgorithm() {
	return this.algorithm;
    }

    /**
     * Generates a KeyPairGenerator object that implements the specified digest
     * algorithm. If the default provider package
     * provides an implementation of the requested digest algorithm,
     * an instance of KeyPairGenerator containing that implementation is
     * returned.
     * If the algorithm is not available in the default 
     * package, other packages are searched.
     *
     * @param algorithm the standard string name of the algorithm. 
     * See Appendix A in the <a href=
     * "../../../guide/security/CryptoSpec.html#AppA">
     * Java Cryptography Architecture API Specification &amp; Reference </a> 
     * for information about standard algorithm names.
     *
     * @return the new KeyPairGenerator object.
     *
     * @exception NoSuchAlgorithmException if the algorithm is
     * not available in the environment.  
     */
    public static KeyPairGenerator getInstance(String algorithm)
    throws NoSuchAlgorithmException {
	try {
	    Object[] objs = Security.getImpl(algorithm, "KeyPairGenerator",
					     (String)null);
	    if (objs[0] instanceof KeyPairGenerator) {
		KeyPairGenerator keyPairGen = (KeyPairGenerator)objs[0];
		keyPairGen.provider = (Provider)objs[1];
		return keyPairGen;
	    } else {
		KeyPairGenerator delegate =
		    new Delegate((KeyPairGeneratorSpi)objs[0], algorithm);
		delegate.provider = (Provider)objs[1];
		return delegate;
	    }
	} catch(NoSuchProviderException e) {
	    throw new NoSuchAlgorithmException(algorithm + " not found");
	}
    }

    /** 
     * Generates a KeyPairGenerator object implementing the specified
     * algorithm, as supplied from the specified provider, 
     * if such an algorithm is available from the provider.
     *
     * @param algorithm the standard string name of the algorithm.
     * See Appendix A in the <a href=
     * "../../../guide/security/CryptoSpec.html#AppA">
     * Java Cryptography Architecture API Specification &amp; Reference </a> 
     * for information about standard algorithm names.
     *
     * @param provider the string name of the provider.
     *
     * @return the new KeyPairGenerator object.
     *
     * @exception NoSuchAlgorithmException if the algorithm is
     * not available from the provider.
     *
     * @exception NoSuchProviderException if the provider is not
     * available in the environment.
     *
     * @exception IllegalArgumentException if the provider name is null
     * or empty.
     * 
     * @see Provider 
     */
    public static KeyPairGenerator getInstance(String algorithm,
					       String provider) 
	throws NoSuchAlgorithmException, NoSuchProviderException
    {
	if (provider == null || provider.length() == 0)
	    throw new IllegalArgumentException("missing provider");
	Object[] objs = Security.getImpl(algorithm, "KeyPairGenerator",
					 provider);
	if (objs[0] instanceof KeyPairGenerator) {
	    KeyPairGenerator keyPairGen = (KeyPairGenerator)objs[0];
	    keyPairGen.provider = (Provider)objs[1];
	    return keyPairGen;
	} else {
	    KeyPairGenerator delegate =
		new Delegate((KeyPairGeneratorSpi)objs[0], algorithm);
	    delegate.provider = (Provider)objs[1];
	    return delegate;
	}
    }

    /** 
     * Generates a KeyPairGenerator object implementing the specified
     * algorithm, as supplied from the specified provider, 
     * if such an algorithm is available from the provider.
     * Note: the <code>provider</code> doesn't have to be registered.
     *
     * @param algorithm the standard string name of the algorithm.
     * See Appendix A in the <a href=
     * "../../../guide/security/CryptoSpec.html#AppA">
     * Java Cryptography Architecture API Specification &amp; Reference </a> 
     * for information about standard algorithm names.
     *
     * @param provider the provider.
     *
     * @return the new KeyPairGenerator object.
     *
     * @exception NoSuchAlgorithmException if the algorithm is
     * not available from the provider.
     *
     * @exception IllegalArgumentException if the <code>provider</code> is
     * null.
     *
     * @see Provider
     *
     * @since 1.4
     */
    public static KeyPairGenerator getInstance(String algorithm,
					       Provider provider) 
	throws NoSuchAlgorithmException
    {
	if (provider == null)
	    throw new IllegalArgumentException("missing provider");
	Object[] objs = Security.getImpl(algorithm, "KeyPairGenerator",
					 provider);
	if (objs[0] instanceof KeyPairGenerator) {
	    KeyPairGenerator keyPairGen = (KeyPairGenerator)objs[0];
	    keyPairGen.provider = (Provider)objs[1];
	    return keyPairGen;
	} else {
	    KeyPairGenerator delegate =
		new Delegate((KeyPairGeneratorSpi)objs[0], algorithm);
	    delegate.provider = (Provider)objs[1];
	    return delegate;
	}
    }

    /** 
     * Returns the provider of this key pair generator object.
     * 
     * @return the provider of this key pair generator object
     */
    public final Provider getProvider() {
	return this.provider;
    }

    /**
     * Initializes the key pair generator for a certain keysize using
     * a default parameter set and the <code>SecureRandom</code>
     * implementation of the highest-priority installed provider as the source
     * of randomness.
     * (If none of the installed providers supply an implementation of
     * <code>SecureRandom</code>, a system-provided source of randomness is
     * used.)
     *
     * @param keysize the keysize. This is an
     * algorithm-specific metric, such as modulus length, specified in
     * number of bits.
     *
     * @exception InvalidParameterException if the <code>keysize</code> is not
     * supported by this KeyPairGenerator object.
     */
    public void initialize(int keysize) {
	initialize(keysize, new SecureRandom());
    }

    /**
     * Initializes the key pair generator for a certain keysize with
     * the given source of randomness (and a default parameter set).
     *
     * @param keysize the keysize. This is an
     * algorithm-specific metric, such as modulus length, specified in
     * number of bits.
     * @param random the source of randomness.
     *
     * @exception InvalidParameterException if the <code>keysize</code> is not
     * supported by this KeyPairGenerator object.
     *
     * @since 1.2
     */
    public void initialize(int keysize, SecureRandom random) {
	// This does nothing, because either
	// 1. the implementation object returned by getInstance() is an
	//    instance of KeyPairGenerator which has its own 
	//    initialize(keysize, random) method, so the application would
	//    be calling that method directly, or
	// 2. the implementation returned by getInstance() is an instance
	//    of Delegate, in which case initialize(keysize, random) is
	//    overridden to call the corresponding SPI method.
	// (This is a special case, because the API and SPI method have the
	// same name.)
    }

    /**
     * Initializes the key pair generator using the specified parameter 
     * set and the <code>SecureRandom</code>
     * implementation of the highest-priority installed provider as the source
     * of randomness.
     * (If none of the installed providers supply an implementation of
     * <code>SecureRandom</code>, a system-provided source of randomness is
     * used.).
     *
     * <p>This concrete method has been added to this previously-defined
     * abstract class.
     * This method calls the KeyPairGeneratorSpi 
     * {@link KeyPairGeneratorSpi.html#
     * initialize(java.security.spec.AlgorithmParameterSpec,
     * java.security.SecureRandom) initialize} method, 
     * passing it <code>params</code> and a source of randomness (obtained
     * from the highest-priority installed provider or system-provided if none
     * of the installed providers supply one).
     * That <code>initialize</code> method always throws an
     * UnsupportedOperationException if it is not overridden by the provider.
     *
     * @param params the parameter set used to generate the keys.
     *
     * @exception InvalidAlgorithmParameterException if the given parameters
     * are inappropriate for this key pair generator.
     *
     * @since 1.2
     */
    public void initialize(AlgorithmParameterSpec params)
	throws InvalidAlgorithmParameterException {
	    initialize(params, new SecureRandom());
    }

    /**
     * Initializes the key pair generator with the given parameter 
     * set and source of randomness.
     *
     * <p>This concrete method has been added to this previously-defined
     * abstract class.
     * This method calls the KeyPairGeneratorSpi {@link 
     * KeyPairGeneratorSpi.html#
     * initialize(java.security.spec.AlgorithmParameterSpec,
     * java.security.SecureRandom) initialize} method, 
     * passing it <code>params</code> and <code>random</code>.
     * That <code>initialize</code>
     * method always throws an
     * UnsupportedOperationException if it is not overridden by the provider.
     *
     * @param params the parameter set used to generate the keys.
     * @param random the source of randomness.
     *
     * @exception InvalidAlgorithmParameterException if the given parameters
     * are inappropriate for this key pair generator.
     *
     * @since 1.2
     */
    public void initialize(AlgorithmParameterSpec params,
			   SecureRandom random)
	throws InvalidAlgorithmParameterException
    {
	// This does nothing, because either
	// 1. the implementation object returned by getInstance() is an
	//    instance of KeyPairGenerator which has its own 
	//    initialize(params, random) method, so the application would
	//    be calling that method directly, or
	// 2. the implementation returned by getInstance() is an instance
	//    of Delegate, in which case initialize(params, random) is
	//    overridden to call the corresponding SPI method.
	// (This is a special case, because the API and SPI method have the
	// same name.)
    }

    /**
     * Generates a key pair.
     *
     * <p>If this KeyPairGenerator has not been initialized explicitly,
     * provider-specific defaults will be used for the size and other
     * (algorithm-specific) values of the generated keys.
     *
     * <p>This will generate a new key pair every time it is called.
     *
     * <p>This method is functionally equivalent to 
     * {@link #generateKeyPair() generateKeyPair}.
     *
     * @return the generated key pair
     *
     * @since 1.2
     */
    public final KeyPair genKeyPair() {
	return generateKeyPair();
    }

    /**
     * Generates a key pair.
     *
     * <p>If this KeyPairGenerator has not been initialized explicitly,
     * provider-specific defaults will be used for the size and other
     * (algorithm-specific) values of the generated keys.
     *
     * <p>This will generate a new key pair every time it is called.
     * 
     * <p>This method is functionally equivalent to 
     * {@link #genKeyPair() genKeyPair}.
     *
     * @return the generated key pair
     */
    public KeyPair generateKeyPair() {
	// This does nothing (except returning null), because either:
	//
	// 1. the implementation object returned by getInstance() is an
	//    instance of KeyPairGenerator which has its own implementation
	//    of generateKeyPair (overriding this one), so the application
	//    would be calling that method directly, or
	//
	// 2. the implementation returned by getInstance() is an instance
	//    of Delegate, in which case generateKeyPair is
	//    overridden to invoke the corresponding SPI method.
	//
	// (This is a special case, because in JDK 1.1.x the generateKeyPair
	// method was used both as an API and a SPI method.)
        return null;
    }


    /*
     * The following class allows providers to extend from KeyPairGeneratorSpi
     * rather than from KeyPairGenerator. It represents a KeyPairGenerator
     * with an encapsulated, provider-supplied SPI object (of type
     * KeyPairGeneratorSpi).
     * If the provider implementation is an instance of KeyPairGeneratorSpi,
     * the getInstance() methods above return an instance of this class, with
     * the SPI object encapsulated.
     *
     * Note: All SPI methods from the original KeyPairGenerator class have been
     * moved up the hierarchy into a new class (KeyPairGeneratorSpi), which has
     * been interposed in the hierarchy between the API (KeyPairGenerator)
     * and its original parent (Object).
     */

    static class Delegate extends KeyPairGenerator {

	// The provider implementation (delegate)
	private KeyPairGeneratorSpi kpairGenSpi;

	// constructor
	public Delegate(KeyPairGeneratorSpi kpairGenSpi, String algorithm) {
	    super(algorithm);
	    this.kpairGenSpi = kpairGenSpi;
	}
    
	// engine method
	public void initialize(int keysize, SecureRandom random) {
	    kpairGenSpi.initialize(keysize, random);
	}

	// engine method
	public void initialize(AlgorithmParameterSpec params,
			       SecureRandom random)
	    throws InvalidAlgorithmParameterException {
		kpairGenSpi.initialize(params, random);;
	}

	// engine method
	public KeyPair generateKeyPair() {
	    return kpairGenSpi.generateKeyPair();
	}
    }
}
