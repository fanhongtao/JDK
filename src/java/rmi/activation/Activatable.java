/*
 * @(#)Activatable.java	1.34 03/12/19
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.rmi.activation;

import java.lang.reflect.Constructor;

import java.rmi.activation.UnknownGroupException;
import java.rmi.activation.UnknownObjectException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.MarshalledObject;
import java.rmi.NoSuchObjectException;

import java.rmi.server.*;

import sun.rmi.server.ActivatableServerRef;

/**
 * The <code>Activatable</code> class provides support for remote
 * objects that require persistent access over time and that
 * can be activated by the system.
 *
 * <p>For the constructors and static <code>exportObject</code> methods,
 * the stub for a remote object being exported is obtained as described in
 * {@link java.rmi.server.UnicastRemoteObject}.
 *
 * @author	Ann Wollrath
 * @version	1.34, 03/12/19
 * @since	1.2
 */
public abstract class Activatable extends RemoteServer {

    /** 
     * @serial Activation Identifier for this object.
     */
    private ActivationID id;
    /** indicate compatibility with the Java 2 SDK v1.2 version of class */
    private static final long serialVersionUID = -3120617863591563455L;
    
    /**
     * Constructor used to register and export the object on a
     * specified port (an anonymous port is chosen if port=0) .
     *
     * A concrete subclass of this class must call this constructor to
     * register and export the object during <i>initial</i> construction.  As
     * a side-effect of activatable object construction, the remote
     * object is both "registered" with the activation system and
     * "exported" (on an anonymous port if port=0) to the RMI runtime
     * so that it is available to accept incoming calls from clients.
     *
     * @param location the location for classes for this object
     * @param data the object's initialization data
     * @param port the port on which the object is exported (an anonymous
     * port is used if port=0)
     * @param restart if true, the object is restarted (reactivated) when
     * either the activator is restarted or the object's activation group
     * is restarted after an unexpected crash; if false, the object is only
     * activated on demand.  Specifying <code>restart</code> to be
     * <code>true</code> does not force an initial immediate activation of
     * a newly registered object;  initial activation is lazy.
     * @exception ActivationException if object registration fails.
     * @exception RemoteException if either of the following fails:
     * a) registering the object with the activation system or b) exporting
     * the object to the RMI runtime.
     * @since 1.2
     */
    protected Activatable(String location,
			  MarshalledObject data,
			  boolean restart,
			  int port)
	throws ActivationException, RemoteException
    {
	super();
	id = exportObject(this, location, data, restart, port);
    }
    
    /**
     * Constructor used to register and export the object on a
     * specified port (an anonymous port is chosen if port=0) . <p>
     *
     * A concrete subclass of this class must call this constructor to
     * register and export the object during <i>initial</i> construction.  As
     * a side-effect of activatable object construction, the remote
     * object is both "registered" with the activation system and
     * "exported" (on an anonymous port if port=0) to the RMI runtime
     * so that it is available to accept incoming calls from clients.
     *
     * @param location the location for classes for this object
     * @param data the object's initialization data
     * @param restart if true, the object is restarted (reactivated) when
     * either the activator is restarted or the object's activation group
     * is restarted after an unexpected crash; if false, the object is only
     * activated on demand.  Specifying <code>restart</code> to be
     * <code>true</code> does not force an initial immediate activation of
     * a newly registered object;  initial activation is lazy.
     * @param port the port on which the object is exported (an anonymous
     * port is used if port=0)
     * @param csf the client-side socket factory for making calls to the
     * remote object
     * @param ssf the server-side socket factory for receiving remote calls
     * @exception ActivationException if object registration fails.
     * @exception RemoteException if either of the following fails:
     * a) registering the object with the activation system or b) exporting
     * the object to the RMI runtime.
     * @since 1.2
     */
    protected Activatable(String location,
			  MarshalledObject data,
			  boolean restart,
			  int port,
			  RMIClientSocketFactory csf,
			  RMIServerSocketFactory ssf)
	throws ActivationException, RemoteException
    {
	super();
	id = exportObject(this, location, data, restart, port, csf, ssf);
    }

    /**
     * Constructor used to activate/export the object on a specified
     * port. An "activatable" remote object must have a constructor that
     * takes two arguments: <ul>
     * <li>the object's activation identifier (<code>ActivationID</code>), and
     * <li>the object's initialization data (a <code>MarshalledObject</code>).
     * </ul><p>
     *
     * A concrete subclass of this class must call this constructor when it is
     * <i>activated</i> via the two parameter constructor described above. As
     * a side-effect of construction, the remote object is "exported"
     * to the RMI runtime (on the specified <code>port</code>) and is
     * available to accept incoming calls from clients.
     *
     * @param id activation identifier for the object
     * @param port the port number on which the object is exported
     * @exception RemoteException if exporting the object to the RMI
     * runtime fails
     * @since 1.2
     */
    protected Activatable(ActivationID id, int port)
	throws RemoteException 
    {
	super();
	this.id = id;
	exportObject(this, id, port);
    }

    /**
     * Constructor used to activate/export the object on a specified
     * port. An "activatable" remote object must have a constructor that
     * takes two arguments: <ul>
     * <li>the object's activation identifier (<code>ActivationID</code>), and
     * <li>the object's initialization data (a <code>MarshalledObject</code>).
     * </ul><p>
     *
     * A concrete subclass of this class must call this constructor when it is
     * <i>activated</i> via the two parameter constructor described above. As
     * a side-effect of construction, the remote object is "exported"
     * to the RMI runtime (on the specified <code>port</code>) and is
     * available to accept incoming calls from clients.
     *
     * @param id activation identifier for the object
     * @param port the port number on which the object is exported
     * @param csf the client-side socket factory for making calls to the
     * remote object
     * @param ssf the server-side socket factory for receiving remote calls
     * @exception RemoteException if exporting the object to the RMI
     * runtime fails
     * @since 1.2
     */
    protected Activatable(ActivationID id, int port,
			  RMIClientSocketFactory csf,
			  RMIServerSocketFactory ssf)
	throws RemoteException 
    {
	super();
	this.id = id;
	exportObject(this, id, port, csf, ssf);
    }
    
    /**
     * Returns the object's activation identifier.  The method is
     * protected so that only subclasses can obtain an object's
     * identifier.
     * @return the object's activation identifier
     * @since 1.2
     */
    protected ActivationID getID() {
	return id;
    }

    /** 
     * Register an object descriptor for an activatable remote
     * object so that is can be activated on demand.
     *
     * @param desc  the object's descriptor
     * @return the stub for the activatable remote object
     * @exception UnknownGroupException if group id in <code>desc</code>
     * is not registered with the activation system
     * @exception ActivationException if activation system is not running
     * @exception RemoteException if remote call fails
     * @since 1.2
     */
    public static Remote register(ActivationDesc desc)
	throws UnknownGroupException, ActivationException, RemoteException
    {
	// register object with activator.
	ActivationID id =
	    ActivationGroup.getSystem().registerObject(desc);
	return sun.rmi.server.ActivatableRef.getStub(desc, id);
    }
    
    /**
     * Informs the system that the object with the corresponding activation
     * <code>id</code> is currently inactive. If the object is currently
     * active, the object is "unexported" from the RMI runtime (only if
     * there are no pending or in-progress calls)
     * so the that it can no longer receive incoming calls. This call
     * informs this VM's ActivationGroup that the object is inactive,
     * that, in turn, informs its ActivationMonitor. If this call
     * completes successfully, a subsequent activate request to the activator
     * will cause the object to reactivate. The operation may still
     * succeed if the object is considered active but has already
     * unexported itself.
     *
     * @param id the object's activation identifier
     * @return true if the operation succeeds (the operation will
     * succeed if the object in currently known to be active and is
     * either already unexported or is currently exported and has no
     * pending/executing calls); false is returned if the object has
     * pending/executing calls in which case it cannot be deactivated
     * @exception UnknownObjectException if object is not known (it may
     * already be inactive)
     * @exception ActivationException if group is not active
     * @exception RemoteException if call informing monitor fails
     * @since 1.2
     */
    public static boolean inactive(ActivationID id)
	throws UnknownObjectException, ActivationException, RemoteException
    {
	return ActivationGroup.currentGroup().inactiveObject(id);
    }

    /**
     * Revokes previous registration for the activation descriptor
     * associated with <code>id</code>. An object can no longer be
     * activated via that <code>id</code>.
     *
     * @param id the object's activation identifier
     * @exception UnknownObjectException if object (<code>id</code>) is unknown
     * @exception ActivationException if activation system is not running
     * @exception RemoteException if remote call to activation system fails
     * @since 1.2
     */
    public static void unregister(ActivationID id)
	throws UnknownObjectException, ActivationException, RemoteException
    {
	ActivationGroup.getSystem().unregisterObject(id);
    }

    /**
     * This <code>exportObject</code> method may be invoked explicitly
     * by an "activatable" object, that does not extend the
     * <code>Activatable</code> class, in order to both a) register
     * the object's activation descriptor, constructed from the supplied
     * <code>location</code>, and <code>data</code>, with
     * the activation system (so the object can be activated), and
     * b) export the remote object, <code>obj</code>, on a specific
     * port (if port=0, then an anonymous port is chosen). Once the
     * object is exported, it can receive incoming RMI calls.<p>
     *
     * This method does not need to be called if <code>obj</code>
     * extends <code>Activatable</code>, since the first constructor
     * calls this method.
     *
     * @param obj the object being exported
     * @param location the object's code location
     * @param data the object's bootstrapping data
     * @param restart if true, the object is restarted (reactivated) when
     * either the activator is restarted or the object's activation group
     * is restarted after an unexpected crash; if false, the object is only
     * activated on demand.  Specifying <code>restart</code> to be
     * <code>true</code> does not force an initial immediate activation of
     * a newly registered object;  initial activation is lazy.
     * @param port the port on which the object is exported (an anonymous
     * port is used if port=0)
     * @return the activation identifier obtained from registering the
     * descriptor, <code>desc</code>, with the activation system
     * the wrong group
     * @exception ActivationException if activation group is not active
     * @exception RemoteException if object registration or export fails
     * @since 1.2
     */
    public static ActivationID exportObject(Remote obj,
					    String location,
					    MarshalledObject data,
					    boolean restart,
					    int port)
	throws ActivationException, RemoteException
    {
	ActivationDesc desc = new ActivationDesc(obj.getClass().getName(),
						 location, data, restart);
	ActivationID id = ActivationGroup.getSystem().registerObject(desc);
	Remote stub = exportObject(obj, id, port);
	ActivationGroup.currentGroup().activeObject(id, obj); 
	return id;
    }

    /**
     * This <code>exportObject</code> method may be invoked explicitly
     * by an "activatable" object, that does not extend the
     * <code>Activatable</code> class, in order to both a) register
     * the object's activation descriptor, constructed from the supplied
     * <code>location</code>, and <code>data</code>, with
     * the activation system (so the object can be activated), and
     * b) export the remote object, <code>obj</code>, on a specific
     * port (if port=0, then an anonymous port is chosen). Once the
     * object is exported, it can receive incoming RMI calls.<p>
     *
     * This method does not need to be called if <code>obj</code>
     * extends <code>Activatable</code>, since the first constructor
     * calls this method.
     *
     * @param obj the object being exported
     * @param location the object's code location
     * @param data the object's bootstrapping data
     * @param restart if true, the object is restarted (reactivated) when
     * either the activator is restarted or the object's activation group
     * is restarted after an unexpected crash; if false, the object is only
     * activated on demand.  Specifying <code>restart</code> to be
     * <code>true</code> does not force an initial immediate activation of
     * a newly registered object;  initial activation is lazy.
     * @param port the port on which the object is exported (an anonymous
     * port is used if port=0)
     * @param csf the client-side socket factory for making calls to the
     * remote object
     * @param ssf the server-side socket factory for receiving remote calls
     * @return the activation identifier obtained from registering the
     * descriptor, <code>desc</code>, with the activation system
     * the wrong group
     * @exception ActivationException if activation group is not active
     * @exception RemoteException if object registration or export fails
     * @since 1.2
     */
    public static ActivationID exportObject(Remote obj,
					    String location,
					    MarshalledObject data,
					    boolean restart,
					    int port,
					    RMIClientSocketFactory csf,
					    RMIServerSocketFactory ssf)
	throws ActivationException, RemoteException
    {
	ActivationDesc desc = new ActivationDesc(obj.getClass().getName(),
						 location, data, restart);
	ActivationID id = ActivationGroup.getSystem().registerObject(desc);
	Remote stub = exportObject(obj, id, port, csf, ssf);
	ActivationGroup.currentGroup().activeObject(id, obj); 
	return id;
    }

    /** 
     * Export the activatable remote object to the RMI runtime to make
     * the object available to receive incoming calls. The object is
     * exported on an anonymous port, if <code>port</code> is zero. <p>
     *
     * During activation, this <code>exportObject</code> method should
     * be invoked explicitly by an "activatable" object, that does not
     * extend the <code>Activatable</code> class. There is no need for objects
     * that do extend the <code>Activatable</code> class to invoke this
     * method directly; this method is called by the second constructor
     * above (which a subclass should invoke from its special activation
     * constructor).
     * 
     * @return the stub for the activatable remote object
     * @param obj the remote object implementation
     * @param id the object's  activation identifier
     * @param port the port on which the object is exported (an anonymous
     * port is used if port=0)
     * @exception RemoteException if object export fails
     * @since 1.2
     */
    public static Remote exportObject(Remote obj,
				      ActivationID id,
				      int port)
	throws RemoteException
    {
	return exportObject(obj, new ActivatableServerRef(id, port));
    }

    /** 
     * Export the activatable remote object to the RMI runtime to make
     * the object available to receive incoming calls. The object is
     * exported on an anonymous port, if <code>port</code> is zero. <p>
     *
     * During activation, this <code>exportObject</code> method should
     * be invoked explicitly by an "activatable" object, that does not
     * extend the <code>Activatable</code> class. There is no need for objects
     * that do extend the <code>Activatable</code> class to invoke this
     * method directly; this method is called by the second constructor
     * above (which a subclass should invoke from its special activation
     * constructor).
     * 
     * @return the stub for the activatable remote object
     * @param obj the remote object implementation
     * @param id the object's  activation identifier
     * @param port the port on which the object is exported (an anonymous
     * port is used if port=0)
     * @param csf the client-side socket factory for making calls to the
     * remote object
     * @param ssf the server-side socket factory for receiving remote calls
     * @exception RemoteException if object export fails
     * @since 1.2
     */
    public static Remote exportObject(Remote obj,
				      ActivationID id,
				      int port,
				      RMIClientSocketFactory csf,
				      RMIServerSocketFactory ssf)
	throws RemoteException
    {
	return exportObject(obj, new ActivatableServerRef(id, port, csf, ssf));
    }
    
    /**
     * Remove the remote object, obj, from the RMI runtime. If
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
	throws NoSuchObjectException
    {
	return sun.rmi.transport.ObjectTable.unexportObject(obj, force);
    }

    /**
     * Exports the specified object using the specified server ref.
     */
    private static Remote exportObject(Remote obj, ActivatableServerRef sref)
	throws RemoteException
    {
	// if obj extends Activatable, set its ref.
	if (obj instanceof Activatable) {
	    ((Activatable) obj).ref = sref;

	}
	return sref.exportObject(obj, null, false);
    }
}
