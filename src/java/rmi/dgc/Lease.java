/*
 * @(#)Lease.java	1.5 01/12/10
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
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
