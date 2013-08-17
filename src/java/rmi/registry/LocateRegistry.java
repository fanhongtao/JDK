/*
 * @(#)LocateRegistry.java	1.9 01/12/10
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.rmi.registry;

import java.rmi.RemoteException;
import java.rmi.UnknownHostException;

/**
 * This class is used to obtain the bootstrap Registry on a particular
 * host (including your local host). The following example demonstrates usage
 * (minus exception handling):
 *
 * <br> Server wishes to make itself available to others:
 *	<br> SomeService service = ...; // remote object for service
 *	<br> Registry registry = LocateRegistry.getRegistry();
 *	<br> registry.bind("I Serve", service);
 *
 * <br> The client wishes to make requests of the above service:
 *	<br> Registry registry = LocateRegistry.getRegistry("foo.services.com");
 *	<br> SomeService service = (SomeService)registry.lookup("I Serve");
 *	<br> service.requestService(...);
 *
 * @see Registry
 */
public final class LocateRegistry {

    /**
     * Find registry package prefix: assumes that the implementation class
     * RegistryHandler is located in the package defined by the prefix.
     */
    private static String registryPkgPrefix =
        System.getProperty("java.rmi.registry.packagePrefix",
			   "sun.rmi.registry");
			  
    private static RegistryHandler handler = null;
    
    /*
     * Private constructor to disable public construction.
     */
    private LocateRegistry() {}

    /**
     * Returns the remote object Registry for the local host.
     */
    public static Registry getRegistry()
	throws RemoteException
    {
	try {
	    return getRegistry(null, Registry.REGISTRY_PORT);
	} catch (UnknownHostException ex) {
	    // Can't happen
	}
	return null;
    }

    /**
     * Returns the remote object Registry on the current host at the
     * specified port.
     */
    public static Registry getRegistry(int port)
	throws RemoteException
    {
	try {
	    return getRegistry(null, port);
	} catch (UnknownHostException ex) {
	    // Can't happen
	}
	return null;
    }
    
    /**
     * Returns the remote object Registry on the specified host at a
     * default (i.e., well-known) port number.  If the host
     * <code>String</code> reference is <code>null</code>, the local
     * host is used.
     */
    public static Registry getRegistry(String host)
	throws RemoteException, UnknownHostException
    {
	return getRegistry(host, Registry.REGISTRY_PORT);
    }
    
    /**
     * Returns the remote object Registry on the specified host at the
     * specified port.  If port <= 0, the default Registry port number
     * is used.  If the host <code>String</code> reference is
     * <code>null</code>, the local host is used.
     *
     * @exception UnknownHostException If the host is not known.
     */
    public static Registry getRegistry(String host, int port)
	throws RemoteException, UnknownHostException
    {
	if (handler != null) {
	    return handler.registryStub(host, port);
	} else {
	    throw new RemoteException("No registry handler present");
	}
	
    }

    /**
     * Create and export a registry on the local host.
     *
     * @param port the port on which the registry is to be exported
     * @exception RemoteException If failure occurs during remote
     * object creation.
     */
    public static Registry createRegistry(int port) throws RemoteException
    {
	if (handler != null) {
	    return handler.registryImpl(port);
	} else {
	    throw new RemoteException("No registry handler present");
	}
    }

    static {
	try {
	    Class cl = Class.forName(registryPkgPrefix + ".RegistryHandler");
	    handler = (RegistryHandler)(cl.newInstance());
	} catch (Exception e) {
	}
    }
}
