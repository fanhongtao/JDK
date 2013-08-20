/*
 * @(#)Provider.java	1.61 04/05/05
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.security;

import java.io.*;
import java.util.*;
import java.lang.ref.*;
import java.lang.reflect.*;

import java.security.cert.CertStoreParameters;

/**
 * This class represents a "provider" for the
 * Java Security API, where a provider implements some or all parts of
 * Java Security. Services that a provider may implement include:
 *
 * <ul>
 *
 * <li>Algorithms (such as DSA, RSA, MD5 or SHA-1).
 *
 * <li>Key generation, conversion, and management facilities (such as for
 * algorithm-specific keys).
 *
 *</ul>
 *
 * <p>Each provider has a name and a version number, and is configured
 * in each runtime it is installed in.
 *
 * <p>See <a href =
 * "../../../guide/security/CryptoSpec.html#Provider">The Provider Class</a>
 * in the "Java Cryptography Architecture API Specification &amp; Reference"
 * for information about how a particular type of provider, the
 * cryptographic service provider, works and is installed. However,
 * please note that a provider can be used to implement any security
 * service in Java that uses a pluggable architecture with a choice
 * of implementations that fit underneath. 
 *
 * <p>Some provider implementations may encounter unrecoverable internal
 * errors during their operation, for example a failure to communicate with a 
 * security token. A {@link ProviderException} should be used to indicate 
 * such errors.
 *
 * <p>The service type <code>Provider</code> is reserved for use by the
 * security framework. Services of this type cannot be added, removed,
 * or modified by applications.
 * The following attributes are automatically placed in each Provider object:
 * <table cellspacing=4>
 * <tr><th>Name</th><th>Value</th>
 * <tr><td><code>Provider.id name</code></td>
  *    <td><code>String.valueOf(provider.getName())</code></td>
 * <tr><td><code>Provider.id version</code></td>
 *     <td><code>String.valueOf(provider.getVersion())</code></td>
 * <tr><td><code>Provider.id info</code></td>
       <td><code>String.valueOf(provider.getInfo())</code></td>
 * <tr><td><code>Provider.id className</code></td>
 *     <td><code>provider.getClass().getName()</code></td>
 * </table>
 *
 * @version 1.61, 05/05/04
 * @author Benjamin Renaud
 * @author Andreas Sterbenz
 */
public abstract class Provider extends Properties {

    // Declare serialVersionUID to be compatible with JDK1.1
    static final long serialVersionUID = -4298000515446427739L;

    private static final sun.security.util.Debug debug =
        sun.security.util.Debug.getInstance
        ("provider", "Provider");

    /**
     * The provider name.
     *
     * @serial
     */
    private String name;

    /**
     * A description of the provider and its services.
     *
     * @serial
     */
    private String info;

    /**
     * The provider version number.
     *
     * @serial
     */
    private double version;


    private transient Set entrySet = null;
    private transient int entrySetCallCount = 0;


    /**
     * Constructs a provider with the specified name, version number,
     * and information.
     *
     * @param name the provider name.
     *
     * @param version the provider version number.
     *
     * @param info a description of the provider and its services.
     */
    protected Provider(String name, double version, String info) {
	this.name = name;
	this.version = version;
	this.info = info;
	putId();
    }

    /**
     * Returns the name of this provider.
     *
     * @return the name of this provider.
     */
    public String getName() {
	return name;
    }

    /**
     * Returns the version number for this provider.
     *
     * @return the version number for this provider.
     */
    public double getVersion() {
	return version;
    }

    /**
     * Returns a human-readable description of the provider and its
     * services.  This may return an HTML page, with relevant links.
     *
     * @return a description of the provider and its services.
     */
    public String getInfo() {
	return info;
    }

    /**
     * Returns a string with the name and the version number
     * of this provider.
     *
     * @return the string with the name and the version number
     * for this provider.
     */
    public String toString() {
	return name + " version " + version;
    }

    /*
     * override the following methods to ensure that provider
     * information can only be changed if the caller has the appropriate
     * permissions.
     */

    /**
     * Clears this provider so that it no longer contains the properties
     * used to look up facilities implemented by the provider.
     * 
     * <p>First, if there is a security manager, its 
     * <code>checkSecurityAccess</code> method is called with the string 
     * <code>"clearProviderProperties."+name</code> (where <code>name</code> 
     * is the provider name) to see if it's ok to clear this provider.
     * If the default implementation of <code>checkSecurityAccess</code> 
     * is used (that is, that method is not overriden), then this results in
     * a call to the security manager's <code>checkPermission</code> method 
     * with a <code>SecurityPermission("clearProviderProperties."+name)</code>
     * permission.
     *
     * @throws  SecurityException
     *          if a security manager exists and its <code>{@link
     *          java.lang.SecurityManager#checkSecurityAccess}</code> method 
     *          denies access to clear this provider
     *
     * @since 1.2
     */
    public synchronized void clear() {
	check("clearProviderProperties."+name);
	if (debug != null) {
	    debug.println("Remove " + name + " provider properties");
	}
	implClear();
    }

    /**
     * Reads a property list (key and element pairs) from the input stream.
     *
     * @param inStream   the input stream.
     * @exception  IOException  if an error occurred when reading from the
     *               input stream.
     * @see java.util.Properties#load
     */
    public synchronized void load(InputStream inStream) throws IOException {
	check("putProviderProperty."+name);
        if (debug != null) {
            debug.println("Load " + name + " provider properties");
        }
	Properties tempProperties = new Properties();
	tempProperties.load(inStream);
	implPutAll(tempProperties);
    }

    /**
     * Copies all of the mappings from the specified Map to this provider.
     * These mappings will replace any properties that this provider had 
     * for any of the keys currently in the specified Map. 
     *
     * @since 1.2
     */
    public synchronized void putAll(Map<?,?> t) {
	check("putProviderProperty."+name);
        if (debug != null) {
            debug.println("Put all " + name + " provider properties");
        }
	implPutAll(t);
    }
    
    /**
     * Returns an unmodifiable Set view of the property entries contained 
     * in this Provider.
     *
     * @see   java.util.Map.Entry
     * @since 1.2
     */
    public synchronized Set<Map.Entry<Object,Object>> entrySet() {
	if (entrySet == null) {
	    if (entrySetCallCount++ == 0)  // Initial call
		entrySet = Collections.unmodifiableMap(this).entrySet();
	    else
		return super.entrySet();   // Recursive call
	}
	
	// This exception will be thrown if the implementation of 
	// Collections.unmodifiableMap.entrySet() is changed such that it
	// no longer calls entrySet() on the backing Map.  (Provider's
	// entrySet implementation depends on this "implementation detail",
	// which is unlikely to change.
	if (entrySetCallCount != 2)
	    throw new RuntimeException("Internal error.");
	
	return entrySet;
    }
    
    /**
     * Returns an unmodifiable Set view of the property keys contained in 
     * this provider.
     *
     * @since 1.2
     */
    public Set<Object> keySet() {
	return Collections.unmodifiableSet(super.keySet());
    }

    /**
     * Returns an unmodifiable Collection view of the property values 
     * contained in this provider.
     *
     * @since 1.2
     */
    public Collection<Object> values() {
	return Collections.unmodifiableCollection(super.values());
    }

    /**
     * Sets the <code>key</code> property to have the specified
     * <code>value</code>.
     * 
     * <p>First, if there is a security manager, its 
     * <code>checkSecurityAccess</code> method is called with the string 
     * <code>"putProviderProperty."+name</code>, where <code>name</code> is the
     * provider name, to see if it's ok to set this provider's property values. 
     * If the default implementation of <code>checkSecurityAccess</code>
     * is used (that is, that method is not overriden), then this results in
     * a call to the security manager's <code>checkPermission</code> method 
     * with a <code>SecurityPermission("putProviderProperty."+name)</code>
     * permission.
     *
     * @param key the property key.
     *
     * @param value the property value.
     *
     * @return the previous value of the specified property
     * (<code>key</code>), or null if it did not have one.
     *
     * @throws  SecurityException
     *          if a security manager exists and its <code>{@link
     *          java.lang.SecurityManager#checkSecurityAccess}</code> method 
     *          denies access to set property values.
     *
     * @since 1.2
     */
    public synchronized Object put(Object key, Object value) {
	check("putProviderProperty."+name);
        if (debug != null) {
            debug.println("Set " + name + " provider property [" + 
			  key + "/" + value +"]");
        }
	return implPut(key, value);
    }

    /**
     * Removes the <code>key</code> property (and its corresponding
     * <code>value</code>).
     * 
     * <p>First, if there is a security manager, its 
     * <code>checkSecurityAccess</code> method is called with the string 
     * <code>"removeProviderProperty."+name</code>, where <code>name</code> is
     * the provider name, to see if it's ok to remove this provider's 
     * properties. If the default implementation of 
     * <code>checkSecurityAccess</code> is used (that is, that method is not 
     * overriden), then this results in a call to the security manager's 
     * <code>checkPermission</code> method with a
     * <code>SecurityPermission("removeProviderProperty."+name)</code>
     * permission.
     *
     * @param key the key for the property to be removed.
     *
     * @return the value to which the key had been mapped,
     * or null if the key did not have a mapping.
     *
     * @throws  SecurityException
     *          if a security manager exists and its <code>{@link
     *          java.lang.SecurityManager#checkSecurityAccess}</code> method 
     *          denies access to remove this provider's properties.
     *
     * @since 1.2
     */
    public synchronized Object remove(Object key) {
	check("removeProviderProperty."+name);
        if (debug != null) {
            debug.println("Remove " + name + " provider property " + key);
        }
	return implRemove(key);
    }

    private static void check(String directive) {
        SecurityManager security = System.getSecurityManager();
        if (security != null) {
            security.checkSecurityAccess(directive);
        }
    }
    
    // legacy properties changed since last call to any services method?
    private transient boolean legacyChanged;
    // serviceMap changed since last call to getServices()
    private transient boolean servicesChanged;
    
    // Map<String,String>
    private transient Map<String,String> legacyStrings;
    
    // Map<ServiceKey,Service>
    // used for services added via putService(), initialized on demand
    private transient Map<ServiceKey,Service> serviceMap;

    // Map<ServiceKey,Service>
    // used for services added via legacy methods, init on demand
    private transient Map<ServiceKey,Service> legacyMap;
    
    // Set<Service>
    // set of all services. initialized on demand, cleared on modification
    private transient Set<Service> serviceSet;
    
    // register the id attributes for this provider
    // this is to ensure that equals() and hashCode() do not incorrectly
    // report to different provider objects as the same
    private void putId() {
	// note: name and info may be null
	super.put("Provider.id name", String.valueOf(name));
	super.put("Provider.id version", String.valueOf(version));
	super.put("Provider.id info", String.valueOf(info));
	super.put("Provider.id className", this.getClass().getName());
    }

    /**
     * Copies all of the mappings from the specified Map to this provider.
     * Internal method to be called AFTER the security check has been
     * performed.
     */
    private void implPutAll(Map t) {
	for (Map.Entry e : ((Map<?,?>)t).entrySet()) {
	    implPut(e.getKey(), e.getValue());
	}
    }
    
    private Object implRemove(Object key) {
	if (key instanceof String) {
	    String keyString = (String)key;
	    if (keyString.startsWith("Provider.")) {
		return null;
	    }
	    legacyChanged = true;
	    if (legacyStrings == null) {
		legacyStrings = new LinkedHashMap<String,String>();
	    }
	    legacyStrings.remove(keyString);
	}
	return super.remove(key);
    }
    
    private Object implPut(Object key, Object value) {
	if ((key instanceof String) && (value instanceof String)) {
	    String keyString = (String)key;
	    if (keyString.startsWith("Provider.")) {
		return null;
	    }
	    legacyChanged = true;
	    if (legacyStrings == null) {
		legacyStrings = new LinkedHashMap<String,String>();
	    }
	    legacyStrings.put(keyString, (String)value);
	}
	return super.put(key, value);
    }
    
    private void implClear() {
	super.clear();
	putId();
	if (legacyStrings != null) {
	    legacyStrings.clear();
	}
	if (legacyMap != null) {
	    legacyMap.clear();
	}
	if (serviceMap != null) {
	    serviceMap.clear();
	}
	legacyChanged = false;
	servicesChanged = false;
	serviceSet = null;
    }
    
    // used as key in the serviceMap and legacyMap HashMaps
    private static class ServiceKey {
	private final String type;
	private final String algorithm;
	private final String originalAlgorithm;
	private ServiceKey(String type, String algorithm, boolean intern) {
	    this.type = type;
	    this.originalAlgorithm = algorithm;
	    algorithm = algorithm.toUpperCase();
	    this.algorithm = intern ? algorithm.intern() : algorithm;
	}
	public int hashCode() {
	    return type.hashCode() + algorithm.hashCode();
	}
	public boolean equals(Object obj) {
	    if (this == obj) {
		return true;
	    }
	    if (obj instanceof ServiceKey == false) {
		return false;
	    }
	    ServiceKey other = (ServiceKey)obj;
	    return this.type.equals(other.type)
	    	&& this.algorithm.equals(other.algorithm);
	}
	boolean matches(String type, String algorithm) {
	    return (this.type == type) && (this.originalAlgorithm == algorithm);
	}
    }

    /**
     * Ensure all the legacy String properties are fully parsed into
     * service objects.
     */
    private void ensureLegacyParsed() {
	if ((legacyChanged == false) || (legacyStrings == null)) {
	    return;
	}
	serviceSet = null;
	if (legacyMap == null) {
	    legacyMap = new LinkedHashMap<ServiceKey,Service>();
	} else {
	    legacyMap.clear();
	}
	for (Map.Entry<String,String> entry : legacyStrings.entrySet()) {
	    parseLegacyPut(entry.getKey(), entry.getValue());
	}
	removeInvalidServices(legacyMap);
	legacyChanged = false;
    }
    
    /**
     * Remove all invalid services from the Map. Invalid services can only
     * occur if the legacy properties are inconsistent or incomplete.
     */
    private void removeInvalidServices(Map<ServiceKey,Service> map) {
	for (Iterator t = map.entrySet().iterator(); t.hasNext(); ) {
	    Map.Entry entry = (Map.Entry)t.next();
	    Service s = (Service)entry.getValue();
	    if (s.isValid() == false) {
		t.remove();
	    }
	}
    }
    
    private String[] getTypeAndAlgorithm(String key) {
	int i = key.indexOf(".");
	if (i < 1) {
	    if (debug != null) {
		debug.println("Ignoring invalid entry in provider "
			+ name + ":" + key);
	    }
	    return null;
	}
	String type = key.substring(0, i);
	String alg = key.substring(i + 1);
	return new String[] {type, alg};
    }
    
    private final static String ALIAS_PREFIX = "Alg.Alias.";
    private final static int ALIAS_LENGTH = ALIAS_PREFIX.length();
    
    private void parseLegacyPut(String name, String value) {
	if (name.startsWith(ALIAS_PREFIX)) {
	    // e.g. put("Alg.Alias.MessageDigest.SHA", "SHA-1");
	    // aliasKey ~ MessageDigest.SHA
	    String stdAlg = value;
	    String aliasKey = name.substring(ALIAS_LENGTH);
	    String[] typeAndAlg = getTypeAndAlgorithm(aliasKey);
	    if (typeAndAlg == null) {
		return;
	    }
	    String type = typeAndAlg[0].intern();
	    String aliasAlg = typeAndAlg[1].intern();
	    ServiceKey key = new ServiceKey(type, stdAlg, true);
	    Service s = (Service)legacyMap.get(key);
	    if (s == null) {
		s = new Service(this);
		s.type = type;
		s.algorithm = stdAlg;
		legacyMap.put(key, s);
	    }
	    legacyMap.put(new ServiceKey(type, aliasAlg, true), s);
	    s.addAlias(aliasAlg);
	} else {
	    String[] typeAndAlg = getTypeAndAlgorithm(name);
	    if (typeAndAlg == null) {
		return;
	    }
	    int i = typeAndAlg[1].indexOf(' ');
	    if (i == -1) {
		// e.g. put("MessageDigest.SHA-1", "sun.security.provider.SHA");
		String type = typeAndAlg[0].intern();
		String stdAlg = typeAndAlg[1].intern();
		String className = value;
		ServiceKey key = new ServiceKey(type, stdAlg, true);
		Service s = (Service)legacyMap.get(key);
		if (s == null) {
		    s = new Service(this);
		    s.type = type;
		    s.algorithm = stdAlg;
		    legacyMap.put(key, s);
		}
		s.className = className;
	    } else { // attribute
		// e.g. put("MessageDigest.SHA-1 ImplementedIn", "Software");
		String attributeValue = value;
		String type = typeAndAlg[0].intern();
		String attributeString = typeAndAlg[1];
		String stdAlg = attributeString.substring(0, i).intern();
		String attributeName = attributeString.substring(i + 1);
		// kill additional spaces
		while (attributeName.startsWith(" ")) {
		    attributeName = attributeName.substring(1);
		}
		attributeName = attributeName.intern();
		ServiceKey key = new ServiceKey(type, stdAlg, true);
		Service s = (Service)legacyMap.get(key);
		if (s == null) {
		    s = new Service(this);
		    s.type = type;
		    s.algorithm = stdAlg;
		    legacyMap.put(key, s);
		}
		s.addAttribute(attributeName, attributeValue);
	    }
	}
    }
    
    /**
     * Get the service describing this Provider's implementation of the
     * specified type of this algorithm or alias. If no such
     * implementation exists, this method returns null. If there are two
     * matching services, one added to this provider using 
     * {@link #putService putService()} and one added via {@link #put put()},
     * the service added via {@link #putService putService()} is returned.
     *
     * @param type the type of {@link Service service} requested
     * (for example, <code>MessageDigest</code>)
     * @param algorithm the case insensitive algorithm name (or alternate 
     * alias) of the service requested (for example, <code>SHA-1</code>)
     *
     * @return the service describing this Provider's matching service
     * or null if no such service exists
     *
     * @throws NullPointerException if type or algorithm is null
     *
     * @since 1.5
     */
    public synchronized Service getService(String type, String algorithm) {
	// avoid allocating a new key object if possible
	ServiceKey key = previousKey;
	if (key.matches(type, algorithm) == false) {
	    key = new ServiceKey(type, algorithm, false);
	    previousKey = key;
	}
	if (serviceMap != null) {
	    Service service = serviceMap.get(key);
	    if (service != null) {
		return service;
	    }
	}
	ensureLegacyParsed();
	return (legacyMap != null) ? legacyMap.get(key) : null;
    }
    
    // ServiceKey from previous getService() call
    // by re-using it if possible we avoid allocating a new object
    // and the toUpperCase() call.
    // re-use will occur e.g. as the framework traverses the provider
    // list and queries each provider with the same values until it finds
    // a matching service
    private static volatile ServiceKey previousKey = 
					    new ServiceKey("", "", false);
    
    /**
     * Get an unmodifiable Set of all services supported by
     * this Provider.
     *
     * @return an unmodifiable Set of all services supported by
     * this Provider
     *
     * @since 1.5
     */
    public synchronized Set<Service> getServices() {
	if (legacyChanged || servicesChanged) {
	    serviceSet = null;
	} else if (serviceSet != null) {
	    return serviceSet;
	}
	ensureLegacyParsed();
	serviceSet = new LinkedHashSet<Service>();
	if (serviceMap != null) {
	    serviceSet.addAll(serviceMap.values());
	}
	if (legacyMap != null) {
	    serviceSet.addAll(legacyMap.values());
	}
	servicesChanged = false;
	return serviceSet;
    }

    /**
     * Add a service. If a service of the same type with the same algorithm
     * name exists and it was added using {@link #putService putService()}, 
     * it is replaced by the new service. 
     * This method also places information about this service
     * in the provider's Hashtable values in the format described in the
     * <a href="../../../guide/security/CryptoSpec.html">
     * Java Cryptography Architecture API Specification &amp; Reference </a>.
     *
     * <p>Also, if there is a security manager, its 
     * <code>checkSecurityAccess</code> method is called with the string 
     * <code>"putProviderProperty."+name</code>, where <code>name</code> is 
     * the provider name, to see if it's ok to set this provider's property 
     * values. If the default implementation of <code>checkSecurityAccess</code>
     * is used (that is, that method is not overriden), then this results in
     * a call to the security manager's <code>checkPermission</code> method with
     * a <code>SecurityPermission("putProviderProperty."+name)</code>
     * permission.
     *
     * @param s the Service to add
     *
     * @throws SecurityException
     *      if a security manager exists and its <code>{@link
     *      java.lang.SecurityManager#checkSecurityAccess}</code> method denies
     *      access to set property values.
     * @throws NullPointerException if s is null
     *
     * @since 1.5
     */
    protected synchronized void putService(Service s) {
	check("putProviderProperty." + name);
	if (debug != null) {
            debug.println(name + ".putService(): " + s);
	}
	if (s == null) {
	    throw new NullPointerException();
	}
	if (serviceMap == null) {
	    serviceMap = new LinkedHashMap<ServiceKey,Service>();
	}
	servicesChanged = true;
	String type = s.getType();
	String algorithm = s.getAlgorithm();
	ServiceKey key = new ServiceKey(type, algorithm, true);
	// remove existing service
	implRemoveService(serviceMap.get(key));
	serviceMap.put(key, s);
	for (String alias : s.getAliases()) {
	    serviceMap.put(new ServiceKey(type, alias, true), s);
	}
	putPropertyStrings(s);
    }
    
    /**
     * Put the string properties for this Service in this Provider's
     * Hashtable.
     */
    private void putPropertyStrings(Service s) {
	String type = s.getType();
	String algorithm = s.getAlgorithm();
	// use super() to avoid permission check and other processing
	super.put(type + "." + algorithm, s.getClassName());
	for (String alias : s.getAliases()) {
	    super.put(ALIAS_PREFIX + type + "." + alias, algorithm);
	}
	for (Map.Entry<String,String> entry : s.attributes.entrySet()) {
	    String key = type + "." + algorithm + " " + entry.getKey();
	    super.put(key, entry.getValue());
	}
    }

    /**
     * Remove the string properties for this Service from this Provider's
     * Hashtable.
     */
    private void removePropertyStrings(Service s) {
	String type = s.getType();
	String algorithm = s.getAlgorithm();
	// use super() to avoid permission check and other processing
	super.remove(type + "." + algorithm);
	for (String alias : s.getAliases()) {
	    super.remove(ALIAS_PREFIX + type + "." + alias);
	}
	for (Map.Entry<String,String> entry : s.attributes.entrySet()) {
	    String key = type + "." + algorithm + " " + entry.getKey();
	    super.remove(key);
	}
    }

    /**
     * Remove a service previously added using 
     * {@link #putService putService()}. The specified service is removed from
     * this provider. It will no longer be returned by 
     * {@link #getService getService()} and its information will be removed 
     * from this provider's Hashtable.
     *
     * <p>Also, if there is a security manager, its 
     * <code>checkSecurityAccess</code> method is called with the string 
     * <code>"removeProviderProperty."+name</code>, where <code>name</code> is 
     * the provider name, to see if it's ok to remove this provider's 
     * properties. If the default implementation of 
     * <code>checkSecurityAccess</code> is used (that is, that method is not 
     * overriden), then this results in a call to the security manager's 
     * <code>checkPermission</code> method with a
     * <code>SecurityPermission("removeProviderProperty."+name)</code>
     * permission.
     *
     * @param s the Service to be removed
     *
     * @throws  SecurityException
     *          if a security manager exists and its <code>{@link
     *          java.lang.SecurityManager#checkSecurityAccess}</code> method denies
     *          access to remove this provider's properties.
     * @throws NullPointerException if s is null
     *
     * @since 1.5
     */
    protected synchronized void removeService(Service s) {
	check("removeProviderProperty." + name);
        if (debug != null) {
            debug.println(name + ".removeService(): " + s);
        }
	if (s == null) {
	    throw new NullPointerException();
	}
	implRemoveService(s);
    }
    
    private void implRemoveService(Service s) {
	if ((s == null) || (serviceMap == null)) {
	    return;
	}
	String type = s.getType();
	String algorithm = s.getAlgorithm();
	ServiceKey key = new ServiceKey(type, algorithm, false);
	Service oldService = serviceMap.get(key);
	if (s != oldService) {
	    return;
	}
	servicesChanged = true;
	serviceMap.remove(key);
	for (String alias : s.getAliases()) {
	    serviceMap.remove(new ServiceKey(type, alias, false));
	}
	removePropertyStrings(s);
    }
    
    /**
     * The description of a security service. It encapsulates the properties
     * of a service and contains a factory method to obtain new implementation
     * instances of this service.
     *
     * <p>Each service has a provider that offers the service, a type,
     * an algorithm name, and the name of the class that implements the
     * service. Optionally, it also includes a list of alternate algorithm
     * names for this service (aliases) and attributes, which are a map of
     * (name, value) String pairs.
     *
     * <p>This class defines the methods {@link #supportsParameter 
     * supportsParameter()} and {@link #newInstance newInstance()}
     * which are used by the Java security framework when it searches for
     * suitable services and instantes them. The valid arguments to those 
     * methods depend on the type of service. For the service types defined 
     * within J2SE, see the
     * <a href="../../../guide/security/CryptoSpec.html">
     * Java Cryptography Architecture API Specification &amp; Reference </a>
     * for the valid values.
     * Note that components outside of J2SE can define additional types of 
     * services and their behavior.
     *
     * <p>Instances of this class are immutable.
     *
     * @since 1.5
     */
    public static class Service {
	
	private String type, algorithm, className;
	private final Provider provider;
	private List<String> aliases;
	private Map<String,String> attributes;

	// Reference to the cached implementation Class object
	private volatile Reference<Class> classRef;
	
	// flag indicating whether this service has its attributes for
	// supportedKeyFormats or supportedKeyClasses set
	// if null, the values have not been initialized
	// if TRUE, at least one of supportedFormats/Classes is non null
	private volatile Boolean hasKeyAttributes;
	
	// supported encoding formats
	private String[] supportedFormats;
	
	// names of the supported key (super) classes
	private Class[] supportedClasses;
	
	private static final Class[] CLASS0 = new Class[0];
	
	// this constructor and these methods are used for parsing
	// the legacy string properties.
	
	private Service(Provider provider) {
	    this.provider = provider;
	    aliases = Collections.<String>emptyList();
	    attributes = Collections.<String,String>emptyMap();
	}

	private boolean isValid() {
	    return (type != null) && (algorithm != null) && (className != null);
	}
	
	private void addAlias(String alias) {
	    if (aliases == Collections.EMPTY_LIST) {
		aliases = new ArrayList<String>(2);
	    }
	    aliases.add(alias);
	}
	
	void addAttribute(String type, String value) {
	    if (attributes == Collections.EMPTY_MAP) {
		attributes = new HashMap<String,String>(8);
	    }
	    attributes.put(type, value);
	}
	
	/**
	 * Construct a new service.
	 *
	 * @param provider the provider that offers this service
	 * @param type the type of this service
	 * @param algorithm the algorithm name
	 * @param className the name of the class implementing this service
	 * @param aliases List of aliases or null if algorithm has no aliases
	 * @param attributes Map of attributes or null if this implementation
	 *		     has no attributes
	 *
	 * @throws NullPointerException if provider, type, algorithm, or
	 * className is null
	 */
	public Service(Provider provider, String type, String algorithm, 
		String className, List<String> aliases, 
		Map<String,String> attributes) {
	    if ((provider == null) || (type == null) || 
		    (algorithm == null) || (className == null)) {
		throw new NullPointerException();
	    }
	    this.provider = provider;
	    this.type = type;
	    this.algorithm = algorithm;
	    this.className = className;
	    if (aliases == null) {
		this.aliases = Collections.<String>emptyList();
	    } else {
		this.aliases = new ArrayList<String>(aliases);
	    }
	    if (attributes == null) {
		this.attributes = Collections.<String,String>emptyMap();
	    } else {
		this.attributes = new HashMap<String,String>(attributes);
	    }
	}
	
	/**
	 * Get the type of this service. For example, <code>MessageDigest</code>.
	 *
	 * @return the type of this service
	 */
	public final String getType() {
	    return type;
	}
	
	/**
	 * Return the name of the algorithm of this service. For example,
	 * <code>SHA-1</code>.
	 *
	 * @return the algorithm of this service
	 */
	public final String getAlgorithm() {
	    return algorithm;
	}
	
	/**
	 * Return the Provider of this service.
	 *
	 * @return the Provider of this service
	 */
	public final Provider getProvider() {
	    return provider;
	}
	
	/**
	 * Return the name of the class implementing this service.
	 *
	 * @return the name of the class implementing this service
	 */
	public final String getClassName() {
	    return className;
	}
	
	// internal only
	private final List<String> getAliases() {
	    return aliases;
	}
	
	/**
	 * Return the value of the specified attribute or null if this
	 * attribute is not set for this Service.
	 *
	 * @param name the name of the requested attribute
	 *
	 * @return the value of the specified attribute or null if the
	 *         attribute is not present
	 *
	 * @throws NullPointerException if name is null
	 */
	public final String getAttribute(String name) {
	    if (name == null) {
		throw new NullPointerException();
	    }
	    return attributes.get(name);
	}
	
	// built in knowledge of the engine types shipped within J2SE
	// this is for the argument checks in the newInstance() and
	// supportsParameter() methods
	
	// Map<String,Object>
	private static final Map<String,Object> knownEngines;
	
	// use no-args constructor, supportsParameter() not used
	private static final Object S_NEITHER = "neither";
	// special constructor used, supportsParameter() not used
	private static final Object S_CONS = "constructor";
	// use no-args constructor, supportsParameter() IS used
	private static final Object S_SUPP = "supports";
	
	static {
	    knownEngines = new HashMap<String,Object>();
	    // JCA
	    knownEngines.put("AlgorithmParameterGenerator", S_NEITHER);
	    knownEngines.put("AlgorithmParameters", S_NEITHER);
	    knownEngines.put("KeyFactory", S_NEITHER);
	    knownEngines.put("KeyPairGenerator", S_NEITHER);
	    knownEngines.put("KeyStore", S_NEITHER);
	    knownEngines.put("MessageDigest", S_NEITHER);
	    knownEngines.put("SecureRandom", S_NEITHER);
	    knownEngines.put("Signature", S_SUPP);
	    knownEngines.put("CertificateFactory", S_NEITHER);
	    knownEngines.put("CertPathBuilder", S_NEITHER);
	    knownEngines.put("CertPathValidator", S_NEITHER);
	    knownEngines.put("CertStore", S_CONS);
	    // JCE
	    knownEngines.put("Cipher", S_SUPP);
	    knownEngines.put("ExemptionMechanism", S_NEITHER);
	    knownEngines.put("Mac", S_SUPP);
	    knownEngines.put("KeyAgreement", S_SUPP);
	    knownEngines.put("KeyGenerator", S_NEITHER);
	    knownEngines.put("SecretKeyFactory", S_NEITHER);
	    // JSSE
	    knownEngines.put("KeyManagerFactory", S_NEITHER);
	    knownEngines.put("SSLContext", S_NEITHER);
	    knownEngines.put("TrustManagerFactory", S_NEITHER);
	    // JGSS
	    knownEngines.put("GssApiMechanism", S_NEITHER);
	    // SASL
	    knownEngines.put("SaslClientFactory", S_NEITHER);
	    knownEngines.put("SaslServerFactory", S_NEITHER);
	}
	
	/**
	 * Return a new instance of the implementation described by this
	 * service. The security provider framework uses this method to
	 * construct implementations. Applications will typically not need 
	 * to call it.
	 *
	 * <p>The default implementation uses reflection to invoke the
	 * standard constructor for this type of service.
	 * Security providers can override this method to implement
	 * instantiation in a different way.
	 * For details and the values of constructorParameter that are 
	 * valid for the various types of services see the
	 * <a href="../../../guide/security/CryptoSpec.html">
	 * Java Cryptography Architecture API Specification &amp; 
	 * Reference</a>.
	 *
	 * @param constructorParameter the value to pass to the constructor,
	 * or null if this type of service does not use a constructorParameter.
	 *
	 * @return a new implementation of this service
	 *
	 * @throws InvalidParameterException if the value of 
	 * constructorParameter is invalid for this type of service.
	 * @throws NoSuchAlgorithmException if instantation failed for
	 * any other reason.
	 */
	public Object newInstance(Object constructorParameter) 
		throws NoSuchAlgorithmException {
	    try {
		Object cap = knownEngines.get(type);
		if (cap == null) {
		    // unknown engine type, use generic code
		    // this is the code path future for non-core
		    // optional packages
		    return newInstanceGeneric(constructorParameter);
		}
		if (cap != S_CONS) {
		    if (constructorParameter != null) {
			throw new InvalidParameterException
			    ("constructorParameter not used with " + type
			    + " engines");
		    }
		    Class clazz = getImplClass();
		    return clazz.newInstance();
		}
		if (type.equals("CertStore") == false) {
		    throw new AssertionError("Unknown engine: " + type);
		}
		if (!(constructorParameter instanceof CertStoreParameters)) {
		    throw new InvalidParameterException
		    	("constructorParameter must be instanceof "
			+ "CertStoreParameters for CertStores");
		}
		Class clazz = getImplClass();
		// use Class.forName() rather than .class to delay
		// class loading
		Constructor cons = clazz.getConstructor(new Class[] 
		   { Class.forName("java.security.cert.CertStoreParameters") });
		return cons.newInstance(new Object[] {constructorParameter});
	    } catch (NoSuchAlgorithmException e) {
		throw e;
	    } catch (InvocationTargetException e) {
		throw new NoSuchAlgorithmException
		    ("Error constructing implementation (algorithm: "
		    + algorithm + ", provider: " + provider.getName() 
		    + ", class: " + className + ")", e.getCause());
	    } catch (Exception e) {
		throw new NoSuchAlgorithmException
		    ("Error constructing implementation (algorithm: "
		    + algorithm + ", provider: " + provider.getName() 
		    + ", class: " + className + ")", e);
	    }
	}
	
	// return the implementation Class object for this service
	private Class getImplClass() throws NoSuchAlgorithmException {
	    try {
		Reference<Class> ref = classRef;
		Class clazz = (ref == null) ? null : ref.get();
		if (clazz == null) {
		    ClassLoader cl = provider.getClass().getClassLoader();
		    if (cl == null) {
			clazz = Class.forName(className);
		    } else {
			clazz = cl.loadClass(className);
		    }
		    classRef = new WeakReference<Class>(clazz);
		}
		return clazz;
	    } catch (ClassNotFoundException e) {
		throw new NoSuchAlgorithmException
	            ("class configured for " + type + "(provider: " + 
		    provider.getName() + ")" + "cannot be found.", e);
	    }
	}
	
	/**
	 * Generic code path for unknown engine types. Call the
	 * no-args constructor if constructorParameter is null, otherwise
	 * use the first matching constructor.
	 */
	private Object newInstanceGeneric(Object constructorParameter)
		throws Exception {
	    Class clazz = getImplClass();
	    if (constructorParameter == null) {
		Object o = clazz.newInstance();
		return o;
	    }
	    Class argClass = constructorParameter.getClass();
	    Constructor[] cons = clazz.getConstructors();
	    // find first public constructor that can take the
	    // argument as parameter
	    for (int i = 0; i < cons.length; i++) {
		Constructor con = cons[i];
		Class[] paramTypes = con.getParameterTypes();
		if (paramTypes.length != 1) {
		    continue;
		}
		if (paramTypes[0].isAssignableFrom(argClass) == false) {
		    continue;
		}
		Object o = con.newInstance(new Object[] {constructorParameter});
		return o;
	    }
	    throw new NoSuchAlgorithmException("No constructor matching "
	    	+ argClass.getName() + " found in class " + className);
	}
	
	/**
	 * Test whether this Service can use the specified parameter.
	 * Returns false if this service cannot use the parameter. Returns
	 * true if this service can use the parameter, if a fast test is
	 * infeasible, or if the status is unknown.
	 *
	 * <p>The security provider framework uses this method with
	 * some types of services to quickly exclude non-matching
	 * implementations for consideration. 
	 * Applications will typically not need to call it.
	 *
	 * <p>For details and the values of parameter that are valid for the 
	 * various types of services see the top of this class and the
	 * <a href="../../../guide/security/CryptoSpec.html">
	 * Java Cryptography Architecture API Specification &amp; 
	 * Reference</a>.
	 * Security providers can override it to implement their own test.
	 *
	 * @param parameter the parameter to test
	 *
	 * @return false if this this service cannot use the specified
	 * parameter; true if it can possibly use the parameter
	 *
	 * @throws InvalidParameterException if the value of parameter is 
	 * invalid for this type of service or if this method cannot be 
	 * used with this type of service
	 */
	public boolean supportsParameter(Object parameter) {
	    Object cap = knownEngines.get(type);
	    if (cap == null) {
		// unknown engine type, return true by default
		return true;
	    }
	    if (cap != S_SUPP) {
		throw new InvalidParameterException("supportsParameter() not "
		    + "used with " + type + " engines");
	    }
	    // allow null for keys without attributes for compatibility
	    if ((parameter != null) && (parameter instanceof Key == false)) {
		throw new InvalidParameterException
		    ("Parameter must be instanceof Key for engine " + type);
	    }
	    if (hasKeyAttributes() == false) {
		return true;
	    }
	    if (parameter == null) {
		return false;
	    }
	    Key key = (Key)parameter;
	    if (supportsKeyFormat(key)) {
		return true;
	    }
	    if (supportsKeyClass(key)) {
		return true;
	    }
	    return false;
	}
	
	/**
	 * Return whether this service has its Supported* properties for
	 * keys defined. Parses the attributes if not yet initialized.
	 */
	private boolean hasKeyAttributes() {
	    Boolean b = hasKeyAttributes;
	    if (b == null) {
		synchronized (this) {
		    String s;
		    s = getAttribute("SupportedKeyFormats");
		    if (s != null) {
			supportedFormats = s.split("\\|");
		    }
		    s = getAttribute("SupportedKeyClasses");
		    if (s != null) {
			String[] classNames = s.split("\\|");
			List<Class> classList = 
			    new ArrayList<Class>(classNames.length);
			for (String className : classNames) {
			    Class clazz = getKeyClass(className);
			    if (clazz != null) {
				classList.add(clazz);
			    }
			}
			supportedClasses = classList.toArray(CLASS0);
		    }
		    boolean bool = (supportedFormats != null)
		    	|| (supportedClasses != null);
		    b = Boolean.valueOf(bool);
		    hasKeyAttributes = b;
		}
	    }
	    return b.booleanValue();
	}
	
	// get the key class object of the specified name
	private Class getKeyClass(String name) {
	    try {
		return Class.forName(name);
	    } catch (ClassNotFoundException e) {
		// ignore
	    }
	    try {
		ClassLoader cl = provider.getClass().getClassLoader();
		if (cl != null) {
		    return cl.loadClass(name);
		}
	    } catch (ClassNotFoundException e) {
		// ignore
	    }
	    return null;
	}
	
	private boolean supportsKeyFormat(Key key) {
	    if (supportedFormats == null) {
		return false;
	    }
	    String format = key.getFormat();
	    if (format == null) {
		return false;
	    }
	    for (String supportedFormat : supportedFormats) {
		if (supportedFormat.equals(format)) {
		    return true;
		}
	    }
	    return false;
	}
	
	private boolean supportsKeyClass(Key key) {
	    if (supportedClasses == null) {
		return false;
	    }
	    Class keyClass = key.getClass();
	    for (Class clazz : supportedClasses) {
		if (clazz.isAssignableFrom(keyClass)) {
		    return true;
		}
	    }
	    return false;
	}
	
	/**
	 * Return a String representation of this service.
	 *
	 * @return a String representation of this service.
	 */
	public String toString() {
	    String aString = aliases.isEmpty()
		? "" : "\r\n  aliases: " + aliases.toString();
	    String attrs = attributes.isEmpty() 
		? "" : "\r\n  attributes: " + attributes.toString();
	    return provider.getName() + ": " + type + "." + algorithm
	    	+ " -> " + className + aString + attrs + "\r\n";
	}
	
    }

}

