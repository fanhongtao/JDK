/*
 * @(#)CertPathBuilder.java	1.6 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
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
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;

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
 * @version 	1.6 01/23/03
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

    // for use with the reflection API
    private static final Class cl = java.security.Security.class;
    private static final Class[] GET_IMPL_PARAMS = { String.class,
						     String.class,
						     String.class };
    private static final Class[] GET_IMPL_PARAMS2 = { String.class,
						      String.class,
						      Provider.class };
    // Get the implMethod via the name of a provider. Note: the name could
    // be null. 
    private static Method implMethod;
    // Get the implMethod2 via a Provider object. 
    private static Method implMethod2;
    private static Boolean implMethod2Set = new Boolean(false);

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
	throws NoSuchAlgorithmException 
    {
	try {
	    if (implMethod == null) {
		throw new NoSuchAlgorithmException(algorithm + " not found");
	    }

	    // The underlying method is static, so we set the object
	    // argument to null.
	    Object[] objs = (Object[])implMethod.invoke(null,
					       new Object[]
					       { algorithm,
						 "CertPathBuilder",
						 (String)null
					       } );
	    return new CertPathBuilder((CertPathBuilderSpi)objs[0],
				       (Provider)objs[1], algorithm);
	} catch (IllegalAccessException iae) {
            NoSuchAlgorithmException nsae = new
                NoSuchAlgorithmException(algorithm + " not found");
            nsae.initCause(iae);
            throw nsae;
	} catch (InvocationTargetException ite) {
            Throwable t = ite.getCause();
            if (t != null && t instanceof NoSuchAlgorithmException)
                throw (NoSuchAlgorithmException)t;
            NoSuchAlgorithmException nsae = new
                NoSuchAlgorithmException(algorithm + " not found");
            nsae.initCause(ite);
            throw nsae;
	}
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
	throws NoSuchAlgorithmException, NoSuchProviderException 
    {
	if (provider == null || provider.length() == 0)
	    throw new IllegalArgumentException("missing provider");
	try {
	    if (implMethod == null) {
		throw new NoSuchAlgorithmException(algorithm + " not found");
	    }

	    // The underlying method is static, so we set the object
	    // argument to null.
	    Object[] objs = (Object[])implMethod.invoke(null,
					       new Object[]
					       { algorithm,
						 "CertPathBuilder",
						 provider
					       } );
	    return new CertPathBuilder((CertPathBuilderSpi)objs[0],
				       (Provider)objs[1], algorithm);
	} catch (IllegalAccessException iae) {
	    NoSuchAlgorithmException nsae = new
	                   NoSuchAlgorithmException(algorithm + " not found");
	    nsae.initCause(iae);
	    throw nsae;
	} catch (InvocationTargetException ite) {
	    Throwable t = ite.getTargetException();
	    if (t != null) {
		if (t instanceof NoSuchProviderException)
		    throw (NoSuchProviderException)t;
                if (t instanceof NoSuchAlgorithmException)
                    throw (NoSuchAlgorithmException)t;
	    }
	    NoSuchAlgorithmException nsae = new
	        NoSuchAlgorithmException(algorithm + " not found");
	    nsae.initCause(ite);
	    throw nsae;
        }
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
					      Provider provider)
	throws NoSuchAlgorithmException
    {
	if (provider == null)
	    throw new IllegalArgumentException("missing provider");
 
	if (implMethod2Set.booleanValue() == false) {
	    synchronized (implMethod2Set) {
		if (implMethod2Set.booleanValue() == false) {
		    implMethod2 = (Method)
			AccessController.doPrivileged(
					   new PrivilegedAction() {
			    public Object run() {
				Method m = null;
				try {
				    m = cl.getDeclaredMethod("getImpl",
							     GET_IMPL_PARAMS2);
				    if (m != null)
					m.setAccessible(true);
				} catch (NoSuchMethodException nsme) {
				    if (debug != null)
					debug.println("CertPathBuilder." +
					      "getInstance(): Cannot find " +
					      "Security.getImpl(String, " +
					      "String, Provider)");
				}
				return m;
			    }
			});
		    implMethod2Set = new Boolean(true);
		}		
	    }
	}

	if (implMethod2 == null) {
	    throw new NoSuchAlgorithmException(algorithm +
					       " not found");
	}

	try {
	    // The underlying method is static, so we set the object
	    // argument to null.
	    Object[] objs = (Object[])implMethod2.invoke(null,
					       new Object[]
					       { algorithm,
						 "CertPathBuilder",
						 provider
					       } );
	    return new CertPathBuilder((CertPathBuilderSpi)objs[0],
				       (Provider)objs[1], algorithm);
	} catch (IllegalAccessException iae) {
	    NoSuchAlgorithmException nsae = new 
                           NoSuchAlgorithmException(algorithm + " not found");
	    nsae.initCause(iae);
	    throw nsae;
	} catch (InvocationTargetException ite) {
            Throwable t = ite.getCause();
            if (t != null && t instanceof NoSuchAlgorithmException)
                throw (NoSuchAlgorithmException)t;
	    NoSuchAlgorithmException nsae = new
                NoSuchAlgorithmException(algorithm + " not found");
	    nsae.initCause(ite);
	    throw nsae;
	}
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
     * where &lt;JAVA_HOME&gt; refers to the directory where the SDK was
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
