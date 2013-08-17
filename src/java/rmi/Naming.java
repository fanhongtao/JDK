/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package java.rmi;

import java.rmi.registry.*;
import java.net.MalformedURLException;

/**
 * The <code>Naming</code> class provides methods for storing and obtaining
 * references to remote objects in the remote object registry. The
 * <code>Naming</code> class's methods take, as one of their arguments, a name
 * that is a URL formatted <code>java.lang.String</code> of the form:
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
 * @version 1.13, 09/05/99
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
     * @exception AccessException if this operation is not permitted
     * @exception MalformedURLException if the name is not an appropriately
     *  formatted URL
     * @since JDK1.1
     */
    public static Remote lookup(String name)
	throws NotBoundException,
	    java.net.MalformedURLException,
	    RemoteException
    {
	ParsedNamingURL parsed = parseURL(name);
	Registry registry = getRegistry(parsed);

	if (parsed.name == null)
	    return registry;
	return registry.lookup(parsed.name);
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
	ParsedNamingURL parsed = parseURL(name);
	Registry registry = getRegistry(parsed);

	if (obj == null)
	    throw new NullPointerException("cannot bind to null");

	registry.bind(parsed.name, obj);
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
	ParsedNamingURL parsed = parseURL(name);
	Registry registry = getRegistry(parsed);

	registry.unbind(parsed.name);
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
	ParsedNamingURL parsed = parseURL(name);
	Registry registry = getRegistry(parsed);

	if (obj == null)
	    throw new NullPointerException("cannot bind to null");

	registry.rebind(parsed.name, obj);
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
	ParsedNamingURL parsed = parseURL(name);
	Registry registry = getRegistry(parsed);

	String prefix = "rmi:";
 	if (parsed.port > 0 || !parsed.host.equals(""))
	    prefix += "//" + parsed.host;
	if (parsed.port > 0)
	    prefix += ":" + parsed.port;
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
    private static Registry getRegistry(ParsedNamingURL parsed)
	throws RemoteException
    {
	return LocateRegistry.getRegistry(parsed.host, parsed.port);
    }

    /**
     * Fix for: 4251878 java.rmi.Naming shouldn't rely upon the
     * parsing functionality of java.net.URL
     *
     * Dissect Naming URL strings to obtain referenced host, port and
     * object name.
     *
     * @return an object which contains each of the above
     * components.
     *
     * @exception MalformedURLException if hostname in url contains '#' or
     *            if incorrect protocol specified
     */
    private static ParsedNamingURL parseURL(String url) 
	throws MalformedURLException 
    {
	ParsedNamingURL parsed = new ParsedNamingURL();
	int startFile = -1;

	// remove the approved protocol
	if (url.startsWith("rmi:")) {
	    url = url.substring(4);
	}

	// Anchors (i.e. '#') are meaningless in rmi URLs - disallow them
	if (url.indexOf('#') >= 0) {
	    throw new MalformedURLException
		("Invalid character, '#', in URL: " + url);
	}

	// No protocol must remain
	int checkProtocol = url.indexOf(':');
	if (checkProtocol >= 0 && (checkProtocol < url.indexOf('/')))
	    throw new java.net.MalformedURLException("invalid protocol: " +
	        url.substring(0, checkProtocol));

	if (url.startsWith("//")) {
	    final int startHost = 2;
	    int nextSlash = url.indexOf("/", startHost);
	    if (nextSlash >= 0) {
		startFile = nextSlash + 1;
	    } else {
		// no trailing slash implies no name
		nextSlash = url.length();
		startFile = nextSlash;
	    }

	    int colon = url.indexOf(":", startHost);
	    if ((colon > 1) && (colon < nextSlash)) {
		// explicit port supplied
		try {
		    parsed.port = 
			Integer.parseInt(url.substring(colon + 1,
						       nextSlash));
		} catch (NumberFormatException e) {
		    throw new MalformedURLException(
		        "invalid port number: " + url);
		}
	    }

	    // if have colon then endhost, else end with slash
	    int endHost;
	    if (colon >= startHost) {
		endHost = colon;
	    } else {
		endHost = nextSlash;
	    }
	    parsed.host = url.substring(startHost, endHost);
	    
	} else if (url.startsWith("/")) {
	    startFile = 1;
	} else {
	    startFile = 0;
	}
	// set the bind name
	parsed.name = url.substring(startFile);
	if (parsed.name.equals("") || parsed.name.equals("/")) {
	    parsed.name = null;
	}

	return parsed;
    }

    /**
     * Simple class to enable multiple URL return values.
     */
    private static class ParsedNamingURL {
	String host = "";
	int port = Registry.REGISTRY_PORT;
	String name = null;
    }
}
