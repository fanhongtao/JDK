/*
 * @(#)RegistryHandler.java	1.3 98/07/01
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
import java.rmi.server.RemoteStub;


/**
 * RegistryHandler is used to interface to the private
 * implementation.
 */
public interface RegistryHandler {

    /**
     * Returns a "stub" for contacting a remote registry
     * on the specified host and port.
     */
    Registry registryStub(String host, int port)
	throws RemoteException, UnknownHostException;

    /**
     * Constructs and exports a Registry on the specified port.
     * The port must be non-zero.
     */
    Registry registryImpl(int port) throws RemoteException;
}
