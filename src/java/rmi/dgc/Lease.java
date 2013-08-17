/*
 * @(#)Lease.java	1.4 98/08/12
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
package java.rmi.dgc;

/**
 * A lease contains a unique VM identifier and a lease duration. A
 * Lease object is used to request and grant leases to remote object
 * references.
 */
public final class Lease implements java.io.Serializable {

    private static final long serialVersionUID = -5713411624328831948L;

    private VMID vmid;
    private long value;

    /**
     * Constructs a lease with a specific VMID and lease duration. The
     * vmid may be null.
     */
    public Lease(VMID id, long duration) 
    {
	vmid = id;
	value = duration;
    }

    /**
     * Returns the client VMID associated with the lease.
     */
    public VMID getVMID() 
    {
	return vmid;
    }

    /**
     * Returns the lease duration.
     */
    public long getValue() 
    {
	return value;
    }
}
