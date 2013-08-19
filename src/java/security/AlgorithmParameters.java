/*
 * @(#)AlgorithmParameters.java	1.22 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
 
package java.security;

import java.io.*;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.InvalidParameterSpecException;

/**
 * This class is used as an opaque representation of cryptographic parameters.
 * 
 * <p>An <code>AlgorithmParameters</code> object for managing the parameters
 * for a particular algorithm can be obtained by
 * calling one of the <code>getInstance</code> factory methods
 * (static methods that return instances of a given class).
 * 
 * <p>There are two ways to request such an implementation: by
 * specifying either just an algorithm name, or both an algorithm name
 * and a package provider. 
 * 
 * <ul>
 *
 * <li>If just an algorithm name is specified, the system will
 * determine if there is an AlgorithmParameters
 * implementation for the algorithm requested
 * available in the environment, and if there is more than one, if
 * there is a preferred one.
 * 
 * <li>If both an algorithm name and a package provider are specified,
 * the system will determine if there is an implementation 
 * in the package requested, and throw an exception if there
 * is not.
 * 
 * </ul>
 * 
 * <p>Once an <code>AlgorithmParameters</code> object is returned, it must be
 * initialized via a call to <code>init</code>, using an appropriate parameter
 * specification or parameter encoding.
 *
 * <p>A transparent parameter specification is obtained from an
 * <code>AlgorithmParameters</code> object via a call to
 * <code>getParameterSpec</code>, and a byte encoding of the parameters is
 * obtained via a call to <code>getEncoded</code>.
 *
 * @author Jan Luehe
 *
 * @version 1.22, 01/23/03
 *
 * @see java.security.spec.AlgorithmParameterSpec
 * @see java.security.spec.DSAParameterSpec
 * @see KeyPairGenerator
 *
 * @since 1.2
 */

public class AlgorithmParameters {

    // The provider
    private Provider provider;

    // The provider implementation (delegate)
    private AlgorithmParametersSpi paramSpi;

    // The algorithm
    private String algorithm;

    // Has this object been initialized?
    private boolean initialized = false;

    /**
     * Creates an AlgorithmParameters object.
     *
     * @param paramSpi the delegate
     * @param provider the provider
     * @param algorithm the algorithm
     */
    protected AlgorithmParameters(AlgorithmParametersSpi paramSpi,
				  Provider provider, String algorithm)
    {
	this.paramSpi = paramSpi;
	this.provider = provider;
	this.algorithm = algorithm;
    }

    /**
     * Returns the name of the algorithm associated with this parameter object.
     * 
     * @return the algorithm name.
     */
    public final String getAlgorithm() {
        return this.algorithm;
    }

    /**
     * Generates a parameter object for the specified algorithm.
     *
     * <p>If the default provider package provides an implementation of the
     * requested algorithm, an instance of AlgorithmParameters containing that
     * implementation is returned.
     * If the algorithm is not available in the default 
     * package, other packages are searched.
     *
     * <p>The returned parameter object must be initialized via a call to
     * <code>init</code>, using an appropriate parameter specification or
     * parameter encoding.
     *
     * @param algorithm the name of the algorithm requested. 
     *
     * @return the new parameter object.
     *
     * @exception NoSuchAlgorithmException if the algorithm is
     * not available in the environment.
     */
    public static AlgorithmParameters getInstance(String algorithm) 
    throws NoSuchAlgorithmException {
	try {
	    Object[] objs = Security.getImpl(algorithm, "AlgorithmParameters",
					     (String)null);
	    return new AlgorithmParameters((AlgorithmParametersSpi)objs[0],
					   (Provider)objs[1],
					   algorithm);
	} catch(NoSuchProviderException e) {
	    throw new NoSuchAlgorithmException(algorithm + " not found");
	}
    }

    /** 
     * Generates a parameter object for the specified algorithm, as supplied
     * by the specified provider, if such an algorithm is available from the
     * provider.
     *
     * <p>The returned parameter object must be initialized via a call to
     * <code>init</code>, using an appropriate parameter specification or
     * parameter encoding.
     *
     * @param algorithm the name of the algorithm requested.
     *
     * @param provider the name of the provider.
     *
     * @return the new parameter object.
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
    public static AlgorithmParameters getInstance(String algorithm,
						  String provider)
	throws NoSuchAlgorithmException, NoSuchProviderException
    {
	if (provider == null || provider.length() == 0)
	    throw new IllegalArgumentException("missing provider");
	Object[] objs = Security.getImpl(algorithm, "AlgorithmParameters",
					 provider);
	return new AlgorithmParameters((AlgorithmParametersSpi)objs[0],
				       (Provider)objs[1],
				       algorithm);
    }

    /** 
     * Generates a parameter object for the specified algorithm, as supplied
     * by the specified provider, if such an algorithm is available from the
     * provider. Note: the <code>provider</code> doesn't have to be registered.
     *
     * <p>The returned parameter object must be initialized via a call to
     * <code>init</code>, using an appropriate parameter specification or
     * parameter encoding.
     *
     * @param algorithm the name of the algorithm requested.
     *
     * @param provider the name of the provider.
     *
     * @return the new parameter object.
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
    public static AlgorithmParameters getInstance(String algorithm,
						  Provider provider)
	throws NoSuchAlgorithmException
    {
	if (provider == null)
	    throw new IllegalArgumentException("missing provider");
	Object[] objs = Security.getImpl(algorithm, "AlgorithmParameters",
					 provider);
	return new AlgorithmParameters((AlgorithmParametersSpi)objs[0],
				       (Provider)objs[1],
				       algorithm);
    }

    /** 
     * Returns the provider of this parameter object.
     * 
     * @return the provider of this parameter object
     */
    public final Provider getProvider() {
	return this.provider;
    }

    /**
     * Initializes this parameter object using the parameters 
     * specified in <code>paramSpec</code>.
     *
     * @param paramSpec the parameter specification.
     *
     * @exception InvalidParameterSpecException if the given parameter
     * specification is inappropriate for the initialization of this parameter
     * object, or if this parameter object has already been initialized.
     */
    public final void init(AlgorithmParameterSpec paramSpec) 
	throws InvalidParameterSpecException
    {
	if (this.initialized)
	    throw new InvalidParameterSpecException("already initialized");
	paramSpi.engineInit(paramSpec);
	this.initialized = true;
    }

    /**
     * Imports the specified parameters and decodes them according to the 
     * primary decoding format for parameters. The primary decoding
     * format for parameters is ASN.1, if an ASN.1 specification for this type
     * of parameters exists.
     *
     * @param params the encoded parameters.
     *
     * @exception IOException on decoding errors, or if this parameter object
     * has already been initialized.
     */
    public final void init(byte[] params) throws IOException {
	if (this.initialized)
	    throw new IOException("already initialized");
	paramSpi.engineInit(params);
	this.initialized = true;
    }

    /**
     * Imports the parameters from <code>params</code> and decodes them 
     * according to the specified decoding scheme.
     * If <code>format</code> is null, the
     * primary decoding format for parameters is used. The primary decoding
     * format is ASN.1, if an ASN.1 specification for these parameters
     * exists.
     *
     * @param params the encoded parameters.
     *
     * @param format the name of the decoding scheme.
     *
     * @exception IOException on decoding errors, or if this parameter object
     * has already been initialized.
     */
    public final void init(byte[] params, String format) throws IOException {
	if (this.initialized)
	    throw new IOException("already initialized");
	paramSpi.engineInit(params, format);
	this.initialized = true;
    }

    /**
     * Returns a (transparent) specification of this parameter object.
     * <code>paramSpec</code> identifies the specification class in which 
     * the parameters should be returned. It could, for example, be
     * <code>DSAParameterSpec.class</code>, to indicate that the
     * parameters should be returned in an instance of the 
     * <code>DSAParameterSpec</code> class.
     *
     * @param paramSpec the specification class in which 
     * the parameters should be returned.
     *
     * @return the parameter specification.
     *
     * @exception InvalidParameterSpecException if the requested parameter
     * specification is inappropriate for this parameter object, or if this
     * parameter object has not been initialized.
     */
    public final AlgorithmParameterSpec getParameterSpec(Class paramSpec)
	throws InvalidParameterSpecException
    {
	if (this.initialized == false) {
	    throw new InvalidParameterSpecException("not initialized");
	}
	return paramSpi.engineGetParameterSpec(paramSpec);
    }

    /**
     * Returns the parameters in their primary encoding format.
     * The primary encoding format for parameters is ASN.1, if an ASN.1
     * specification for this type of parameters exists.
     *
     * @return the parameters encoded using their primary encoding format.
     *
     * @exception IOException on encoding errors, or if this parameter object
     * has not been initialized.
     */
    public final byte[] getEncoded() throws IOException
    {
	if (this.initialized == false) {
	    throw new IOException("not initialized");
	}
	return paramSpi.engineGetEncoded();
    }

    /**
     * Returns the parameters encoded in the specified scheme.
     * If <code>format</code> is null, the
     * primary encoding format for parameters is used. The primary encoding
     * format is ASN.1, if an ASN.1 specification for these parameters
     * exists.
     *
     * @param format the name of the encoding format.
     *
     * @return the parameters encoded using the specified encoding scheme.
     *
     * @exception IOException on encoding errors, or if this parameter object
     * has not been initialized.
     */
    public final byte[] getEncoded(String format) throws IOException
    {
	if (this.initialized == false) {
	    throw new IOException("not initialized");
	}
	return paramSpi.engineGetEncoded(format);
    }

    /**
     * Returns a formatted string describing the parameters.
     *
     * @return a formatted string describing the parameters, or null if this
     * parameter object has not been initialized.
     */
    public final String toString() {
	if (this.initialized == false) {
	    return null;
	}
	return paramSpi.engineToString();
    }
}
