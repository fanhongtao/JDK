/*
 * @(#)Security.java	1.2 00/01/12
 *
 * Copyright 1996-2000 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */

package java.security;

import java.lang.reflect.*;
import java.util.*;
import java.io.*;

/**
 * <p>This class centralizes all security properties and common security
 * methods. One of its primary uses is to manage providers.
 *
 * @author Benjamin Renaud
 * @version 1.89, 99/02/03
 */

public final class Security {

    // Do providers need to be reloaded?
    private static boolean reloadProviders = false;

    /* Are we debugging? -- for developers */
    static final boolean debug = false;

    /* Are we displaying errors? -- for users */
    static final boolean error = true;

    /* The java.security properties */
    private static Properties props; 

    /* A vector of providers, in order of priority */
    private static Vector providers;

    // Where we cache provider properties
    private static Hashtable providerPropertiesCache;

    // Where we cache engine provider properties
    private static Hashtable engineCache;

    // An element in the cache
    private static class ProviderProperty {
	String className;
	Provider provider;
    }

    static {
	// doPrivileged here because there are multiple
	// things in initialize that might require privs.
	// (the FileInputStream call and the File.exists call,
	// the securityPropFile call, etc)
	AccessController.doPrivileged(new PrivilegedAction() {
	    public Object run() { 
		initialize();
		return null;
	    }
	});
    }
    
    private static void initialize() {
	props = new Properties();
	providers = new Vector();
	providerPropertiesCache = new Hashtable();
	engineCache = new Hashtable();

	File propFile = securityPropFile("java.security");
	if (!propFile.exists()) {
	    System.err.println
		("security properties not found. using defaults.");
	    initializeStatic();
	} else {
	    try {
		FileInputStream fis = new FileInputStream(propFile);
		InputStream is = new BufferedInputStream(fis);
		props.load(is);
		is.close();
	    } catch (IOException e) {
		error("could not load security properties file from " +
		      propFile + ". using defaults.");
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
    private static synchronized void loadProviders() {

	int i = 1;
	sun.misc.Launcher l = sun.misc.Launcher.getLauncher();

	while (true) {
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
		} else if (l == null) {
		    reloadProviders = true;
		}
	    }
	}
    }

    /*
     * Reload the providers (provided as extensions) that could not be loaded 
     * (because there was no system class loader available).when this class
     * was initialized.
     */
    private static synchronized void reloadProviders() {
	if (reloadProviders) {
	    sun.misc.Launcher l = sun.misc.Launcher.getLauncher();
	    if (l != null) {
		reloadProviders = false;
		providers.removeAllElements();
		int i = 1;
		while (true) {
		    final String name =
			props.getProperty("security.provider." + i++);
		    if (name == null) {
			break;
		    } else {
			Provider prov =
			    (Provider)AccessController.doPrivileged(
   				            new PrivilegedAction() {
				public Object run() { 
				    return Provider.loadProvider(name);
				}
			    });
			if (prov != null) {
			    providers.addElement(prov);
			}
		    }
		}
		// empty provider-property cache
		providerPropertiesCache.clear();
		engineCache.clear();
	    }
	}
    }

    private static File securityPropFile(String filename) {
	// maybe check for a system property which will specify where to
	// look. Someday.
	String sep = File.separator;
	return new File(System.getProperty("java.home") + sep + "lib" + sep + 
			"security" + sep + filename);
    }

    /**
     * Looks up providers, and returns the property (and its associated
     * provider) mapping the key, if any.
     * The order in which the providers are looked up is the
     * provider-preference order, as specificed in the security
     * properties file.
     */
    private static ProviderProperty getProviderProperty(String key) {
	ProviderProperty entry
	    = (ProviderProperty)providerPropertiesCache.get(key);
	if (entry != null) {
	    return entry;
	}

	for (int i = 0; i < providers.size(); i++) {

	    String matchKey = null;
	    Provider prov = (Provider)providers.elementAt(i);	    
	    String prop = prov.getProperty(key);

	    if (prop == null) {
		// Is there a match if we do a case-insensitive property name
		// comparison? Let's try ...
		for (Enumeration enum = prov.keys();
		     enum.hasMoreElements() && prop==null; ) {
		    matchKey = (String)enum.nextElement();
		    if (key.equalsIgnoreCase(matchKey)) {
			prop = prov.getProperty(matchKey);
			break;
		    }
		}
	    }

	    if (prop != null) {
		ProviderProperty newEntry = new ProviderProperty();
		newEntry.className = prop;
		newEntry.provider = prov;
		providerPropertiesCache.put(key, newEntry);
		if (matchKey != null) {
		    // Store the property value in the cache under the exact
		    // property name, as specified by the provider
		    providerPropertiesCache.put(matchKey, newEntry);
		}
		return newEntry;
	    }
	}

	return entry;
    }

    /**
     * Returns the property (if any) mapping the key for the given provider.
     */
    private static String getProviderProperty(String key, Provider provider) {
	String prop = provider.getProperty(key);
	if (prop == null) {
	    // Is there a match if we do a case-insensitive property name
	    // comparison? Let's try ...
	    for (Enumeration enum = provider.keys();
		 enum.hasMoreElements() && prop==null; ) {
		String matchKey = (String)enum.nextElement();
		if (key.equalsIgnoreCase(matchKey)) {
		    prop = provider.getProperty(matchKey);
		    break;
		}
	    }
	}
	return prop;
    }

    /**
     * We always map names to standard names
     */
    private static String getStandardName(String alias, String engineType,
        Provider prov) {
	return getProviderProperty("Alg.Alias." + engineType + "." + alias,
				   prov);
    }

    /** 
     * Gets a specified property for an algorithm. The algorithm name
     * should be a standard name. See Appendix A in the <a href=
     * "../../../guide/security/CryptoSpec.html#AppA">
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
     *
     * @deprecated This method used to return the value of a proprietary
     * property in the master file of the "SUN" Cryptographic Service
     * Provider in order to determine how to parse algorithm-specific
     * parameters. Use the new provider-based and algorithm-independent
     * <code>AlgorithmParameters</code> and <code>KeyFactory</code> engine
     * classes (introduced in JDK 1.2) instead.
     */
    public static String getAlgorithmProperty(String algName,
					      String propName) {
	reloadProviders();
	ProviderProperty entry = getProviderProperty("Alg." + propName
						     + "." + algName);
	if (entry != null) {
	    return entry.className;
	} else {
	    return null;
	}
    }

    /*
     * Lookup the algorithm in our list of providers. Process
     * each provider in priority order one at a time looking for
     * either the direct engine property or a matching alias.
     */
    private static ProviderProperty getEngineClassName(String algName,
						       String engineType)
        throws NoSuchAlgorithmException
    {
	ProviderProperty pp;
	String key = engineType;

	if (algName != null)
	    key += "." + algName;
	pp = (ProviderProperty)engineCache.get(key);
	if (pp != null)
	    return pp;

	synchronized (Security.class) {
	    for (int i = 0; i < providers.size(); i++) {
		Provider prov = (Provider)providers.elementAt(i);
		try {
		    pp = getEngineClassName(algName, prov.getName(),
					    engineType);
		} catch (NoSuchAlgorithmException e) {
		    continue;
		} catch (NoSuchProviderException e) {
		    // can't happen except for sync failures
		    continue;
		}

		/* Cache it */
		engineCache.put(key, pp);
		return pp;
	    }
	}

	throw new NoSuchAlgorithmException(engineType + " not available");
    }


    private static ProviderProperty getEngineClassName(String algName,
						       String provider, 
						       String engineType) 
	throws NoSuchAlgorithmException, NoSuchProviderException
    {
	if (provider == null) {
	    return getEngineClassName(algName, engineType);
	}

	// check if the provider is installed
	Provider prov = getProvider(provider);
	if (prov == null) {
	    throw new NoSuchProviderException("no such provider: " +
					      provider);
	}

	String key;
	if (engineType.equalsIgnoreCase("SecureRandom") && algName == null)
	    key = engineType;
	else
	    key = engineType + "." + algName;
	
	String className = getProviderProperty(key, prov);
	if (className == null) {
	    if (engineType.equalsIgnoreCase("SecureRandom") &&
		algName == null)
		throw new NoSuchAlgorithmException
		    ("SecureRandom not available for provider " + provider);
	    else {
		// try algName as alias name
		String stdName = getStandardName(algName, engineType, prov);
		if (stdName != null) key = engineType + "." + stdName;
		if ((stdName == null)
		    || (className = getProviderProperty(key, prov)) == null)
		    throw new NoSuchAlgorithmException("no such algorithm: " +
						       algName
						       + " for provider " +
						       provider);
	    }
	}
	
	ProviderProperty entry = new ProviderProperty();
	entry.className = className;
	entry.provider = prov;

	return entry;
    }

    /**
     * Adds a new provider, at a specified position. The position is
     * the preference order in which providers are searched for
     * requested algorithms. Note that it is not guaranteed that this
     * preference will be respected. The position is 1-based, that is,
     * 1 is most preferred, followed by 2, and so on.
     * 
     * <p>If the given provider is installed at the requested position,
     * the provider that used to be at that position, and all providers
     * with a position greater than <code>position</code>, are shifted up
     * one position (towards the end of the list of installed providers).
     * 
     * <p>A provider cannot be added if it is already installed.
     * 
     * <p>First, if there is a security manager, its
     * <code>checkSecurityAccess</code> 
     * method is called with the string
     * <code>"insertProvider."+provider.getName()</code> 
     * to see if it's ok to add a new provider. 
     * If the default implementation of <code>checkSecurityAccess</code> 
     * is used (i.e., that method is not overriden), then this will result in
     * a call to the security manager's <code>checkPermission</code> method
     * with a
     * <code>SecurityPermission("insertProvider."+provider.getName())</code>
     * permission.
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
     * @throws  SecurityException
     *          if a security manager exists and its <code>{@link
     *          java.lang.SecurityManager#checkSecurityAccess}</code> method
     *          denies access to add a new provider
     *
     * @see #getProvider
     * @see #removeProvider 
     * @see java.security.SecurityPermission
     */
    public static synchronized int insertProviderAt(Provider provider,
						    int position) {
	reloadProviders();

	check("insertProvider."+provider.getName());

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

	// empty provider-property cache
	providerPropertiesCache.clear();
	engineCache.clear();
	
	return position;
    }

    /**
     * Adds a provider to the next position available.
     *
     * <p>First, if there is a security manager, its
     * <code>checkSecurityAccess</code> 
     * method is called with the string
     * <code>"insertProvider."+provider.getName()</code> 
     * to see if it's ok to add a new provider. 
     * If the default implementation of <code>checkSecurityAccess</code> 
     * is used (i.e., that method is not overriden), then this will result in
     * a call to the security manager's <code>checkPermission</code> method
     * with a
     * <code>SecurityPermission("insertProvider."+provider.getName())</code>
     * permission.
     * 
     * @param provider the provider to be added.
     *
     * @return the preference position in which the provider was 
     * added, or -1 if the provider was not added because it is
     * already installed.
     *
     * @throws  SecurityException
     *          if a security manager exists and its <code>{@link
     *          java.lang.SecurityManager#checkSecurityAccess}</code> method
     *          denies access to add a new provider
     * 
     * @see #getProvider
     * @see #removeProvider
     * @see java.security.SecurityPermission
     */
    public static int addProvider(Provider provider) {
	return insertProviderAt(provider, providers.size() + 1);
    }

    /**
     * Removes the provider with the specified name.
     *
     * <p>When the specified provider is removed, all providers located
     * at a position greater than where the specified provider was are shifted
     * down one position (towards the head of the list of installed
     * providers).
     *
     * <p>This method returns silently if the provider is not installed.
     * 
     * <p>First, if there is a security manager, its
     * <code>checkSecurityAccess</code> 
     * method is called with the string <code>"removeProvider."+name</code> 
     * to see if it's ok to remove the provider. 
     * If the default implementation of <code>checkSecurityAccess</code> 
     * is used (i.e., that method is not overriden), then this will result in
     * a call to the security manager's <code>checkPermission</code> method
     * with a <code>SecurityPermission("removeProvider."+name)</code>
     * permission.
     *
     * @param name the name of the provider to remove.
     *
     * @throws  SecurityException
     *          if a security manager exists and its <code>{@link
     *          java.lang.SecurityManager#checkSecurityAccess}</code> method
     *          denies
     *          access to remove the provider
     *
     * @see #getProvider
     * @see #addProvider
     */
    public static synchronized void removeProvider(String name) {
	reloadProviders();
	check("removeProvider."+name);
	Provider provider = getProvider(name);
	if (provider != null) {
	    for (Iterator i=providers.iterator(); i.hasNext(); )
		if (i.next()==provider)
		    i.remove();

	    // empty provider-property cache
	    providerPropertiesCache.clear();
	    engineCache.clear();
	}
    }
    
    /**
     * Returns an array containing all the installed providers. The order of
     * the providers in the array is their preference order.
     * 
     * @return an array of all the installed providers.
     */
    public static synchronized Provider[] getProviders() {
	reloadProviders();
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
    public static synchronized Provider getProvider(String name) {
	reloadProviders();
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
     * Returns an array of objects: the first object in the array is
     * an instance of an implementation of the requested algorithm
     * and type, and the second object in the array identifies the provider
     * of that implementation.
     * The <code>provider</code> argument can be null, in which case all
     * configured providers will be searched in order of preference.
     */
    static Object[] getImpl(String algorithm, String type, String provider)
	throws NoSuchAlgorithmException, NoSuchProviderException
    {
	reloadProviders();

	ProviderProperty pp = getEngineClassName(algorithm, provider, type);
	String className = pp.className;

	try {
	    // java.security.<type>.Spi is a system class, therefore
	    // Class.forName() always works
	    Class typeClass;
	    if (type.equals("CertificateFactory")) {
		typeClass = Class.forName("java.security.cert." + type
					  + "Spi");
	    } else {
		typeClass = Class.forName("java.security." + type + "Spi");
	    }

	    // Load the implementation class using the same class loader that
	    // was used to load the associated provider.
	    // In order to get the class loader of a class, the caller's class
	    // loader must be the same as or an ancestor of the class loader
	    // being returned.
	    // Since java.security.Security is a system class, it can get the
	    // class loader of any class (the system class loader is an
	    // ancestor of all class loaders).
	    ClassLoader cl = pp.provider.getClass().getClassLoader();
	    Class implClass;
	    if (cl != null) {
		implClass = cl.loadClass(className);
	    } else {
		implClass = Class.forName(className);
	    }

	    if (checkSuperclass(implClass, typeClass)) {
		Object obj = implClass.newInstance();
		return new Object[] { obj, pp.provider };
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
					       ") cannot be instantiated.\n"+ 
					       e.getMessage());
	} catch (IllegalAccessException e) {
	    throw new NoSuchAlgorithmException("class " + className + 
					       " configured for " + type +
					       "(provider: " + provider +
					       ") cannot be accessed.\n" + 
					       e.getMessage());
	} catch (SecurityException e) {
	    throw new NoSuchAlgorithmException("class " + className + 
					       " configured for " + type +
					       "(provider: " + provider +
					       ") cannot be accessed.\n" + 
					       e.getMessage());
	}
    }

    /**
     * Gets a security property value.
     *
     * <p>First, if there is a security manager, its
     * <code>checkPermission</code>  method is called with a 
     * <code>java.security.SecurityPermission("getProperty."+key)</code>
     * permission to see if it's ok to retrieve the specified
     * security property value.. 
     *
     * @param key the key of the property being retrieved.
     *
     * @return the value of the security property corresponding to key.
     *
     * @throws  SecurityException
     *          if a security manager exists and its <code>{@link
     *          java.lang.SecurityManager#checkPermission}</code> method
     *          denies
     *          access to retrieve the specified security property value
     * 
     * @see java.security.SecurityPermission
     */
    public static String getProperty(String key) {
	SecurityManager sm = System.getSecurityManager();
	if (sm != null) {
	    sm.checkPermission(new SecurityPermission("getProperty."+
						      key));
	}
	return props.getProperty(key);
    }

    /**
     * Sets a security property value.
     *
     * <p>First, if there is a security manager, its
     * <code>checkPermission</code> method is called with a 
     * <code>java.security.SecurityPermission("setProperty."+key)</code>
     * permission to see if it's ok to set the specified
     * security property value.
     *
     * @param key the name of the property to be set.
     *
     * @param datum the value of the property to be set.
     *
     * @throws  SecurityException
     *          if a security manager exists and its <code>{@link
     *          java.lang.SecurityManager#checkPermission}</code> method
     *          denies access to set the specified security property value
     * 
     * @see java.security.SecurityPermission
     */
    public static void setProperty(String key, String datum) {
	check("setProperty."+key);
	props.put(key, datum);
    }

    private static void check(String directive) {	
	SecurityManager security = System.getSecurityManager();
	if (security != null) {
	    security.checkSecurityAccess(directive);
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
	

