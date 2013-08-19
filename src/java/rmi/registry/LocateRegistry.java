/*
 * @(#)LocateRegistry.java	1.30 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.rmi.registry;

import java.rmi.RemoteException;
import java.rmi.server.ObjID;
import java.rmi.server.RMIClientSocketFactory;
import java.rmi.server.RMIServerSocketFactory;

/**
 * <code>LocateRegistry</code> is used to obtain a reference to a bootstrap
 * remote object registry on a particular host (including the local host), or
 * to create a remote object registry that accepts calls on a specific port.
 *
 * <p> Note that a <code>getRegistry</code> call does not actually make a
 * connection to the remote host.  It simply creates a local reference to
 * the remote registry and will succeed even if no registry is running on
 * the remote host.  Therefore, a subsequent method invocation to a remote
 * registry returned as a result of this method may fail.
 *
 * @version 1.30, 01/23/03
 * @author  Ann Wollrath
 * @author  Peter Jones
 * @since   JDK1.1
 * @see     java.rmi.registry.Registry
 */
public final class LocateRegistry {

    /**
     * Private constructor to disable public construction.
     */
    private LocateRegistry() {}

    /**
     * Returns a reference to the the remote object <code>Registry</code> for
     * the local host on the default registry port of 1099.
     *
     * @return reference (a stub) to the remote object registry
     * @exception RemoteException if the reference could not be created
     * @since JDK1.1
     */
    public static Registry getRegistry()
	throws RemoteException
    {
	return getRegistry(null, Registry.REGISTRY_PORT);
    }

    /**
     * Returns a reference to the the remote object <code>Registry</code> for
     * the local host on the specified <code>port</code>.
     *
     * @param port port on which the registry accepts requests
     * @return reference (a stub) to the remote object registry
     * @exception RemoteException if the reference could not be created
     * @since JDK1.1
     */
    public static Registry getRegistry(int port)
	throws RemoteException
    {
	return getRegistry(null, port);
    }

    /**
     * Returns a reference to the remote object <code>Registry</code> on the
     * specified <code>host</code> on the default registry port of 1099.  If
     * <code>host</code> is <code>null</code>, the local host is used.
     *
     * @param host host for the remote registry
     * @return reference (a stub) to the remote object registry
     * @exception RemoteException if the reference could not be created
     * @since JDK1.1
     */
    public static Registry getRegistry(String host)
	throws RemoteException
    {
	return getRegistry(host, Registry.REGISTRY_PORT);
    }

    /**
     * Returns a reference to the remote object <code>Registry</code> on the
     * specified <code>host</code> and <code>port</code>. If <code>host</code>
     * is <code>null</code>, the local host is used.
     *
     * @param host host for the remote registry
     * @param port port on which the registry accepts requests
     * @return reference (a stub) to the remote object registry
     * @exception RemoteException if the reference could not be created
     * @since JDK1.1
     */
    public static Registry getRegistry(String host, int port)
	throws RemoteException
    {
	return getRegistry(host, port, null);
    }

    /**
     * Returns a locally created remote reference to the remote object
     * <code>Registry</code> on the specified <code>host</code> and
     * <code>port</code>.  Communication with this remote registry will
     * use the supplied <code>RMIClientSocketFactory</code> <code>csf</code>
     * to create <code>Socket</code> connections to the registry on the
     * remote <code>host</code> and <code>port</code>.
     *
     * @param host host for the remote registry
     * @param port port on which the registry accepts requests
     * @param csf  client-side <code>Socket</code> factory used to
     *      make connections to the registry.  If <code>csf</code>
     *      is null, then the default client-side <code>Socket</code>
     *      factory will be used in the registry stub.
     * @return reference (a stub) to the remote registry
     * @exception RemoteException if the reference could not be created
     * @since 1.2
     */
    public static Registry getRegistry(String host, int port,
				       RMIClientSocketFactory csf)
	throws RemoteException
    {
	Registry registry = null;

	if (port <= 0)
	    port = Registry.REGISTRY_PORT;

	if (host == null || host.length() == 0) {
	    // If host is blank (as returned by "file:" URL in 1.0.2 used in
	    // java.rmi.Naming), try to convert to real local host name so
	    // that the RegistryImpl's checkAccess will not fail.
	    try {
		host = java.net.InetAddress.getLocalHost().getHostAddress();
	    } catch (Exception e) {
		// If that failed, at least try "" (localhost) anyway...
		host = "";
	    }
	}
	
	if (csf == null) {
	    registry = (Registry) sun.rmi.server.RemoteProxy.
		getStub("sun.rmi.registry.RegistryImpl", ObjID.REGISTRY_ID,
			host, port);
	} else {
	    registry = (Registry) sun.rmi.server.RemoteProxy.
		getStub("sun.rmi.registry.RegistryImpl", ObjID.REGISTRY_ID,
			host, port, csf);
	}

	return registry;
    }

    /**
     * Creates and exports a <code>Registry</code> on the local host
     * that accepts requests on the specified <code>port</code>.
     *
     * @param port the port on which the registry accepts requests
     * @return the registry
     * @exception RemoteException if the registry could not be exported
     * @since JDK1.1
     */
    public static Registry createRegistry(int port) throws RemoteException
    {
	return new sun.rmi.registry.RegistryImpl(port);
    }

    /**
     * Creates and exports a <code>Registry</code> on the local host that
     * uses custom socket factories for communication with that registry.
     * The registry that is created listens for incoming requests on the
     * given <code>port</code> using a <code>ServerSocket</code> created
     * from the supplied <code>RMIServerSocketFactory</code>.  A client
     * that receives a reference to this registry will use a
     * <code>Socket</code> created from the supplied
     * <code>RMIClientSocketFactory</code>.
     *
     * @param port port on which the registry accepts requests
     * @param csf  client-side <code>Socket</code> factory used to
     *      make connections to the registry
     * @param ssf  server-side <code>ServerSocket</code> factory
     *      used to accept connections to the registry
     * @return the registry
     * @exception RemoteException if the registry could not be exported
     * @since 1.2
     */
    public static Registry createRegistry(int port, 
					  RMIClientSocketFactory csf, 
					  RMIServerSocketFactory ssf) 
	throws RemoteException
    {
	return new sun.rmi.registry.RegistryImpl(port, csf, ssf);
    }
}
