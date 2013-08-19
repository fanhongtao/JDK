/*
 * @(#)NameServiceStartThread.java	1.11 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.corba.se.internal.Activation;


import java.io.File;

import org.omg.CosNaming.NamingContext;
import com.sun.corba.se.internal.POA.POAORB;
import com.sun.corba.se.internal.PCosNaming.NameService;
import com.sun.corba.se.internal.orbutil.ORBConstants;

// REVISIT: After Merlin to see if we can get rid of this Thread and
// make the registration of PNameService for INS and BootStrap neat.
public class NameServiceStartThread extends java.lang.Thread
{
    private POAORB orb;
    private File dbDir; 	
    private InitialNamingImpl ins;
    public NameServiceStartThread( POAORB theOrb, 
		File theDir, InitialNamingImpl theIns ) 
    {
	orb = theOrb;
	dbDir = theDir;
	ins = theIns;
    }

    public void run( )
    {
       	// start Name Service
	try
	{
       	    NameService nameService = new NameService(orb, dbDir );
       	    NamingContext rootContext = 
		nameService.initialNamingContext();
       	    ins.bind( ORBConstants.PERSISTENT_NAME_SERVICE_NAME, rootContext, 
                false);
            orb.register_initial_reference( 
                ORBConstants.PERSISTENT_NAME_SERVICE_NAME, rootContext );
        } catch( Exception e ) {
	    System.err.println( 
                "NameService did not start successfully" );
	    e.printStackTrace( );
	}

    }
}
