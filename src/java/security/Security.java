/*
 * @(#)Security.java	1.58 98/07/01
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
 * <p>This class centralizes all security properties and common security
 * methods. One of its primary uses is to manage providers.
 *
 * @version 1.54 97/02/06
 * @author Benjamin Renaud */
public final class Security {

    /* Are we debugging? -- for developers */
    static boolean debug = false;

    /* Are we displaying errors? -- for users */
    static boolean error = true;

    /* The java.security properties */
    private static Properties props; 

    /* Where we cache provider properties */
    private static Properties propCache;

    /* A vector of providers, in order of priority */
    private static Vector providers;

    static {
	initialize();
    }
    
    private static void initialize() {
	props = new Properties();
	propCache = new Properties();
	providers = new Vector();

	File propFile = securityPropFile("java.security");
	if (!propFile.exists()) {
	    System.err.println("security properties not found. using defaults.");
	    initializeStatic();
	} else {
	    try {
		FileInputStream fis = new FileInputStream(propFile);
		InputStream is = new BufferedInputStream(fis);
		props.load(is);
		is.close();
	    } catch (IOException e) {
		error("could not load security properties file from " + propFile +
		      ". using defaults.");
		initializeStatic();
	    }
	}
	loadProviders();
    }

    /* 
     * Initialize to default values, if <java.home>/lib/java.security
     * is not found.
     */
    private static void initializeStatic() {
	props.put("security.provider.1", "sun.security.provider.Sun");
	props.put("system.scope","sun.security.provider.IdentityDatabase");
    }

    /**
     * Don't let anyone instantiate this. 
     */
    private Security() {
    }

    /**
     * Loops through provider declarations, which are expected to be
     * of the form:
     *
     * security.provider.1=sun.security.provider.Sun
     * security.provider.2=sun.security.jsafe.Jsafe
     * etc.
     *
     * The order determines the default search order when looking for 
     * an algorithm.
     */
    private static void loadProviders() {

	int i = 1;

	while(true) {

	    String name = props.getProperty("security.provider." + i++);
	    if (name == null) {
		break;

	    } else {
		Provider prov = Provider.loadProvider(name);
		if (prov != null) {
		    /* This must manipulate the datastructure
		       directly, because going through addProviders
		       causes a security check to happen, which
		       sometimes will cause the security
		       initialization to fail with bad
		       consequences. */
		    providers.addElement(prov);
		}
	    }
	}
    }

    static File securityPropFile(String filename) {
	// maybe check for a system property which will specify where to
	// look. Someday.
	String sep = File.separator;
	return new File(System.getProperty("java.home") + sep + "lib" + sep + 
			"security" + sep + filename);
    }

    /**
     * Looks up providers, and returns the property mapping the key,
     * if any. The order in which the providers are looked up is the
     * provider-preference order, as specificed in the security
     * properties file.
     */
    static String getProviderProperty(String key) {
	
	String prop = propCache.getProperty(key);
	if (prop != null) {
	    return prop;
	}

	for (int i = 0; i < providers.size(); i++) {
	    Provider prov = (Provider)providers.elementAt(i);
	    
	    prop = prov.getProperty(key);
	    if (prop != null) {
		propCache.put(key, prop);
		return prop;
	    }
	}
	return prop;
    }

    /**
     * We always map names to standard names
     */
    static String getStandardName(String alias, String engineType) {
	return getProviderProperty("Alg.Alias." + engineType + "." + alias);
    }

    /** 
     * Gets a specified property for an algorithm. The algorithm name
     * should be a standard name. See Appendix A in the <a href=
     * "../guide/security/CryptoSpec.html#AppA">
     * Java Cryptography Architecture API Specification &amp; Reference </a> 
     * for information about standard algorithm names.
     * One possible use is by specialized algorithm parsers, which may map 
     * classes to algorithms which they understand (much like Key parsers 
     * do).
     *
     * @param algName the algorithm name.
     *
     * @param propName the name of the property to get.
     * 
     * @return the value of the specified property.  
     */
    public static String getAlgorithmProperty(String algName,
					      String propName) {
	return getProviderProperty("Alg." + propName + "." + algName);
    }

    /** 
     * Given an algorithm name, returns the name of PublicKey class
     * capable of handling keys for that algorithm. The algorithm name
     * should be a standard name. See Appendix A in the <a href=
     * "../guide/security/CryptoSpec.html#AppA">
     * Java Cryptography Architecture API Specification &amp; Reference </a> 
     * for information about standard algorithm names.
     *
     * @param algName the standard algorithm name for which to get
     * a public key class name.
     */
    static String getPublicKeyClassName(String algName, String format) {

	String stdName = getStandardName(algName, "Key");

	if (stdName == null) {
	    stdName = algName;
	}

	String formatAndAlg = "PublicKey." + format + "." + stdName;
	return getProviderProperty(formatAndAlg);
    }


    /** Given an algorithm name, returns the name of PrivateKey class
     * capable of handling keys for that algorithm. The algorithm name
     * should be a standard name. See Appendix A in the <a href=
     * "../guide/security/CryptoSpec.html#AppA">
     * Java Cryptography Architecture API Specification &amp; Reference </a> 
     * for information about standard algorithm names.
     */
    static String getPrivateKeyClassName(String algName, String format) {

	String stdName = getStandardName(algName, "Key");

	if (stdName == null) {
	    stdName = algName;
	}

	return getProviderProperty("PrivateKey." + format + "." + stdName);
    }

    static String getEngineClassName(String algName,
				     String engineType)
    throws NoSuchAlgorithmException {
	/* First get the standard name */
	String stdName = getStandardName(algName, engineType);
	
	if (stdName == null) {
	    stdName = algName;
	}

	Class impl = null;

	Enumeration enum = providers.elements();

	String classname = getProviderProperty(engineType + "." + stdName);

	if (classname != null) {
	    return classname;
	}

	throw new NoSuchAlgorithmException("algorithm " + algName + 
					   " not available.");
    }

    /** Given an algorithm name, returns the name of Signature class
     * capable of handling keys for that algorithm. The algorithm name
     * should be a standard name. See Appendix A in the <a href=
     * "../guide/security/CryptoSpec.html#AppA">
     * Java Cryptography Architecture API Specification &amp; Reference </a> 
     * for information about standard algorithm names.
     */
    private static String getEngineClassName(String algName, String provider, 
					     String engineType) 
    throws NoSuchAlgorithmException, NoSuchProviderException {

	if (provider == null) {
	    return getEngineClassName(algName, engineType);
	}

	/* First get the standard name */
	String stdName = getStandardName(algName, engineType);
	
	if (stdName == null) {
	    stdName = algName;
	}

	Provider prov = getProvider(provider);
	if (prov == null) {
	    throw new NoSuchProviderException("no such provider: " +
						provider);
	}
	
	String className = prov.getProperty(engineType + "." + stdName);
	if (className == null) {
	    throw new NoSuchAlgorithmException("no such algorithm: " +
						     algName + 
						     " for provider " +
						 provider);
	}
	return className;
    }

    /**
     * Adds a new provider, at a specified position. The position is
     * the preference order in which providers are searched for
     * requested algorithms. Note that it is not guaranteed that this
     * preference will be respected. The position is 1-based, that is,
     * 1 is most preferred, followed by 2, and so on. Sometimes it
     * will be legal to add a provider, but only in the last position,
     * in which case the <code>position</code> argument will be ignored. 
     * 
     * <p>If the given provider is installed at the requested position,
     * the provider that used to be at that position, and all providers
     * with a position greater than <code>position</code>, are shifted up
     * one position (towards the end of the list of installed providers).
     * 
     * <p>A provider cannot be added if it is already installed.
     *
     * @param provider the provider to be added.
     *
     * @param position the preference position that the caller would
     * like for this provider.
     *
     * @return the actual preference position in which the provider was 
     * added, or -1 if the provider was not added because it is
     * already installed.
     *
     * @see #getProvider
     * @see #removeProvider 
     */
    public static int insertProviderAt(Provider provider, int position) {

	check();

	/* First check if the provider is already installed */
	Provider already = getProvider(provider.getName());
	if (already != null) {
	    return -1;
	}	
		
	int size = providers.size();
	if (position > size || position <= 0) {
	    position = size+1;
	}

	providers.insertElementAt(provider, position-1);

	/* clear the prop caches */
	propCache = new Properties();
	
	return position;
    }

    /**
     * Adds a provider to the next position available.
     *
     * @param provider the provider to be added.
     *
     * @return the preference position in which the provider was 
     * added, or -1 if the provider was not added because it is
     * already installed.
     * 
     * @see #getProvider
     * @see #removeProvider
     */
    public static int addProvider(Provider provider) {
	
	return insertProviderAt(provider, providers.size() + 1);
    }

    /**
     * Removes the provider with the specified name.
     *
     * <p>When the specified provider is removed, all providers located
     * at a position greater than where the specified provider was are shifted
     * down one position (towards the head of the list of installed providers).
     *
     * <p>This method returns silently if the provider is not installed.
     *
     * @param name the name of the provider to remove.
     *
     * @see #getProvider
     * @see #addProvider
     */
    public static void removeProvider(String name) {

	check();
	
	Provider provider = getProvider(name);

	if (provider != null) {
	    providers.removeElement(provider);
	}
    }

    
    /**
     * Returns all providers currently installed.
     * 
     * @return an array of all providers currently installed.
     */
    public static Provider[] getProviders() {
	check();
	Provider[] result = new Provider[providers.size()];
	providers.copyInto(result);
	return result;
    }

    /**
     * Returns the provider installed with the specified name, if
     * any. Returns null if no provider with the speicified name is
     * installed.
     * 
     * @param name the name of the provider to get.
     * 
     * @return the provider of the specified name.
     *
     * @see #removeProvider
     * @see #addProvider
     */
    public static Provider getProvider(String name) {
	check();
	Enumeration enum = providers.elements();
	while (enum.hasMoreElements()) {
	    Provider prov = (Provider)enum.nextElement();
	    if (prov.getName().equals(name)) {
		return prov;
	    }
	}
	return null;
    }

    private static boolean checkSuperclass(Class subclass, Class superclass) {
	while(!subclass.equals(superclass)) {
	    subclass = subclass.getSuperclass();
	    if (subclass == null) {
		return false;
	    }
	}
	return true;
    }

    /*
     * Return an object configured to implemented type. Provider can
     * be null, in which case all providers will be searched in order
     * of preference.
     */
    static Object getImpl(String algorithm, String type, String provider)
    throws NoSuchAlgorithmException, NoSuchProviderException {	

	String className = getEngineClassName(algorithm, provider, type);

	try {
	    Class typeClass = Class.forName("java.security." + type);
	    Class cl = Class.forName(className);

	    if (checkSuperclass(cl, typeClass)) {
		return cl.newInstance();
	    } else {
		throw new NoSuchAlgorithmException("class configured for " + 
						   type + ": " + className + 
						   " not a " + type);
	    }
	} catch (ClassNotFoundException e) {
	    throw new NoSuchAlgorithmException("class configured for " + 
					       type + "(provider: " + 
					       provider + ")" + 
					       "cannot be found.\n" + 
					       e.getMessage());
	} catch (InstantiationException e) {
	    throw new NoSuchAlgorithmException("class " + className + 
					       " configured for " + type +
					       "(provider: " + provider + 
					       ") cannot be instantiated.\n" + 
					       e.getMessage());
	} catch (IllegalAccessException e) {
	    throw new NoSuchAlgorithmException("class " + className + 
					       " configured for " + type +
					       "(provider: " + provider +
					       ") cannot be accessed.\n" + 
					       e.getMessage());
	}
    }

    /**
     * Gets a security property.
     *
     * @param key the key of the property being retrieved.
     *
     * @return the valeu of the security property corresponding to key.
     */
    public static String getProperty(String key) {
	check();
	return props.getProperty(key);
    }

    /**
     * Sets a security property.
     *
     * @param key the name of the property to be set.
     *
     * @param datum the value of the property to be set.
     */
    public static void setProperty(String key, String datum) {
	check();
	props.put(key, datum);
    }

    private static void check() {
	
	SecurityManager security = System.getSecurityManager();
	if (security != null) {
	    security.checkSecurityAccess("java");
	}
    }
    
    /**
     * Print an error message that may be significant to a user.
     */
    static void error(String msg) {
	if (debug) {
	    System.err.println(msg);
	}
    }

    /**
     * Print an error message that may be significant to a user.
     */
    static void error(String msg, Throwable t) {
	error(msg);
	if (debug) {
	    t.printStackTrace();
	}
    }
	
    /**
     * Print an debugging message that may be significant to a developer.
     */
    static void debug(String msg) {
	if (debug) {
	    System.err.println(msg);
	}
    }

    /**
     * Print an debugging message that may be significant to a developer.
     */
    static void debug(String msg, Throwable t) {
	if (debug) {
	    t.printStackTrace();
	    System.err.println(msg);
	}
    }

    
	
}
	

