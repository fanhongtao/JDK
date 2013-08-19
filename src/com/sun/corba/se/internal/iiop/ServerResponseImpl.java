/*
 * @(#)ServerResponseImpl.java	1.41 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.corba.se.internal.iiop;

import org.omg.CORBA.SystemException;

import com.sun.corba.se.internal.core.ServerResponse;
import com.sun.corba.se.internal.core.ServerRequest;
import com.sun.corba.se.internal.core.IOR;
import com.sun.corba.se.internal.core.ServiceContext;
import com.sun.corba.se.internal.core.ServiceContexts;
import com.sun.corba.se.internal.iiop.messages.MessageBase;
import com.sun.corba.se.internal.iiop.messages.ReplyMessage;
import com.sun.corba.se.internal.POA.POAImpl;  // REVISIT - remove later
import com.sun.corba.se.internal.POA.POAORB;  // REVISIT - remove later

public class ServerResponseImpl
    extends     IIOPOutputStream
    implements  ServerResponse
{
    protected ServerResponseImpl(ServerRequestImpl request,
				 ServiceContexts svc)
    {
        this(request,
            MessageBase.createReply(
                        (com.sun.corba.se.internal.iiop.ORB) request.orb(),
                        request.getGIOPVersion(),
                        request.getRequestId(), ReplyMessage.NO_EXCEPTION,
                        svc, null),
            null);
    }

    protected ServerResponseImpl(ServerRequestImpl request,
				 ServiceContexts svc, boolean user)
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

    protected ServerResponseImpl(ServerRequestImpl request, ReplyMessage reply, IOR ior)
    {
	super( request.getGIOPVersion(),
               request.getConnection().getORB(),
               request.getConnection());
	setMessage(reply);
        
        ORB orb = (ORB)request.getConnection().getORB();

	runServantPostInvoke(orb, request);

	if( request.executePIInResponseConstructor() ) {
	    // Invoke server request ending interception points (send_*):
	    // Note: this may end up with a SystemException or an internal
	    // Runtime ForwardRequest
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
	    removeThreadInfo(orb, request);
	}

	reply.write(this);
	if (reply.getIOR() != null) {
	    reply.getIOR().write(this);
        }

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

    /**
     * Check to see if response is local.
     */
    public boolean isLocal(){
        return false;
    }

    static void runServantPostInvoke(ORB orb, ServerRequest request)
    {
	// Run ServantLocator::postinvoke.  This may cause a SystemException
	// which will throw out of the constructor and return later
	// to construct a reply for that exception.  The internal logic
	// of returnServant makes sure that postinvoke is only called once.
	// REVISIT: instead of instanceof, put method on all orbs.
	POAORB poaorb = null;
	// This flag is to deal with BootstrapServer use of reply streams,
	// with ServerDelegate's use of reply streams when a POA ORB is
	// present, etc.
	if (request.executeReturnServantInResponseConstructor()) {
	    // It is possible to get marshaling errors in the skeleton after
	    // postinvoke has completed.  We must set this to false so that
	    // when the error exception reply is constructed we don't try
	    // to incorrectly access poa current (which will be the wrong
	    // one or an empty stack.
	    request.setExecuteReturnServantInResponseConstructor(false);
	    request.setExecuteRemoveThreadInfoInResponseConstructor(true);
	    if (orb instanceof POAORB) {
		poaorb = (POAORB) orb;
		POAImpl poaimpl = poaorb.getCurrent().getPOA();
		// REVISIT: is synchronization necessary still?
		synchronized (poaimpl) {
		    poaimpl.returnServant();
		}
	    }
	}
    }

    static void removeThreadInfo(ORB orb, ServerRequest request)
    {
	request.setExecuteRemoveThreadInfoInResponseConstructor(false);
	POAORB poaorb = (POAORB)orb;
	POAImpl poaimpl = poaorb.getCurrent().getPOA();
	// REVISIT: is synchronization necessary still?
	synchronized (poaimpl) {
	    poaimpl.removeThreadInfo();
	}
    }

    private ReplyMessage reply;
    private IOR ior; // forwarded IOR
}
