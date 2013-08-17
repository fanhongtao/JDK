/*
 * @(#)ServerRef.java	1.5 98/08/12
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

import java.rmi.*;

/**
 * A ServerRef represents the server-side handle for a remote object
 * implementation.
 */
public interface ServerRef extends RemoteRef {

    /** 
     * Find or create a client stub object for the supplied Remote.
     * @param obj the remote object implementation
     * @param data information necessary to export the object
     * 		(e.g. port number)
     */
    RemoteStub exportObject(Remote obj, Object data)
	throws RemoteException;

    /**
     * Return the hostname of the current client.  When called from a
     * thread actively handling a remote method invocation the
     * hostname of the client is returned.
     * @exception ServerNotActiveException If called outside of servicing
     * a remote method invocation.
     */
    String getClientHost() throws ServerNotActiveException;
}
