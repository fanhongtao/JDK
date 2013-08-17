/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package java.rmi.server;

import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.security.SecureRandom;
import java.util.Random;

import sun.security.action.GetBooleanAction;

/**
 * An <code>ObjID</code> is used to identify remote objects uniquely
 * in a VM over time.  Each identifier contains an object number and an
 * address space identifier that is unique with respect to a specific host.
 * An object identifier is assigned to a remote object when it is exported.
 *
 * If the property <code>java.rmi.server.randomIDs</code> is true, then the
 * object number component (64 bits) of an <code>ObjID</code> created with the
 * no argument constructor will contain a cryptographically strong random
 * number.
 *
 * @version 1.23, 02/06/02
 * @author  Ann Wollrath
 * @since   JDK1.1
 */
public final class ObjID implements java.io.Serializable {
    /** well-known id for the registry. */
    public static final int REGISTRY_ID = 0;
    /** well-known id for the activator. */
    public static final int ACTIVATOR_ID = 1;
    /** well-known id for the distributed garbage collector. */
    public static final int DGC_ID = 2;

    /**
     * @serial object number
     * @see #hashCode
     */
    private long objNum;

    /**
     * @serial address space identifier (unique to host)
     */
    private UID space;

    private final static UID mySpace;
    private final static Random generator;

    /* indicate compatibility with JDK 1.1.x version of class */
    private static final long serialVersionUID = -6386392263968365220L;

    /**
     * Generates a unique object identifier.  If the property
     * <code>java.rmi.server.randomIDs</code> is true, then the object number
     * component (64 bits) of an <code>ObjID</code> created with the no
     * argument constructor will contain a cryptographically strong random
     * number.
     * @since JDK1.1
     */
    public ObjID () {
	/*
	 * Create a new UID if the SecureRandom generator is used (mySpace
	 * will be null in this case).  Using a different UID for each ObjID
	 * ensures that ObjIDs will be unique in this VM incarnation when
	 * paired with the result of the secure random number generator.
	 */
	space = (mySpace != null) ? mySpace : new UID();
	objNum = generator.nextLong();
    }

    /**
     * Generates a "well-known" object ID.  An object ID generated via
     * this constructor will not clash with any object IDs generated
     * via the default constructor.
     * @param num a unique well-known object number
     * @since JDK1.1
     */
    public ObjID (int num) {
	space = new UID((short)0);
	objNum = num;
    }

    /**
     * Private constructor for creating an object ID given its contents
     * that is read from a stream.
     * @since JDK1.1
     */
    private ObjID(long objNum, UID space) {
	this.objNum = objNum;
	this.space = space;
    }

    /**
     * Marshals object id to output stream.
     * @param out output stream to write object ID to
     * @throws IOException if an I/O error occurred
     * @since JDK1.1
     */
    public void write(ObjectOutput out) throws java.io.IOException {
	out.writeLong(objNum);
	space.write(out);
    }

    /**
     * Constructs an object id whose contents is read from the specified input
     * stream.
     * @param in input stream to read object ID from
     * @return object ID instance read from stream
     * @throws IOException if an I/O error occurred
     * @since JDK1.1
     */
    public static ObjID read(ObjectInput in) throws java.io.IOException {
	long num = in.readLong();
	UID space = UID.read(in);
	return new ObjID(num, space);
    }

    /**
     * Returns the hash code for the <code>ObjID</code> (the object number).
     * @since JDK1.1
     */
    public int hashCode() {
	return (int) objNum;
    }

    /**
     * Two object identifiers are considered equal if they have the
     * same contents.
     * @since JDK1.1
     */
    public boolean equals(Object obj) {
	if ((obj != null) && (obj instanceof ObjID)) {
	    ObjID id = (ObjID)obj;
	    return (objNum == id.objNum && space.equals(id.space));
	} else {
	    return false;
	}
    }

    /**
     * Returns a string containing the object id representation. The
     * address space identifier is included in the string
     * representation only if the object id is from a non-local
     * address space.
     * @since JDK1.1
     */
    public String toString() {
	return "[" + (space.equals(mySpace) ? "" : space + ", ") +
	    objNum + "]";
    }

    private static class InsecureRandom extends Random {
	// use serialVersionUID from JDK 1.2.2 for interoperability
	private static final long serialVersionUID = -698228687531590145L;
	private long nextNum;

	synchronized public long nextLong() {
	    return nextNum++;
	}
    }

    static {
	Boolean tmp = (Boolean) java.security.AccessController.doPrivileged(
	    new GetBooleanAction("java.rmi.server.randomIDs"));
	boolean randomIDs = tmp.booleanValue();

	if (randomIDs) {
	    generator = new SecureRandom();
	    mySpace = null;
	} else {
	    generator = new InsecureRandom();
	    /*
	     * The InsecureRandom implementation guarantees that object
	     * numbers will not repeat, so the same UID value can be used
	     * for all instances of ObjID.
	     */
	    mySpace = new UID();
	}
    }
}
