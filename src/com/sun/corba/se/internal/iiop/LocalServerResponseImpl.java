/*
 * @(#)LocalServerResponseImpl.java	1.29 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.corba.se.internal.iiop;

import org.omg.CORBA.SystemException;

import com.sun.corba.se.internal.core.ServerResponse;
import com.sun.corba.se.internal.core.IOR;
import com.sun.corba.se.internal.core.ServiceContext;
import com.sun.corba.se.internal.core.ServiceContexts;
import com.sun.corba.se.internal.core.ClientResponse;
import com.sun.corba.se.internal.iiop.messages.MessageBase;
import com.sun.corba.se.internal.iiop.messages.ReplyMessage;

class LocalServerResponseImpl
    extends     IIOPOutputStream
    implements  ServerResponse
{
    LocalServerResponseImpl(LocalServerRequestImpl request, ServiceContexts svc)
    {
        this(request,
            MessageBase.createReply(
                            (com.sun.corba.se.internal.iiop.ORB) request.orb(),
                            request.getGIOPVersion(),
                            request.getRequestId(), ReplyMessage.NO_EXCEPTION,
                            svc, null),
            null);
    }

    LocalServerResponseImpl(LocalServerRequestImpl request, ServiceContexts svc,
            boolean user)
    {
        this(request,
            MessageBase.createReply(
                            (com.sun.corba.se.internal.iiop.ORB) request.orb(),
                            request.getGIOPVersion(), request.getRequestId(),
                            user ? ReplyMessage.USER_EXCEPTION :
                                   ReplyMessage.SYSTEM_EXCEPTION,
                            svc, null),
            null);
    }

    LocalServerResponseImpl( LocalServerRequestImpl request, ReplyMessage reply,
			     IOR ior)
    {
        super(request.getGIOPVersion(),
              (com.sun.corba.se.internal.iiop.ORB)(request.orb()), null, false);
        setMessage(reply);

	ORB orb = (ORB)request.orb();

	ServerResponseImpl.runServantPostInvoke(orb, request);

	if( request.executePIInResponseConstructor() ) {
	    // Invoke server request ending interception points (send_*):
	    // Note: this may end up with a SystemException or an internal
	    // Runtime ForwardRequest.
	    orb.invokeServerPIEndingPoint( reply );

	    // Note this will be executed even if a ForwardRequest or
	    // SystemException is thrown by a Portable Interceptors ending 
	    // point since we end up in this constructor again anyway.
	    orb.cleanupServerPIRequest();

	    // See (Local)ServerRequestImpl.createSystemExceptionResponse
	    // for why this is necesary.
	    request.setExecutePIInResponseConstructor(false);
	}

	// Once you get here then the final reply is available (i.e.,
	// postinvoke and interceptors have completed.
	if (request.executeRemoveThreadInfoInResponseConstructor()) {
	    ServerResponseImpl.removeThreadInfo(orb, request);
	}
        
	reply.write(this);
	if (reply.getIOR() != null)
	    reply.getIOR().write(this);

        this.reply = reply;
        this.ior = reply.getIOR();
    }

    public boolean isSystemException() {
        if (reply != null)
            return reply.getReplyStatus() == ReplyMessage.SYSTEM_EXCEPTION;
        return false;
    }

    public boolean isUserException() {
        if (reply != null)
            return reply.getReplyStatus() == ReplyMessage.USER_EXCEPTION;
        return false;
    }

    public boolean isLocationForward() {
        if (ior != null)
            return true;
        return false;
    }

    public IOR getForwardedIOR() {
    	return ior;
    }

    public int getRequestId() {
        if (reply != null)
            return reply.getRequestId();
        return -1;
    }

    public ServiceContexts getServiceContexts() {
        if (reply != null)
            return reply.getServiceContexts();
        return null;
    }

    public SystemException getSystemException() {
        if (reply != null)
            return reply.getSystemException();
        return null;
    }

    public ReplyMessage getReply()
    {
    	return reply ;
    }

    public ClientResponse getClientResponse()
    {
        // set the size of the marshalled data in the message header
        getMessage().setSize(getByteBuffer(), getSize());

        // Construct a new ClientResponse out of the buffer in this ClientRequest
        LocalClientResponseImpl result =
            new LocalClientResponseImpl(
                (com.sun.corba.se.internal.iiop.ORB) orb(),
                toByteArray(), reply);

        // NOTE (Ram J) (06/02/2000) if we set result.setIndex(bodyBegin) here
        // then the LocalClientResponse does not need to read the headers anymore.
        // This will be an optimisation which is can be done to speed up the
        // local invocation by avoiding reading the headers in the local cases.

        // BUGFIX(Ram Jeyaraman) result.setOffset is now done in
        // LocalClientResponseImpl constructor.
        /*
          // Skip over all of the GIOP header information.  This positions
          // the offset in the buffer so that the skeleton can correctly read
          // the marshalled arguments.
          result.setOffset( bodyBegin ) ;
        */

        return result ;
    }

    /**
     * Check to see if the response is local.
     */
    public boolean isLocal(){
        return true;
    }

    private ReplyMessage reply;
    private IOR ior; // forwarded IOR
}
