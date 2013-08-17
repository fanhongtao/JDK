/*
 * @(#)Naming.java	1.2 00/01/12
 *
 * Copyright 1996-2000 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package java.rmi;

import java.rmi.registry.*;
import java.net.URL;
import java.net.MalformedURLException;

/**
 * The <code>Naming</code> class provides methods for storing and obtaining
 * references to remote objects in the remote object registry. The
 * <code>Naming</code> class's methods take, as one of their arguments, a name
 * that is URL formatted <code>java.lang.String</code> of the form:
 *
 * <PRE>
 *    //host:port/name
 * </PRE>
 * 
 * <P>where <code>host</code> is the host (remote or local) where the registry
 * is located, <code>port</code> is the port number on which the registry
 * accepts calls, and where <code>name</code> is a simple string uninterpreted
 * by the registry. Both <code>host</code> and <code>port</code> are optional.
 * If <code>host</code> is omitted, the host defaults to the local host. If
 * <code>port</code> is omitted, then the port defaults to 1099, the
 * "well-known" port that RMI's registry, <code>rmiregistry</code>, uses.
 *
 * <P><em>Binding</em> a name for a remote object is associating or
 * registering a name for a remote object that can be used at a later time to
 * look up that remote object.  A remote object can be associated with a name
 * using the <code>Naming</code> class's <code>bind</code> or
 * <code>rebind</code> methods.
 *
 * <P>Once a remote object is registered (bound) with the RMI registry on the
 * local host, callers on a remote (or local) host can lookup the remote
 * object by name, obtain its reference, and then invoke remote methods on the
 * object.  A registry may be shared by all servers running on a host or an
 * individual server process may create and use its own registry if desired
 * (see <code>java.rmi.registry.LocateRegistry.createRegistry</code> method
 * for details).
 *
 * @version 1.10, 07/12/98
 * @author  Ann Wollrath
 * @author  Roger Riggs
 * @since   JDK1.1
 * @see     java.rmi.registry.Registry
 * @see     java.rmi.registry.LocateRegistry
 * @see     java.rmi.registry.LocateRegistry#createRegistry(int)
 */
public final class Naming {
    /**
     * Disallow anyone from creating one of these
     */
    private Naming() {}

    /**
     * Returns a reference, a stub, for the remote object associated
     * with the specified <code>name</code>.
     *
     * @param name a URL-formatted name for the remote object
     * @return a reference for a remote object
     * @exception NotBoundException if name is not currently bound
     * @exception RemoteException if registry could not be contacted
     * @exception AccessException if this operation is not permitted (if
     * originating from a non-local host, for example)
     * @since JDK1.1
     */
    public static Remote lookup(String name)
	throws NotBoundException,
	    java.net.MalformedURLException,
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
     * Binds the specified <code>name</code> to a remote object.
     *
     * @param name a URL-formatted name for the remote object
     * @param obj a reference for the remote object (usually a stub)
     * @exception AlreadyBoundException if name is already bound
     * @exception MalformedURLException if the name is not an appropriately
     *  formatted URL
     * @exception RemoteException if registry could not be contacted
     * @exception AccessException if this operation is not permitted (if
     * originating from a non-local host, for example)
     * @since JDK1.1
     */
    public static void bind(String name, Remote obj)
	throws AlreadyBoundException,
	    java.net.MalformedURLException,
	    RemoteException
    {
	URL url = cleanURL(name);
	Registry registry = getRegistry(url);

	if (obj == null)
	    throw new NullPointerException("cannot bind to null");

	registry.bind(getName(url), obj);
    }

    /**
     * Destroys the binding for the specified name that is associated
     * with a remote object.
     *
     * @param name a URL-formatted name associated with a remote object
     * @exception NotBoundException if name is not currently bound
     * @exception MalformedURLException if the name is not an appropriately
     *  formatted URL
     * @exception RemoteException if registry could not be contacted
     * @exception AccessException if this operation is not permitted (if
     * originating from a non-local host, for example)
     * @since JDK1.1
     */
    public static void unbind(String name)
	throws RemoteException,
	    NotBoundException,
	    java.net.MalformedURLException
    {
	URL url = cleanURL(name);
	Registry registry = getRegistry(url);

	registry.unbind(getName(url));
    }

    /** 
     * Rebinds the specified name to a new remote object. Any existing
     * binding for the name is replaced.
     *
     * @param name a URL-formatted name associated with the remote object
     * @param obj new remote object to associate with the name
     * @exception MalformedURLException if the name is not an appropriately
     *  formatted URL
     * @exception RemoteException if registry could not be contacted
     * @exception AccessException if this operation is not permitted (if
     * originating from a non-local host, for example)
     * @since JDK1.1
     */
    public static void rebind(String name, Remote obj)
	throws RemoteException, java.net.MalformedURLException
    {
	URL url = cleanURL(name);
	Registry registry = getRegistry(url);

	if (obj == null)
	    throw new NullPointerException("cannot bind to null");

	registry.rebind(getName(url), obj);
    }

    /**
     * Returns an array of the names bound in the registry.  The names are
     * URL-formatted strings. The array contains a snapshot of the names
     * present in the registry at the time of the call.
     *
     * @param name a URL-formatted name that specifies the remote registry
     * @return an array of names (in the appropriate URL format) bound
     *  in the registry
     * @exception MalformedURLException if the name is not an appropriately
     *  formatted URL
     * @exception RemoteException if registry could not be contacted.
     * @since JDK1.1
     */
    public static String[] list(String name)
	throws RemoteException, java.net.MalformedURLException
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

    /**
     * Returns a registry reference obtained from information in the URL.
     */
    private static Registry getRegistry(URL url)
	throws RemoteException
    {
	String host = url.getHost();
	int port = url.getPort();

	return LocateRegistry.getRegistry(host, port);
    }

    /**
     * Extracts only the name portion from the specified URL.
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
	URL url = new URL("http:");

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
