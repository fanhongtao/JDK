/*
 * @(#)CertPathValidator.java	1.6 03/01/23
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
 * A class for validating certification paths (also known as certificate 
 * chains).
 * <p>
 * This class uses a provider-based architecture, as described in the Java 
 * Cryptography Architecture. To create a <code>CertPathValidator</code>, 
 * call one of the static <code>getInstance</code> methods, passing in the 
 * algorithm name of the <code>CertPathValidator</code> desired and 
 * optionally the name of the provider desired. 
 * <p>
 * Once a <code>CertPathValidator</code> object has been created, it can
 * be used to validate certification paths by calling the {@link #validate
 * validate} method and passing it the <code>CertPath</code> to be validated
 * and an algorithm-specific set of parameters. If successful, the result is
 * returned in an object that implements the 
 * <code>CertPathValidatorResult</code> interface.
 * <p>
 * <b>Concurrent Access</b>
 * <p>
 * The static methods of this class are guaranteed to be thread-safe.
 * Multiple threads may concurrently invoke the static methods defined in
 * this class with no ill effects.
 * <p>
 * However, this is not true for the non-static methods defined by this class.
 * Unless otherwise documented by a specific provider, threads that need to
 * access a single <code>CertPathValidator</code> instance concurrently should
 * synchronize amongst themselves and provide the necessary locking. Multiple
 * threads each manipulating a different <code>CertPathValidator</code>
 * instance need not synchronize.
 *
 * @see CertPath
 *
 * @version 	1.6 01/23/03
 * @since	1.4
 * @author	Yassir Elley
 */
public class CertPathValidator {

    /*
     * Constant to lookup in the Security properties file to determine
     * the default certpathvalidator type. In the Security properties file, 
     * the default certpathvalidator type is given as:
     * <pre>
     * certpathvalidator.type=PKIX
     * </pre>
     */
    private static final String CPV_TYPE = "certpathvalidator.type";
    private static final Debug debug = Debug.getInstance("certpath");
    private CertPathValidatorSpi validatorSpi;
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
     * Creates a <code>CertPathValidator</code> object of the given algorithm, 
     * and encapsulates the given provider implementation (SPI object) in it.
     *
     * @param validatorSpi the provider implementation
     * @param provider the provider
     * @param algorithm the algorithm name
     */
    protected CertPathValidator(CertPathValidatorSpi validatorSpi, 
	Provider provider, String algorithm) 
    {
	this.validatorSpi = validatorSpi;
	this.provider = provider;
	this.algorithm = algorithm;
    }

    /**
     * Returns a <code>CertPathValidator</code> object that implements the 
     * specified algorithm.
     *
     * <p> If the default provider package provides an implementation of the
     * specified <code>CertPathValidator</code> algorithm, an instance of 
     * <code>CertPathValidator</code> containing that implementation is 
     * returned. If the requested algorithm is not available in the default 
     * package, other packages are searched.
     * 
     * @param algorithm the name of the requested <code>CertPathValidator</code>
     * algorithm
     * @return a <code>CertPathValidator</code> object that implements the
     * specified algorithm
     * @exception NoSuchAlgorithmException if the requested algorithm
     * is not available in the default provider package or any of the other
     * provider packages that were searched
     */
    public static CertPathValidator getInstance(String algorithm)
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
						 "CertPathValidator",
						 (String)null
					       } );
	    return new CertPathValidator((CertPathValidatorSpi)objs[0],
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
     * Returns a <code>CertPathValidator</code> object that implements the
     * specified algorithm, as supplied by the specified provider.
     *
     * @param algorithm the name of the requested <code>CertPathValidator</code>
     * algorithm
     * @param provider the name of the provider
     * @return a <code>CertPathValidator</code> object that implements the
     * specified algorithm, as supplied by the specified provider
     * @exception NoSuchAlgorithmException if the requested algorithm
     * is not available from the specified provider
     * @exception NoSuchProviderException if the provider has not been
     * configured
     * @exception IllegalArgumentException if the <code>provider</code> is
     * null
     */
    public static CertPathValidator getInstance(String algorithm, 
	String provider) 
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
						 "CertPathValidator",
						 provider
					       } );
	    return new CertPathValidator((CertPathValidatorSpi)objs[0],
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
     * Returns a <code>CertPathValidator</code> object that implements the
     * specified algorithm, as supplied by the specified provider.
     * Note: the <code>provider</code> doesn't have to be registered.
     *
     * @param algorithm the name of the requested 
     * <code>CertPathValidator</code> algorithm
     * @param provider the provider
     * @return a <code>CertPathValidator</code> object that implements the
     * specified algorithm, as supplied by the specified provider
     * @exception NoSuchAlgorithmException if the requested algorithm
     * is not available from the specified provider
     * @exception IllegalArgumentException if the <code>provider</code> is
     * null
     */
    public static CertPathValidator getInstance(String algorithm, 
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
					debug.println("CertPathValidator." +
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
						 "CertPathValidator",
						 provider
					       } );
	    return new CertPathValidator((CertPathValidatorSpi)objs[0],
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
     * Returns the <code>Provider</code> of this
     * <code>CertPathValidator</code>.
     *
     * @return the <code>Provider</code> of this <code>CertPathValidator</code>
     */
    public final Provider getProvider() {
	return this.provider;
    }

    /**
     * Returns the algorithm name of this <code>CertPathValidator</code>.
     *
     * @return the algorithm name of this <code>CertPathValidator</code>
     */
    public final String getAlgorithm() {
	return this.algorithm;
    }

    /**
     * Validates the specified certification path using the specified 
     * algorithm parameter set. 
     * <p>
     * The <code>CertPath</code> specified must be of a type that is 
     * supported by the validation algorithm, otherwise an
     * <code>InvalidAlgorithmParameterException</code> will be thrown. For 
     * example, a <code>CertPathValidator</code> that implements the PKIX
     * algorithm validates <code>CertPath</code> objects of type X.509.
     *
     * @param certPath the <code>CertPath</code> to be validated
     * @param params the algorithm parameters
     * @return the result of the validation algorithm
     * @exception CertPathValidatorException if the <code>CertPath</code>
     * does not validate
     * @exception InvalidAlgorithmParameterException if the specified 
     * parameters or the type of the specified <code>CertPath</code> are 
     * inappropriate for this <code>CertPathValidator</code>
     */ 
    public final CertPathValidatorResult validate(CertPath certPath, 
	CertPathParameters params)
	throws CertPathValidatorException, InvalidAlgorithmParameterException 
    {
	return validatorSpi.engineValidate(certPath, params);
    }

    /**
     * Returns the default <code>CertPathValidator</code> type as specified in 
     * the Java security properties file, or the string &quot;PKIX&quot;
     * if no such property exists. The Java security properties file is 
     * located in the file named &lt;JAVA_HOME&gt;/lib/security/java.security, 
     * where &lt;JAVA_HOME&gt; refers to the directory where the SDK was 
     * installed.
     *
     * <p>The default <code>CertPathValidator</code> type can be used by 
     * applications that do not want to use a hard-coded type when calling one 
     * of the <code>getInstance</code> methods, and want to provide a default 
     * type in case a user does not specify its own.
     *
     * <p>The default <code>CertPathValidator</code> type can be changed by 
     * setting the value of the "certpathvalidator.type" security property 
     * (in the Java security properties file) to the desired type.
     *
     * @return the default <code>CertPathValidator</code> type as specified 
     * in the Java security properties file, or the string &quot;PKIX&quot;
     * if no such property exists.
     */
    public final static String getDefaultType() {
        String cpvtype;
        cpvtype = (String)AccessController.doPrivileged(new PrivilegedAction() {
            public Object run() {
                return Security.getProperty(CPV_TYPE);
            }
        });
        if (cpvtype == null) {
            cpvtype = "PKIX";
        }
        return cpvtype;
    }
}
