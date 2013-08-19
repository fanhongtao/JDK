/*
 * @(#)INSObjectKeyEntry.java	1.4 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.sun.corba.se.internal.core;

import java.util.Hashtable;

/**
 * This class contains two elements
 * 1. INS target object's IOR
 * 2. INS target object's Subcontract.
 * This is used in Subcontract registry to get the handle of subcontract for
 * a give INS object Key and in ServerSubcontract to get the target object's
 * ior, which will be returned when INS locate message is sent from the client
 * with the given ObjectKey.
 */
public class INSObjectKeyEntry {
    private IOR ior;
    private ServerSubcontract sc;

    public INSObjectKeyEntry ( IOR aIOR,  ServerSubcontract aSC ) {
        ior = aIOR;
        sc = aSC;
    }

    public IOR getIOR( ) {
        return ior;
    }

    public ServerSubcontract getServerSubcontract( ) { 
        return sc;
    }
}
