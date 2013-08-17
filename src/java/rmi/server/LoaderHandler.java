/*
 * @(#)LoaderHandler.java	1.5 98/07/01
 *
 * Copyright 1995-1998 by Sun Microsystems, Inc.,
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
