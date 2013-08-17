/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.rmi.server;

import java.rmi.Remote;
import java.rmi.UnmarshalException;
import java.rmi.NoSuchObjectException;

/**
 * The <code>RemoteObject</code> class implements the
 * <code>java.lang.Object</code> behavior for remote objects.
 * <code>RemoteObject</code> provides the remote semantics of Object by
 * implementing methods for hashCode, equals, and toString.
 *
 * @version 1.23, 02/06/02
 * @author  Ann Wollrath
 * @since   JDK1.1
 */
public abstract class RemoteObject implements Remote, java.io.Serializable {

    /** the object's remote reference. */
    transient protected RemoteRef ref;
    
    /** indicate compatibility with JDK 1.1.x version of class */
    private static final long serialVersionUID = -3215090123894869218L;

    /**
     * Creates a remote object.
     * @since JDK1.1
     */
    protected RemoteObject() {
	ref = null;
    }
    
    /**
     * Creates a remote object, initialized with the specified remote
     * reference.
     * @param newref remote reference
     * @since JDK1.1
     */
    protected RemoteObject(RemoteRef newref) {
	ref = newref;
    }

    /**
     * Returns the remote reference for the remote object.
     * @return remote reference for the remote object
     * @since 1.2
     */
    public RemoteRef getRef() {
	return ref;
    }
    
    /**
     * Returns the stub for the remote object <code>obj</code> passed
     * as a parameter. This operation is only valid <i>after</i>
     * the object has been exported.
     * @param obj the remote object whose stub is neede
     * @return the stub for the remote object, <code>obj</code>.
     * @exception NoSuchObjectException if the stub for the
     * remote object could not be found.
     * @since 1.2
     */
    public static Remote toStub(Remote obj) throws NoSuchObjectException {
	if (obj instanceof RemoteStub) {
	    return (RemoteStub)obj; 
	} else {
	    return sun.rmi.transport.ObjectTable.getStub(obj);
	}
    }

    /**
     * Returns a hashcode for a remote object.  Two remote object stubs
     * that refer to the same remote object will have the same hash code
     * (in order to support remote objects as keys in hash tables).
     *
     * @see		java.util.Hashtable
     * @since JDK1.1
     */
    public int hashCode() {
	return (ref == null) ? super.hashCode() : ref.remoteHashCode();
    }

    /**
     * Compares two remote objects for equality.
     * Returns a boolean that indicates whether this remote object is
     * equivalent to the specified Object. This method is used when a
     * remote object is stored in a hashtable.
     * If the specified Object is not itself an instance of RemoteObject,
     * then this method delegates by returning the result of invoking the
     * <code>equals</code> method of its parameter with this remote object
     * as the argument.
     * @param	obj	the Object to compare with
     * @return	true if these Objects are equal; false otherwise.
     * @see		java.util.Hashtable
     * @since JDK1.1
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
	     * use the result of its equals method, to support symmetry is a
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
     * @since JDK1.1
     */
    public String toString() {
	String classname = this.getClass().getName();
	return (ref == null) ? classname :
	    classname + "[" +ref.remoteToString() + "]";
    }


    /**
     * writeObject for object serialization.  Writes out the class
     * name of the remote reference contained in this class and
     * delegates to the reference to write out its representation.
     * 
     * @serialData Writes out the unqualified class name of the remote
     * reference field, <code>ref</code>, in <code>UTF-8</code> and
     * delegates to the <code>ref</code> field to write out its
     * representation.  Different information will be written to
     * <code>out</code> depending upon the <code>ref</code> field's
     * type.  Default serialization is not used. 
     */
    private void writeObject(java.io.ObjectOutputStream out)
	throws java.io.IOException, java.lang.ClassNotFoundException
    {
	if (ref == null) {
	    throw new java.rmi.MarshalException("Invalid remote object");
	} else {
	    String refClassName = ref.getRefClass(out);
	    if (refClassName == null || refClassName.length() == 0) {
		/*
		 * No reference class name specified, so serialize
		 * remote reference.
		 */
		out.writeUTF("");
		out.writeObject(ref);
	    } else {
		/*
		 * Built-in reference class specified, so delegate
		 * to reference to write out its external form.
		 */
		out.writeUTF(refClassName);
		ref.writeExternal(out);
	    }
	}
    }

    /**
     * readObject for object serialization. Reads in the unqualified
     * class name of the remote reference field, <code>ref</code>, in
     * <code>UTF-8</code> and delegates to the <code>ref</code> field
     * to read in its representation. The <code>ref</code> field is
     * read via a direct call to
     * <code>ref.readExternal(ObjectInputStream in)</code>. Default
     * serialization is not used.  
     */
    private void readObject(java.io.ObjectInputStream in) 
	throws java.io.IOException, java.lang.ClassNotFoundException
    {
	try {
	    String refClassName = in.readUTF();
	    if (refClassName == null || refClassName.length() == 0) {
		/*
		 * No reference class name specified, so construct
		 * remote reference from its serialized form.
		 */
		ref = (RemoteRef) in.readObject();
	    } else {
		/*
		 * Built-in reference class specified, so delegate
		 * to reference to initialize its fields  from its
		 * external form.
		 */
		Class refClass = Class.forName(RemoteRef.packagePrefix + "." +
					       refClassName);
		ref = (RemoteRef) refClass.newInstance();
		ref.readExternal(in);
	    }
	} catch (InstantiationException e) {
	    throw new UnmarshalException("Unable to create remote reference",
					 e);
	} catch (IllegalAccessException e) {
	    throw new UnmarshalException("Illegal access creating remote reference");
	}
    }
}
