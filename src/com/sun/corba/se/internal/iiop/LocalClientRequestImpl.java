/*
 * @(#)LocalClientRequestImpl.java	1.29 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.corba.se.internal.iiop;

import com.sun.corba.se.internal.core.Request;
import com.sun.corba.se.internal.core.ClientRequest;
import com.sun.corba.se.internal.core.ServiceContext;
import com.sun.corba.se.internal.core.ServiceContexts;
import com.sun.corba.se.internal.core.ClientResponse;
import com.sun.corba.se.internal.core.ServerRequest;
import com.sun.corba.se.internal.core.ServerResponse;
import com.sun.corba.se.internal.core.IOR;
import com.sun.corba.se.internal.core.GIOPVersion;
import com.sun.corba.se.internal.iiop.messages.MessageBase;
import com.sun.corba.se.internal.iiop.messages.RequestMessage;
import com.sun.corba.se.internal.orbutil.ORBConstants;
import com.sun.corba.se.internal.core.ORBVersion;
import com.sun.corba.se.internal.orbutil.ORBUtility;
import com.sun.corba.se.internal.ior.ObjectKeyFactory ;
import com.sun.corba.se.internal.ior.ObjectKey ;
import com.sun.corba.se.internal.ior.ObjectKeyTemplate ;
import com.sun.corba.se.internal.ior.IIOPProfile;

public class LocalClientRequestImpl extends IIOPOutputStream 
    implements ClientRequest 
{
    public LocalClientRequestImpl( GIOPVersion gv, 
	com.sun.corba.se.internal.iiop.ORB orb, IOR ior, short addrDisposition, 
        String operationName, boolean oneway, ServiceContexts svc, 
	int requestId)
    {
	super(gv, orb, null, false);

	this.isOneway = oneway;
	boolean responseExpected = !isOneway;

        IIOPProfile iop = ior.getProfile();
	ObjectKey okey = iop.getObjectKey();
	ObjectKeyTemplate oktemp = okey.getTemplate() ;
	ORBVersion version = oktemp.getORBVersion() ;
	orb.setORBVersion( version ) ;

        this.request = MessageBase.createRequest(orb, gv, requestId,
	    responseExpected, ior, addrDisposition, operationName, svc, null);
	setMessage(request);
	request.write(this);

	// mark beginning of msg body for possible later use
	bodyBegin = getSize();
    }

    public int getRequestId() {
	return request.getRequestId();
    }
    
    public boolean isOneWay() {
	return isOneway;
    }

    public ServiceContexts getServiceContexts() {
	return request.getServiceContexts();
    }

    public String getOperationName() {
	return request.getOperation();
    }

    public ObjectKey getObjectKey() {
	return request.getObjectKey();
    }

    public ServerRequest getServerRequest()
    {
	// Set the size of the marshalled data in the message header.
	getMessage().setSize( getByteBuffer(), getSize() ) ;

	// Construct a new ServerRequest out of the buffer in this ClientRequest
	LocalServerRequestImpl serverRequest = new LocalServerRequestImpl(
									  (com.sun.corba.se.internal.iiop.ORB)orb(), toByteArray(), request ) ;

	// Skip over all of the GIOP header information.  This positions
	// the offset in the buffer so that the skeleton can correctly read
	// the marshalled arguments.
	serverRequest.setIndex( bodyBegin ) ;

	return serverRequest ;
    }
    
    public ClientResponse invoke() 
    {	
	com.sun.corba.se.internal.iiop.ORB myORB = (com.sun.corba.se.internal.iiop.ORB)orb() ;

	ServerResponse serverResponse = myORB.process( getServerRequest() ) ;

	LocalServerResponseImpl lsr = (LocalServerResponseImpl)serverResponse ;

	return lsr.getClientResponse() ;
    }

    /**
     * Check to see if the request is local.
     */
    public boolean isLocal(){
        return true;
    }

    private RequestMessage request;
    private int bodyBegin;
    private boolean isOneway;
}
