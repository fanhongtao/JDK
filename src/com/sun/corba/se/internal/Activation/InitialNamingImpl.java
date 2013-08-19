/*
 * @(#)InitialNamingImpl.java	1.19 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.corba.se.internal.Activation;

import org.omg.CORBA.ORB;
import org.omg.CORBA.Object;
import org.omg.CORBA.INITIALIZE;
import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.ORBPackage.InvalidName;
import com.sun.corba.se.internal.CosNaming.BootstrapServer;
import com.sun.corba.se.ActivationIDL.InitialNameServicePackage.NameAlreadyBound;
import com.sun.corba.se.ActivationIDL._InitialNameServiceImplBase;

/**
 * 
 * @version 	1.19, 03/06/20
 * @author	Rohit Garg
 * @since	JDK1.2
 */
public class InitialNamingImpl extends _InitialNameServiceImplBase
{
    InitialNamingImpl(ORB theOrb, BootstrapServer initialBoot)
    {
	orb = theOrb;
	bootServer = initialBoot;

	((com.sun.corba.se.internal.corba.ORB)orb).connect(this);

	try {
	    bind("InitialNameService", this, false);
	} catch (NameAlreadyBound ex) {
	    throw new INITIALIZE(MinorCodes.CANNOT_ADD_INITIAL_NAMING, 
				 CompletionStatus.COMPLETED_NO);
	}
    }

    synchronized public void bind(String name, Object obj, boolean save)
	throws NameAlreadyBound 
    {
        // If no Service is registered with the name then register the Service
        if( bootServer.getService( name ) == null ) {
	    bootServer.addService(name, orb.object_to_string(obj), save);
        } else {
	    throw new NameAlreadyBound();     
        }
    }

    ORB orb;

    BootstrapServer bootServer;
}
