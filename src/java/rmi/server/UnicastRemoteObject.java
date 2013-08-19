/*
 * @(#)UnicastRemoteObject.java	1.30 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package java.rmi.server;

import java.rmi.*;

/**
 * The UnicastRemoteObject class defines a non-replicated remote
 * object whose references are valid only while the server process is
 * alive.  The UnicastRemoteObject class provides support for
 * point-to-point active object references (invocations, parameters,
 * and results) using TCP streams.
 *
 * <p>Objects that require remote behavior should extend RemoteObject,
 * typically via UnicastRemoteObject. If UnicastRemoteObject is not
 * extended, the implementation class must then assume the
 * responsibility for the correct semantics of the hashCode, equals,
 * and toString methods inherited from the Object class, so that they
 * behave appropriately for remote objects.
 *
 * @version 1.30, 01/23/03
 * @author  Ann Wollrath
 * @author  Peter Jones
 * @since   JDK1.1
 * @see     RemoteServer
 * @see     RemoteObject
 */
public class UnicastRemoteObject extends RemoteServer {

    /**
     * @serial port number on which to export object 
     */
    private int port = 0;

    /**
     * @serial client-side socket factory (if any) 
     */
    private RMIClientSocketFactory csf = null;
    
    /** 
     * @serial server-side socket factory (if any) to use when
     * exporting object 
     */
    private RMIServerSocketFactory ssf = null;

    /* indicate compatibility with JDK 1.1.x version of class */
    private static final long serialVersionUID = 4974527148936298033L;

    /**
     * Creates and exports a new UnicastRemoteObject object using an
     * anonymous port.
     * @throws RemoteException if failed to export object
     * @since JDK1.1
     */
    protected UnicastRemoteObject() throws RemoteException
    {
	this(0);
    }

    /**
     * Creates and exports a new UnicastRemoteObject object using the
     * particular supplied port.
     * @param port the port number on which the remote object receives calls
     * (if <code>port</code> is zero, an anonymous port is chosen)
     * @throws RemoteException if failed to export object
     * @since 1.2
     */
    protected UnicastRemoteObject(int port) throws RemoteException
    {
	this.port = port;
	exportObject((Remote)this, port);
    }

    /**
     * Creates and exports a new UnicastRemoteObject object using the
     * particular supplied port and socket factories.
     * @param port the port number on which the remote object receives calls
     * (if <code>port</code> is zero, an anonymous port is chosen)
     * @param csf the client-side socket factory for making calls to the
     * remote object
     * @param ssf the server-side socket factory for receiving remote calls
     * @throws RemoteException if failed to export object
     * @since 1.2
     */
    protected UnicastRemoteObject(int port,
				  RMIClientSocketFactory csf,
				  RMIServerSocketFactory ssf)
	throws RemoteException
    {
	this.port = port;
	this.csf = csf;
	this.ssf = ssf;
	exportObject((Remote)this, port, csf, ssf);
    }

    /**
     * Re-export the remote object when it is deserialized.
     */
    private void readObject(java.io.ObjectInputStream in) 
	throws java.io.IOException, java.lang.ClassNotFoundException
    {
	in.defaultReadObject();
	reexport();
    }
    
    /**
     * Returns a clone of the remote object that is distinct from
     * the original.
     *
     * @exception CloneNotSupportedException if clone failed due to
     * a RemoteException.
     * @return the new remote object
     * @since JDK1.1
     */
    public Object clone() throws CloneNotSupportedException
    {
	try {
	    UnicastRemoteObject cloned = (UnicastRemoteObject)super.clone();
	    cloned.reexport();
	    return cloned;
	} catch (RemoteException e) {
	    throw new ServerCloneException("Clone failed", e);
	}
    }

    /*
     * Exports this UnicastRemoteObject using its initialized fields because
     * its creation bypassed running its constructors (via deserialization
     * or cloning, for example).
     */
    private void reexport() throws RemoteException
    {
	if (csf == null && ssf == null) {
	    exportObject((Remote)this, port);
	} else {
	    exportObject((Remote)this, port, csf, ssf);
	}
    }

    /** 
     * Exports the remote object to make it available to receive incoming
     * calls using an anonymous port.
     * @param obj the remote object to be exported
     * @return remote object stub
     * @exception RemoteException if export fails
     * @since JDK1.1
     */
    public static RemoteStub exportObject(Remote obj)
	throws RemoteException
    {
	return (RemoteStub)exportObject(obj, 0);
    }

    /* parameter types for server ref constructor invocation used below */
    private static Class[] portParamTypes = {
	int.class
    };

    /** 
     * Exports the remote object to make it available to receive incoming
     * calls, using the particular supplied port.
     * @param obj the remote object to be exported
     * @param port the port to export the object on
     * @return remote object stub
     * @exception RemoteException if export fails
     * @since 1.2
     */
    public static Remote exportObject(Remote obj, int port)
	throws RemoteException
    {
	// prepare arguments for server ref constructor
	Object[] args = new Object[] { new Integer(port) };

	return exportObject(obj, "UnicastServerRef", portParamTypes, args);
    }

    /* parameter types for server ref constructor invocation used below */
    private static Class[] portFactoryParamTypes = {
	int.class, RMIClientSocketFactory.class, RMIServerSocketFactory.class
    };

    /**
     * Exports the remote object to make it available to receive incoming
     * calls, using a transport specified by the given socket factory.
     * @param obj the remote object to be exported
     * @param port the port to export the object on
     * @param csf the client-side socket factory for making calls to the
     * remote object
     * @param ssf the server-side socket factory for receiving remote calls
     * @return remote object stub
     * @exception RemoteException if export fails
     * @since 1.2
     */
    public static Remote exportObject(Remote obj, int port,
				      RMIClientSocketFactory csf,
				      RMIServerSocketFactory ssf)
	throws RemoteException
    {
	// prepare arguments for server ref constructor
	Object[] args = new Object[] { new Integer(port), csf, ssf };
	
	return exportObject(obj, "UnicastServerRef2", portFactoryParamTypes,
			    args);
    }

    /**
     * Removes the remote object, obj, from the RMI runtime. If
     * successful, the object can no longer accept incoming RMI calls.
     * If the force parameter is true, the object is forcibly unexported
     * even if there are pending calls to the remote object or the
     * remote object still has calls in progress.  If the force
     * parameter is false, the object is only unexported if there are
     * no pending or in progress calls to the object.
     *
     * @param obj the remote object to be unexported
     * @param force if true, unexports the object even if there are
     * pending or in-progress calls; if false, only unexports the object
     * if there are no pending or in-progress calls
     * @return true if operation is successful, false otherwise
     * @exception NoSuchObjectException if the remote object is not
     * currently exported
     * @since 1.2
     */
    public static boolean unexportObject(Remote obj, boolean force)
	throws java.rmi.NoSuchObjectException
    {
	return sun.rmi.transport.ObjectTable.unexportObject(obj, force);
    }

    /*
     * Creates an instance of given server ref type with constructor chosen
     * by indicated paramters and supplied with given arguements, and
     * export remote object with it.
     */
    private static Remote exportObject(Remote obj, String refType,
				       Class[] params, Object[] args)
	throws RemoteException
    {
	// compose name of server ref class and find it
	String refClassName = RemoteRef.packagePrefix + "." + refType;
	Class refClass;
	try {
	    refClass = Class.forName(refClassName);
	} catch (ClassNotFoundException e) {
	    throw new ExportException(
		"No class found for server ref type: " + refType);
	}

	if (!ServerRef.class.isAssignableFrom(refClass)) {
	    throw new ExportException(
		"Server ref class not instance of " +
		ServerRef.class.getName() + ": " + refClass.getName());
	}

	// create server ref instance using given constructor and arguments
	ServerRef serverRef;
	try {
	    java.lang.reflect.Constructor cons =
		refClass.getConstructor(params);
	    serverRef = (ServerRef) cons.newInstance(args);
	    // if impl does extends UnicastRemoteObject, set its ref
	    if (obj instanceof UnicastRemoteObject)
		((UnicastRemoteObject)obj).ref = serverRef;

	} catch (Exception e) {
	    throw new ExportException(
		"Exception creating instance of server ref class: " +
		refClass.getName(), e);
	}

	return serverRef.exportObject(obj, null);
    }
}

