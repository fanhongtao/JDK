/*
 * @(#)RegistryHandler.java	1.2 00/01/12
 *
 * Copyright 1997-2000 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */

package java.rmi.registry;

import java.rmi.RemoteException;
import java.rmi.UnknownHostException;

/**
 * <code>RegistryHandler</code> is an interface used internally by the RMI
 * runtime in previous implementation versions.  It should never be accessed
 * by application code.
 *
 * @version 1.5, 09/21/98
 * @author  Ann Wollrath
 * @since   JDK1.1
 * @deprecated no replacement
 */
public interface RegistryHandler {

    /**
     * Returns a "stub" for contacting a remote registry
     * on the specified host and port.
     *
     * @deprecated no replacement.  As of JDK1.2, RMI no longer uses the
     * <code>RegistryHandler</code> to obtain the registry's stub.
     */
    Registry registryStub(String host, int port)
	throws RemoteException, UnknownHostException;

    /**
     * Constructs and exports a Registry on the specified port.
     * The port must be non-zero.
     *
     * @deprecated no replacement.  As of JDK1.2, RMI no longer uses the
     * <code>RegistryHandler</code> to obtain the registry's implementation.
     */
    Registry registryImpl(int port) throws RemoteException;
}
