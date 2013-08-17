/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.naming.internal;

import java.io.InputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.naming.NamingEnumeration;

/**
 * VersionHelper is used by JNDI to accomodate differences between
 * JDK 1.1.x and the Java 2 platform.
 * 
 * @author Rosanna Lee
 * @author Scott Seligman
 * @version 1.6 02/02/06
 */

public abstract class VersionHelper {
    private static VersionHelper helper = null;

    final static String[] PROPS = new String[] {
	javax.naming.Context.INITIAL_CONTEXT_FACTORY,
	javax.naming.Context.OBJECT_FACTORIES,
	javax.naming.Context.URL_PKG_PREFIXES,
	javax.naming.Context.STATE_FACTORIES,
	javax.naming.Context.PROVIDER_URL,
	javax.naming.Context.DNS_URL,
	// The following shouldn't create a runtime dependence on ldap package.
	javax.naming.ldap.LdapContext.CONTROL_FACTORIES
    };

    public final static int INITIAL_CONTEXT_FACTORY = 0;
    public final static int OBJECT_FACTORIES = 1;
    public final static int URL_PKG_PREFIXES = 2;
    public final static int STATE_FACTORIES = 3;
    public final static int PROVIDER_URL = 4;
    public final static int DNS_URL = 5;
    public final static int CONTROL_FACTORIES = 6;

    VersionHelper() {} // Disallow anyone from creating one of these.

    static {
	try {
	    Class.forName("java.net.URLClassLoader"); // 1.2 test
	    Class.forName("java.security.PrivilegedAction"); // 1.2 test
	    helper = (VersionHelper)
		Class.forName(
		    "com.sun.naming.internal.VersionHelper12").newInstance();
	} catch (Exception e) {
	}

	// Use 1.1 helper if 1.2 test fails, or if we cannot create 1.2 helper
	if (helper == null) {
	    try {
		helper = (VersionHelper)
		    Class.forName(
		      "com.sun.naming.internal.VersionHelper11").newInstance();
	    } catch (Exception e) {
		// should never happen
	    }
	}
    }

    public static VersionHelper getVersionHelper() {
	return helper;
    }

    public abstract Class loadClass(String className) 
	throws ClassNotFoundException;

    abstract Class loadClass(String className, ClassLoader cl)
	throws ClassNotFoundException;

    public abstract Class loadClass(String className, String codebase) 
	throws ClassNotFoundException, MalformedURLException;

    /*
     * Returns a JNDI property from the system properties.  Returns
     * null if the property is not set, or if there is no permission
     * to read it.
     */
    abstract String getJndiProperty(int i);

    /*
     * Reads each property in PROPS from the system properties, and
     * returns their values -- in order -- in an array.  For each
     * unset property, the corresponding array element is set to null.
     * Returns null if there is no permission to call System.getProperties().
     */
    abstract String[] getJndiProperties();

    /*
     * Returns the resource of a given name associated with a particular
     * class (never null), or null if none can be found.
     */
    abstract InputStream getResourceAsStream(Class c, String name);

    /*
     * Returns an input stream for a file in <java.home>/lib,
     * or null if it cannot be located or opened.
     *
     * @param filename	The file name, sans directory.
     */
    abstract InputStream getJavaHomeLibStream(String filename);

    /*
     * Returns an enumeration (never null) of InputStreams of the
     * resources of a given name associated with a particular class
     * loader.  Null represents the bootstrap class loader in some
     * Java implementations.
     */
    abstract NamingEnumeration getResources(ClassLoader cl, String name)
	throws IOException;

    /*
     * Returns the context class loader associated with the current thread.
     * Null indicates the bootstrap class loader in some Java implementations.
     *
     * @throws SecurityException if the class loader is not accessible.
     */
    abstract ClassLoader getContextClassLoader();

    static protected URL[] getUrlArray(String codebase) 
	throws MalformedURLException {
	// Parse codebase into separate URLs
	StringTokenizer parser = new StringTokenizer(codebase);
	Vector vec = new Vector(10);
	while (parser.hasMoreTokens()) {
	    vec.addElement(parser.nextToken());
	}
	String[] url = new String[vec.size()];
	for (int i = 0; i < url.length; i++) {
	    url[i] = (String)vec.elementAt(i);
	}
	
	URL[] urlArray = new URL[url.length];
	for (int i = 0; i < urlArray.length; i++) {
	    urlArray[i] = new URL(url[i]);
	}
	return urlArray;
    }
}
