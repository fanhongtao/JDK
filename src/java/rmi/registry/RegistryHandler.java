/*
 * @(#)RegistryHandler.java	1.5 98/09/21
 *
 * Copyright 1997, 1998 by Sun Microsystems, Inc.,
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
