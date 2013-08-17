/*
 * @(#)Provider.java	1.39 98/10/27
 *
 * Copyright 1996-1998 by Sun Microsystems, Inc.,
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

import java.io.*;
import java.util.*;

/**
 * This class represents a "provider" for the
 * Java Security API, where a provider implements some or all parts of
 * Java Security, including:<ul>
 *
 * <li>Algorithms (such as DSA, RSA, MD5 or SHA-1).
 *
 * <li>Key generation, conversion, and management facilities (such as for
 * algorithm-specific keys).
 *
 * </ul>
 *
 * <p>Each provider has a name and a version number, and is configured
 * in each runtime it is installed in.
 *
 * <p>See <a href =
 * "../../../guide/security/CryptoSpec.html#Provider">The Provider Class</a>
 * in the "Java Cryptography Architecture API Specification &amp; Reference"
 * for information about how providers work and how to install them.
 *
 * @version 1.39 00/05/10
 * @author Benjamin Renaud
 */
public abstract class Provider extends Properties {

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
     * Instantiates a provider forom its fully-qualified class name.
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
		return (Provider)obj;
	    } else {
		debug(name + " not a provider");
	    }
	} catch (Exception e) {
	    debug("error loading provider " + name, e);
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
     * @since JDK1.2
     */
    public synchronized void clear() {
	check("clearProviderProperties."+name);
	super.clear();
    }

    /**
     * Reads a property list (key and element pairs) from the input stream.
     *
     * @param      in   the input stream.
     * @exception  IOException  if an error occurred when reading from the
     *               input stream.
     * @see java.util.Properties#load
     */
    public synchronized void load(InputStream inStream) throws IOException {
	check("loadProviderProperties."+name);
	super.load(inStream);
    }

    /**
     * Copies all of the mappings from the specified Map to this provider.
     * These mappings will replace any properties that this provider had 
     * for any of the keys currently in the specified Map. 
     *
     * @since JDK1.2
     */
    public synchronized void putAll(Map t) {
	check("putAllProviderProperties."+name);
	super.putAll(t);
    }

    /**
     * Returns an unmodifiable Set view of the property entries contained 
     * in this Provider.
     *
     * @see   java.util.Map.Entry
     * @since JDK1.2
     */
    public Set entrySet() {
	return Collections.unmodifiableMap(this).entrySet();
    }

    /**
     * Returns an unmodifiable Set view of the property keys contained in 
     * this provider.
     *
     * @since JDK1.2
     */
    public Set keySet() {
	return Collections.unmodifiableMap(this).keySet();
    }

    /*
     * Returns an unmodifiable Collection view of the property values 
     * contained in this provider.
     *
     * @since JDK1.2
     */
    public Collection values() {
	return Collections.unmodifiableMap(this).values();
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
     * @since JDK1.2
     */
    public synchronized Object put(Object key, Object value) {
	check("putProviderProperty."+name);
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
     * @since JDK1.2
     */
    public synchronized Object remove(Object key) {
	check("removeProviderProperty."+name);
	return super.remove(key);
    }

    private static void check(String directive) {
         SecurityManager security = System.getSecurityManager();
        if (security != null) {
            security.checkSecurityAccess(directive);
        }
    }

    private static void debug(String msg) {
	Security.debug(msg);
    }

    private static void debug(String msg, Throwable t) {
	Security.debug(msg, t);
    }

    // Declare serialVersionUID to be compatible with JDK1.1
    static final long serialVersionUID = -4298000515446427739L;
}

