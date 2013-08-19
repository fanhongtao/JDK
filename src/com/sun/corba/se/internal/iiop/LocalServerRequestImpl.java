/*
 * @(#)LocalServerRequestImpl.java	1.28 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.corba.se.internal.iiop;

import org.omg.CORBA.SystemException;

import com.sun.corba.se.internal.core.ServerRequest;
import com.sun.corba.se.internal.core.ServiceContext;
import com.sun.corba.se.internal.core.DuplicateServiceContext;
import com.sun.corba.se.internal.core.UEInfoServiceContext;
import com.sun.corba.se.internal.core.ServiceContexts;
import com.sun.corba.se.internal.core.ServerResponse;
import com.sun.corba.se.internal.core.IOR;
import com.sun.corba.se.internal.orbutil.ORBUtility;  //d11638
import org.omg.CORBA.portable.UnknownException;
import org.omg.CORBA.UNKNOWN;
import org.omg.CORBA.CompletionStatus;
import com.sun.corba.se.internal.ior.ObjectKey;
import com.sun.corba.se.internal.iiop.messages.MessageBase;
import com.sun.corba.se.internal.iiop.messages.ReplyMessage;
import com.sun.corba.se.internal.iiop.messages.RequestMessage;

class LocalServerRequestImpl extends IIOPInputStream implements ServerRequest {
    org.omg.CORBA.portable.OutputStream replyStream;
    org.omg.CORBA.portable.OutputStream exceptionReplyStream;

    LocalServerRequestImpl(ORB orb, byte[] buf, RequestMessage header)
    {
        super(orb, buf, header.getSize(), header.isLittleEndian(), header, null );

        this.request = header;
    }

    public int getRequestId() {
    	return request.getRequestId();
    }

    public boolean isOneWay() {
    	return !request.isResponseExpected();
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

    public ServerResponse createResponse(ServiceContexts svc)
    {
    	return new LocalServerResponseImpl(this, svc);
    }

    public org.omg.CORBA.portable.OutputStream createReply() {
        if (replyStream == null) {
            replyStream = (org.omg.CORBA.portable.OutputStream)
                                createResponse(null);
        }
        return replyStream;
    }

    public org.omg.CORBA.portable.OutputStream createExceptionReply() {
        if (exceptionReplyStream == null) {
            exceptionReplyStream = (org.omg.CORBA.portable.OutputStream)
                                        createUserExceptionResponse(null);
        }
        return exceptionReplyStream;
    }

    public ServerResponse createUserExceptionResponse(
	ServiceContexts svc)
    {
    	return new LocalServerResponseImpl(this, svc, true);
    }

    public ServerResponse createUnknownExceptionResponse(
	        UnknownException ex) {
        ServiceContexts contexts = null;
        SystemException sys = new UNKNOWN( 0,
            CompletionStatus.COMPLETED_MAYBE);

            try {
                contexts = new ServiceContexts(
                    (com.sun.corba.se.internal.corba.ORB)orb());
                UEInfoServiceContext uei = new UEInfoServiceContext(sys);
                contexts.put(uei) ;
            } catch (DuplicateServiceContext d) {
            // can't happen
            }

        return createSystemExceptionResponse(sys,contexts);
    }

    public ServerResponse createSystemExceptionResponse(
    	SystemException ex, ServiceContexts svc) {

	// Only do this if interceptors have been initialized on this request
	// and have not completed their lifecycle (otherwise the info stack
	// may be empty or have a different request's entry on top).
	if (executePIInResponseConstructor()) {
	    // Inform Portable Interceptors of the SystemException.  This is
	    // required to be done here because the ending interception point
	    // is called in the ServerResponseImpl constructor called below
	    // but we do not currently write the SystemException into the
	    // response until after the ending point is called.
	    ORB orb = (ORB)orb();
	    orb.setServerPIInfo( ex );
	}

	if (orb() != null && ((ORB)orb()).subcontractDebugFlag && ex != null)
            ORBUtility.dprint(this, "Sending SystemException:", ex);

        LocalServerResponseImpl response =
            new LocalServerResponseImpl(this, svc, false);
        ORBUtility.writeSystemException(ex, response);
        return response;
    }

    public ServerResponse createLocationForward(
	        IOR ior, ServiceContexts svc) {
        ReplyMessage reply = MessageBase.createReply(
                                    (com.sun.corba.se.internal.iiop.ORB) orb(),
                                    request.getGIOPVersion(),
                                    request.getRequestId(),
                                    ReplyMessage.LOCATION_FORWARD,
                                    svc, ior);
        LocalServerResponseImpl response =
            new LocalServerResponseImpl(this, reply, ior);

        return response;
    }

    private RequestMessage request;

    /**
     * Check to see if the request is local.
     */
    public boolean isLocal(){
        return true;
    }

    private boolean _executeReturnServantInResponseConstructor = false;

    public boolean executeReturnServantInResponseConstructor()
    {
	return _executeReturnServantInResponseConstructor;
    }

    public void setExecuteReturnServantInResponseConstructor(boolean b)
    {
	_executeReturnServantInResponseConstructor = b;
    }

    
    private boolean _executeRemoveThreadInfoInResponseConstructor = false;

    public boolean executeRemoveThreadInfoInResponseConstructor()
    {
	return _executeRemoveThreadInfoInResponseConstructor;
    }

    public void setExecuteRemoveThreadInfoInResponseConstructor(boolean b)
    {
	_executeRemoveThreadInfoInResponseConstructor = b;
    }

    
    private boolean _executePIInResponseConstructor = false;
                                                            
    public boolean executePIInResponseConstructor() {
        return _executePIInResponseConstructor;
    }

    public void setExecutePIInResponseConstructor( boolean b ) {
        _executePIInResponseConstructor = b;
    }
}
