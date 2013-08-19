/*
 * @(#)ClientRequestImpl.java	1.41 03/01/23
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
import com.sun.corba.se.internal.iiop.Connection;
import com.sun.corba.se.internal.core.IOR;
import com.sun.corba.se.internal.core.GIOPVersion;
import com.sun.corba.se.internal.iiop.messages.MessageBase;
import com.sun.corba.se.internal.iiop.messages.RequestMessage;
import com.sun.corba.se.internal.core.ORBVersion;
import com.sun.corba.se.internal.orbutil.ORBConstants;
import com.sun.corba.se.internal.orbutil.ORBUtility;
import com.sun.corba.se.internal.ior.ObjectKeyFactory ;
import com.sun.corba.se.internal.ior.ObjectKey ;
import com.sun.corba.se.internal.ior.ObjectKeyTemplate ;
import com.sun.corba.se.internal.ior.IIOPProfile;

public class ClientRequestImpl extends IIOPOutputStream
    implements ClientRequest
{
    public ClientRequestImpl(GIOPVersion gv, IOR ior, short addrDisposition,
	String operationName, boolean oneway, ServiceContexts svc,
	int requestId, Connection conn)
    {
        super(gv, conn.getORB(), conn);

        this.isOneway = oneway;
        boolean responseExpected = !isOneway;

        IIOPProfile iop = ior.getProfile();
	ObjectKey okey = iop.getObjectKey();
	ObjectKeyTemplate oktemp = okey.getTemplate() ;
	ORBVersion version = oktemp.getORBVersion() ;
	ORB orb = conn.getORB();
	orb.setORBVersion( version ) ;

        this.request = MessageBase.createRequest(orb, gv, requestId,
	    responseExpected, ior, addrDisposition, operationName, svc, null);
        setMessage(request);
        request.write(this);
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


    public ClientResponse invoke()
    {
        return (ClientResponse)super.invoke(isOneway);
    }

    /**
     * Check to see if request is local.
     */
    public boolean isLocal(){
        return false;
    }

    private RequestMessage request;
    protected boolean isOneway;
}
