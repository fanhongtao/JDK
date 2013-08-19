/*
 * @(#)SubcontractRegistry.java	1.43 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.corba.se.internal.core;

import java.util.*;
import org.omg.IOP.TAG_INTERNET_IOP ;
import com.sun.corba.se.internal.ior.ObjectKey ;
import com.sun.corba.se.internal.ior.ObjectKeyTemplate ;
import com.sun.corba.se.internal.ior.ObjectKeyFactory ;
import com.sun.corba.se.internal.ior.IORTemplate ;
import com.sun.corba.se.internal.ior.IIOPProfileTemplate ;
import com.sun.corba.se.internal.ior.WireObjectKeyTemplate ;
import com.sun.corba.se.internal.ior.ObjectId ;
import com.sun.corba.se.internal.corba.INSSubcontract;
import com.sun.corba.se.internal.orbutil.MinorCodes ;
import com.sun.corba.se.internal.orbutil.ORBConstants ;
import org.omg.CORBA.BAD_PARAM ;
import org.omg.CORBA.INTERNAL ;
import org.omg.CORBA.CompletionStatus ;
import org.omg.CORBA.ORB ;

/**
 * This is a registry of all client subcontract classes and
 * server subcontract instances.
 *
 * In order to allow reuse of this in more than one context
 * (that is, ORBs), we try not to throw any CORBA specific
 * exceptions. Most of the methods either throw Java exceptions
 * or return nulls or false on failure.
 */
public class SubcontractRegistry {
    protected ORB orb;
    protected int size;
    protected ServerSubcontract[] serverRegistry;
    protected Class[] clientFactory;
    protected int defaultId;
    private INSSubcontract insSubcontract = null;

    public void dumpServers()
    {
	System.out.println( "Dump of registered server subcontracts:" ) ;
	for (int ctr=0; ctr<serverRegistry.length; ctr++ ) {
	    ServerSubcontract sc = serverRegistry[ctr] ;
	    if (sc != null)
		System.out.println( "serverRegistry[" + ctr + "] = " + sc ) ;
	}
    }

    public SubcontractRegistry(ORB orb, int defaultId) {
        this(orb, defaultId, 256);
    }

    public SubcontractRegistry(ORB orb, int defaultId, int size) {
	this.orb = orb;
        this.size = size;
        this.defaultId = defaultId;
        serverRegistry = new ServerSubcontract[size];
        clientFactory = new Class[size];
    }

    public synchronized boolean registerClient(Class clientSC,
					       int scid)
    {
        if (scid < size) {
            clientFactory[scid] = clientSC;
	    return true;
	}
	return false;
    }

    public synchronized boolean registerServer(ServerSubcontract sc,
					       int scid) {
        if (scid < size) {
	    serverRegistry[scid] = sc;
	    return true;
        }
	return false;
    }

    // REVISIT replace this with a more general registration mechanism
    protected ServerSubcontract bootstrapServer = null;
    public void registerBootstrapServer(ServerSubcontract sc)
    {
	this.bootstrapServer = sc;
    }

    // **************************************************
    // Methods to find the subcontract side subcontract
    // **************************************************

    public ServerSubcontract getServerSubcontract(int scid)
    {
	ServerSubcontract sc=null;
        if ((scid >= 0) && (scid < size))
            sc = serverRegistry[scid];

	if ( sc == null )
	    return serverRegistry[defaultId];
	else
	    return sc;
    }

    /** Find a server subcontract based on the object key.
    * This is a key method in the server side dispatch of
    * requests.  This method is called from the ORB process method
    * in response to an incoming request.
    * <p>
    * Note that we should probably at some point replace the fixed
    * names with a name registry for subcontracts whose entire
    * object key is a name.
    */
    public ServerSubcontract getServerSubcontract(ObjectKey objKey)
    {
	ObjectKeyTemplate oktemp = objKey.getTemplate() ;

	if (oktemp instanceof WireObjectKeyTemplate) {
	    String okstring = new String( objKey.getBytes(orb) );
	    // The orginal (circa 1997) bootstrap protocol used a 32 bit int
	    // corresponding to the characters "INIT".  We allow for this
	    // to be marshalled either big endian or little endian here.
	    // If it had always been a string, we would not need to do this.
	    if (okstring.equals( "INIT" ) || okstring.equals( "TINI" ))
		return bootstrapServer ;

            // INS Object Key strings will be in the ASCII format, Like
            // for example NameService. If we are successful in finding
            // the subcontract based on the key then just return that, else
            // there is an error.
            INSObjectKeyEntry keyEntry = 
                INSObjectKeyMap.getInstance( ).getEntry(okstring);
            if( keyEntry != null ) {
                return getINSSubcontract( );
            }
	} else {
	    int scid = oktemp.getSubcontractId();
	    return getServerSubcontract( scid );
	}

	throw new INTERNAL( MinorCodes.NO_SERVER_SUBCONTRACT,
	    CompletionStatus.COMPLETED_NO ) ;
    }

 
    private synchronized INSSubcontract getINSSubcontract ( ) {
        if( insSubcontract == null ) {
            insSubcontract = new INSSubcontract( orb );
        }
        return insSubcontract;
    }


    // **************************************************
    // Methods to find the client side subcontract
    // **************************************************

    public ClientSubcontract getClientSubcontract(ObjectKey key)
    {
	return getClientSubcontract( key.getTemplate() ) ;
    }

    public ClientSubcontract getClientSubcontract( IORTemplate temp )
    {
	// REVISIT we eventually need to fix this if we need to handle
	// multi-profile IORs.  For now, it only returns a subcontract
	// if the template has exactly one IIOP profile template and no
	// other templates.  In this case, we simply delegate to
	// getClientSubcontract( ObjectKeyTemplate ).
	//
	// Note that this is the place to insert a lookup for subcontracts
	// for fault tolerance or client side load balancing.
	if (temp.size() != 1)
	    throw new INTERNAL( MinorCodes.SERVER_SC_TEMP_SIZE,
		CompletionStatus.COMPLETED_NO ) ;

	Iterator iter = temp.iteratorById( TAG_INTERNET_IOP.value ) ;
	Object obj = iter.next() ;
	if (!(obj instanceof IIOPProfileTemplate))
	    throw new INTERNAL( MinorCodes.SERVER_SC_NO_IIOP_PROFILE,
		CompletionStatus.COMPLETED_NO ) ;

	IIOPProfileTemplate iptemp = (IIOPProfileTemplate)(obj) ;
	return getClientSubcontract( iptemp.getObjectKeyTemplate() ) ;
    }

    public ClientSubcontract getClientSubcontract( ObjectKeyTemplate temp )
    {
	int scid = temp.getSubcontractId() ;
	return getClientSubcontract( scid ) ;
    }

    public ClientSubcontract getClientSubcontract( int scid )
    {
	Class repClass = null ;
	ClientSubcontract rep = null ;

	if ((scid >= 0) && (scid < size))
	    repClass = clientFactory[scid] ;

	if (repClass == null) {
	    scid = defaultId ;
	    repClass = clientFactory[scid] ;
	}

	try {
	    rep = (ClientSubcontract)(repClass.newInstance()) ;
        } catch (Exception e1){
	    // Serious error: could not create instance of class.
	    // Should not happen.
	    throw new INTERNAL( MinorCodes.NO_CLIENT_SC_CLASS,
		CompletionStatus.COMPLETED_NO ) ;
        }

	rep.setId( scid ) ;
	return rep ;
    }
}
