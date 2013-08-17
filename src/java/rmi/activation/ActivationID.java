/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.rmi.activation;

import java.rmi.*;
import java.rmi.server.RemoteObject;
import java.rmi.server.RemoteRef;
import java.rmi.server.UID;

import sun.rmi.server.RemoteProxy;
import sun.security.action.GetPropertyAction;

/**
 * Activation makes use of special identifiers to denote remote
 * objects that can be activated over time. An activation identifier
 * (an instance of the class <code>ActivationID</code>) contains several
 * pieces of information needed for activating an object: <ul>
 * <li> a remote reference to the object's activator, and
 * <li> a unique identifier for the object. </ul> <p>
 *
 * An activation id for an object can be obtained by registering
 * an object with the activation system. Registration is accomplished
 * in a few ways: <ul>
 * <li>via the <code>Activatable.register</code> method
 * <li>via the first <code>Activatable</code> constructor (that takes
 * three arguments and both registers and exports the object, and
 * <li>via the first <code>Activatable.exportObject</code> method
 * that takes the activation descriptor, object and port as arguments;
 * this method both registers and exports the object. </ul>
 *
 * @author	Ann Wollrath
 * @version	1.20, 02/06/02
 * @see		Activatable
 * @since	1.2
 */
public class ActivationID implements java.io.Serializable {
    /**
     * @serial the object's activator 
     */
    private Activator activator;

    /**
     * @serial the object's unique id 
     */
    private UID uid = new UID();

    /** indicate compatibility with the Java 2 SDK v1.2 version of class */
    private static final long serialVersionUID = -4608673054848209235L;

    /**
     * The constructor for <code>ActivationID</code> takes a single
     * argument, activator, that specifies a remote reference to the
     * activator responsible for activating the object associated with
     * this identifier. An instance of <code>ActivationID</code> is globally
     * unique.
     *
     * @param activator reference to the activator responsible for
     * activating the object
     * @since 1.2
     */
    public ActivationID(Activator activator) {
	this.activator = activator;
    }

    /**
     * Activate the object for this id.
     *
     * @param force if true, forces the activator to contact the group
     * when activating the object (instead of returning a cached reference);
     * if false, returning a cached value is acceptable.
     * @return the reference to the active remote object
     * @exception ActivationException if activation fails
     * @exception UnknownObjectException if the object is unknown
     * @exception RemoteException if remote call fails
     * @since 1.2
     */
    public Remote activate(boolean force)
	throws ActivationException, UnknownObjectException, RemoteException
    {
 	try {
 	    MarshalledObject mobj =
 		(MarshalledObject)(activator.activate(this, force));
 	    return (Remote)mobj.get();
 	} catch (UnknownObjectException e) {
 	    throw e;
 	} catch (RemoteException e) {
 	    throw e;
 	} catch (java.io.IOException e) {
 	    throw new ActivationException("activation failed", e);
 	} catch (java.lang.ClassNotFoundException e) {
 	    throw new ActivationException("activation failed", e);
	}
	
    }
    
    /**
     * Returns a hashcode for the activation id.  Two identifiers that
     * refer to the same remote object will have the same hash code.
     *
     * @see java.util.Hashtable
     * @since 1.2
     */
    public int hashCode() {
	return uid.hashCode();
    }

    /**
     * Compares two activation ids for content equality.
     * Returns true if both of the following conditions are true:
     * 1) the unique identifiers equivalent (by content), and
     * 2) the activator specified in each identifier
     *    refers to the same remote object.
     *
     * @param	obj	the Object to compare with
     * @return	true if these Objects are equal; false otherwise.
     * @see		java.util.Hashtable
     * @since 1.2
     */
    public boolean equals(Object obj) {
	if (obj instanceof ActivationID) {
	    ActivationID id = (ActivationID)obj;
	    return (uid.equals(id.uid) && activator.equals(id.activator));
	} else {
	    return false;
	}
    }
    
    /**
     * writeObject for object serialization. Writes out a
     * <code>java.rmi.server.UID</code> and the reference to the
     * activator responsible for activating the object associated with
     * this id, the remote reference contained in the
     * <code>activator</code> field.
     *
     * @serialData Writes out a <code>java.rmi.server.UID</code>, and
     * the unqualified class name, in <code>UTF-8</code>, of the
     * remote reference contained in the <code>activator</code>
     * field. Delegates to the <code>activator</code>'s remote
     * reference to write itself to <code>out</code>.  Directly calls
     * <code>writeExternal(ObjectStream out)</code> on the return
     * value of <code>activator.getRef()</code>. Default serialization
     * is not used.  
     */
    private void writeObject(java.io.ObjectOutputStream out)
	throws java.io.IOException, java.lang.ClassNotFoundException
    {
	out.writeObject(uid);
	RemoteRef ref = ((RemoteObject)activator).getRef();
	
	out.writeUTF(ref.getRefClass(out));
	ref.writeExternal(out);
    }

    /**
     * readObject for object serialization. Reads in a
     * <code>java.rmi.server.UID</code> and a remote reference. The
     * remote reference is read via a direct call to
     * <code>readExternal(ObjectInputStream in)</code>. Default
     * serialization is not used. The reference is used to create the
     * <code>activator</code> field in this object. That is, the
     * <code>activator</code> field is set to the stub returned from
     * <code>RemoteProxy.getStub(activatorClassName, ref)</code>.
     */
    private void readObject(java.io.ObjectInputStream in) 
	throws java.io.IOException, java.lang.ClassNotFoundException
    {
	uid = (UID)in.readObject();
	
	try {
	    Class refClass = Class.forName(RemoteRef.packagePrefix + "." +
					   in.readUTF());
	    RemoteRef ref = (RemoteRef)refClass.newInstance();
	    ref.readExternal(in);
	    activator =
		(Activator)RemoteProxy.getStub(activatorClassName, ref);
	    
	} catch (InstantiationException e) {
	    throw new UnmarshalException("Unable to create remote reference",
					 e);
	} catch (IllegalAccessException e) {
	    throw new UnmarshalException("Illegal access creating remote reference");
	}
    }

    private static String activatorClassName;
    
    static 
    {
	activatorClassName = (String) java.security.AccessController.doPrivileged(
	      new GetPropertyAction("java.rmi.activation.activator.class",
				    "sun.rmi.server.Activation$ActivatorImpl"));
    }
}
