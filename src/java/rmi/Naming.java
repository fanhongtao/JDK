/*
 * @(#)Naming.java	1.6 98/11/30
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
package java.rmi;

import java.rmi.registry.*;
import java.net.URL;
import java.net.MalformedURLException;

/**
 * This is the bootstrap mechanism for obtaining references to remote
 * objects based on Uniform Resource Locator (URL) syntax.  The URL
 * for a remote object is specified using the usual host, port and
 * name:
 *<br>	rmi://host:port/name
 *<br>	host = host name of registry  (defaults to current host)
 *<br>	port = port number of registry (defaults to the registry port number)
 *<br>  name = name for remote object
 */
public final class Naming {
    /*
     * Disallow anyone from creating one of these
     */
    private Naming() {}

    /**
     * Returns the remote object for the URL.
     * @exception RemoteException If registry could not be contacted.
     * @exception NotBoundException If name is not currently bound.
     */
    public static Remote lookup(String name)
	throws NotBoundException,
	    java.net.MalformedURLException,
	    UnknownHostException,
	    RemoteException
    {
	URL url = cleanURL(name);
	Registry registry = getRegistry(url);

	String file = getName(url);
	if (file == null)
	    return registry;
	return registry.lookup(file);
    }
    
    /**
     * Binds the name to the specified remote object.
     * @exception RemoteException If registry could not be contacted.
     * @exception AlreadyBoundException If name is already bound.
     */
    public static void bind(String name, Remote obj)
	throws AlreadyBoundException,
	    java.net.MalformedURLException,
	    UnknownHostException,
	    RemoteException
    {
	URL url = cleanURL(name);
	Registry registry = getRegistry(url);

	if (obj == null)
	    throw new NullPointerException("cannot bind to null");

	registry.bind(getName(url), obj);
    }
    
    /**
     * Unbind the name.
     * @exception RemoteException If registry could not be contacted.
     * @exception NotBoundException If name is not currently bound.
     */
    public static void unbind(String name)
	throws RemoteException,
	    NotBoundException,
	    java.net.MalformedURLException,
	    UnknownHostException
    {
	URL url = cleanURL(name);
	Registry registry = getRegistry(url);

	registry.unbind(getName(url));
    }

    /** 
     * Rebind the name to a new object; replaces any existing binding.
     * @exception RemoteException If registry could not be contacted.
     */
    public static void rebind(String name, Remote obj)
	throws RemoteException,
	    java.net.MalformedURLException,
	    UnknownHostException
    {
	URL url = cleanURL(name);
	Registry registry = getRegistry(url);

	if (obj == null)
	    throw new NullPointerException("cannot bind to null");

	registry.rebind(getName(url), obj);
    }
    
    /**
     * Returns an array of strings of the URLs in the registry.
     * The array contains a snapshot of the names present in the registry.
     * @exception RemoteException If registry could not be contacted.
     */
    public static String[] list(String name)
	throws RemoteException,
	    java.net.MalformedURLException,
	    UnknownHostException
    {
	URL url = cleanURL(name);
	Registry registry = getRegistry(url);

	String host = url.getHost();
	int port = url.getPort();

	String prefix = "rmi:";
 	if (port > 0 || !host.equals(""))
	    prefix += "//" + host;
	if (port > 0)
	    prefix += ":" + port;
	prefix += "/";

	String[] names = registry.list();
	for (int i = 0; i < names.length; i++) {
	    names[i] = prefix + names[i];
	}
	return names;
    }

    /** Function to find registry from URL
     */
    private static Registry getRegistry(URL url)
	throws RemoteException, UnknownHostException
    {
	String host = url.getHost();
	int port = url.getPort();

	return LocateRegistry.getRegistry(host, port);
    }

    /** Function to extract only the name from the URL.
     */
    private static String getName(URL url)
    {
	String name = url.getFile();
	if (name == null || name.equals("/"))
	    return null;
	return name.substring(1);
    }

    /**
     * Creates an HTTP URL from the specified "name" URL to be used in
     * obtaining a registry reference. It checks for and removes the
     * "rmi:" protocol if it was specified as part of the "name".
     *
     * @exception MalformedURLException if hostname in url contains '#' or
     *            if incorrect protocol specified
     */
    private static URL cleanURL(String name)
	throws java.net.MalformedURLException
    {
	URL url = new URL("file:");

	// Anchors (i.e. '#') are meaningless in rmi URLs - disallow them
	if (name.indexOf('#') >= 0) {
	    throw new MalformedURLException
		("Invalid character, '#', in URL: " + name);
	}

	// remove the approved protocol
	if (name.startsWith("rmi:"))
	    name = name.substring(4);

	// No protocol must remain
	int colon = name.indexOf(':');
	if (colon >= 0 && colon < name.indexOf('/') )
	    throw new java.net.MalformedURLException("No protocol needed");

	url = new URL(url, name);
	return url;
    }
}
