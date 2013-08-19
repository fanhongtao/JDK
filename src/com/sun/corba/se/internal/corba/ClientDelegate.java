/*
 * @(#)ClientDelegate.java	1.87 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

/*
 * Licensed Materials - Property of IBM
 * RMI-IIOP v1.0
 * Copyright IBM Corp. 1998 1999  All Rights Reserved
 *
 * US Government Users Restricted Rights - Use, duplication or
 * disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 */
package com.sun.corba.se.internal.corba;

import java.util.Iterator;

import org.omg.CORBA.INTERNAL;
import org.omg.CORBA.ORB;
import org.omg.CORBA.portable.RemarshalException;
import org.omg.CORBA.SystemException;
import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.WrongTransaction;
import org.omg.CORBA.Request;
import org.omg.CORBA.NamedValue;
import org.omg.CORBA.NVList;
import org.omg.CORBA.Context;
import org.omg.CORBA.ContextList;
import org.omg.CORBA.ExceptionList;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.DATA_CONVERSION;
import org.omg.CORBA.UNKNOWN;

import org.omg.CORBA.portable.ObjectImpl;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Delegate;
import org.omg.CORBA.portable.ServantObject;
import org.omg.CORBA.portable.ApplicationException;
import org.omg.CORBA.portable.UnknownException;
import org.omg.IOP.TAG_CODE_SETS;

import javax.rmi.CORBA.Util;
import javax.rmi.CORBA.Tie;

import java.rmi.RemoteException;

import com.sun.org.omg.SendingContext.CodeBase;

import com.sun.corba.se.internal.ior.IIOPProfile;
import com.sun.corba.se.internal.ior.IIOPProfileTemplate;
import com.sun.corba.se.internal.ior.ObjectKey;

import com.sun.corba.se.internal.core.IOR;
import com.sun.corba.se.internal.core.ClientGIOP;
import com.sun.corba.se.internal.core.ClientSubcontract;
import com.sun.corba.se.internal.core.ClientRequest;
import com.sun.corba.se.internal.core.ClientResponse;
import com.sun.corba.se.internal.core.ServiceContext;
import com.sun.corba.se.internal.core.ServiceContexts;
import com.sun.corba.se.internal.core.MarshalOutputStream;
import com.sun.corba.se.internal.core.MarshalInputStream;
import com.sun.corba.se.internal.core.CodeSetComponentInfo;
import com.sun.corba.se.internal.core.CodeSetConversion;
import com.sun.corba.se.internal.core.UEInfoServiceContext;
import com.sun.corba.se.internal.core.CodeSetServiceContext;
import com.sun.corba.se.internal.core.SendingContextServiceContext;
import com.sun.corba.se.internal.core.ORBVersionServiceContext;
import com.sun.corba.se.internal.core.NoSuchServiceContext;
import com.sun.corba.se.internal.core.DuplicateServiceContext;
import com.sun.corba.se.internal.core.GIOPVersion;
import com.sun.corba.se.internal.core.ORBVersion;

import com.sun.corba.se.internal.orbutil.MinorCodes;

import com.sun.corba.se.internal.util.JDKBridge;
import com.sun.corba.se.internal.orbutil.ORBUtility;

import com.sun.corba.se.internal.iiop.messages.ReplyMessage;
import com.sun.corba.se.internal.iiop.Connection;
import com.sun.corba.se.internal.iiop.ClientRequestImpl;
import com.sun.corba.se.internal.iiop.LocalClientRequestImpl;
import com.sun.corba.se.internal.iiop.messages.KeyAddr;
import com.sun.corba.se.internal.core.ORBVersionFactory;

import com.sun.corba.se.internal.ior.CodeSetsComponent;
import com.sun.corba.se.internal.ior.TaggedComponent;
import com.sun.corba.se.internal.orbutil.ORBConstants;

import com.sun.corba.se.internal.iiop.messages.KeyAddr;
import com.sun.corba.se.internal.iiop.messages.ProfileAddr;
import com.sun.corba.se.internal.iiop.messages.ReferenceAddr;

/**
 * ClientDelegate is the RMI client-side subcontract or representation
 * It implements RMI delegate as well as our internal ClientSubcontract
 * interface.
 */
public class ClientDelegate
    extends org.omg.CORBA_2_3.portable.Delegate
    implements ClientSubcontract
{
    protected void dprint( String msg )
    {
	ORBUtility.dprint( this, msg ) ;
    }

    protected com.sun.corba.se.internal.corba.ORB orb;
    protected IOR ior;
    protected IOR locatedIOR;
    protected boolean isCachedHashValue = false;
    protected int cachedHashValue;
    protected int scid; // my subcontract id
    protected ServantObject servant;
    protected ClassLoader servantClassLoader ;

    protected short addressingDisposition = KeyAddr.value;

    protected ThreadLocal isNextIsLocalValid = new ThreadLocal( ) {
        protected Object initialValue( ) {
            return Boolean.TRUE;
        }
    };

    // the connection with which to store the codebase returned by the
    // server

    // orb version to be stored into thread local

    public ClientDelegate() {
	super();
	servant = null;      
    }

    public ClientDelegate(com.sun.corba.se.internal.corba.ORB orb,
			  IOR ior,
			  int id) 
    {
        this(orb,ior,id,null);
    }

    public ClientDelegate(com.sun.corba.se.internal.corba.ORB orb,
			  IOR ior,
			  int id,
			  java.lang.Object servant) 
    {
	this();
	setOrb(orb);
	this.ior = ior;
	this.locatedIOR = ior;
	this.scid = id;
	if (servant != null && servant instanceof Tie) {
	    servantClassLoader = servant.getClass().getClassLoader() ;
	    this.servant = new ServantObject();
	    this.servant.servant = ((Tie)servant).getTarget();
	} else {
	    this.servant = null;
	    servantClassLoader = null ;
	}                    
    }

    public void unexport() {
	// DO NOT set the IOR to null.  (Un)exporting is only concerns
	// the servant not the IOR.  If the ior is set to null then
	// null pointer exceptions happen during an colocated invocation.
	// It is better to let the invocation proceed and get OBJECT_NOT_EXIST
	// from the server side.
	//ior = null;
	servant = null;
    }

    public IOR marshal() {
	return ior;
    }

    public IOR getIOR() {
	return ior;
    }

    public IOR getLocatedIOR() {
        return locatedIOR;
    }

    public void unmarshal(IOR ior) {
	this.ior = ior;
	this.locatedIOR = ior;
    }

    public void setAddressingDisposition(short value){
        this.addressingDisposition = value;    
    }
    
    public short getAddressingDisposition() {
        return this.addressingDisposition;
    }
    
    public void setLocatedIOR( IOR locatedIOR ) {
        this.locatedIOR = locatedIOR;
    }

    public void setId(int id) {
	this.scid = id;
    }

    public int getId()
    {
	return scid ;
    }

    public void setOrb(com.sun.corba.se.internal.core.ORB orb) {
	this.orb = (com.sun.corba.se.internal.corba.ORB) orb;
        this.addressingDisposition = this.orb.getGIOPAddressDisposition();
    }

    /*
     * NOTE: This version of invoke should ONLY be used externally 
     * by a DII call (it is used internally by the streams invoke).
     * Otherwise the PortableInterceptor internal stack will become
     * unbalanced.
     *
     * It is invoked from the DII implementation: RequestImpl.doInvocation().
     *
     * invoke(org.omg.CORBA.Object, OutputStream) calls this one, so
     * do any service context processing here.
     *
     * This is also called from GenericPOAClientSC's
     * invoke(org.omg.CORBA.Object, OutputStream).
     */
    public ClientResponse invoke(ClientRequest request) {
	if (orb.subcontractDebugFlag)
	    dprint( "invoke(ClientRequest) called" ) ;

        ClientResponse resp = request.invoke();
	// We know for sure now that we've sent a message.
	// So OK to not send initial again.
	if (request.getConnection() != null) {
	    request.getConnection().setPostInitialContexts();
	}

        // Notify Portable Interceptors of client response.
	// This must happen here so that both stream-based and DII
	// calls have their hands on the response.
        orb.setClientPIInfo( resp );

        if (request.isOneWay()) {
            return null;
        }

        consumeServiceContexts(resp);

        // Now that we have the service contexts processed and the
        // correct ORBVersion set, we must finish initializing the
        // stream.
        resp.performORBVersionSpecificInit();


        // FIX(Ram J) (04/28/2000) The location forwarded behaviour and the
        // system exception(COMM_FAILURE) behaviour (i.e retry from root ior)
        // is handled in this.invoke(Object, OutputStream) which called this
        // method. The client stubs should not directly call this method.
        //  However, there are a few methods in the ClientDelegate : is_a(),
        // get_interface_def() and  non_existent() which directly call
        // this method. Such methods should handle the location forwarded
        // responses by building new requests and system exception(COMM_FAILURE)
        // responses by retrying from root ior.

        return resp;
    }

    /*
     * This version of invoke should be used by everthing but a DII call.
     */
    public InputStream invoke(org.omg.CORBA.Object self, OutputStream out)
	throws ApplicationException,
	       org.omg.CORBA.portable.RemarshalException
    {

        ClientRequest request = (ClientRequest) out;

        ClientResponse response = this.invoke(request);

        // For PI code:
        Exception exception = null;

        if (request.isOneWay()) { //oneway calls
            // Invoke Portable Interceptors with receive_other
            exception = orb.invokeClientPIEndingPoint(
                ReplyMessage.NO_EXCEPTION, exception );
            continueOrThrowSystemOrRemarshal( exception );
            return null;
        }

        // COMMENT(Ram J) 10/01/2000 There is no need to set the response
        // stream's connection to that of the request stream. The response
        // stream will have a connection associated if the response had been
        // built out of the server response; else if the response was built out
        // of SystemException arising out of response handling, then there
        // will be no connection or service context available with the response
        // stream. In such a case, the consumeServiceContexts has to return
        // gracefully.
        /*
	// consume service contexts that were returned to the client
	Connection c = request.getConnection();
	if (response != null)
            response.setConnection(c);
        */

        // Processing of service contexts is done in invoke(ClientRequest)

        if (response.isSystemException()) {

            SystemException se = response.getSystemException();

            boolean doRemarshal = false;

            // FIX(Ram J) (04/28/2000) added  locatedIOR = ior
            // and throw RemarshallException to force retrying
            // from root ior, if system exception is COMM_FAILURE.
            // WARNING: There is a risk of infinite loopback if the requests on
            // location forwarded ior result in system exception (COMM_FAILURE)
            if (se instanceof org.omg.CORBA.COMM_FAILURE
                && se.completed == CompletionStatus.COMPLETED_NO) {
                if (locatedIOR != ior) {
                    locatedIOR = ior; // retry from root ior

                    doRemarshal = true;
                }
            }

	    /* This used to live in a loop in IIOPOutputStream.invoke.
	       That loop had a retry count to bound the number of tries.
	       However that loop did not work once fragmentation was
	       put in.  So it was moved here.  However, there is no
	       place to hang the count since this throws out to the Stub.
	       We need invocation level (i.e., thread local) date like
	       in PI to put this count. That is scheduled for Taggart.
	       For now, just turn this off.
            if (se.minor == MinorCodes.CONN_CLOSE_REBIND && 
                (se instanceof org.omg.CORBA.COMM_FAILURE)) {

                doRemarshal = true;
            }
	    */

            if (doRemarshal) {
                    
		// Invoke Portable Interceptors with receive_exception:
		exception = orb.invokeClientPIEndingPoint(
                    ReplyMessage.SYSTEM_EXCEPTION, se );

		// If PI did not change the exception, throw a
		// Remarshal.  Otherwise, throw the exception PI
		// wants thrown.
		if( se == exception ) {
		    // exception = null is to maintain symmetry with
		    // GenericPOAClientSC.
		    //exception = null;
		    throw new RemarshalException();
                }
                else {
                    continueOrThrowSystemOrRemarshal( exception );
                    throw new INTERNAL("Assertion failed. " +
                                       "exception should not be null." );
                }
            }

            ServiceContexts contexts =  response.getServiceContexts();
            if (contexts != null) {
                try {
                    UEInfoServiceContext usc =
			(UEInfoServiceContext)
			contexts.get(UEInfoServiceContext.SERVICE_CONTEXT_ID);
                    Throwable unknown = usc.getUE() ;
                    UnknownException ue = new UnknownException(unknown);

                    // Invoke Portable Interceptors with receive_exception:
                    exception = orb.invokeClientPIEndingPoint(
                        ReplyMessage.SYSTEM_EXCEPTION, ue );

                    continueOrThrowSystemOrRemarshal( exception );
		    throw new INTERNAL(
			"Assertion failed.  exception should not be null." );
                } catch (NoSuchServiceContext exc) {
                    // NO-OP: handled by system exception below
                }
            }

            // Invoke Portable Interceptors with receive_exception:
            exception = orb.invokeClientPIEndingPoint(
                ReplyMessage.SYSTEM_EXCEPTION, se );

            continueOrThrowSystemOrRemarshal( exception );

            // Note: We should never need to execute this line, but
            // we should assert in case exception is null somehow.
            throw new INTERNAL(
		"Assertion failed: exception should not be null." );
        } else if (response.isUserException()) {
            ApplicationException appException =
                new ApplicationException(response.peekUserExceptionId(),
                           (org.omg.CORBA.portable.InputStream)response);

            // Invoke Portable Interceptors with receive_exception
            // (user exception):
            exception = orb.invokeClientPIEndingPoint(
                ReplyMessage.USER_EXCEPTION, appException );

            if( !(exception instanceof ApplicationException) ) {
                continueOrThrowSystemOrRemarshal( exception );
            }

            throw (ApplicationException)exception;
        } else if (response.isLocationForward()) {
            // FIX(Ram J) (04/28/2000) added setting locatedIOR
            locatedIOR = response.getForwardedIOR();

            // Invoke Portable Interceptors with receive_other:
	    Exception newException = orb.invokeClientPIEndingPoint(
		ReplyMessage.LOCATION_FORWARD, null );

            // For consistency with corresponding code in GenericPOAClientSC:
	    if( !(newException instanceof RemarshalException) ) {
		exception = newException;
	    }

            // If PI did not change exception, throw Remarshal, else
            // throw the exception PI wants thrown.
            if( exception != null ) {
                continueOrThrowSystemOrRemarshal( exception );
            }

            throw new org.omg.CORBA.portable.RemarshalException();
        } else if (response.isDifferentAddrDispositionRequested()) {
            // set the desired target addressing disposition.
            addressingDisposition = response.getAddrDisposition();

            // Invoke Portable Interceptors with receive_other:
	    Exception newException = orb.invokeClientPIEndingPoint(
		ReplyMessage.NEEDS_ADDRESSING_MODE, null);

            // For consistency with corresponding code in GenericPOAClientSC:
	    if( !(newException instanceof RemarshalException) ) {
		exception = newException;
	    }

            // If PI did not change exception, throw Remarshal, else
            // throw the exception PI wants thrown.
            if( exception != null ) {
                continueOrThrowSystemOrRemarshal( exception );
            }

            throw new org.omg.CORBA.portable.RemarshalException();            
        } else /* normal response */ {
            // Invoke Portable Interceptors with receive_reply:
            exception = orb.invokeClientPIEndingPoint(
                ReplyMessage.NO_EXCEPTION, null );

            // Remember: not thrown if exception is null.
            continueOrThrowSystemOrRemarshal( exception );

            return (InputStream) response;
        }
    }

    // Filters the given exception into a SystemException or a
    // RemarshalException and throws it.  Assumes the given exception is
    // of one of these two types.  This is a utility method for
    // the above invoke code which must do this numerous times.
    // If the exception is null, no exception is thrown.
    //
    // Note that this code is duplicated in GenericPOAClientSC.java
    private void continueOrThrowSystemOrRemarshal( Exception exception )
        throws SystemException, RemarshalException
    {
        if( exception == null ) {
            // do nothing.
        }
        else if( exception instanceof RemarshalException ) {
            throw (RemarshalException)exception;
        }
        else {
            throw (SystemException)exception;
        }
    }

    public ServiceContexts getServiceContexts( Connection c,
	int requestId, String opName, boolean isOneWay, GIOPVersion giopVersion )
    {
	ServiceContexts contexts = new ServiceContexts( orb ) ;

        addCodeSetServiceContext(c, contexts, giopVersion);

	// ORBVersion servicecontext needs to be sent
	ORBVersionServiceContext ovsc = new ORBVersionServiceContext(
	                ORBVersionFactory.getORBVersion() ) ;
	try {
	    contexts.put( ovsc ) ;
	} catch (DuplicateServiceContext dsc) {
	    throw new INTERNAL() ;
	}

	// NOTE : We only want to send the runtime context the first time
	if ((c != null) && !c.isPostInitialContexts()) {
	    // Do not do c.setPostInitialContexts() here.
	    // If a client interceptor send_request does a ForwardRequest
	    // which ends up using the same connection then the service 
	    // context would not be sent.
	    SendingContextServiceContext scsc =
		new SendingContextServiceContext( orb.getServantIOR() ) ; //d11638
	    try {
		contexts.put( scsc ) ;
	    } catch (DuplicateServiceContext dsc) {
		throw new INTERNAL() ;
	    }
	}

	return contexts ;
    }

    public void consumeServiceContexts(ClientResponse response)
    {
	ServiceContexts ctxts = response.getServiceContexts();
	ServiceContext sc ;

        if (ctxts == null) {
            return; // no service context available, return gracefully.
        }

	try {
	    sc = ctxts.get( SendingContextServiceContext.SERVICE_CONTEXT_ID ) ;

	    SendingContextServiceContext scsc =
		(SendingContextServiceContext)sc ;
	    IOR ior = scsc.getIOR() ;

	    try {
	        // set the codebase returned by the server
	        if (response.getConnection() != null) {
	            response.getConnection().setCodeBaseIOR(ior);
	        }
	    } catch (ThreadDeath td) {
		throw td ;
	    } catch (Throwable t) {
		throw new DATA_CONVERSION( MinorCodes.BAD_STRINGIFIED_IOR,
					   CompletionStatus.COMPLETED_NO);
	    }
	} catch (NoSuchServiceContext exc) {
	    // ignore: this type not present
	}

	// see if the version subcontract is present, if yes, then set
	// the ORBversion
	try {
	    sc = ctxts.get( ORBVersionServiceContext.SERVICE_CONTEXT_ID ) ;

	    ORBVersionServiceContext ovsc =
	       (ORBVersionServiceContext) sc;

	    ORBVersion version = ovsc.getVersion();
	    orb.setORBVersion( version ) ;
	} catch (NoSuchServiceContext exc) {
	    // ignore: this type not present

            // If we were talking to Kestrel, we would have set the
            // ORB version as such at invoke time after examining
            // the IOR.
	}
    }

    /*
     * This is the method used by the rest of the system to
     * create requests.  It is used both my streams-based stubs
     * and by DII-based calls (i.e., from RequestImpl.doInvocation()
     * before marshaling arguments.
     */
    public ClientRequest createRequest( String opName, boolean isOneWay )
    {
	// Create request object which is also the outputstream
	ClientRequest request = null;
	ClientGIOP giop = orb.getClientGIOP();
	int id = giop.allocateRequestId() ;

        // Initiate this request with Portable Interceptors.
	// This is done here so if the following COMM_FAILURE occurs we will
	// keep the PI ClientRequestInfo stack balanced.  The COMM_FAILURE
	// happens before invoking starting points in getConnection so we
	// don't need to invoke ending points.
        orb.initiateClientPIRequest( false );

	try {

	    request = createRequest( locatedIOR, opName, isOneWay, id );

	} catch (org.omg.CORBA.COMM_FAILURE ex) {
	    if (locatedIOR == ior) {
		throw ex;
	    }
	    // The COMM_FAILURE may have happened because the server died.
	    // Try creating the request with the original IOR.

	    request = createRequest(ior, opName, isOneWay, id);
	}

	return request;
    }

    /*
     * This helper method actually creates the approprieate
     * request object.
     */
    protected ClientRequest createRequest(IOR iorForThisRequest, String opName,
					  boolean isOneWay, int requestId)

    {
	IIOPProfile iop = iorForThisRequest.getProfile();
	ObjectKey  key = iop.getObjectKey();        
	Connection conn = null ;
	ServiceContexts svc = null ;

	GIOPVersion giopVersion = 
	    GIOPVersion.chooseRequestVersion(orb, iorForThisRequest);

	if (iorForThisRequest.isLocal()) {
	    svc = getServiceContexts( conn, requestId, opName, isOneWay, giopVersion ) ;

            // Set the sources of info for the Portable Interceptors
	    // ClientRequestInfo object, and invoke the client interceptor
	    // starting points.  Note these 2 lines are duplicated below.
	    // We explicitly pass in the effective target in
	    // case it is different than the locatedIOR field.  For example,
	    // in case of the COMM_FAILURE in the other createRequest().
            orb.setClientPIInfo( null, this, iorForThisRequest, iop, requestId,
				 opName, isOneWay, svc );
            try {
                orb.invokeClientPIStartingPoint();
            }
            catch( RemarshalException e ) {
                // If this is a forward request, recursively call createRequest
                // with the located IOR.

                return createRequest( locatedIOR, opName, isOneWay, requestId);
            }

	    return new LocalClientRequestImpl(
				GIOPVersion.V1_2,
				(com.sun.corba.se.internal.iiop.ORB)orb, 
				iorForThisRequest, this.addressingDisposition, 
                                opName, isOneWay, svc, requestId);
	} else {
            conn = orb.getClientGIOP().getConnection(iorForThisRequest);

            // Code set negotiation occurs on the first request
            if (conn != null && conn.getCodeSetContext() == null)
                performCodeSetNegotiation(conn, giopVersion);
            
	    svc = getServiceContexts( conn, requestId, opName, isOneWay, giopVersion ) ;

            // Set Portable Interceptors info.  See above comment.
            orb.setClientPIInfo( conn, this, iorForThisRequest, iop, requestId,
				 opName, isOneWay, svc );
            try {
                orb.invokeClientPIStartingPoint();
            }
            catch( RemarshalException e ) {
                // If this is a forward request, recursively call createRequest
                // with the located IOR.

                return createRequest( locatedIOR, opName, isOneWay, requestId);
            }

	    return new ClientRequestImpl(giopVersion, iorForThisRequest, 
                                         this.addressingDisposition, 
                                         opName, isOneWay, svc,
					 requestId, conn);
	}
    }

    // Invoked by DII layer (from RequestImpl.doInvocation())
    // after unmarshaling reply and exceptions.
    public void releaseReply(ClientResponse resp, String method,
			     Exception exception)
	throws WrongTransaction, SystemException
    {
    }

    public void releaseReply(org.omg.CORBA.Object self, 
			     InputStream input)
    {
	orb.sendCancelRequestIfFinalFragmentNotSent();

        // Invoke Portable Interceptors cleanup.  This is done to handle
        // exceptions during stream marshaling.  More generally, exceptions
        // that occur in the ORB after send_request (which includes
        // after returning from _request) before _invoke:
        orb.cleanupClientPIRequest();
    }

    public boolean is_equivalent(org.omg.CORBA.Object obj,
				 org.omg.CORBA.Object ref)
    {
	if ( ref == null )
	    return false;
	ObjectImpl oi = (ObjectImpl)ref;

	ClientSubcontract del = (ClientSubcontract)oi._get_delegate();
	if (del == this)
	    return true;
	if (!(del instanceof ClientDelegate))
	    return false;
	return ior.isEquivalent(del.marshal());
    }

    public int hash(org.omg.CORBA.Object obj, int maximum)
    {
	int h = this.hashCode();
	if ( h > maximum )
	    return 0;
	return h;
    }

    public int hashCode()
    {
	// This is not synchronized since the returned value is constant
	// so the overhead of synchronization is unnecessary.
	if (! isCachedHashValue) {
	    cachedHashValue = ior.stringify().hashCode();
	    isCachedHashValue = true;
	}
	return cachedHashValue;
    }

    public org.omg.CORBA.Object duplicate(org.omg.CORBA.Object orig) {
	return orig;
    }

    public void release(org.omg.CORBA.Object obj) {
	// DONT clear out internal variables to release memory !!
	// This delegate may be pointed-to by other objrefs !
    }

    public org.omg.CORBA.Request create_request(org.omg.CORBA.Object obj,
						org.omg.CORBA.Context ctx,
						String operation,
						org.omg.CORBA.NVList arg_list,
						org.omg.CORBA.NamedValue result)
    {
	return new RequestImpl(orb, obj, ctx, operation, arg_list,
			       result, null, null);
    }

    public org.omg.CORBA.Request request(org.omg.CORBA.Object obj,
					 String operation)
    {
	return new RequestImpl(orb, obj, null, operation, null, null, null,
			       null);
    }

    public org.omg.CORBA.ORB orb(org.omg.CORBA.Object self)
    {
	return this.orb;
    }

    public org.omg.CORBA.Request create_request(org.omg.CORBA.Object obj,
						org.omg.CORBA.Context ctx,
						String operation,
						org.omg.CORBA.NVList arg_list,
						org.omg.CORBA.NamedValue result,
						org.omg.CORBA.ExceptionList exclist,
						org.omg.CORBA.ContextList ctxlist)
    {
	return new RequestImpl(orb, obj, ctx, operation, arg_list, result,
			       exclist, ctxlist);
    }

    private static String AppExcInSpecialMethod =
	"ApplicationException in SpecialMethod - should not happen";

    public org.omg.CORBA.Object get_interface_def(org.omg.CORBA.Object obj) 
    {
	InputStream is = null;
        try {
	    ClientRequest os = createRequest("_interface", false);
	    is = (InputStream) invoke((org.omg.CORBA.Object)null,
				      (OutputStream)os);

	    ObjectImpl objimpl = (ObjectImpl) is.read_Object();

	    // check if returned object is of correct type
	    if ( !objimpl._is_a("IDL:omg.org/CORBA/InterfaceDef:1.0") )
                throw new org.omg.CORBA.UNKNOWN(
                    "InterfaceDef object of wrong type returned by server", 0,
                    CompletionStatus.COMPLETED_MAYBE);

	    // instantiate the stub
	    ObjectImpl stub;
	    try {
                stub = (ObjectImpl)
                    JDKBridge.loadClass("org.omg.CORBA._InterfaceDefStub").
		        newInstance();
	    } catch (Exception ex) {
                throw new org.omg.CORBA.UNKNOWN(
		    "org.omg.CORBA._InterfaceDefStub class not available", 0,
                    CompletionStatus.COMPLETED_NO);
	    }

	    org.omg.CORBA.portable.Delegate del = objimpl._get_delegate();
	    stub._set_delegate(del);

	    return stub;

	} catch (ApplicationException e) {
	    // This cannot happen.
	    throw new INTERNAL(AppExcInSpecialMethod);
	} catch (RemarshalException e) {
	    return get_interface_def(obj);
	} finally {
	    releaseReply((org.omg.CORBA.Object)null, (InputStream)is);
        }
    }

    public boolean is_a(org.omg.CORBA.Object obj, String dest) 
    {
        // dest is the typeId of the interface to compare against.
        // repositoryIds is the list of typeIds that the stub knows about.

        // First we look for an answer using local information.

        String [] repositoryIds = ((ObjectImpl)obj)._ids();
        String myid = ior.getTypeId();
        if ( dest.equals(myid) ) {
            return true;
	}
        for ( int i=0; i<repositoryIds.length; i++ ) {
            if ( dest.equals(repositoryIds[i]) ) {
		return true;
	    }
	}

        // But repositoryIds may not be complete, so it may be necessary to
        // go to server.

	InputStream is = null;
        try {
            ClientRequest os = createRequest("_is_a", false);
            os.write_string(dest);
            is = (InputStream) invoke((org.omg.CORBA.Object) null,
				      (OutputStream)os);

	    return is.read_boolean();

        } catch (ApplicationException e) {
	    // This cannot happen.
	    throw new INTERNAL(AppExcInSpecialMethod);
	} catch (RemarshalException e) {
	    return is_a(obj, dest);
	} finally {
	    releaseReply((org.omg.CORBA.Object)null, (InputStream)is);
        }
    }

    public boolean non_existent(org.omg.CORBA.Object obj) 
    {
	InputStream is = null;
        try {
            ClientRequest os = createRequest("_non_existent", false);
            is = (InputStream) invoke((org.omg.CORBA.Object)null,
				      (OutputStream)os);

	    return is.read_boolean();

	} catch (ApplicationException e) {
	    // This cannot happen.
	    throw new INTERNAL(AppExcInSpecialMethod);
	} catch (RemarshalException e) {
	    return non_existent(obj);
	} finally {
	    releaseReply((org.omg.CORBA.Object)null, (InputStream)is);
        }
    }

    public OutputStream request(org.omg.CORBA.Object self,
				String operation,
				boolean responseExpected) {
	return (OutputStream) createRequest(operation, !responseExpected);
    }

    /**
     * Returns true if this object is implemented by a local servant.
     * We maintain a ThreadLocal to keep track of isLocal() calls to make
     * sure that we do not loop more than 2 times. In the first call we
     * set the isNextIsLocalValid to false so that the next call on the same
     * thread would force to return false in case servant_preinvoke() returns
     * null. 
     *
     * @param self The object reference which delegated to this delegate.
     * @return true only if the servant incarnating this object is located in
     * this Java VM. Return false if the servant is not local or the ORB
     * does not support local stubs for this particular servant. The default
     * behavior of is_local() is to return false.
     */
    public boolean is_local(org.omg.CORBA.Object self) {
        if ( ((Boolean) isNextIsLocalValid.get()).booleanValue( ) == true ) {
	    return ior.isLocal();
        }
        isNextIsLocalValid.set( Boolean.TRUE );
        return false;
    }

    public boolean useLocalInvocation( org.omg.CORBA.Object self )
    {
	return false ;
    }

    /**
     * Returns a Java reference to the servant which should be used for this
     * request. servant_preinvoke() is invoked by a local stub.
     * If a ServantObject object is returned, then its servant field
     * has been set to an object of the expected type (Note: the object may
     * or may not be the actual servant instance). The local stub may cast
     * the servant field to the expected type, and then invoke the operation
     * directly. The ServantRequest object is valid for only one invocation,
     * and cannot be used for more than one invocation.
     *
     * @param self The object reference which delegated to this delegate.
     *
     * @param operation a string containing the operation name.
     * The operation name corresponds to the operation name as it would be
     * encoded in a GIOP request.
     *
     * @param a Class object representing the expected type of the servant.
     * The expected type is the Class object associated with the operations
     * class of the stub's interface (e.g. A stub for an interface Foo,
     * would pass the Class object for the FooOperations interface).
     *
     * @return a ServantObject object.
     * The method may return a null value if it does not wish to support
     * this optimization (e.g. due to security, transactions, etc).
     * The method must return null if the servant is not of the expected type.
     */
    public ServantObject servant_preinvoke(org.omg.CORBA.Object self,
					   String operation,
					   Class expectedType) {
	if (servant != null &&
	    expectedType.isAssignableFrom(servant.servant.getClass())) {

	    // _REVISIT_ Security check? If we put a security check
	    // on the client side of a connection (IIOPConnection.send()?)
	    // to ensure that the client has permission, do so here as
	    // well. Since a permission check is done when the socket
	    // is initially opened, but not on each use, we probably
	    // need to do the check ourselves since the call stack can
	    // be completely different each time, and a new caller may
	    // not have permission.

	    return servant;
	}
        // Set it to false to disallow isLocal to be called again and again
        // in case of servant being null in _servant_preinvoke() call
        isNextIsLocalValid.set( Boolean.FALSE );
	return null;
    }

    /* Returns the codebase for object reference provided.
     * @param self the object reference whose codebase needs to be returned.
     * @return the codebase as a space delimited list of url strings or
     * null if none.
     */
    public String get_codebase(org.omg.CORBA.Object self) {
	if (ior != null) {
	    return ior.getCodebase();
	}
	return null;
    }

    public String toString() {
	return ior.stringify();
    }

    /**
     * This method overrides the org.omg.CORBA.portable.Delegate.equals method,
     * and does the equality check based on IOR equality.
     */
    public boolean equals(org.omg.CORBA.Object self, java.lang.Object other) {

        if (!(other instanceof ObjectImpl)) {
            return false;   
        }
        
        ObjectImpl otherObj = (ObjectImpl) other;
        if (otherObj._get_delegate() instanceof ClientDelegate) {
            ClientDelegate otherDel = (ClientDelegate) otherObj._get_delegate();
            IOR otherIor = otherDel.getIOR();
            return this.ior.equals(otherIor);
        } else if (otherObj._get_delegate() instanceof Delegate) {
            return super.equals(self, other);   
        }

        return false;
    }
    
    private void performCodeSetNegotiation(Connection conn,
                                           GIOPVersion giopVersion) {

        // conn.getCodeSetContext() is null when no other requests have
        // been made on this connection to trigger code set negotation.
        if (conn != null &&
            conn.getCodeSetContext() == null &&
            !giopVersion.equals(GIOPVersion.V1_0)) {
                        
            synchronized(conn) {
                // Double checking.  Don't let any other
                // threads use this connection until the
                // code sets are straight.
                if (conn.getCodeSetContext() != null)
                    return;
                
                // This only looks at the first code set component.  If
                // there can be multiple locations with multiple code sets,
                // this requires more work.
                IIOPProfileTemplate temp = getLocatedIOR().getProfile().getTemplate();
                Iterator iter = temp.iteratorById(TAG_CODE_SETS.value);
                if (!iter.hasNext()) {
                    // Didn't have a code set component.  The default will
                    // be to use ISO8859-1 for char data and throw an
                    // exception if wchar data is used.
                    return;
                }

                // Get the native and conversion code sets the
                // server specified in its IOR
                CodeSetComponentInfo serverCodeSets
                    = ((CodeSetsComponent)iter.next()).getCodeSetComponentInfo();

                // Perform the negotiation between this ORB's code sets and
                // the ones from the IOR
                CodeSetComponentInfo.CodeSetContext result
                    = CodeSetConversion.impl().negotiate(conn.getORB().getCodeSetComponentInfo(),
                                                         serverCodeSets);
                
                conn.setCodeSetContext(result);
            }
        }
    }

    private void addCodeSetServiceContext(Connection conn,
                                          ServiceContexts ctxs,
                                          GIOPVersion giopVersion) {

        // REVISIT.  OMG issue 3318 concerning sending the code set
        // service context more than once was deemed too much for the
        // RTF.  Here's our strategy for the moment:
        //
        // Send it on every request (necessary in cases of fragmentation
        // with multithreaded clients or when the first thing on a
        // connection is a LocateRequest).  Provide an ORB property
        // to disable multiple sends.
        //
        // Note that the connection is null in the local case and no
        // service context is included.  We use the ORB provided
        // encapsulation streams.
        //
        // Also, there will be no negotiation or service context
        // in GIOP 1.0.  ISO8859-1 is used for char/string, and
        // wchar/wstring are illegal.
        //
        if (giopVersion.equals(GIOPVersion.V1_0) || conn == null)
            return;
        
        CodeSetComponentInfo.CodeSetContext codeSetCtx = null;

        if (conn.getORB().alwaysSendCodeSetServiceContext() ||
            !conn.isPostInitialContexts()) {

            // Get the negotiated code sets (if any) out of the connection
            codeSetCtx = conn.getCodeSetContext();
        }

        // Either we shouldn't send the code set service context, or
        // for some reason, the connection doesn't have its code sets.
        // Perhaps the server didn't include them in the IOR.  Uses
        // ISO8859-1 for char and makes wchar/wstring illegal.
        if (codeSetCtx == null)
            return;

        CodeSetServiceContext cssc = new CodeSetServiceContext(codeSetCtx);
        try {
            ctxs.put(cssc);
        } catch (DuplicateServiceContext dsc) {
            // Ignore
        }
    }    
}
