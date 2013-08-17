/*
 * @(#)ObjID.java	1.6 96/11/18
 * 
 * Copyright (c) 1995, 1996 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the confidential and proprietary information of Sun
 * Microsystems, Inc. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Sun.
 * 
 * SUN MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF THE
 * SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE, OR NON-INFRINGEMENT. SUN SHALL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING
 * THIS SOFTWARE OR ITS DERIVATIVES.
 * 
 * CopyrightVersion 1.1_beta
 */
package java.rmi.server;

import java.io.ObjectInput;
import java.io.ObjectOutput;

/**
 * The class ObjID is used to identify remote objects uniquely in a VM
 * over time. Each identifier contains an object number and an address
 * space identifier that is unique with respect to a specific host. An
 * object identifier is assigned to a remote object when it is
 * exported.
 */
public final class ObjID implements java.io.Serializable {
    /** well-known id for the registry */
    public static final int REGISTRY_ID = 0;
    /** well-known id for the distributed garbage collector */
    public static final int DGC_ID = 2;

    /** object number */
    private long objNum;
    /** address space identifier (unique to host) */
    private UID space;

    private static long nextNum = 0;
    private static UID mySpace = new UID();

    /**
     * Generate unique object identifier.
     */
    public ObjID () {
	synchronized (mySpace) {
	    space = mySpace;
	    objNum = nextNum++;
	}
    }

    /**
     * Generate a "well-known" object ID.  An object ID generated via
     * this constructor will not clash with any object IDs generated
     * via the default constructor.
     * @param num a unique well-known object number
     */
    public ObjID (int num) {
	space = new UID((short)0);
	objNum = num;
    }

    /**
     * Private constructor for creating an object ID given its contents
     * that is read from a stream.
     */
    private ObjID(long objNum, UID space) 
    {
	this.objNum = objNum;
	this.space = space;
    }
    
    /**
     * Marshal object id to output stream.
     */
    public void write(ObjectOutput out) throws java.io.IOException
    {
	out.writeLong(objNum);
	space.write(out);
    }
    
    /**
     * The read method constructs an object id whose contents is read
     * from the specified input stream.
     */
    public static ObjID read(ObjectInput in)
	throws java.io.IOException
    {
	long num = in.readLong();
	UID space = UID.read(in);
	return new ObjID(num, space);
    }

    /**
     * The hashcode is the object number.
     */
    public int hashCode() 
    {
	return (int) objNum;
    }

    /**
     * Two object identifiers are considered equal if they have the
     * same contents.
     */
    public boolean equals(Object obj) 
    {
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
     */
    public String toString()
    {
	return "[" + (space.equals(mySpace) ? "" : space + ", ") + 
	    objNum + "]";
    }
}
