/*
 * @(#)LoaderHandler.java	1.10 98/07/12
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
 * <code>LoaderHandler</code> is an interface used internally by the RMI
 * runtime in previous implementation versions.  It should never be accessed
 * by application code.
 *
 * @version 1.10, 07/12/98
 * @author  Ann Wollrath
 * @since   JDK1.1
 *
 * @deprecated no replacement
 */
public interface LoaderHandler {

    /** package of system <code>LoaderHandler</code> implementation */
    final static String packagePrefix = "sun.rmi.server";

    /**
     * Loads a class from the location specified by the
     * <code>java.rmi.server.codebase</code> property.
     *
     * @param  name the name of the class to load
     * @return the <code>Class</code> object representing the loaded class
     * @exception MalformedURLException
     *            if the system property <b>java.rmi.server.codebase</b>
     *            contains an invalid URL
     * @exception ClassNotFoundException
     *            if a definition for the class could not
     *            be found at the codebase location.
     * @since JDK1.1
     * @deprecated no replacement
     */
    Class loadClass(String name)
	throws MalformedURLException, ClassNotFoundException;

    /**
     * Loads a class from a URL.
     *
     * @param codebase  the URL from which to load the class
     * @param name      the name of the class to load
     * @return the <code>Class</code> object representing the loaded class
     * @exception MalformedURLException
     *            if the <code>codebase</code> paramater
     *            contains an invalid URL
     * @exception ClassNotFoundException
     *            if a definition for the class could not
     *            be found at the specified URL
     * @since JDK1.1
     * @deprecated no replacement
     */
    Class loadClass(URL codebase, String name)
	throws MalformedURLException, ClassNotFoundException;

    /**
     * Returns the security context of the given class loader.
     *
     * @param loader  a class loader from which to get the security context
     * @return the security context
     * @since JDK1.1
     * @deprecated no replacement
     */
    Object getSecurityContext(ClassLoader loader);
}
