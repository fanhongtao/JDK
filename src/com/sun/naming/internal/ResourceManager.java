/*
 * @(#)ResourceManager.java	1.6 00/02/02
 *
 * Copyright 1999, 2000 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */

package com.sun.naming.internal;

import java.applet.Applet;
import java.io.InputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.naming.*;

/**
  * The ResourceManager class facilitates the reading of JNDI resource files.
  * 
  * @author Rosanna Lee
  * @author Scott Seligman
  * @version 1.6 00/02/02
  */

public final class ResourceManager {

    /*
     * Name of provider resource files (without the package-name prefix.)
     */
    private static final String PROVIDER_RESOURCE_FILE_NAME =
	    "jndiprovider.properties";

    /*
     * Name of application resource files.
     */
    private static final String APP_RESOURCE_FILE_NAME = "jndi.properties";

    /*
     * Name of properties file in <java.home>/lib.
     */
    private static final String JRELIB_PROPERTY_FILE_NAME = "jndi.properties";

    /*
     * The standard JNDI properties that specify colon-separated lists.
     */
    private static final String[] listProperties = {
	Context.OBJECT_FACTORIES,
	Context.URL_PKG_PREFIXES,
	Context.STATE_FACTORIES,
	// The following shouldn't create a runtime dependence on ldap package.
	javax.naming.ldap.LdapContext.CONTROL_FACTORIES
    };

    private static final VersionHelper helper =
	    VersionHelper.getVersionHelper();

    /*
     * A cache of the properties that have been constructed by
     * the ResourceManager.  A Hashtable from a provider resource
     * file is keyed on an object in the resource file's package.
     * One from application resource files is keyed on the thread's
     * context class loader (or the string "bootstrap" if that is null).
     */
    private static final Hashtable propertiesCache = new Hashtable(11);

    /*
     * A cache of factory objects (ObjectFactory, StateFactory, ControlFactory).
     *
     * Key is loader+propValue; value is a Vector of class name/factory objects.
     * Used in getFactories().
     */
    private static final Hashtable factoryCache = new Hashtable(11);

    /*
     * A cache of URL factory objects (ObjectFactory)
     *
     * Key is loader+className+propValue; value is factory itself, or "none" if
     * a previous search revealed no factory for the key.
     * Used in getFactory().
     */
    private static final Hashtable urlFactoryCache = new Hashtable(11);


    // There should be no instances of this class.
    private ResourceManager() {
    }


    // ---------- Public methods ----------

    /*
     * Given the environment parameter passed to the initial context
     * constructor, returns the full environment for that initial
     * context (never null).  This is based on the environment
     * parameter, the applet parameters (where appropriate), the
     * system properties, and all application resource files.
     *
     * <p> This method will modify <tt>env</tt> and save
     * a reference to it.  The caller may no longer modify it.
     *
     * @param env	environment passed to initial context constructor.
     *			Null indicates an empty environment.
     *
     * @throws NamingException if an error occurs while reading a
     *		resource file
     */
    public static Hashtable getInitialEnvironment(Hashtable env)
	    throws NamingException
    {
	String[] props = VersionHelper.PROPS;	// system/applet properties
	if (env == null) {
	    env = new Hashtable(11);
	}
	Applet applet = (Applet)env.get(Context.APPLET);

	// Merge property values from env param, applet params, and system
	// properties.  The first value wins:  there's no concatenation of
	// colon-separated lists.
	// Read system properties by first trying System.getProperties(),
	// and then trying System.getProperty() if that fails.  The former
	// is more efficient due to fewer permission checks.
	//
	String[] jndiSysProps = helper.getJndiProperties();
	for (int i = 0; i < props.length; i++) {
	    Object val = env.get(props[i]);
	    if (val == null) {
		if (applet != null) {
		    val = applet.getParameter(props[i]);
		}
		if (val == null) {
		    // Read system property.
		    val = (jndiSysProps != null)
			? jndiSysProps[i]
			: helper.getJndiProperty(i);
		}
		if (val != null) {
		    env.put(props[i], val);
		}
	    }
	}

	// Merge the above with the values read from all application
	// resource files.  Colon-separated lists are concatenated.
	mergeTables(env, getApplicationResources());
	return env;
    }

    /**
      * Retrieves the property from the environment, or from the provider
      * resource file associated with the given context.  The environment
      * may in turn contain values that come from applet parameters,
      * system properties, or application resource files.
      *
      * If <tt>concat</tt> is true and both the environment and the provider
      * resource file contain the property, the two values are concatenated
      * (with a ':' separator).
      *
      * Returns null if no value is found.
      *
      * @param propName	The non-null property name
      * @param env	The possibly null environment properties
      * @param ctx	The possibly null context
      * @param concat	True if multiple values should be concatenated
      * @return the property value, or null is there is none.
      * @throws NamingException if an error occurs while reading the provider
      * resource file.
      */
    public static String getProperty(String propName, Hashtable env,
	Context ctx, boolean concat)
	    throws NamingException {

	String val1 = (env != null) ? (String)env.get(propName) : null;
	if ((ctx == null) ||
	    ((val1 != null) && !concat)) {
	    return val1;
	}
	String val2 = (String)getProviderResource(ctx).get(propName);
	if (val1 == null) {
	    return val2;
	} else if ((val2 == null) || !concat) {
	    return val1;
	} else {
	    return (val1 + ":" + val2);
	}
    }

    /**
     * Retreives an enumeration of factory classes/object specified by a
     * property.
     * 
     * The property is gotten from the environment and the provider
     * resource file associated with the given context and concantenated.
     * See getProperty(). The resulting property value is a list of class names.
     *<p>
     * This method then loads each class using the current thread's context
     * class loader and keeps them in a vector. Any class that cannot be loaded
     * is ignored. The resulting vector is then cached in
     * a hash table, keyed by the context class loader and the property's 
     * value. The next time threads of the same context class loader call this 
     * method, they can use the cached vector.
     *<p>
     * After obtaining the vector either from the cache or by creating one from
     * the property value, this method then creates and returns a 
     * FactoryEnumeration using the vector. As the FactoryEnumeration is 
     * traversed, the cached Class object in the vector is instantiated and 
     * replaced by an instance of the factory object itself.
     *<p>
     * Note that multiple threads can be accessing the same cached vector
     * via FactoryEnumeration, which locks the vector during each next().
     * The size of the vector will not change,
     * but a cached Class object might be replaced by an instantiated factory
     * object.
     *
     * @param propName	The non-null property name
     * @param env	The possibly null environment properties
     * @param ctx	The possibly null context
     * @return An enumeration of factory classes/objects; null if none.
     * @exception NamingException If encounter problem while reading the provider
     * property file.
     * @see javax.naming.spi.NamingManager#getObjectInstance
     * @see javax.naming.spi.NamingManager#getStateToBind
     * @see javax.naming.spi.DirectoryManager#getObjectInstance
     * @see javax.naming.spi.DirectoryManager#getStateToBind
     * @see javax.naming.ldap.ControlFactory#getControlInstance
     */
    public static FactoryEnumeration getFactories(String propName, Hashtable env,
	Context ctx) throws NamingException {

	String facProp = getProperty(propName, env, ctx, true);
	if (facProp == null)
	    return null;  // no classes specified; return null

	// Construct key based on context class loader and property val
	FactoryKey key = new FactoryKey(facProp); 

	synchronized (factoryCache) {
	    Vector v = (Vector)factoryCache.get(key);
	    if (v != null) {
		// Cached vector
		return v.size() == 0 ? null : new FactoryEnumeration(v);
	    } else {
		// Populate Vector with classes named in facProp; skipping
		// those that we cannot load
		StringTokenizer parser = new StringTokenizer(facProp, ":");
		v = new Vector(5);
		while (parser.hasMoreTokens()) {
		    try {
			// System.out.println("loading");
			v.addElement(
			    helper.loadClass(parser.nextToken(), key.loader));
		    } catch (Exception e) {
			// ignore ClassNotFoundException, IllegalArgumentException
		    }
		}
		// System.out.println("adding to cache: " + v);
		factoryCache.put(key, v);
		return new FactoryEnumeration(v);
	    }
	}
    }

    /**
     * Retreives a factory from a list of packages specified in a
     * property.
     * 
     * The property is gotten from the environment and the provider
     * resource file associated with the given context and concantenated.
     * propValSuffix is added to the end of this list.
     * See getProperty(). The resulting property value is a list of package 
     * prefixes.
     *<p>
     * This method then constructs a list of class names by concatenating
     * each package prefix with classSuffix and attempts to load and 
     * instantiate the class until one succeeds.
     * Any class that cannot be loaded is ignored. 
     * The resulting object is then cached in a hash table, keyed by the
     * context class loader and the property's value, and classSuffix.
     * The next time threads of the same context class loader call this 
     * method, they use the cached factory..
     * If no factory can be loaded, "none" is recorded in the hashtable
     * so that next time it'll return null quickly.
     *
     * @param propName	The non-null property name
     * @param env	The possibly null environment properties
     * @param ctx	The possibly null context
     * @param classSuffix The non-null class name (e.g. ".ldap.ldapURLContextFactory).
     * @param defaultPkgPrefix The non-null default package prefix.
     *        (e.g., "com.sun.jndi.url").
     * @return An factory object; null if none.
     * @exception NamingException If encounter problem while reading the provider
     * property file, or problem instantiating the factory.
     *
     * @see javax.naming.spi.NamingManager#getURLContext
     * @see javax.naming.spi.NamingManager#getURLObject
     */
    public static Object getFactory(String propName, Hashtable env, Context ctx,
	String classSuffix, String defaultPkgPrefix) throws NamingException {

	// Merge property with provider property and supplied default
	String facProp = getProperty(propName, env, ctx, true);
	if (facProp != null)
	    facProp += (":" + defaultPkgPrefix);
	else
	    facProp = defaultPkgPrefix;

	// Construct key based on context class loader, class name, and property val
	FactoryKey key = new FactoryKey(classSuffix + facProp); 

	synchronized (urlFactoryCache) {
	    Object factory = urlFactoryCache.get(key);
	    if (factory != null) {
		// already in cache, return
		return factory.equals("none") ? null : factory;
	    }

	    // Not previously cached; find first factory and cache
	    StringTokenizer parser = new StringTokenizer(facProp, ":");
	    String className;
	    while (factory == null && parser.hasMoreTokens()) {
		className = parser.nextToken() + classSuffix;
		try {
		    // System.out.println("loading " + className);
		    factory = helper.loadClass(className, key.loader).newInstance();
		} catch (InstantiationException e) {
		    NamingException ne = 
			new NamingException("Cannot instantiate " + className);
		    ne.setRootCause(e);
		    throw ne;
		} catch (IllegalAccessException e) {
		    NamingException ne = 
			new NamingException("Cannot access " + className);
		    ne.setRootCause(e);
		    throw ne;
		} catch (Exception e) {
		    // ignore ClassNotFoundException, IllegalArgumentException, etc
		}
	    }

	    if (factory != null) {
		urlFactoryCache.put(key, factory); // cache
	    } else {
		urlFactoryCache.put(key, "none");  // to indicate none will be found
	    }
	    return factory;
	}
    }


    // ---------- Private methods ----------

    /*
     * Returns the properties contained in the provider resource file
     * of an object's package.  Returns an empty hash table if the
     * object is null or the resource file cannot be found.  The
     * results are cached.
     *
     * @throws NamingException if an error occurs while reading the file.
     */
    private static Hashtable getProviderResource(Object obj)
	    throws NamingException
    {
	if (obj == null) {
	    return (new Hashtable(1));
	}
	synchronized (propertiesCache) {
	    Class c = obj.getClass();

	    Hashtable props = (Hashtable)propertiesCache.get(c);
	    if (props != null) {
		return props;
	    }
	    props = new Properties();

	    InputStream istream =
		helper.getResourceAsStream(c, PROVIDER_RESOURCE_FILE_NAME);

	    if (istream != null) {
		try {
		    ((Properties)props).load(istream);
		} catch (IOException e) {
		    NamingException ne = new ConfigurationException(
			    "Error reading provider resource file for " + c);
		    ne.setRootCause(e);
		    throw ne;
		}
	    }
	    propertiesCache.put(c, props);
	    return props;
	}
    }


    /*
     * Returns the Hashtable (never null) that results from merging
     * all application resource files available to this thread's
     * context class loader.  The properties file in <java.home>/lib
     * is also merged in.  The results are cached.
     *
     * When running under JDK 1.1, the Hashtable returned is always empty.
     *
     * SECURITY NOTES:
     * 1.  JNDI needs permission to read the application resource files.
     * 2.  Any class will be able to use JNDI to view the contents of
     * the application resource files in its own classpath.  Give
     * careful consideration to this before storing sensitive
     * information there.
     *
     * @throws NamingException if an error occurs while reading a resource
     *	file.
     */
    private static Hashtable getApplicationResources() throws NamingException {

	ClassLoader cl;
	try {
	    cl = helper.getContextClassLoader();
	} catch (SecurityException e) {
	    return (new Hashtable(1));
	}
	Object key = (cl != null) ? (Object)cl : (Object)"bootstrap";

	synchronized (propertiesCache) {
	    Hashtable result = (Hashtable)propertiesCache.get(key);
	    if (result != null) {
		return result;
	    }

	    try {
		NamingEnumeration resources =
		    helper.getResources(cl, APP_RESOURCE_FILE_NAME);
		while (resources.hasMore()) {
		    Properties props = new Properties();
		    props.load((InputStream)resources.next());

		    if (result == null) {
			result = props;
		    } else {
			mergeTables(result, props);
		    }
		}

		// Merge in properties from file in <java.home>/lib.
		InputStream istream =
		    helper.getJavaHomeLibStream(JRELIB_PROPERTY_FILE_NAME);
		if (istream != null) {
		    Properties props = new Properties();
		    props.load(istream);

		    if (result == null) {
			result = props;
		    } else {
			mergeTables(result, props);
		    }
		}
		
	    } catch (IOException e) {
		NamingException ne = new ConfigurationException(
			"Error reading application resource file");
		ne.setRootCause(e);
		throw ne;
	    }
	    if (result == null) {
		result = new Hashtable(11);
	    }
	    propertiesCache.put(key, result);
	    return result;
	}
    }

    /*
     * Merge the properties from one hash table into another.  Each
     * property in props2 that is not in props1 is added to props1.
     * For each property in both hash tables that is one of the
     * standard JNDI properties that specify colon-separated lists,
     * the values are concatenated and stored in props1.
     */
    private static void mergeTables(Hashtable props1, Hashtable props2) {
	Enumeration keys = props2.keys();

	while (keys.hasMoreElements()) {
	    String prop = (String)keys.nextElement();
	    Object val1 = props1.get(prop);
	    if (val1 == null) {
		props1.put(prop, props2.get(prop));
	    } else if (isListProperty(prop)) {
		String val2 = (String)props2.get(prop);
		props1.put(prop, ((String)val1) + ":" + val2);
	    }
	}
    }

    /*
     * Is a property one of the standard JNDI properties that specify
     * colon-separated lists?
     */
    private static boolean isListProperty(String prop) {
	prop = prop.intern();
	for (int i = 0; i < listProperties.length; i++) {
	    if (prop == listProperties[i]) {
		return true;
	    }
	}
	return false;
    }

    // --------------- factory loading --------------------

    /**
     * The key used in the factoryCache and urlFactoryCache hash tables.
     */
    final static private class FactoryKey {
	private String propVal;
	private ClassLoader loader;

	FactoryKey(String propVal) {
	    try {
		loader = helper.getContextClassLoader();
	    } catch (SecurityException e) {
	    }
	    this.propVal = propVal;
	}

	public boolean equals(Object obj) {
	    if (!(obj instanceof FactoryKey)) {
		return false;
	    }
	    FactoryKey other = (FactoryKey)obj;
	    return (propVal.equals(other.propVal) && 
		((loader == other.loader) ||
		    (loader != null && loader.equals(other.loader))));
	}

	public int hashCode() {
	    return propVal.hashCode() + (loader != null? loader.hashCode() : 0);
	}
/*
	public String toString() {
	    return (propVal + loader);
	}
*/
    }
}
