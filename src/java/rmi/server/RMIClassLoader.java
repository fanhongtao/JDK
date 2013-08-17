/*
 * @(#)RMIClassLoader.java	1.16 98/07/12
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
 * @version 1.16, 07/12/98
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
     * Load a class from the codebase URL path specified by the
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
     * Load a class from a codebase URL.
     *
     * @param codebase  the URL to load the class from
     * @param name      the name of the class to load
     * @return the <code>Class</code> object representing the loaded class
     * @exception MalformedURLException
     *            if the <code>codebase</code> paramater
     *            contains an invalid URL
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
     * Load a class from a codebase URL path.
     *
     * @param codebase  the list of URLs to load the class from
     * @param name      the name of the class to load
     * @return the <code>Class</code> object representing the loaded class
     * @exception MalformedURLException
     *            if the <code>codebase</code> paramater
     *            contains an invalid URL
     * @exception ClassNotFoundException
     *            if a definition for the class could not
     *            be found at the specified location
     * @since JDK1.2
     */
    public static Class loadClass(String codebase, String name)
	throws MalformedURLException, ClassNotFoundException
    {
	return sun.rmi.server.LoaderHandler.loadClass(codebase, name);
    }

    /**
     * Returns the class annotation (representing the location for
     * a class) that RMI will use to annotate the call stream when
     * marshalling objects of the given class.
     *
     * @param cl  the class to obtain the annotation for
     * @return a string to be used to annotate the class when marshalled
     * @since JDK1.2
     */
    public static String getClassAnnotation(Class cl) {
	return sun.rmi.server.LoaderHandler.getClassAnnotation(cl);
    }

    /**
     * Return the security context of the given class loader.
     *
     * @param loader  a class loader from which to get the security context
     * @return the security context
     * @since JDK1.1
     * @deprecated no replacement.  As of JDK1.2, RMI no longer uses this
     * method to obtain a classloader's security context.
     * @see java.lang.SecurityManager#getSecurityContext()
     */
    public static Object getSecurityContext(ClassLoader loader)
    {
	return sun.rmi.server.LoaderHandler.getSecurityContext(loader);
    }
}
