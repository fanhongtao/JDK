/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.security;

import java.lang.reflect.*;
import java.util.*;
import java.io.*;
import java.security.InvalidParameterException;

/**
 * <p>This class centralizes all security properties and common security
 * methods. One of its primary uses is to manage providers.
 *
 * @author Benjamin Renaud
 * @version 1.102, 02/06/02
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

    // Where we cache search results
    private static Hashtable searchResultsCache;

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
	searchResultsCache = new Hashtable(5);

	File propFile = securityPropFile("java.security");
	if (!propFile.exists()) {
	    System.err.println
		("security properties not found. using defaults.");
	    initializeStatic();
	} else {
	    try {
		FileInputStream is = new FileInputStream(propFile);
		// Inputstream has been buffered in Properties class
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
		Provider prov = Provider.loadProvider(name.trim());
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
				    return Provider.loadProvider(name.trim());
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
		searchResultsCache.clear();
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
     * classes (introduced in the Java 2 platform) instead.
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

	throw new NoSuchAlgorithmException(algName.toUpperCase() + " " +
					   engineType + " not available");
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
	searchResultsCache.clear();
		
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
	    searchResultsCache.clear();
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

    /**
    * Returns an array containing all installed providers that satisfy the
    * specified selection criterion, or null if no such providers have been
    * installed. The returned providers are ordered
    * according to their <a href=
    * "#insertProviderAt(java.security.Provider, int)">preference order</a>. 
    * 
    * <p> A cryptographic service is always associated with a particular
    * algorithm or type. For example, a digital signature service is
    * always associated with a particular algorithm (e.g., DSA),
    * and a CertificateFactory service is always associated with
    * a particular certificate type (e.g., X.509).
    *
    * <p>The selection criterion must be specified in one of the following two formats:
    * <ul>
    * <li> <i>&lt;crypto_service>.&lt;algorithm_or_type></i> <p> The
    * cryptographic service name must not contain any dots.
    * <p> A 
    * provider satisfies the specified selection criterion iff the provider implements the 
    * specified algorithm or type for the specified cryptographic service.
    * <p> For example, "CertificateFactory.X.509" 
    * would be satisfied by any provider that supplied
    * a CertificateFactory implementation for X.509 certificates.
    * <li> <i>&lt;crypto_service>.&lt;algorithm_or_type> &lt;attribute_name>:&lt attribute_value></i>
    * <p> The cryptographic service name must not contain any dots. There
     * must be one or more space charaters between the the <i>&lt;algorithm_or_type></i>
     * and the <i>&lt;attribute_name></i>.
    * <p> A provider satisfies this selection criterion iff the
    * provider implements the specified algorithm or type for the specified 
    * cryptographic service and its implementation meets the
    * constraint expressed by the specified attribute name/value pair.
    * <p> For example, "Signature.SHA1withDSA KeySize:1024" would be
    * satisfied by any provider that implemented
    * the SHA1withDSA signature algorithm with a keysize of 1024 (or larger).
    *  
    * </ul>
    *
    * <p> See Appendix A in the <a href=
    * "../../../guide/security/CryptoSpec.html#AppA">
    * Java Cryptogaphy Architecture API Specification &amp; Reference </a>
    * for information about standard cryptographic service names, standard
    * algorithm names and standard attribute names.
    *
    * @param filter the criterion for selecting
    * providers. The filter is case-insensitive.
    *
    * @return all the installed providers that satisfy the selection
    * criterion, or null if no such providers have been installed.
    *
    * @throws InvalidParameterException
    *         if the filter is not in the required format
    *
    * @ see #getProviders(java.util.Map)
    */
    public static Provider[] getProviders(String filter) {
	String key = null;
	String value = null;
	int index = filter.indexOf(':');

	if (index == -1) {
	    key = new String(filter);
	    value = "";
	} else {
	    key = filter.substring(0, index);
	    value = filter.substring(index + 1);
	}

	Hashtable hashtableFilter = new Hashtable(1);
	hashtableFilter.put(key, value);

	return (getProviders(hashtableFilter));
    }

    /**
     * Returns an array containing all installed providers that satisfy the specified
     * selection criteria, or null if no such providers have been installed. 
     * The returned providers are ordered
     * according to their <a href=
     * "#insertProviderAt(java.security.Provider, int)">preference order</a>. 
     * 
     * <p>The selection criteria are represented by a map.
     * Each map entry represents a selection criterion. 
     * A provider is selected iff it satisfies all selection
     * criteria. The key for any entry in such a map must be in one of the
     * following two formats:
     * <ul>
     * <li> <i>&lt;crypto_service>.&lt;algorithm_or_type></i>
     * <p> The cryptographic service name must not contain any dots.
     * <p> The value associated with the key must be an empty string.
     * <p> A provider
     * satisfies this selection criterion iff the provider implements the 
     * specified algorithm or type for the specified cryptographic service.
     * <li>  <i>&lt;crypto_service>.&lt;algorithm_or_type> &lt;attribute_name></i>
     * <p> The cryptographic service name must not contain any dots. There
     * must be one or more space charaters between the <i>&lt;algorithm_or_type></i>
     * and the <i>&lt;attribute_name></i>.
     * <p> The value associated with the key must be a non-empty string.
     * A provider satisfies this selection criterion iff the
     * provider implements the specified algorithm or type for the specified 
     * cryptographic service and its implementation meets the
     * constraint expressed by the specified attribute name/value pair. 
     * </ul>
     *
     * <p> See Appendix A in the <a href=
     * "../../../guide/security/CryptoSpec.html#AppA">
     * Java Cryptogaphy Architecture API Specification &amp; Reference </a>
     * for information about standard cryptographic service names, standard
     * algorithm names and standard attribute names.
     *
     * @param filter the criteria for selecting
     * providers. The filter is case-insensitive.
     *
     * @return all the installed providers that satisfy the selection
     * criteria, or null if no such providers have been installed. 
     *
     * @throws InvalidParameterException
     *         if the filter is not in the required format
     *
     * @ see #getProviders(java.lang.String)
     */
    public static Provider[] getProviders(Map filter) {
	// Get all installed providers first.
	// Then only return those providers who satisfy the selection criteria.
	Provider[] allProviders = Security.getProviders();
	Set keySet = filter.keySet();
	HashSet candidates = new HashSet(5);

	// Returns all installed providers
	// if the selection criteria is null.
	if ((keySet == null) || (allProviders == null)) {
	    return allProviders;
	}
	
	boolean firstSearch = true;

	// For each selection criterion, remove providers
	// which don't satisfy the criterion from the candidate set.
	for (Iterator ite = keySet.iterator(); ite.hasNext(); ) {
	    String key = (String)ite.next();
	    String value = (String)filter.get(key);
	    
	    HashSet newCandidates = getAllQualifyingCandidates(key, value, 
							       allProviders);
	    if (firstSearch) {
		candidates = newCandidates;
		firstSearch = false;
	    }

	    if ((newCandidates != null) && !newCandidates.isEmpty()) {
		// For each provider in the candidates set, if it
		// isn't in the newCandidate set, we should remove
		// it from the candidate set.
		for (Iterator cansIte = candidates.iterator();
		     cansIte.hasNext(); ) {
		    Provider prov = (Provider)cansIte.next();
		    if (!newCandidates.contains(prov)) {
			cansIte.remove();
		    }
		}
	    } else {
		candidates = null;
		break;
	    }
	}

	if ((candidates == null) || (candidates.isEmpty()))
	    return null;

	Object[] candidatesArray = candidates.toArray();
	Provider[] result = new Provider[candidatesArray.length];

	for (int i = 0; i < result.length; i++) {
	    result[i] = (Provider)candidatesArray[i];
	}
	
	return result;
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
	String name = props.getProperty(key);
	if (name != null)
	    name = name.trim();	// could be a class name with trailing ws
	return name;
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
	invalidateSMCache(key);  /* See below. */
    }

    /*
     * Implementation detail:  If the property we just set in
     * setProperty() was either "package.access" or
     * "package.definition", we need to signal to the SecurityManager
     * class that the value has just changed, and that it should
     * invalidate it's local cache values.
     *
     * Rather than create a new API entry for this function,
     * we use reflection to set a private variable.
     */
    private static void invalidateSMCache(String key) {
	
	final boolean pa = key.equals("package.access");
	final boolean pd = key.equals("package.definition");

	if (pa || pd) {
	    AccessController.doPrivileged(new PrivilegedAction() {
		public Object run() {
		    try {
			/* Get the class via the bootstrap class loader. */
			Class cl = Class.forName(
			    "java.lang.SecurityManager", false, null);
			Field f = null;
			boolean accessible = false;

			if (pa) {
			    f = cl.getDeclaredField("packageAccessValid");
			    accessible = f.isAccessible();
			    f.setAccessible(true);
			} else {
			    f = cl.getDeclaredField("packageDefinitionValid");
			    accessible = f.isAccessible();
			    f.setAccessible(true);
			}
			f.setBoolean(f, false);
			f.setAccessible(accessible);
		    }
		    catch (Exception e1) {
			/* If we couldn't get the class, it hasn't
			 * been loaded yet.  If there is no such
			 * field, we shouldn't try to set it.  There
			 * shouldn't be a security execption, as we
			 * are loaded by boot class loader, and we
			 * are inside a doPrivileged() here.
			 *
			 * NOOP: don't do anything...
			 */
		    }
		    return null;
		}  /* run */
	    });  /* PrivilegedAction */
	}  /* if */
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

    /*
    * Returns all providers who satisfy the specified
    * criterion.
    */
    private static HashSet getAllQualifyingCandidates(String filterKey,
						 String filterValue,
						 Provider[] allProviders) {
	String[] filterComponents = getFilterComponents(filterKey,
							filterValue);

	// The first component is the service name.
	// The second is the algorithm name.
	// If the third isn't null, that is the attrinute name.
	String serviceName = filterComponents[0];
	String algName = filterComponents[1];
	String attrName = filterComponents[2];

	// Check whether we can find anything in the cache
	String cacheKey = serviceName + '.' + algName;
	HashSet candidates = (HashSet)searchResultsCache.get(cacheKey);

	// If there is no entry for the cacheKey in the cache,
	// let's build an entry for it first.
	HashSet forCache = getProvidersNotUsingCache(serviceName,
						     algName,
						     null,
						     null,
						     null,
						     allProviders);

	if ((forCache == null) || (forCache.isEmpty())) {
	    return null;
	} else {
	    searchResultsCache.put(cacheKey, forCache);
	    if (attrName == null) {
		return forCache;
	    }
	    return getProvidersNotUsingCache(serviceName, algName, attrName,
					     filterValue, candidates, 
					     allProviders);
	}
    }
	
    private static HashSet getProvidersNotUsingCache(String serviceName,
						     String algName,
						     String attrName,
						     String filterValue,
						     HashSet candidates,
						     Provider[] allProviders) {
	if ((attrName != null) && (candidates != null) &&
	    (!candidates.isEmpty())) {
	    for (Iterator cansIte = candidates.iterator();
		 cansIte.hasNext(); ) {
		Provider prov = (Provider)cansIte.next();
		if (!isCriterionSatisfied(prov, serviceName, algName, 
					  attrName, filterValue)) {
		    cansIte.remove();
		}
	    }
	}

	if ((candidates == null) || (candidates.isEmpty())) {
	    if (candidates == null)
		candidates = new HashSet(5);
	    for (int i = 0; i < allProviders.length; i++) {
		if (isCriterionSatisfied(allProviders[i], serviceName, 
					 algName,
					 attrName, filterValue)) {
		    candidates.add(allProviders[i]);
		}
	    }
	}

	return candidates;
    }

    /*
     * Returns true if the given provider satisfies
     * the selection criterion key:value.
     */
    private static boolean isCriterionSatisfied(Provider prov, 
						String serviceName,
						String algName,
						String attrName,
						String filterValue) {
	String key = serviceName + '.' + algName;

	if (attrName != null) {
	    key += ' ' + attrName;
	}
      	// Check whether the provider has a property
	// whose key is the same as the given key.
	String propValue = getProviderProperty(key, prov);

	if (propValue == null) {
	    // Check whether we have an alias instead
	    // of a standard name in the key.
	    String standardName = getProviderProperty("Alg.Alias." + 
						      serviceName + "." +
						      algName,
						      prov);
	    if (standardName != null) {
		key = serviceName + "." + standardName;

		if (attrName != null) {
		    key += ' ' + attrName;
		}

		propValue = getProviderProperty(key, prov);
	    }
	    
	    if (propValue == null) {
		// The provider doesn't have the given
		// key in its property list.
		return false;
	    }
	}

	// If the key is in the format of:
	// <crypto_service>.<algorithm_or_type>,
        // there is no need to check the value.
	
	if (attrName == null) {
	    return true;
	}

	// If we get here, the key must be in the
	// format of <crypto_service>.<algorithm_or_provider> <attribute_name>.
	if (isStandardAttr(attrName)) {
	    return isConstraintSatisfied(attrName, filterValue, propValue);
	} else {
	    return filterValue.equalsIgnoreCase(propValue);
	}
    }
	    
    /*
     * Returns true if the attribute is a standard attribute;
     * otherwise, returns false.
     */
    private static boolean isStandardAttr(String attribute) {
	// For now, we just have two standard attributes: KeySize and ImplementedIn.
	if (attribute.equalsIgnoreCase("KeySize"))
	    return true;
	
	if (attribute.equalsIgnoreCase("ImplementedIn"))
	    return true;

	return false;
    }

    /*
     * Returns true if the requested attribute value is supported;
     * otherwise, returns false.
     */
    private static boolean isConstraintSatisfied(String attribute,
						 String value,
						 String prop) {
	// For KeySize, prop is the max key size the
	// provider supports for a specific <crypto_service>.<algorithm>.
	if (attribute.equalsIgnoreCase("KeySize")) {
	    int requestedSize = (new Integer(value)).intValue();
	    int maxSize = (new Integer(prop)).intValue();
	    if (requestedSize <= maxSize) {
		return true;
	    } else {
		return false;
	    }
	}

	// For Type, prop is the type of the implementation
	// for a specific <crypto service>.<algorithm>.
	if (attribute.equalsIgnoreCase("ImplementedIn")) {
	    return value.equalsIgnoreCase(prop);
	}

	return false;
    }

    static String[] getFilterComponents(String filterKey, String filterValue) {
	int algIndex = filterKey.indexOf('.');

	if (algIndex < 0) {
	    // There must be a dot in the filter, and the dot
	    // shouldn't be at the beginning of this string.
	    throw new InvalidParameterException("Invalid filter");
	}

	String serviceName = filterKey.substring(0, algIndex);
	String algName = null;
	String attrName = null;

	if (filterValue.length() == 0) {
	    // The filterValue is an empty string. So the filterKey 
	    // should be in the format of <crypto_service>.<algorithm_or_type>.
	    algName = filterKey.substring(algIndex + 1).trim();
	    if (algName.length() == 0) {
		// There must be a algorithm or type name.
		throw new InvalidParameterException("Invalid filter");
	    }
	} else {	
	    // The filterValue is a non-empty string. So the filterKey must be
	    // in the format of
	    // <crypto_service>.<algorithm_or_type> <attribute_name>
	    int attrIndex = filterKey.indexOf(' ');

	    if (attrIndex == -1) {
		// There is no attribute name in the filter.
		throw new InvalidParameterException("Invalid filter");
	    } else {
		attrName = filterKey.substring(attrIndex + 1).trim();
		if (attrName.length() == 0) {
		    // There is no attribute name in the filter.
		    throw new InvalidParameterException("Invalid filter");
		}
	    }
	
	    // There must be an algorithm name in the filter.
	    if ((attrIndex < algIndex) ||
		(algIndex == attrIndex - 1)) {
		throw new InvalidParameterException("Invalid filter");
	    } else {
		algName = filterKey.substring(algIndex + 1, attrIndex);
	    }
	}

	String[] result = new String[3];
	result[0] = serviceName;
	result[1] = algName;
	result[2] = attrName;

	return result;
    }
}
	

