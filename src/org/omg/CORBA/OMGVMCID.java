/*
 * @(#)OMGVMCID.java	1.9 03/12/19
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.omg.CORBA;

/**
 * The vendor minor code ID reserved for OMG. Minor codes for the standard
 * exceptions are prefaced by the VMCID assigned to OMG, defined as the
 * constant OMGVMCID, which, like all VMCIDs, occupies the high order 20 bits.
 */

public interface OMGVMCID {

    /**
     * The vendor minor code ID reserved for OMG. This value is or'd with
     * the high order 20 bits of the minor code to produce the minor value
     * in a system exception.
     */
    static final int value = 0x4f4d0000;
}

