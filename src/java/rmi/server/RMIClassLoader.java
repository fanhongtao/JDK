/*
 * @(#)RMIClassLoader.java	1.11 98/07/01
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

/**
 * The RMIClassLoader class provides static methods for loading classes
 * over the network.  Classes can be loaded from either a particular URL,
 * or from the URL specified in the <b>java.rmi.server.codebase</b>
 * system property.
 * 
 * @version	1.11, 07/01/98
 * @author Ann Wollrath
 */
public class RMIClassLoader {
    /*
     * Disallow anyone from creating one of these.
     */
    private RMIClassLoader() {}

    private static LoaderHandler handler = null;

    private static synchronized LoaderHandler getHandler() 
    {
	if (handler == null) {
	    try {
		Class cl = Class.forName(LoaderHandler.packagePrefix +
					 ".LoaderHandler");
		handler = (LoaderHandler)cl.newInstance();
	    } catch (Exception e) {
		throw new Error("No LoaderHandler present");
	    }
	}
	return handler;
    }

    /**
     * Load a class from the URL specified in the
     * <b>java.rmi.server.codebase</b> property.
     * @param name  the name of the class to load
     * @return the Class object representing the loaded class
     * @exception MalformedURLException
     *            The system property <b>java.rmi.server.codebase</b>
     *            does not contain a valid URL.
     * @exception ClassNotFoundException
     *            A definition for the class could not
     *            be found at the codebase URL.
     */
    public static Class loadClass(String name)
	throws MalformedURLException, ClassNotFoundException
    {
	return getHandler().loadClass(name);
    }
    
    /**
     * Load a class from a URL.
     * @param codebase  the URL from which to load the class
     * @param name      the name of the class to load
     * @return the Class object representing the loaded class
     * @exception MalformedURLException
     *            The codebase paramater was null.
     * @exception ClassNotFoundException
     *            A definition for the class could not
     *            be found at the specified URL.
     */
    public static Class loadClass(URL codebase, String name)
	throws MalformedURLException, ClassNotFoundException
    {
	return getHandler().loadClass(codebase, name);
    }

    /**
     * Returns the security context of the given class loader
     * @param loader a class loader from which to get the security
     * 	      context
     * @return the security context (e.g., a URL)
     */
    public static Object getSecurityContext(ClassLoader loader)
    {
	return getHandler().getSecurityContext(loader);
    }
}
