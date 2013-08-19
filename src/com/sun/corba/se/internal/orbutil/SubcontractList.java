/*
 * @(#)SubcontractList.java	1.17 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
/*
 * Licensed Materials - Property of IBM
 * RMI-IIOP v1.0
 * Copyright IBM Corp. 1998 1999  All Rights Reserved
 *
 * US Government Users Restricted Rights - Use, duplication or
 * disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 */

package com.sun.corba.se.internal.orbutil;

import com.sun.corba.se.internal.corba.ServerDelegate;

public final class SubcontractList {
    /**
     * Following are the IDs (numbers) assigned to all our
     * subcontracts.
     */
    public static final int Generic = 2;

    public static final int defaultSubcontract = Generic;
    
    public static final SubcontractEntry[] subcontracts
	= new SubcontractEntry[] {
	new SubcontractEntry(Generic, ServerDelegate.class, null)
	    };

    public static class SubcontractEntry {
	public int   id;
	public Class serverSC;
	public Class clientSC;

	public SubcontractEntry(int id, Class serverSC, Class clientSC) {
	    this.id = id;
	    this.serverSC = serverSC;
	    this.clientSC = clientSC;
	}
    }
}


