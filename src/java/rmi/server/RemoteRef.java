/*
 * @(#)RemoteRef.java	1.7 98/08/12
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
 * RemoteRef represents the handle for a remote object.
 */
public interface RemoteRef extends java.io.Externalizable {

    /**
     * Find server package prefix: assumes that the implementation of
     * server ref classes (e.g., UnicastRef, UnicastServerRef) are
     * located in the package defined by the prefix.
     */
    final static String packagePrefix =
    System.getProperty("java.rmi.server.packagePrefix", "sun.rmi.server");
    
    /**
     * Creates an appropriate call object for a new remote method
     * invocation on this object.  Passing operation array and index,
     * allows the stubs generator to assign the operation indexes and
     * interpret them. The remote reference may need the operation to
     * encode in the call.
     *
     * @exception RemoteException if registry could not be contacted.
     */
    RemoteCall newCall(RemoteObject obj, Operation[] op, int opnum, long hash) 
	throws RemoteException;
    
    /**
     * Executes the remote call.
     * 
     * Invoke will raise any "user" exceptions which
     * should pass through and not be caught by the stub.  If any
     * exception is raised during the remote invocation, invoke should
     * take care of cleaning up the connection before raising the
     * "user" or remote exception.
     *
     * @exception java.lang.Exception if a general exception occurs.
     */
    void invoke(RemoteCall call) throws Exception;
    
    /**
     * Allows the remote reference to clean up (or reuse) the connection.
     * Done should only be called if the invoke returns successfully
     * (non-exceptionally) to the stub.
     *
     * @exception RemoteException if registry could not be contacted.
     */
    void done(RemoteCall call) throws RemoteException;
    
    /**
     * Returns the class name of the ref type to be serialized onto
     * the stream 'out'.
     */
    String getRefClass(java.io.ObjectOutput out);
    
    /**
     * Returns a hashcode for a remote object.  Two remote object stubs
     * that refer to the same remote object will have the same hash code
     * (in order to support remote objects as keys in hash tables).
     *
     * @see		java.util.Hashtable
     */
    int remoteHashCode();

    /**
     * Compares two remote objects for equality.
     * Returns a boolean that indicates whether this remote object is
     * equivalent to the specified Object. This method is used when a
     * remote object is stored in a hashtable.
     * @param	obj	the Object to compare with
     * @return	true if these Objects are equal; false otherwise.
     * @see		java.util.Hashtable
     */
    boolean remoteEquals(RemoteRef obj);

    /**
     * Returns a String that represents the reference of this remote
     * object.
     */
    String remoteToString();
	
}
