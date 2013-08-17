/*
 * @(#)NameServiceStartThread.java	1.9 01/12/03
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.corba.se.internal.Activation;


import java.io.File;

import org.omg.CosNaming.NamingContext;
import org.omg.CORBA.portable.ObjectImpl;
import com.sun.corba.se.internal.core.IOR;
import com.sun.corba.se.internal.core.ClientSubcontract;
import com.sun.corba.se.internal.core.ServerSubcontract;
import com.sun.corba.se.internal.ior.ObjectKeyTemplate;
import com.sun.corba.se.internal.POA.POAORB;
import com.sun.corba.se.internal.PCosNaming.NameService;
import com.sun.corba.se.internal.orbutil.ORBConstants;
import com.sun.corba.se.internal.core.INSObjectKeyMap;
import com.sun.corba.se.internal.core.INSObjectKeyEntry;

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
       	    ins.bind("NameService", rootContext, false);
            // Register the Subcontract registry for Persistent Naming
            // Service. This will be used by INS protocol to get the 
            // handle of Persistent Naming Service. 
            com.sun.corba.se.internal.core.SubcontractRegistry scr =
                orb.getSubcontractRegistry();
            if( scr != null ) {
                ObjectImpl oi = (ObjectImpl)rootContext;
                ClientSubcontract rep = (ClientSubcontract)oi._get_delegate();
                IOR ior = rep.marshal();
                ObjectKeyTemplate temp =
                    ior.getProfile().getTemplate().getObjectKeyTemplate();
                int scid = temp.getSubcontractId() ;
                ServerSubcontract sc = scr.getServerSubcontract(scid);
                INSObjectKeyEntry entry = new INSObjectKeyEntry( ior, sc );
                INSObjectKeyMap.getInstance().setEntry( "NameService", entry );
            }
        } catch( Exception e ) {
	    System.err.println( 
                "NameService did not start successfully" );
	    e.printStackTrace( );
	}

    }
}
