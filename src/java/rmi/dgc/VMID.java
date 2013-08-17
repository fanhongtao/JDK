/*
 * @(#)VMID.java	1.6 96/12/16
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

package java.rmi.dgc;

import java.io.*;
import java.net.*;
import java.rmi.server.UID;

/**
 * A VMID is a identifier that is unique across all Java virtual
 * machines.  VMIDs are used by the distributed garbage collector
 * to identify client VMs.
 *
 * @version	1.6, 12/16/96
 * @author	Ann Wollrath
 * @author	Peter Jones
 */
public final class VMID implements java.io.Serializable {

    /** array of bytes uniquely identifying this host */
    private static byte[] localAddr;
    /** true if address for this host actually is unique */
    private static boolean localAddrUnique;
    static {
	try {
	    InetAddress localInetAddress = InetAddress.getLocalHost();
	    byte[] raw = localInetAddress.getAddress();
	    localAddr = raw;

	    if (raw == null ||		// if local host unknown,
		((raw[0] | raw[1] | raw[2] | raw[3]) == 0) ||
		((raw[0] == 127) &&	// or if it is localhost (127.0.0.1)
		 (raw[1] ==   0) &&	// (maybe because of applet)
		 (raw[2] ==   0) &&	// security manager?)
		 (raw[3] ==   1)))
		localAddrUnique = false; // then can't get unique host address
	    else
		localAddrUnique = true;
	} catch (Exception e) {
	    localAddr = null;
	    localAddrUnique = false;
	}
    }

    /** array of bytes uniquely identifying host created on */
    private byte[] addr;

    /** unique identifier with respect to host created on */
    private UID uid;

    /** use serialVersionUID from prebeta for interoperability */
    private static final long serialVersionUID = -538642295484486218L;

    /**
     * Create a new VMID.  Each new VMID returned from this constructor
     * is unique for all Java virtual machines under the following
     * conditions: a) the conditions for uniqueness for objects of
     * the class <b>java.rmi.server.UID</b> are satisfied, and b) an
     * address can be obtained for this host that is unique and constant
     * for the lifetime of this object.  <p>
     * The static method <b>isUnique</b> can be invoked to determine
     * if an accurate address can be obtained for this host.
     */
    public VMID()
    {
	addr = localAddr;
	uid = new UID();
    }

    /**
     * Return true if an accurate address can be determined for this
     * host.  If false, reliable VMID cannot be generated from this host
     * @return true if host address can be determined, false otherwise
     */
    public static boolean isUnique()
    {
	return localAddrUnique;
    }

    /**
     * Compute hash code for this VMID.
     */
    public int hashCode() {
	return uid.hashCode();
    }

    /**
     * Compare this VMID to another, and return true if they are the
     * same identifier.
     */
    public boolean equals(Object obj) {
	if ((obj != null) && (obj instanceof VMID)) {
	    VMID vmid = (VMID) obj;
	    if (!uid.equals(vmid.uid))
		return false;
	    if ((addr == null) ^ (vmid.addr == null))
		return false;
	    if (addr != null) {
		if (addr.length != vmid.addr.length)
		    return false;
		for (int i = 0; i < addr.length; ++ i)
		    if (addr[i] != vmid.addr[i])
			return false;
	    }
	    return true;
	} else {
	    return false;
	}
    }

    /**
     * Return string representation of this VMID.
     */
    public String toString() {
	StringBuffer result = new StringBuffer();
	if (addr != null)
	    for (int i = 0; i < addr.length; ++ i) {
		if (i > 0)
		    result.append('.');
		result.append(Integer.toString(((int) addr[i]) & 0xFF, 10));
	    }
	result.append(':');
	result.append(uid.toString());
	return result.toString();
    }
}
