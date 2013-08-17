/*
 * @(#)ServerRef.java	1.7 01/12/10
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
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
