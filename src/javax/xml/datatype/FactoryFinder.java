// $Id: FactoryFinder.java,v 1.1.10.2 2004/09/21 12:59:26 nb131165 Exp $
/*
 * @(#)FactoryFinder.java	1.4 04/10/19
 * 
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.xml.datatype;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import java.util.Properties;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

/**
 * <p>Implement pluggabile Datatypes.</p>
 * 
 * <p>This class is duplicated for each JAXP subpackage so keep it in
 * sync.  It is package private for secure class loading.</p>
 *
 * @author <a href="mailto:Jeff.Suttor@Sun.com">Jeff Suttor</a>
 * @version $Revision: 1.1.10.2 $, $Date: 2004/09/21 12:59:26 $
 * @since 1.5
 */
class FactoryFinder {
	
	/**
	 * <p>Name of class to display in output messages.</p>
	 */
	private static final String CLASS_NAME = "javax.xml.datatype.FactoryFinder";
	
    /**
     * <p>Debug flag to trace loading process.</p>
     */
    private static boolean debug = false;
    
    /**
     * <p>Cache properties for performance.</p>
     */
	private static Properties cacheProps = new Properties();
	
	/**
	 * <p>First time requires initialization overhead.</p>
	 */
	private static boolean firstTime = true;
    
    /**
     *<p> Take care of restrictions imposed by java security model </p>
     */
    private static SecuritySupport ss = new SecuritySupport();
	
	/**
	 * <p>Check to see if debugging enabled by property.</p>
	 * 
	 * <p>Use try/catch block to support applets, which throws
     * SecurityException out of this code.</p>
	 * 
	 */
    static {
        try {
            debug = ss.getSystemProperty("jaxp.debug") != null;
        } catch (Exception x) {
        	; // NOP, ignore exception
        }
    }

	/**
	 * <p>Output debugging messages.</p>
	 * 
	 * @param msg <code>String</code> to print to <code>stderr</code>.
	 */
    private static void debugPrintln(String msg) {
        if (debug) {
            System.err.println(
            	CLASS_NAME
            	+ ":"
            	+ msg);
        }
    }

    /**
     * <p>Find the appropriate <code>ClassLoader</code> to use.</p>
     * 
     * <p>The context ClassLoader is prefered.</p>
     * 
     * @return <code>ClassLoader</code> to use.
     * 
     * @throws ConfigurationError If a valid <code>ClassLoader</code> cannot be identified. 
     */
    private static ClassLoader findClassLoader()
        throws ConfigurationError {
        ClassLoader classLoader;

        // Figure out which ClassLoader to use for loading the provider
        // class.  If there is a Context ClassLoader then use it.

        classLoader = ss.getContextClassLoader();            

        debugPrintln(
            "Using context class loader: "
            + classLoader);

        if (classLoader == null) {
            // if we have no Context ClassLoader
            // so use the current ClassLoader
            classLoader = FactoryFinder.class.getClassLoader();
            debugPrintln(
                "Using the class loader of FactoryFinder: "
                + classLoader);                
        }
                    
        return classLoader;
    }

    /**
     * <p>Create an instance of a class using the specified ClassLoader.</p>
     * 
     * @param className Name of class to create.
     * @param classLoader ClassLoader to use to create named class.
     * 
     * @return New instance of specified class created using the specified ClassLoader.
     * 
     * @throws ConfigurationError If class could not be created.
     */
    private static Object newInstance(
    	String className,
        ClassLoader classLoader)
        throws ConfigurationError {
        	
        try {
            Class spiClass;
            if (classLoader == null) {
                spiClass = Class.forName(className);
            } else {
                spiClass = classLoader.loadClass(className);
            }
            
            if (debug) {
            	debugPrintln("Loaded " + className + " from " + which(spiClass));
            }
             
            return spiClass.newInstance();
        } catch (ClassNotFoundException x) {
            throw new ConfigurationError(
                "Provider " + className + " not found", x);
        } catch (Exception x) {
            throw new ConfigurationError(
                "Provider " + className + " could not be instantiated: " + x,
                x);
        }
    }

    /**
     * Finds the implementation Class object in the specified order.  Main
     * entry point.
     * Package private so this code can be shared.
     *
     * @param factoryId Name of the factory to find, same as a property name
     * @param fallbackClassName Implementation class name, if nothing else is found.  Use null to mean no fallback.
     *
     * @return Class Object of factory, never null
     * 
     * @throws ConfigurationError If Class cannot be found.
     */
    static Object find(String factoryId, String fallbackClassName)
        throws ConfigurationError {
        	
        ClassLoader classLoader = findClassLoader();

        // Use the system property first
        try {
            String systemProp = ss.getSystemProperty(factoryId);
            if (systemProp != null) {
                debugPrintln("found " + systemProp + " in the system property " + factoryId);
                return newInstance(systemProp, classLoader);
            }
        } catch (SecurityException se) {
        	; // NOP, explicitly ignore SecurityException
        }

        // try to read from $java.home/lib/jaxp.properties
        try {
            String javah = ss.getSystemProperty("java.home");
            String configFile = javah + File.separator + "lib" + File.separator + "jaxp.properties";
			String factoryClassName = null;
			if (firstTime) {
				synchronized (cacheProps) {
					if (firstTime) {
						File f = new File(configFile);
						firstTime = false;
						if (ss.doesFileExist(f)) {
							debugPrintln("Read properties file " + f);
							cacheProps.load(ss.getFileInputStream(f));
						}
					}
				}
			}
			factoryClassName = cacheProps.getProperty(factoryId);
            debugPrintln("found " + factoryClassName + " in $java.home/jaxp.properties"); 
			
			if (factoryClassName != null) {
				return newInstance(factoryClassName, classLoader);
			}
        } catch (Exception ex) {
            if (debug) {
            	ex.printStackTrace();
            } 
        }
        
        // Try Jar Service Provider Mechanism
        Object provider = findJarServiceProvider(factoryId);
        if (provider != null) {
            return provider;
        }

        if (fallbackClassName == null) {
            throw new ConfigurationError(
                "Provider for " + factoryId + " cannot be found", null);
        }

        debugPrintln("loaded from fallback value: " + fallbackClassName);
        return newInstance(fallbackClassName, classLoader);
    }

    /*
     * Try to find provider using Jar Service Provider Mechanism
     *
     * @return instance of provider class if found or null
     */
    private static Object findJarServiceProvider(String factoryId)
        throws ConfigurationError
    {

        String serviceId = "META-INF/services/" + factoryId;
        InputStream is = null;

        // First try the Context ClassLoader
        ClassLoader cl = ss.getContextClassLoader();
        if (cl != null) {
            is = ss.getResourceAsStream(cl, serviceId);

            // If no provider found then try the current ClassLoader
            if (is == null) {
                cl = FactoryFinder.class.getClassLoader();
                is = ss.getResourceAsStream(cl, serviceId);
            }
        } else {
            // No Context ClassLoader, try the current
            // ClassLoader
            cl = FactoryFinder.class.getClassLoader();
            is = ss.getResourceAsStream(cl, serviceId);
        }

        if (is == null) {
            // No provider found
            return null;
        }

        debugPrintln("found jar resource=" + serviceId +
               " using ClassLoader: " + cl);

        BufferedReader rd;
        try {
            rd = new BufferedReader(new InputStreamReader(is, "UTF-8"));
        } catch (java.io.UnsupportedEncodingException e) {
            rd = new BufferedReader(new InputStreamReader(is));
        }
        
        String factoryClassName = null;
        try {
            // XXX Does not handle all possible input as specified by the
            // Jar Service Provider specification
            factoryClassName = rd.readLine();
            rd.close();
        } catch (IOException x) {
            // No provider found
            return null;
        }

        if (factoryClassName != null &&
            ! "".equals(factoryClassName)) {
            debugPrintln("found in resource, value="
                   + factoryClassName);

            return newInstance(factoryClassName, cl);
        }

        // No provider found
        return null;
    }
    
	/**
	 * <p>Configuration Error.</p>
	 */
    static class ConfigurationError extends Error {
    	
    	/**
    	 * <p>Exception that caused the error.</p>
    	 */
        private Exception exception;

        /**
         * <p>Construct a new instance with the specified detail string and
         * exception.</p>
         * 
         * @param msg Detail message for this error.
         * @param x Exception that caused the error.
         */
        ConfigurationError(String msg, Exception x) {
            super(msg);
            this.exception = x;
        }

		/**
		 * <p>Get the Exception that caused the error.</p>
		 * 
		 * @return Exception that caused the error.
		 */
        Exception getException() {
            return exception;
        }
    }



    /**
     * Returns the location where the given Class is loaded from.
     * 
     * @param clazz Class to find load location.
     * 
     * @return Location where class would be loaded from.
     */
    private static String which(Class clazz) {
        try {
            String classnameAsResource = clazz.getName().replace('.', '/') + ".class";
    
            ClassLoader loader = clazz.getClassLoader();
            
            URL it;
    
            if (loader != null) {
            	it = loader.getResource(classnameAsResource);
            } else {
            	it = ClassLoader.getSystemResource(classnameAsResource);
            } 
    
            if (it != null) {
            	return it.toString();
            } 
        } catch (Throwable t) {
            // work defensively.
            if (debug) {
            	t.printStackTrace();
            } 
        }
        return "unknown location";
    }



    /**
     * The following nested classes allow getContextClassLoader() to be
     * called only on JDK 1.2 and yet run in older JDK 1.1 JVMs
     */
    private abstract static class ClassLoaderFinder {
    	
    	/**
    	 * <p>Get Context Class loader.</p>
    	 * 
    	 * @return Context class loader.
    	 */
        abstract ClassLoader getContextClassLoader();
    }

	/**
	 * <p>Actual ClassLoader finder implementation.</p>
	 */
    static class ClassLoaderFinderConcrete extends ClassLoaderFinder {
    	
    	/**
    	 * <p>Get Context Class loader.</p>
    	 * 
    	 * @return Context class loader.
    	 */
        ClassLoader getContextClassLoader() {
            return Thread.currentThread().getContextClassLoader();
        }
    }
}
