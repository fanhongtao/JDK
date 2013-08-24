// $Id: FactoryFinder.java,v 1.7 2004/03/17 10:31:30 nb131165 Exp $
/*
 * @(#)FactoryFinder.java	1.8 06/09/06
 * 
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.xml.parsers;

import java.io.File;
import java.io.FileInputStream;

import java.util.Properties;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

/**
 * This class is duplicated for each JAXP subpackage so keep it in
 * sync.  It is package private.
 *
 * This code is designed to implement the JAXP 1.1 spec pluggability
 * feature and is designed to run on JDK version 1.1 and later including
 * JVMs that perform early linking like the Microsoft JVM in IE 5.  Note
 * however that it must be compiled on a JDK version 1.2 or later system
 * since it calls Thread#getContextClassLoader().  The code also runs both
 * as part of an unbundled jar file and when bundled as part of the JDK.
 */
class FactoryFinder {
    /** Temp debug code - this will be removed after we test everything
     */
    private static boolean debug = false;
    static Properties cacheProps= new Properties();
    static SecuritySupport ss = new SecuritySupport() ;
    static boolean firstTime = true;

    // Define system property "jaxp.debug" to get output
    static {
        // Use try/catch block to support applets, which throws
        // SecurityException out of this code.
        try {
            String val = ss.getSystemProperty("jaxp.debug");
            // Allow simply setting the prop to turn on debug
            debug = val != null && (! "false".equals(val));
        } catch (SecurityException se) {
            debug = false;
        }
    }
    

    private static void dPrint(String msg) {
        if (debug) {
            System.err.println("JAXP: " + msg);
        }
    }
    
    /**
     * Create an instance of a class using the specified ClassLoader and
     * optionally fall back to the current ClassLoader if not found.
     *
     * @param className Name of the concrete class corresponding to the
     * service provider
     *
     * @param cl ClassLoader to use to load the class, null means to use
     * the bootstrap ClassLoader
     *
     * @param doFallback true if the current ClassLoader should be tried as
     * a fallback if the class is not found using cl
     */
    private static Object newInstance(String className, ClassLoader cl,
                                      boolean doFallback)
        throws ConfigurationError
    {
        // assert(className != null);

        try {
            Class providerClass;
            if (cl == null) {
                // If classloader is null Use the bootstrap ClassLoader.  
                // Thus Class.forName(String) will use the current
                // ClassLoader which will be the bootstrap ClassLoader.
                providerClass = Class.forName(className);
            } else {
                try {
                    providerClass = cl.loadClass(className);
                } catch (ClassNotFoundException x) {
                    if (doFallback) {
                        // Fall back to current classloader
                        cl = FactoryFinder.class.getClassLoader();
                        providerClass = Class.forName(className, true, cl);
                    } else {
                        throw x;
                    }
                }
            }
                        
            Object instance = providerClass.newInstance();
            dPrint("created new instance of " + providerClass +
                   " using ClassLoader: " + cl);
            return instance;
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
     * @return Class object of factory, never null
     *
     * @param factoryId             Name of the factory to find, same as
     *                              a property name
     * @param fallbackClassName     Implementation class name, if nothing else
     *                              is found.  Use null to mean no fallback.
     *
     * Package private so this code can be shared.
     */
    static Object find(String factoryId, String fallbackClassName)
        throws ConfigurationError
    {        

        // Figure out which ClassLoader to use for loading the provider
        // class.  If there is a Context ClassLoader then use it.
        
        ClassLoader classLoader = ss.getContextClassLoader();
        
        if (classLoader == null) {
            // if we have no Context ClassLoader
            // so use the current ClassLoader
            classLoader = FactoryFinder.class.getClassLoader();
        }

        dPrint("find factoryId =" + factoryId);
        
        // Use the system property first
        try {
            String systemProp = ss.getSystemProperty(factoryId);
            if( systemProp!=null) {                
                dPrint("found system property, value=" + systemProp);
                return newInstance(systemProp, classLoader, true );
            }
        } catch (SecurityException se) {
            //if first option fails due to any reason we should try next option in the
            //look up algorithm.
        }

        // try to read from $java.home/lib/jaxp.properties
        try {
            String javah = ss.getSystemProperty("java.home");
            String configFile = javah + File.separator +
                "lib" + File.separator + "jaxp.properties";
            String factoryClassName = null;
            if(firstTime){
                synchronized(cacheProps){
                    if(firstTime){
                        File f=new File( configFile );
                        firstTime = false;
                        if(ss.doesFileExist(f)){
                            dPrint("Read properties file "+f);
                            //cacheProps.load( new FileInputStream(f));
                            cacheProps.load(ss.getFileInputStream(f));
                        }
                    }
                }
            }
            factoryClassName = cacheProps.getProperty(factoryId);            

            if(factoryClassName != null){
                dPrint("found in $java.home/jaxp.properties, value=" + factoryClassName);
                return newInstance(factoryClassName, classLoader, true);
            }
        } catch(Exception ex ) {
            if( debug ) ex.printStackTrace();
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

        dPrint("loaded from fallback value: " + fallbackClassName);
        return newInstance(fallbackClassName, classLoader, true);
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

        dPrint("found jar resource=" + serviceId +
               " using ClassLoader: " + cl);

        // Read the service provider name in UTF-8 as specified in
        // the jar spec.  Unfortunately this fails in Microsoft
        // VJ++, which does not implement the UTF-8
        // encoding. Theoretically, we should simply let it fail in
        // that case, since the JVM is obviously broken if it
        // doesn't support such a basic standard.  But since there
        // are still some users attempting to use VJ++ for
        // development, we have dropped in a fallback which makes a
        // second attempt using the platform's default encoding. In
        // VJ++ this is apparently ASCII, which is a subset of
        // UTF-8... and since the strings we'll be reading here are
        // also primarily limited to the 7-bit ASCII range (at
        // least, in English versions), this should work well
        // enough to keep us on the air until we're ready to
        // officially decommit from VJ++. [Edited comment from
        // jkesselm]
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
            dPrint("found in resource, value="
                   + factoryClassName);

        // Note: here we do not want to fall back to the current
        // ClassLoader because we want to avoid the case where the
        // resource file was found using one ClassLoader and the
        // provider class was instantiated using a different one.
        return newInstance(factoryClassName, cl, false);
        }

        // No provider found
        return null;
    }

    static class ConfigurationError extends Error {
        private Exception exception;

        /**
         * Construct a new instance with the specified detail string and
         * exception.
         */
        ConfigurationError(String msg, Exception x) {
            super(msg);
            this.exception = x;
        }

        Exception getException() {
            return exception;
        }
    }

}
