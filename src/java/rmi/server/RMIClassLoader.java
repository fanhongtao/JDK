/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.rmi.server;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * <code>RMIClassLoader</code> provides static methods for loading classes
 * from a network location (one or more URLs) and obtaining the location
 * from which an existing class can be loaded.  These methods are used by
 * the RMI runtime when marshalling and unmarshalling classes of parameters
 * and return values.
 *
 * @version 1.23, 02/06/02
 * @author  Ann Wollrath
 * @author  Peter Jones
 * @since   JDK1.1
 */
public class RMIClassLoader {
    /*
     * Disallow anyone from creating one of these.
     */
    private RMIClassLoader() {}

    /**
     * Loads a class from the codebase URL path specified by the
     * <code>java.rmi.server.codebase</code> property.
     *
     * @param name  the name of the class to load
     * @return the <code>Class</code> object representing the loaded class
     * @exception MalformedURLException
     *            if the system property <b>java.rmi.server.codebase</b>
     *            contains an invalid URL
     * @exception ClassNotFoundException
     *            if a definition for the class could not
     *            be found at the codebase location
     * @since JDK1.1
     * @deprecated replaced by <code>loadClass(String,String)</code> method
     * @see #loadClass(String,String)
     */
    public static Class loadClass(String name)
	throws MalformedURLException, ClassNotFoundException
    {
	return sun.rmi.server.LoaderHandler.loadClass(name);
    }

    /**
     * Loads a class from a codebase URL.  If the given codebase is
     * <code>null</code>, then the <code>Class</code> object returned is
     * equivalent to the <code>Class</code> object returned by
     * <code>RMIClassLoader.loadClass(name)</code>.
     *
     * @param codebase  the URL to load the class from
     * @param name      the name of the class to load
     * @return the <code>Class</code> object representing the loaded class
     * @exception MalformedURLException
     *            if the <code>codebase</code> paramater
     *            contains an invalid non-null URL
     * @exception ClassNotFoundException
     *            if a definition for the class could not
     *            be found at the specified URL
     * @since JDK1.1
     */
    public static Class loadClass(URL codebase, String name)
	throws MalformedURLException, ClassNotFoundException
    {
	return sun.rmi.server.LoaderHandler.loadClass(codebase, name);
    }

    /**
     * Loads a class from a codebase URL path.  If the given codebase is
     * <code>null</code>, then the <code>Class</code> object returned is
     * equivalent to the <code>Class</code> object returned by
     * <code>RMIClassLoader.loadClass(name)</code>.
     *
     * @param codebase  the list of space-separated URLs to load the class from
     * @param name      the name of the class to load
     * @return the <code>Class</code> object representing the loaded class
     * @exception MalformedURLException
     *            if the <code>codebase</code> paramater
     *            contains an invalid non-null URL
     * @exception ClassNotFoundException
     *            if a definition for the class could not
     *            be found at the specified location
     * @since 1.2
     */
    public static Class loadClass(String codebase, String name)
	throws MalformedURLException, ClassNotFoundException
    {
	return sun.rmi.server.LoaderHandler.loadClass(codebase, name);
    }

    /**
     * Returns a class loader that loads classes from the given codebase URL
     * path.  The class loader returned is the class loader that the
     * <code>#loadClass(String,String)</code> method would use to load classes
     * from the given codebase.  If a class loader with the same codebase URL
     * path already exists for RMI runtime, it will be returned; otherwise, a
     * new class loader will be created.  If the given codebase is null, it
     * returns the class loader used to load classes via the
     * <code>#loadClass(String)</code> method.
     * 
     * @param codebase the list of space-separated URLs which the
     * the class loader will load classes from
     * @return a class loader that loads classes from the given codebase URL
     * path
     * @exception MalformedURLException
     *            if the <code>codebase</code> paramater
     *            contains an invalid non-null URL
     * @exception SecurityException
     *            if the caller does not have permission to
     *            connect to all of the URLs in <code>codebase</code> URL path
     * @since 1.3
     */
    public static ClassLoader getClassLoader(String codebase)
	throws MalformedURLException, SecurityException
    {
	return sun.rmi.server.LoaderHandler.getClassLoader(codebase);
    }

    /**
     * Returns the class annotation (representing the location for
     * a class) that RMI will use to annotate the call stream when
     * marshalling objects of the given class.
     *
     * @param cl  the class to obtain the annotation for
     * @return a string to be used to annotate the class when marshalled
     * @since 1.2
     */
    public static String getClassAnnotation(Class cl) {
	return sun.rmi.server.LoaderHandler.getClassAnnotation(cl);
    }

    /**
     * Returns the security context of the given class loader.
     *
     * @param loader  a class loader from which to get the security context
     * @return the security context
     * @since JDK1.1
     * @deprecated no replacement.  As of the Java 2 platform v1.2, RMI no
     * longer uses this method to obtain a class loader's security context.
     * @see java.lang.SecurityManager#getSecurityContext()
     */
    public static Object getSecurityContext(ClassLoader loader)
    {
	return sun.rmi.server.LoaderHandler.getSecurityContext(loader);
    }
}
