/*
 * @(#)ServerRequestImpl.java	1.80 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.corba.se.internal.iiop;

import org.omg.CORBA.SystemException;
import org.omg.CORBA.portable.UnknownException;
import org.omg.CORBA.UNKNOWN;
import org.omg.CORBA.INTERNAL;
import org.omg.CORBA.CompletionStatus;

import com.sun.corba.se.internal.core.ServerRequest;
import com.sun.corba.se.internal.core.ServiceContext;
import com.sun.corba.se.internal.core.DuplicateServiceContext;
import com.sun.corba.se.internal.core.SendingContextServiceContext;
import com.sun.corba.se.internal.core.ORBVersionServiceContext;
import com.sun.corba.se.internal.core.ORBVersionFactory;
import com.sun.corba.se.internal.core.UEInfoServiceContext;
import com.sun.corba.se.internal.core.ServiceContexts;
import com.sun.corba.se.internal.core.ServerResponse;
import com.sun.corba.se.internal.core.IOR;
import com.sun.corba.se.internal.core.MarshalOutputStream;
import com.sun.corba.se.internal.ior.ObjectKey;
import com.sun.corba.se.internal.orbutil.ORBUtility;
import com.sun.corba.se.internal.core.ORBVersion;
import com.sun.corba.se.internal.iiop.messages.MessageBase;
import com.sun.corba.se.internal.iiop.messages.ReplyMessage;
import com.sun.corba.se.internal.iiop.messages.RequestMessage;

public class ServerRequestImpl extends IIOPInputStream implements ServerRequest
{

    private void dprint( String msg )
    {
	ORBUtility.dprint( this, msg ) ;
    }

    org.omg.CORBA.portable.OutputStream replyStream;
    org.omg.CORBA.portable.OutputStream exceptionReplyStream;

    public static final int UNKNOWN_EXCEPTION_INFO_ID = 9;

    protected ServerRequestImpl(Connection c, byte[] buf, RequestMessage header)
	throws java.io.IOException
    {
	super(c, buf, header);
	com.sun.corba.se.internal.corba.ORB theOrb = (com.sun.corba.se.internal.corba.ORB)orb() ;
	if (theOrb.subcontractDebugFlag)
	    dprint( "Constructing ServerRequestImpl object" ) ;

	this.request = header;
    }

    protected ServerRequestImpl()
	throws java.io.IOException
    {
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
        return new ServerResponseImpl(this,
                                      getServiceContextsForReply(getConnection(),
                                                                 svc));
    }
    
    public org.omg.CORBA.portable.OutputStream createReply() {
        if ( replyStream == null ) {
            replyStream = 
		(org.omg.CORBA.portable.OutputStream)
		createResponse(null);
        } 
        return replyStream;
    }

    public org.omg.CORBA.portable.OutputStream createExceptionReply() {
        if ( exceptionReplyStream == null ) {
            exceptionReplyStream =  
		(org.omg.CORBA.portable.OutputStream)
		createUserExceptionResponse(null);
        }
        return exceptionReplyStream;
    }
    
    public ServerResponse createUserExceptionResponse(ServiceContexts svc) {
	return new ServerResponseImpl((ServerRequestImpl)this, 
                                      getServiceContextsForReply(getConnection(),
                                                                 svc), 
                                      true);
    }

    /**
     * Create a response that represents an unknown exception.
     *
     */
    public ServerResponse createUnknownExceptionResponse(
	UnknownException ex
    )
    {
	ServiceContexts contexts = null;
	SystemException sys = new UNKNOWN( 0, 
	    CompletionStatus.COMPLETED_MAYBE);

        try {
	    contexts = new ServiceContexts( (com.sun.corba.se.internal.corba.ORB)orb() );
	    UEInfoServiceContext uei = new UEInfoServiceContext(sys);
	    contexts.put( uei ) ;
        } catch (DuplicateServiceContext d) {
	    // can't happen
        }

	return createSystemExceptionResponse(sys,contexts);
    }

    public ServerResponse createSystemExceptionResponse(
	SystemException ex, ServiceContexts svc) 
    {
	// It is possible that fragments of response have already been
	// sent.  Then an error may occur (e.g. marshaling error like
	// non serializable object).  In that case it is too late
	// to send the exception.  We just return the existing fragmented
	// stream here.  This will cause an incomplete last fragment
	// to be sent.  Then the other side will get a marshaling error
	// when attempting to unmarshal.

	IIOPOutputStream existingOutputStream =
	    ((IIOPConnection)getConnection())
	        .getIdToFragmentedOutputStreamEntry(request.getRequestId());
		
	if (existingOutputStream != null) {
	    return (ServerResponse) existingOutputStream;
	}

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
        
	ServerResponseImpl response =
	    new ServerResponseImpl((ServerRequestImpl)this, 
                                   getServiceContextsForReply(getConnection(),
                                                              svc), 
                                   false);
	ORBUtility.writeSystemException(ex, response);
	return response;
    }

    public ServerResponse createLocationForward(
	IOR ior, ServiceContexts svc)
    {
        ReplyMessage reply 
            = MessageBase.createReply((ORB) orb(), 
				      request.getGIOPVersion(),
                                      request.getRequestId(), 
                                      ReplyMessage.LOCATION_FORWARD,
                                      getServiceContextsForReply(getConnection(),
                                                                 svc), 
                                      ior);
        ServerResponseImpl response = new ServerResponseImpl(this, reply, ior);

        return response;
    }

    protected RequestMessage request;
    
    /**
     * Check to see if request is local.
     */
    public boolean isLocal(){
        return false;
    }

    /**
     * Returns ServiceContexts for reply.  If the contexts parameter is null,
     * it creates a new ServiceContexts instance with the code base (first
     * time only) and ORB version contexts.  Otherwise, it tries to add these
     * to the instance given, and returns that instance.
     */
    private ServiceContexts getServiceContextsForReply(Connection c,
                                                       ServiceContexts contexts) 
    {
	com.sun.corba.se.internal.corba.ORB theOrb 
            = (com.sun.corba.se.internal.corba.ORB)orb() ;

        if (theOrb.subcontractDebugFlag)
            dprint( "getServiceContextsReply called with connection " + c ) ;

        if (contexts == null)
            contexts = new ServiceContexts( theOrb )  ;
			
	// NOTE : We only want to send the runtime context the first time

	if (c != null && !c.isPostInitialContexts()) {

	    c.setPostInitialContexts();
	    SendingContextServiceContext scsc = 
		new SendingContextServiceContext(theOrb.getServantIOR()) ; 

	    try {
		contexts.put( scsc ) ;
                if (theOrb.subcontractDebugFlag)
                    dprint( "Added SendingContextServiceContext" ) ;
	    } catch (DuplicateServiceContext dsc) {
                // Ignore
	    }
	}

        // send ORBVersion servicecontext as part of the Reply

        ORBVersionServiceContext ovsc 
            = new ORBVersionServiceContext(ORBVersionFactory.getORBVersion());

        try {
            contexts.put( ovsc ) ;
            if (theOrb.subcontractDebugFlag)
                dprint("Added ORB version service context");
        } catch (DuplicateServiceContext dsc) {
	    // Ignore
        }

        return contexts;
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
