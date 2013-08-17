/*
 * @(#)LoaderHandler.java	1.6 01/12/10
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.rmi.server;

import java.net.MalformedURLException;
import java.net.URL;

public interface LoaderHandler {
    /**
     * Find loader handler package prefix: assumes that the implementation of
     * the LoaderHandler class is located in the package defined by the
     * prefix.
     */
    final static String packagePrefix =
    System.getProperty("java.rmi.loader.packagePrefix", "sun.rmi.server");

    /**
     * Load class using java.rmi.server.codebase property.
     *
     * @exception java.lang.ClassNotFoundException if the class could not be
     *              found.
     * @exception java.net.MalformedURLException   if the URL is malformed.
     */
    Class loadClass(String name)
	throws MalformedURLException, ClassNotFoundException;

    /**
     * Load class from codebase URL specified.
     *
     * @exception java.lang.ClassNotFoundException if the class could not be
     *              found.
     * @exception java.net.MalformedURLException   if the URL is malformed.
     */
    Class loadClass(URL codebase, String name)
	throws MalformedURLException, ClassNotFoundException;

    /**
     * Returns the security context of the given class loader
     * (e.g., a URL)
     */
    Object getSecurityContext(ClassLoader loader);
    
}
