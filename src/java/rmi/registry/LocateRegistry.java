/*
 * @(#)LocateRegistry.java	1.8 98/07/01
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
