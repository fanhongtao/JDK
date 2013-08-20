/*
 * @(#)CertPathBuilder.java	1.9 04/06/28
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.security.cert;

import java.security.AccessController;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivilegedAction;
import java.security.Provider;
import java.security.Security;
import sun.security.util.Debug;

import sun.security.jca.*;
import sun.security.jca.GetInstance.Instance;

/**
 * A class for building certification paths (also known as certificate chains).
 * <p>
 * This class uses a provider-based architecture, as described in the Java 
 * Cryptography Architecture. To create a <code>CertPathBuilder</code>, call 
 * one of the static <code>getInstance</code> methods, passing in the 
 * algorithm name of the <code>CertPathBuilder</code> desired and optionally 
 * the name of the provider desired.
 * <p>
 * Once a <code>CertPathBuilder</code> object has been created, certification 
 * paths can be constructed by calling the {@link #build build} method and 
 * passing it an algorithm-specific set of parameters. If successful, the 
 * result (including the <code>CertPath</code> that was built) is returned 
 * in an object that implements the <code>CertPathBuilderResult</code> 
 * interface.
 * <p>
 * <b>Concurrent Access</b>
 * <p>
 * The static methods of this class are guaranteed to be thread-safe.
 * Multiple threads may concurrently invoke the static methods defined in
 * this class with no ill effects.
 * <p>
 * However, this is not true for the non-static methods defined by this class.
 * Unless otherwise documented by a specific provider, threads that need to
 * access a single <code>CertPathBuilder</code> instance concurrently should
 * synchronize amongst themselves and provide the necessary locking. Multiple
 * threads each manipulating a different <code>CertPathBuilder</code> instance
 * need not synchronize.
 * 
 * @see CertPath
 *
 * @version 	1.9 06/28/04
 * @since	1.4
 * @author	Sean Mullan
 * @author	Yassir Elley
 */
public class CertPathBuilder {

    /*
     * Constant to lookup in the Security properties file to determine
     * the default certpathbuilder type. In the Security properties file,
     * the default certpathbuilder type is given as:
     * <pre>
     * certpathbuilder.type=PKIX
     * </pre>
     */
    private static final String CPB_TYPE = "certpathbuilder.type";
    private static final Debug debug = Debug.getInstance("certpath");
    private CertPathBuilderSpi builderSpi;
    private Provider provider;
    private String algorithm;

    /**
     * Creates a <code>CertPathBuilder</code> object of the given algorithm, 
     * and encapsulates the given provider implementation (SPI object) in it.
     *
     * @param builderSpi the provider implementation
     * @param provider the provider
     * @param algorithm the algorithm name
     */
    protected CertPathBuilder(CertPathBuilderSpi builderSpi, Provider provider, 
	String algorithm) 
    {
	this.builderSpi = builderSpi;
	this.provider = provider;
	this.algorithm = algorithm;
    }

    /**
     * Returns a <code>CertPathBuilder</code> object that implements the
     * specified algorithm.
     * <p>
     * If the default provider package provides an implementation of the
     * specified <code>CertPathBuilder</code> algorithm, an instance of 
     * <code>CertPathBuilder</code> containing that implementation is returned.
     * If the requested algorithm is not available in the default package, 
     * other packages are searched.
     * 
     * @param algorithm the name of the requested <code>CertPathBuilder</code> 
     * algorithm
     * @return a <code>CertPathBuilder</code> object that implements the
     * specified algorithm
     * @throws NoSuchAlgorithmException if the requested algorithm is
     * not available in the default provider package or any of the other
     * provider packages that were searched
     */
    public static CertPathBuilder getInstance(String algorithm)
	    throws NoSuchAlgorithmException {
	Instance instance = GetInstance.getInstance("CertPathBuilder", 
	    CertPathBuilderSpi.class, algorithm);
	return new CertPathBuilder((CertPathBuilderSpi)instance.impl,
	    instance.provider, algorithm);
    }

    /**
     * Returns a <code>CertPathBuilder</code> object that implements the
     * specified algorithm, as supplied by the specified provider.
     *
     * @param algorithm the name of the requested <code>CertPathBuilder</code> 
     * algorithm
     * @param provider the name of the provider
     * @return a <code>CertPathBuilder</code> object that implements the 
     * specified algorithm, as supplied by the specified provider
     * @throws NoSuchAlgorithmException if the requested algorithm is
     * not available from the specified provider
     * @throws NoSuchProviderException if the provider has not been configured
     * @exception IllegalArgumentException if the <code>provider</code> is
     * null
     */
    public static CertPathBuilder getInstance(String algorithm, String provider)
	   throws NoSuchAlgorithmException, NoSuchProviderException {
	Instance instance = GetInstance.getInstance("CertPathBuilder", 
	    CertPathBuilderSpi.class, algorithm, provider);
	return new CertPathBuilder((CertPathBuilderSpi)instance.impl,
	    instance.provider, algorithm);
    }

    /**
     * Returns a <code>CertPathBuilder</code> object that implements the
     * specified algorithm, as supplied by the specified provider.
     * Note: the <code>provider</code> doesn't have to be registered.
     *
     * @param algorithm the name of the requested <code>CertPathBuilder</code> 
     * algorithm
     * @param provider the provider
     * @return a <code>CertPathBuilder</code> object that implements the 
     * specified algorithm, as supplied by the specified provider
     * @exception NoSuchAlgorithmException if the requested algorithm is
     * not available from the specified provider
     * @exception IllegalArgumentException if the <code>provider</code> is
     * null.
     */
    public static CertPathBuilder getInstance(String algorithm,
	    Provider provider) throws NoSuchAlgorithmException {
	Instance instance = GetInstance.getInstance("CertPathBuilder", 
	    CertPathBuilderSpi.class, algorithm, provider);
	return new CertPathBuilder((CertPathBuilderSpi)instance.impl,
	    instance.provider, algorithm);
    }
    
    /**
     * Returns the provider of this <code>CertPathBuilder</code>.
     *
     * @return the provider of this <code>CertPathBuilder</code>
     */
    public final Provider getProvider() {
	return this.provider;
    }

    /**
     * Returns the name of the algorithm of this <code>CertPathBuilder</code>.
     *
     * @return the name of the algorithm of this <code>CertPathBuilder</code>
     */
    public final String getAlgorithm() {
	return this.algorithm;
    }

    /**
     * Attempts to build a certification path using the specified algorithm
     * parameter set.
     *
     * @param params the algorithm parameters
     * @return the result of the build algorithm
     * @throws CertPathBuilderException if the builder is unable to construct 
     *  a certification path that satisfies the specified parameters
     * @throws InvalidAlgorithmParameterException if the specified parameters 
     * are inappropriate for this <code>CertPathBuilder</code>
     */
    public final CertPathBuilderResult build(CertPathParameters params)
	throws CertPathBuilderException, InvalidAlgorithmParameterException
    {
	return builderSpi.engineBuild(params);
    }

    /**
     * Returns the default <code>CertPathBuilder</code> type as specified in
     * the Java security properties file, or the string &quot;PKIX&quot;
     * if no such property exists. The Java security properties file is
     * located in the file named &lt;JAVA_HOME&gt;/lib/security/java.security,
     * where &lt;JAVA_HOME&gt; refers to the directory where the JDK was
     * installed.
     *
     * <p>The default <code>CertPathBuilder</code> type can be used by
     * applications that do not want to use a hard-coded type when calling one
     * of the <code>getInstance</code> methods, and want to provide a default
     * type in case a user does not specify its own.
     *
     * <p>The default <code>CertPathBuilder</code> type can be changed by
     * setting the value of the "certpathbuilder.type" security property
     * (in the Java security properties file) to the desired type.
     *
     * @return the default <code>CertPathBuilder</code> type as specified
     * in the Java security properties file, or the string &quot;PKIX&quot;
     * if no such property exists.
     */
    public final static String getDefaultType() {
        String cpbtype;
        cpbtype = (String)AccessController.doPrivileged(new PrivilegedAction() {
            public Object run() {
                return Security.getProperty(CPB_TYPE);
            }
        });
        if (cpbtype == null) {
            cpbtype = "PKIX";
        }
        return cpbtype;
    }
}
