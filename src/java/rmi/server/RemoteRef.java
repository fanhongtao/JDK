/*
 * @(#)RemoteRef.java	1.12 98/07/08
 *
 * Copyright 1996-1998 by Sun Microsystems, Inc.,
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
 * <code>RemoteRef</code> represents the handle for a remote object. A
 * <code>RemoteStub</code> uses a remote reference to carry out a
 * remote method invocation to a remote object.
 *
 * @version 1.12, 07/08/98
 * @author  Ann Wollrath
 * @since   JDK1.1
 * @see     java.rmi.server.RemoteStub
 */
public interface RemoteRef extends java.io.Externalizable {

    /** indicate compatibility with JDK 1.1.x version of class */
    static final long serialVersionUID = 3632638527362204081L;

    /**
     * Initialize the server package prefix: assumes that the
     * implementation of server ref classes (e.g., UnicastRef,
     * UnicastServerRef) are located in the package defined by the
     * prefix.
     */
    final static String packagePrefix = "sun.rmi.server";

    /**
     * Invoke a method. This form of delegating method invocation
     * to the reference allows the reference to take care of
     * setting up the connection to the remote host, marshaling
     * some representation for the method and parameters, then
     * communicating the method invocation to the remote host.
     * This method either returns the result of a method invocation
     * on the remote object which resides on the remote host or
     * throws a RemoteException if the call failed or an
     * application-level exception if the remote invocation throws
     * an exception.
     *    
     * @param obj the object that contains the RemoteRef (e.g., the
     *            RemoteStub for the object.
     * @param method the method to be invoked
     * @param params the parameter list
     * @param opnum  a hash that may be used to represent the method
     * @exception Exception if any exception occurs during remote method
     * invocation
     * @since JDK1.2
     */
    Object invoke(Remote obj,
		  java.lang.reflect.Method method,
		  Object[] params,
		  long opnum)
	throws Exception;
    
    /**
     * Creates an appropriate call object for a new remote method
     * invocation on this object.  Passing operation array and index,
     * allows the stubs generator to assign the operation indexes and
     * interpret them. The remote reference may need the operation to
     * encode in the call.
     *
     * @since JDK1.1
     * @deprecated JDK1.2 style stubs no longer use this method. Instead of
     * using a sequence of method calls on the stub's the remote reference
     * (<code>newCall</code>, <code>invoke</code>, and <code>done</code>), a
     * stub uses a single method, <code>invoke(Remote, Method, Object[],
     * int)</code>, on the remote reference to carry out parameter
     * marshalling, remote method executing and unmarshalling of the return
     * value.
     *
     * <p>JDK1.2 stubs are generated using <code>rmic -v1.2</code>. By
     * default, <code>rmic</code> generates stubs compatible with JDK1.1 and
     * JDK1.2. The compatible stubs can also be generated using <code>rmic
     * -vcompat</code>.
     * @see #invoke(Remote,java.lang.reflect.Method,Object[],long)
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
     * @since JDK1.1
     * @deprecated JDK1.2 style stubs no longer use this method. Instead of
     * using a sequence of method calls to the remote reference
     * (<code>newCall</code>, <code>invoke</code>, and <code>done</code>), a
     * stub uses a single method, <code>invoke(Remote, Method, Object[],
     * int)</code>, on the remote reference to carry out parameter
     * marshalling, remote method executing and unmarshalling of the return
     * value.
     *
     * <p>JDK1.2 stubs are generated using <code>rmic -v1.2</code>. By
     * default, <code>rmic</code> generates stubs compatible with JDK1.1 and
     * JDK1.2. The compatible stubs can also be generated using <code>rmic
     * -vcompat</code>.
     * @see #invoke(Remote,java.lang.reflect.Method,Object[],long)
     */
    void invoke(RemoteCall call) throws Exception;
    
    /**
     * Allows the remote reference to clean up (or reuse) the connection.
     * Done should only be called if the invoke returns successfully
     * (non-exceptionally) to the stub.
     *
     * @since JDK1.1
     * @deprecated JDK1.2 style stubs no longer use this method. Instead of
     * using a sequence of method calls to the remote reference
     * (<code>newCall</code>, <code>invoke</code>, and <code>done</code>), a
     * stub uses a single method, <code>invoke(Remote, Method, Object[],
     * int)</code>, on the remote reference to carry out parameter
     * marshalling, remote method executing and unmarshalling of the return
     * value.
     *
     * <p>JDK1.2 stubs are generated using <code>rmic -v1.2</code>. By
     * default, <code>rmic</code> generates stubs compatible with JDK1.1 and
     * JDK1.2. The compatible stubs can also be generated using <code>rmic
     * -vcompat</code>.
     * @see #invoke(Remote,java.lang.reflect.Method,Object[],long)
     */
    void done(RemoteCall call) throws RemoteException;
    
    /**
     * Returns the class name of the ref type to be serialized onto
     * the stream 'out'.
     * @param out the output stream to which the reference will be serialized
     * @return the class name (without package qualification) of the reference
     * type
     * @since JDK1.1
     */
    String getRefClass(java.io.ObjectOutput out);
    
    /**
     * Returns a hashcode for a remote object.  Two remote object stubs
     * that refer to the same remote object will have the same hash code
     * (in order to support remote objects as keys in hash tables).
     *
     * @see		java.util.Hashtable
     * @since JDK1.1
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
     * @since JDK1.1
     */
    boolean remoteEquals(RemoteRef obj);

    /**
     * Returns a String that represents the reference of this remote
     * object.
     * @since JDK1.1
     */
    String remoteToString();
	
}
