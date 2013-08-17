/*
 * @(#)KeyPairGenerator.java	1.11 98/07/01
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

/**
 * The KeyPairGenerator class is used to generate pairs of
 * public and private keys. Key generators are constructed using the
 * <code>getInstance</code> factory methods (static methods that
 * return instances of a given class).
 * 
 * <p>Key generation is an area that sometimes
 * does not lend itself well to algorithm independence. For example,
 * it is possible to generate a DSA key pair specifying key family
 * parameters (p, q and g), while it is not possible to do so for
 * an RSA key pair. That is, those parameters are applicable to DSA
 * but not to RSA.
 * 
 * <P>There are therefore two ways to generate a key pair: in an 
 * algorithm-independent
 * manner, and in an algorithm-specific manner. The only difference
 * between the two is the initialization of the object. 
 * 
 * <p>All key pair generators share the concepts of a "strength" and a
 * source of randomness. The measure of strength is universally shared 
 * by all algorithms,
 * though it is interpreted differently for different algorithms.
 * The <a href = "#initialize ">initialize</a> method in this 
 * KeyPairGenerator class 
 * takes these two universally shared
 * types of arguments. 
 * 
 * <p>Since no other parameters are specified when you call this
 * algorithm-independent <code>initialize</code>
 * method, all other values, such as algorithm parameters, public
 * exponent, etc., are defaulted to standard values. 
 * 
 * <P>
 * It is sometimes desirable to initialize a key pair generator object
 * using algorithm-specific semantics. For example, you may want to
 * initialize a DSA key generator for a given set of parameters 
 * <code>p</code>, <code>q</code> and <code>g</code>,
 * or an RSA key generator for a given public exponent.
 * 
 * <P>
 * This is done through algorithm-specific standard interfaces. Rather than
 * calling the algorithm-independent KeyPairGenerator <code>initialize</code>
 * method, the key pair generator is cast to an algorithm-specific interface 
 * so that one of its specialized parameter initialization methods can be
 * called. An example is the DSAKeyPairGenerator interface (from
 * <code>java.security.interfaces</code>).
 * 
 *  <p>See <a href =
 * "../guide/security/CryptoSpec.html#KPG">The KeyPairGenerator Class</a> 
 * in the "Java Cryptography Architecture API Specification &amp; Reference"
 * for more information and examples.
 * 
 * @see java.security.interfaces.DSAKeyPairGenerator
 */
public abstract class KeyPairGenerator {

    private String algorithm;

    /**
     * Creates a KeyPairGenerator object for the specified algorithm.
     *
     * @param algorithm the standard string name of the algorithm. 
     * See Appendix A in the <a href=
     * "../guide/security/CryptoSpec.html#AppA">
     * Java Cryptography Architecture API Specification &amp; Reference </a> 
     * for information about standard algorithm names.
     */
    protected KeyPairGenerator(String algorithm) {
	this.algorithm = algorithm;
    }
    
    /**
     * Returns the standard name of the algorithm for this key generator.
     * See Appendix A in the <a href=
     * "../guide/security/CryptoSpec.html#AppA">
     * Java Cryptography Architecture API Specification &amp; Reference </a> 
     * for information about standard algorithm names.
     * 
     * @return the standard string name of the algorithm. 
   */
    public String getAlgorithm() {
	return algorithm;
    }

    /**
     * Generates a KeyPairGenerator object that implements the algorithm
     * requested, as available in the environment.
     *
     * @param algorithm the standard string name of the algorithm. 
     * See Appendix A in the <a href=
     * "../guide/security/CryptoSpec.html#AppA">
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
	    return (KeyPairGenerator)Security.getImpl(algorithm, 
						      "KeyPairGenerator",
						      null);
	} catch(NoSuchProviderException e) {
	    throw new InternalError("please send a bug report via " + 
				    System.getProperty("java.vendor.url.bug"));
	}
    }

    /** 
     * Generates a KeyPairGenerator object implementing the specified
     * algorithm, as supplied from the specified provider, 
     * if such an algorithm is available from the provider.
     *
     * @param algorithm the standard string name of the algorithm.
     * See Appendix A in the <a href=
     * "../guide/security/CryptoSpec.html#AppA">
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
     * @see Provider 
     */
    public static KeyPairGenerator getInstance(String algorithm,
					       String provider) 
    throws NoSuchAlgorithmException, NoSuchProviderException {	
	return (KeyPairGenerator)Security.getImpl(algorithm, 
						  "KeyPairGenerator",
						  provider);
    }

    /**
     * Initializes the key pair generator for a certain strength.
     *
     * @param strength the strength of the key. This is an
     * algorithm-specific metric, such as modulus length.
     *
     * @param random the source of randomness for this generator.
     */
    public abstract void initialize(int strength, SecureRandom random);

    /**
     * Initializes the key pair generator for a certain strength using
     * a system-provided source of randomness.
     *
     * @param strength the strength of the key. This is an
     * algorithm-specific metric, such as modulus length.
     */
    public void initialize(int strength) {
	initialize(strength, new SecureRandom());
    }


    /**
     * Generates a key pair. Unless an initialization method is called
     * using a KeyPairGenerator interface, algorithm-specific defaults
     * will be used. This will generate a new key pair every time it
     * is called.
     */
    public abstract KeyPair generateKeyPair();
}
