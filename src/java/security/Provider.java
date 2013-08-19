/*
 * @(#)Provider.java	1.53 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.security;

import java.io.*;
import java.util.*;

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
 * @version 1.53, 01/23/03
 * @author Benjamin Renaud
 */
public abstract class Provider extends Properties {

    private static final sun.security.util.Debug debug =
        sun.security.util.Debug.getInstance
        ("jca", "Provider");

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
    }

    /**
     * Constructs a provider with the specified name. Assigns it
     * version 1.0.
     *
     * @param name the provider name.
     */
    Provider(String name) {
	this(name, 1.0, "no information available");
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

    /*
     * Instantiates a provider from its fully-qualified class name.
     *
     * <p>The assumption is made that providers configured in the
     * security properties file will always be supplied as part
     * of an INSTALLED extension or specified on the class path
     * (and therefore can be loaded using the class loader returned by
     * a call to <code>ClassLoader.getSystemClassLoader</code>, whose
     * delegation parent is the extension class loader for installed
     * extensions).
     *
     * <p>If an applet or application wants to install a provider that is
     * supplied within a BUNDLED extension, it will be able to do so
     * only at runtime, by calling the <code>Security.addProvider</code>
     * method (which is subject to a security check).
     */
    static Provider loadProvider(String name) {
	try {
	    ClassLoader cl = ClassLoader.getSystemClassLoader();
	    Class provClass;
	    if (cl != null) {
		provClass = cl.loadClass(name);
	    } else {
		provClass = Class.forName(name);
	    }
	    Object obj = provClass.newInstance();
	    if (obj instanceof Provider) {
		if (debug != null) {
                    debug.println("Loaded provider " + name);
                }
		return (Provider)obj;
	    } else {
		if (debug != null) {
		    debug.println(name + " is not a provider");
		}
	    }
	} catch (Exception e) {
	    if (debug != null) {
	        debug.println("Error loading provider " + name);
		e.printStackTrace();
	    }
	}
	return null;
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
     * <p>First, if there is a security manager, its <code>checkSecurityAccess</code> 
     * method is called with the string <code>"clearProviderProperties."+name</code>
     * (where <code>name</code> is the provider name) to see if it's ok to clear this provider.
     * If the default implementation of <code>checkSecurityAccess</code> 
     * is used (that is, that method is not overriden), then this results in
     * a call to the security manager's <code>checkPermission</code> method with a
     * <code>SecurityPermission("clearProviderProperties."+name)</code>
     * permission.
     *
     * @throws  SecurityException
     *          if a security manager exists and its <code>{@link
     *          java.lang.SecurityManager#checkSecurityAccess}</code> method denies
     *          access to clear this provider
     *
     * @since 1.2
     */
    public synchronized void clear() {
	check("clearProviderProperties."+name);
	if (debug != null) {
	    debug.println("Remove " + name + " provider properties");
	}
	super.clear();
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
	putAllInternal(tempProperties);
    }

    /**
     * Copies all of the mappings from the specified Map to this provider.
     * These mappings will replace any properties that this provider had 
     * for any of the keys currently in the specified Map. 
     *
     * @since 1.2
     */
    public synchronized void putAll(Map t) {
	check("putProviderProperty."+name);
        if (debug != null) {
            debug.println("Put all " + name + " provider properties");
        }
	putAllInternal(t);
    }
    
    /**
     * Copies all of the mappings from the specified Map to this provider.
     * Internal method to be called AFTER the security check has been
     * performed.
     */
    private void putAllInternal(Map t) {
	Iterator i = t.entrySet().iterator();
	while (i.hasNext()) {
	    Map.Entry e = (Map.Entry) i.next();
	    super.put(e.getKey(), e.getValue());
	}
    }

    /**
     * Returns an unmodifiable Set view of the property entries contained 
     * in this Provider.
     *
     * @see   java.util.Map.Entry
     * @since 1.2
     */
    public synchronized Set entrySet() {
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
    public Set keySet() {
	return Collections.unmodifiableSet(super.keySet());
    }

    /**
     * Returns an unmodifiable Collection view of the property values 
     * contained in this provider.
     *
     * @since 1.2
     */
    public Collection values() {
	return Collections.unmodifiableCollection(super.values());
    }

    /**
     * Sets the <code>key</code> property to have the specified
     * <code>value</code>.
     * 
     * <p>First, if there is a security manager, its <code>checkSecurityAccess</code> 
     * method is called with the string <code>"putProviderProperty."+name</code>,
     * where <code>name</code> is the provider name,
     * to see if it's ok to set this provider's property values.
     * If the default implementation of <code>checkSecurityAccess</code> 
     * is used (that is, that method is not overriden), then this results in
     * a call to the security manager's <code>checkPermission</code> method with a
     * <code>SecurityPermission("putProviderProperty."+name)</code>
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
     *          java.lang.SecurityManager#checkSecurityAccess}</code> method denies
     *          access to set property values.
     *
     * @since 1.2
     */
    public synchronized Object put(Object key, Object value) {
	check("putProviderProperty."+name);
        if (debug != null) {
            debug.println("Set " + name + " provider property [" + 
			  key + "/" + value +"]");
        }
	return super.put(key, value);
    }

    /**
     * Removes the <code>key</code> property (and its corresponding
     * <code>value</code>).
     * 
     * <p>First, if there is a security manager, its <code>checkSecurityAccess</code> 
     * method is called with the string <code>""removeProviderProperty."+name</code>,
     * where <code>name</code> is the provider name,
     * to see if it's ok to remove this provider's properties. 
     * If the default implementation of <code>checkSecurityAccess</code> 
     * is used (that is, that method is not overriden), then this results in
     * a call to the security manager's <code>checkPermission</code> method with a
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
     *          java.lang.SecurityManager#checkSecurityAccess}</code> method denies
     *          access to remove this provider's properties.
     *
     * @since 1.2
     */
    public synchronized Object remove(Object key) {
	check("removeProviderProperty."+name);
        if (debug != null) {
            debug.println("Remove " + name + " provider property " + key);
        }
	return super.remove(key);
    }

    private static void check(String directive) {
         SecurityManager security = System.getSecurityManager();
        if (security != null) {
            security.checkSecurityAccess(directive);
        }
    }

    // Declare serialVersionUID to be compatible with JDK1.1
    static final long serialVersionUID = -4298000515446427739L;
}


