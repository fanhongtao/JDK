/*
 * @(#)RemoteObject.java	1.8 98/08/12
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

import java.rmi.Remote;
import java.rmi.UnmarshalException;

/**
 * The RemoteObject class implements the java.lang.Object behavior for
 * remote objects.  RemoteObject provides the remote semantics of
 * Object by implementing methods for hashCode, equals, and toString.
 */
public abstract class RemoteObject implements Remote, java.io.Serializable {

    private static final long serialVersionUID = -3215090123894869218L;

    transient protected RemoteRef ref;
    
    /**
     * Create a remote object.
     */
    protected RemoteObject() {
	ref = null;
    }
    
    /**
     * Create a remote object, initialized with the specified remote reference.
     */
    protected RemoteObject(RemoteRef newref) {
	ref = newref;
    }

    /**
     * Returns a hashcode for a remote object.  Two remote object stubs
     * that refer to the same remote object will have the same hash code
     * (in order to support remote objects as keys in hash tables).
     *
     * @see		java.util.Hashtable
     */
    public int hashCode() {
	return (ref == null) ? super.hashCode() : ref.remoteHashCode();
    }

    /**
     * Compares two remote objects for equality.
     * Returns a boolean that indicates whether this remote object is
     * equivalent to the specified Object. This method is used when a
     * remote object is stored in a hashtable.
     * @param	obj	the Object to compare with
     * @return	true if these Objects are equal; false otherwise.
     * @see		java.util.Hashtable
     */
    public boolean equals(Object obj) {
	if (obj instanceof RemoteObject) {
	    if (ref == null) {
		return obj == this;
	    } else {
		return ref.remoteEquals(((RemoteObject)obj).ref);
	    }
	} else if (obj != null) {
	    /*
	     * Fix for 4099660: if object is not an instance of RemoteObject,
	     * use the result of its equals method, to support symmetry if a
	     * remote object implementation class that does not extend
	     * RemoteObject wishes to support equality with its stub objects.
	     */
	    return obj.equals(this);
	} else {
	    return false;
	}
    }

    /**
     * Returns a String that represents the value of this remote object.
     */
    public String toString()
    {
	String classname = this.getClass().getName();
	return (ref == null) ? classname :
	    classname + "[" +ref.remoteToString() + "]";
    }


    /**
     * writeObject for object serialization. Writes out the class name of
     * the remote reference and delegates to the reference to write out
     * its representation.
     */
    private void writeObject(java.io.ObjectOutputStream out)
	throws java.io.IOException, java.lang.ClassNotFoundException
    {
	if (ref == null) {
	    throw new java.rmi.MarshalException("Invalid remote object");
	} else {
	    out.writeUTF(ref.getRefClass(out));
	    ref.writeExternal(out);
	}
	
    }

    /**
     * readObject for object serialization. Reads in the class name of
     * the remote reference and delegates to the reference to read in
     * its representation.
     */
    private void readObject(java.io.ObjectInputStream in) 
	throws java.io.IOException, java.lang.ClassNotFoundException
    {
	try {
	    Class refClass = Class.forName(RemoteRef.packagePrefix + "." +
					   in.readUTF());
	    ref = (RemoteRef)refClass.newInstance();
	    ref.readExternal(in);
	} catch (InstantiationException e) {
	    throw new UnmarshalException("Unable to create remote reference",
					 e);
	} catch (IllegalAccessException e) {
	    throw new UnmarshalException("Illegal access creating remote reference");
	}
    }

}
