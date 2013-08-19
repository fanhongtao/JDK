/*
 * @(#)AlgorithmParameterGenerator.java	1.39 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.security;

import java.security.spec.AlgorithmParameterSpec;

/**
 * The <code>AlgorithmParameterGenerator</code> class is used to generate a
 * set of
 * parameters to be used with a certain algorithm. Parameter generators
 * are constructed using the <code>getInstance</code> factory methods
 * (static methods that return instances of a given class).
 * 
 * <P>The object that will generate the parameters can be initialized
 * in two different ways: in an algorithm-independent manner, or in an
 * algorithm-specific manner:
 *
 * <ul>
 * <li>The algorithm-independent approach uses the fact that all parameter
 * generators share the concept of a "size" and a
 * source of randomness. The measure of size is universally shared 
 * by all algorithm parameters, though it is interpreted differently
 * for different algorithms. For example, in the case of parameters for
 * the <i>DSA</i> algorithm, "size" corresponds to the size
 * of the prime modulus (in bits).
 * When using this approach, algorithm-specific parameter generation
 * values - if any - default to some standard values, unless they can be
 * derived from the specified size.<P>
 *
 * <li>The other approach initializes a parameter generator object
 * using algorithm-specific semantics, which are represented by a set of
 * algorithm-specific parameter generation values. To generate
 * Diffie-Hellman system parameters, for example, the parameter generation
 * values usually
 * consist of the size of the prime modulus and the size of the
 * random exponent, both specified in number of bits.
 * </ul>
 *
 * <P>In case the client does not explicitly initialize the
 * AlgorithmParameterGenerator
 * (via a call to an <code>init</code> method), each provider must supply (and
 * document) a default initialization. For example, the Sun provider uses a
 * default modulus prime size of 1024 bits for the generation of DSA
 * parameters.
 *
 * @author Jan Luehe
 *
 * @version 1.39, 01/23/03
 *
 * @see AlgorithmParameters
 * @see java.security.spec.AlgorithmParameterSpec
 *
 * @since 1.2
 */

public class AlgorithmParameterGenerator {

    // The provider
    private Provider provider;

    // The provider implementation (delegate)
    private AlgorithmParameterGeneratorSpi paramGenSpi;

    // The algorithm
    private String algorithm;

    /**
     * Creates an AlgorithmParameterGenerator object.
     *
     * @param paramGenSpi the delegate
     * @param provider the provider
     * @param algorithm the algorithm
     */
    protected AlgorithmParameterGenerator
    (AlgorithmParameterGeneratorSpi paramGenSpi, Provider provider,
     String algorithm) {
	this.paramGenSpi = paramGenSpi;
	this.provider = provider;
	this.algorithm = algorithm;
    }

    /**
     * Returns the standard name of the algorithm this parameter
     * generator is associated with.
     * 
     * @return the string name of the algorithm. 
     */
    public final String getAlgorithm() {
	return this.algorithm;
    }

    /**
     * Generates an AlgorithmParameterGenerator object that implements the 
     * specified digest algorithm. If the default provider package
     * provides an implementation of the requested digest algorithm,
     * an instance of AlgorithmParameterGenerator containing that
     * implementation 
     * is returned. If the algorithm is not available in the default 
     * package, other packages are searched.
     *
     * @param algorithm the string name of the algorithm this
     * parameter generator is associated with.
     *
     * @return the new AlgorithmParameterGenerator object.
     *
     * @exception NoSuchAlgorithmException if the algorithm is
     * not available in the environment.  
     */
    public static AlgorithmParameterGenerator getInstance(String algorithm)
	throws NoSuchAlgorithmException {
	    try {
		Object[] objs = Security.getImpl(algorithm,
						 "AlgorithmParameterGenerator",
						 (String)null);
		return new AlgorithmParameterGenerator
		    ((AlgorithmParameterGeneratorSpi)objs[0],
		     (Provider)objs[1],
		     algorithm);
	    } catch(NoSuchProviderException e) {
		throw new NoSuchAlgorithmException(algorithm + " not found");
	    }
    }

    /** 
     * Generates an AlgorithmParameterGenerator object for the requested
     * algorithm, as supplied from the specified provider, 
     * if such a parameter generator is available from the provider.
     *
     * @param algorithm the string name of the algorithm.
     *
     * @param provider the string name of the provider.
     *
     * @return the new AlgorithmParameterGenerator object.
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
    public static AlgorithmParameterGenerator getInstance(String algorithm,
							  String provider) 
	throws NoSuchAlgorithmException, NoSuchProviderException
    {
	if (provider == null || provider.length() == 0)
	    throw new IllegalArgumentException("missing provider");
	Object[] objs = Security.getImpl(algorithm,
					 "AlgorithmParameterGenerator",
					 provider);
	return new AlgorithmParameterGenerator
	    ((AlgorithmParameterGeneratorSpi)objs[0], (Provider)objs[1],
	     algorithm);
    }

    /** 
     * Generates an AlgorithmParameterGenerator object for the requested
     * algorithm, as supplied from the specified provider, 
     * if such a parameter generator is available from the provider.
     * Note: the <code>provider</code> doesn't have to be registered. 
     *
     * @param algorithm the string name of the algorithm.
     *
     * @param provider the provider.
     *
     * @return the new AlgorithmParameterGenerator object.
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
    public static AlgorithmParameterGenerator getInstance(String algorithm,
							  Provider provider) 
	throws NoSuchAlgorithmException
    {
	if (provider == null)
	    throw new IllegalArgumentException("missing provider");
	Object[] objs = Security.getImpl(algorithm,
					 "AlgorithmParameterGenerator",
					 provider);
	return new AlgorithmParameterGenerator
	    ((AlgorithmParameterGeneratorSpi)objs[0], (Provider)objs[1],
	     algorithm);
    }

    /** 
     * Returns the provider of this algorithm parameter generator object.
     * 
     * @return the provider of this algorithm parameter generator object
     */
    public final Provider getProvider() {
	return this.provider;
    }

    /**
     * Initializes this parameter generator for a certain size.
     * To create the parameters, the <code>SecureRandom</code>
     * implementation of the highest-priority installed provider is used as
     * the source of randomness.
     * (If none of the installed providers supply an implementation of
     * <code>SecureRandom</code>, a system-provided source of randomness is
     * used.)
     *
     * @param size the size (number of bits).
     */
    public final void init(int size) {
	paramGenSpi.engineInit(size, new SecureRandom());
    }

    /**
     * Initializes this parameter generator for a certain size and source
     * of randomness.
     *
     * @param size the size (number of bits).
     * @param random the source of randomness.
     */
    public final void init(int size, SecureRandom random) {
	paramGenSpi.engineInit(size, random);
    }

    /**
     * Initializes this parameter generator with a set of algorithm-specific
     * parameter generation values.
     * To generate the parameters, the <code>SecureRandom</code>
     * implementation of the highest-priority installed provider is used as
     * the source of randomness.
     * (If none of the installed providers supply an implementation of
     * <code>SecureRandom</code>, a system-provided source of randomness is
     * used.)
     *
     * @param genParamSpec the set of algorithm-specific parameter generation values.
     *
     * @exception InvalidAlgorithmParameterException if the given parameter
     * generation values are inappropriate for this parameter generator.
     */
    public final void init(AlgorithmParameterSpec genParamSpec)
	throws InvalidAlgorithmParameterException {
	    paramGenSpi.engineInit(genParamSpec, new SecureRandom());
    }

    /**
     * Initializes this parameter generator with a set of algorithm-specific
     * parameter generation values.
     *
     * @param genParamSpec the set of algorithm-specific parameter generation values.
     * @param random the source of randomness.
     *
     * @exception InvalidAlgorithmParameterException if the given parameter
     * generation values are inappropriate for this parameter generator.
     */
    public final void init(AlgorithmParameterSpec genParamSpec,
			   SecureRandom random)
	throws InvalidAlgorithmParameterException {
	    paramGenSpi.engineInit(genParamSpec, random);
    }

    /**
     * Generates the parameters.
     *
     * @return the new AlgorithmParameters object.
     */
    public final AlgorithmParameters generateParameters() {
	return paramGenSpi.engineGenerateParameters();
    }
}
