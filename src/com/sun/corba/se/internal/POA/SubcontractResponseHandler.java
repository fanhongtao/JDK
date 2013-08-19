/*
 * @(#)SubcontractResponseHandler.java	1.20 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
 // Mortazavi 99/05/25

package com.sun.corba.se.internal.POA;

import org.omg.PortableServer.*;
import org.omg.CORBA.portable.*;
import org.omg.CORBA.INTERNAL ;

import com.sun.corba.se.internal.core.ServerRequest;
import com.sun.corba.se.internal.core.ServiceContext;
import com.sun.corba.se.internal.core.DuplicateServiceContext;
import com.sun.corba.se.internal.core.SendingContextServiceContext;
import com.sun.corba.se.internal.core.ORBVersionServiceContext;
import com.sun.corba.se.internal.core.ORBVersionFactory;
import com.sun.corba.se.internal.core.ServiceContexts;
import com.sun.corba.se.internal.core.ServerResponse;
import com.sun.corba.se.internal.core.MarshalOutputStream;
import com.sun.corba.se.internal.iiop.Connection;
import com.sun.corba.se.internal.orbutil.ORBUtility;
import com.sun.corba.se.internal.ior.ObjectKey ;
import com.sun.corba.se.internal.ior.ObjectKeyFactory ;


public class SubcontractResponseHandler 
    implements org.omg.CORBA.portable.ResponseHandler
{
    private com.sun.corba.se.internal.core.ServerRequest serverRequest;
    private org.omg.CORBA.ORB orb;

    public 
	SubcontractResponseHandler(com.sun.corba.se.internal.core.ServerRequest req, org.omg.CORBA.ORB scOrb)
    {
	serverRequest = req;
	orb = scOrb;
    }

    public 
	org.omg.CORBA.portable.OutputStream 
	createReply() 
    {
	return (org.omg.CORBA.portable.OutputStream)
            serverRequest.createResponse(
					 getServiceContextsForReply(
								    serverRequest.getConnection()));
    }

    public 
	org.omg.CORBA.portable.OutputStream 
	createExceptionReply() 
    {
	return        
	    (org.omg.CORBA.portable.OutputStream)
            serverRequest.createUserExceptionResponse(
						      getServiceContextsForReply(
										 serverRequest.getConnection()));
    }


    private 
	ServiceContexts 
	getServiceContextsForReply(Connection c) 
    {
	com.sun.corba.se.internal.corba.ORB theOrb = (com.sun.corba.se.internal.corba.ORB)orb ;
	/*
	  if (theOrb.subcontractDebugFlag)
	  dprint( "getServiceContextsReply called with connection " + c ) ;
	*/

	ServiceContexts contexts = new ServiceContexts( theOrb )  ;

	// NOTE : We only want to send the runtime context the first time

	if (c!=null && !c.isPostInitialContexts()) {
            /*
	      if (theOrb.subcontractDebugFlag)
	      dprint( "Adding SendingContextServiceContext" ) ;
            */
	    c.setPostInitialContexts();
	    SendingContextServiceContext scsc = 
		new SendingContextServiceContext(theOrb.getServantIOR()) ; //d11638
	    try {
		contexts.put( scsc ) ;
	    } catch (DuplicateServiceContext dsc) {
	    }
	}

        // send ORBVersion servicecontext as part of the Reply

        ORBVersionServiceContext ovsc = new ORBVersionServiceContext(
                        ORBVersionFactory.getORBVersion());
        try {
            contexts.put( ovsc ) ;
        } catch (DuplicateServiceContext dsc) {
	    throw new INTERNAL() ;
        }



	/*
	  if (theOrb.subcontractDebugFlag)
	  dprint( "Checking for transaction support" ) ;
	*/

	com.sun.corba.se.internal.POA.POAORB myorb ; 

	try {
	    myorb = (com.sun.corba.se.internal.POA.POAORB)orb;
	} catch (ClassCastException cce) {
	    /*
	      if (theOrb.subcontractDebugFlag) 
	      dprint( "Non-POA ORB: returning normally" ) ;
	    */

	    // This is not the POA ORB, so it can't be transactional:
	    // just return the current contexts.
	    return contexts ;
	}

	ObjectKey okey = serverRequest.getObjectKey();
	int tscid = okey.getTemplate().getSubcontractId() ;

	myorb.getServiceSpecificServiceContexts(tscid, serverRequest, contexts);

	try {
	    ((com.sun.corba.se.internal.POA.POAORB)theOrb).
		sendingReplyServiceContexts(contexts);
	} catch (Throwable ex) {
	    ; //Do not let hook errors escape.
	}

	/*
	  if (theOrb.subcontractDebugFlag)
	  dprint( "getServiceContextsReply completed successfuly" ) ;
	*/

        return contexts;
    }    

}
