/*
 * @(#)GenericPOAServerSC.java	1.80 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.corba.se.internal.POA;

import java.util.Iterator ;

import org.omg.PortableServer.*;
import org.omg.PortableServer.ServantLocatorPackage.*;
import org.omg.CORBA.Any;
import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.SystemException;
import org.omg.CORBA.OBJECT_NOT_EXIST;
import org.omg.CORBA.OBJ_ADAPTER;
import org.omg.CORBA.INTERNAL;
import org.omg.CORBA.UNKNOWN;
import org.omg.CORBA.portable.*;

import org.omg.PortableServer.DynamicImplementation;

import com.sun.corba.se.internal.core.*;
import com.sun.corba.se.internal.corba.*;
import com.sun.corba.se.internal.orbutil.ORBUtility; //d11638
import com.sun.corba.se.internal.orbutil.ORBConstants;
import com.sun.corba.se.internal.ior.ObjectKey ;
import com.sun.corba.se.internal.ior.ObjectKeyFactory ;
import com.sun.corba.se.internal.ior.ObjectId ;
import com.sun.corba.se.internal.ior.ObjectKeyTemplate ;
import com.sun.corba.se.internal.ior.POAObjectKeyTemplate ;
import com.sun.corba.se.internal.ior.IIOPProfile ;
import com.sun.corba.se.internal.ior.POAId ;

import com.sun.corba.se.internal.iiop.RequestCanceledException;

import javax.rmi.CORBA.Tie;

/** The general-purpose server-side subcontract for the POA ORB.
 *  It handles ORB processing for transient, persistent, non-transactional
 *  and transactional server objects.
 *  Its main functionality is:
 *     1. on each incoming request, find a POA and deliver the request to it.
 *     2. create delegates (to be embedded in object references) for a server
 *	  object in a POA with a given quality of service. The subcontract
 *	  thus controls the layout of fields in the objectKey in the IOR.
 *  The quality of service of the server object is known from the subcontract-id
 *  variable ("scid").
 */

public class GenericPOAServerSC extends ServerDelegate
{
    public GenericPOAServerSC()
    {
        super();
    }

    public GenericPOAServerSC(POAORB o)
    {
        super(o);
    }

    public static boolean isTransient(int subcontractId)
    {
        // 2nd bit in subcontract id is 0 for transient case.
        return ((subcontractId & 2)==0);
    }


    /** dispatch() is the start of the upcall for remote invocations.
     *  The IIOPInputStream cursor is at the end of the RequestHeader
     *  and the request's arguments can be read directly. On return
     *  the IIOPOutputStream should contain the entire reply message
     *  (ReplyHeader + return arguments).
     */
    public ServerResponse dispatch(ServerRequest req)
    {
        if (orb.subcontractDebugFlag)
            dprint( "dispatch entered" ) ;

        POAORB poaorb = (POAORB)orb;
        ObjectKey okey = req.getObjectKey();
        ObjectKeyTemplate oktemp = okey.getTemplate() ;
        int sId = oktemp.getServerId() ;

        if (orb.subcontractDebugFlag)
            dprint( "dispatch: sId = " + sId ) ;


        // to set the codebase information, if any transmitted; and also
        // appropriate ORB Version.
        consumeServiceContexts(req);

        // Now that we have the service contexts processed and the
        // correct ORBVersion set, we must finish initializing the
        // stream.
        req.performORBVersionSpecificInit();

        // If this is a transient SC, then check if the serverid matches
        // this server's transientServerId. Else check if the serverid
        // matches this server's persistentServerId.

        if ( (isTransient(scid) && sId == poaorb.getTransientServerId())
             || (!isTransient(scid) && poaorb.persistentServerIdInitialized
                 && sId == poaorb.getPersistentServerId()) ) {

            return internalDispatch(req, okey);
        } else if ( poaorb.getBadServerIdHandler() != null ) { // e.g. in ORBD
            try {
                if (orb.subcontractDebugFlag)
                    dprint( "dispatch: handling Bad server id (may be ORBD)" ) ;

                // will throw an exception: ForwardException
                poaorb.getBadServerIdHandler().handle(okey);
            } catch ( ForwardException fex ) {
                IOR ior = fex.getIOR();
                return req.createLocationForward(ior, null);
            }
        } else {
            throw new OBJECT_NOT_EXIST(
                                       com.sun.corba.se.internal.orbutil.MinorCodes.BAD_SERVER_ID,
                                       CompletionStatus.COMPLETED_NO);
        }

        return null; // to keep javac happy
    }

    private ServerResponse internalDispatch(ServerRequest req, ObjectKey okey )
    {
        if (orb.subcontractDebugFlag)
            dprint( "internalDispatch entered" ) ;

        byte[] objectId = okey.getId().getId() ;
        ObjectKeyTemplate oktemp = okey.getTemplate() ;
	POAId poaid = null ;
	if (oktemp instanceof POAObjectKeyTemplate) {
	    POAObjectKeyTemplate poktemp = (POAObjectKeyTemplate)oktemp ;
	    poaid = poktemp.getPOAId() ;
	}

        int targetScid = oktemp.getSubcontractId() ;

        if (orb.subcontractDebugFlag)
            dprint( "internalDispatch: targetScid = "  + targetScid ) ;

        String operation = req.getOperationName();
        CookieHolder cookieHolder = new CookieHolder();

        POAImpl poaimpl=null;
        Servant servant=null;
        //The following is and INTERNAL minor code.
        //Use it only with INTERNAL exceptions.
        int mc = MinorCodes.SERVANT_LOOKUP ;
        ServerResponse resp = null ;

        try {
            poaimpl = (POAImpl)getPOA(poaid);

            // Prepare Portable Interceptors for a new server request
            // and invoke receive_request_service_contexts.  The starting
            // point may throw a SystemException or ForwardRequest.
            byte[] adapterId = oktemp.getAdapterId( orb );
            ((POAORB)orb).initializeServerPIInfo( req, poaimpl,
                objectId, adapterId );
            ((POAORB)orb).invokeServerPIStartingPoint();

            servant =
		poaimpl.getServant(objectId, cookieHolder, operation, req);

	    if (servant == null) {
		boolean raiseObjectAdapterException = true;
		if (SpecialMethod.isSpecialMethod(operation)) {
		    SpecialMethod specialMethod = 
			SpecialMethod.getSpecialMethod(operation);
		    if (specialMethod instanceof NonExistent ||
			specialMethod instanceof NotExistent)
			{
			    raiseObjectAdapterException = false;
			}
		}
		if (raiseObjectAdapterException) {
		    throw new OBJ_ADAPTER(MinorCodes.NULL_SERVANT,
					  CompletionStatus.COMPLETED_NO);
		}
	    }

            // Store server request interceptor information and invoke
            // receive_request if this is not DSI.

	    // Note: we do not know the MDI on a null servant.
	    // We only end up in that situation if _non_existent called.
            ((POAORB)orb).setServerPIInfo( servant,
		(servant == null ?
		 "unknown" : 
		 servant._all_interfaces( poaimpl, objectId )[0]) );

            if( ((servant != null) &&
		 !(servant instanceof DynamicImplementation) ) ||
                SpecialMethod.isSpecialMethod( operation ) )
            {
                ((POAORB)orb).invokeServerPIIntermediatePoint();
            }

            mc = MinorCodes.SERVANT_DISPATCH ;
            resp = dispatchToServant(servant, req, objectId,
                                     poaimpl, targetScid);

        } catch ( POADestroyed ex ) {
            if (orb.subcontractDebugFlag)
                dprint( "internalDispatch: POADestroyed exception caught" ) ;

            // REVISIT: does this need to be called explicitly?
            if (poaimpl != null) {
                poaimpl.returnServantAndRemoveThreadInfo();
            }

            // Destroyed POAs can be recreated by normal adapter activation.
            // So just restart the dispatch.
            return internalDispatch(req, okey);
        } catch ( ForwardRequest ex ) {
            if (orb.subcontractDebugFlag)
                dprint( "internalDispatch: ForwardRequest exception caught" ) ;

            return handleInternalDispatchForwardRequest(
                (ObjectImpl)ex.forward_reference, req );
        } catch ( InternalRuntimeForwardRequest ex ) {
            if (orb.subcontractDebugFlag)
                dprint( "internalDispatch: InternalRuntimeForwardRequest caught" ) ;

            // Thrown by Portable Interceptors from InterceptorInvoker,
            // through iiop.ServerResponseImpl constructor.
            return handleInternalDispatchForwardRequest(
                (ObjectImpl)ex.forward, req );
        } catch (RequestCanceledException ex) {

            if (orb.subcontractDebugFlag) {
                dprint("internalDispatch: RequestCanceledException caught");
            }

	    // No need to do anything specific.  This is handled
	    // out in RequestProcessor.

            throw ex;

        } catch ( Throwable t ) {
            if (orb.subcontractDebugFlag) {
                dprint( "internalDispatch: Throwable caught: " + t ) ;
                t.printStackTrace( System.out) ;
            }

	    resp = orb.handleThrowableDuringServerDispatch(
                req,
		t,
		CompletionStatus.COMPLETED_MAYBE);
	}

        return resp ;
    }

    // Internal utility method to handle a ForwardRequest from within
    // internalDispatch.
    private ServerResponse handleInternalDispatchForwardRequest(
        ObjectImpl foi, com.sun.corba.se.internal.core.ServerRequest req)
    {
        // Get the IOR from the ForwardRequest and send it back.
        ClientSubcontract delegate = (ClientSubcontract)foi._get_delegate();
        IOR ior = delegate.marshal();
        ServerResponse resp = req.createLocationForward(ior, null);
        return resp;
    }

    // Allow access from ServantCachePOAClientSC
    POA getPOA(POAId poaid)
    {
	if (poaid == null)
	    throw new INTERNAL() ;

        POAORB poaorb = (POAORB)orb;
        POA poa=null;
        try {
            Iterator iter = poaid.iterator() ;
            poa = poaorb.getRootPOA();
            while (iter.hasNext()) {
                String name = (String)(iter.next()) ;
                poa = poa.find_POA( name, true ) ;
            }
        } catch ( org.omg.PortableServer.POAPackage.AdapterNonExistent ex ){
            throw new OBJ_ADAPTER(MinorCodes.POA_NOT_FOUND,
                                  CompletionStatus.COMPLETED_NO);
        } catch ( OBJECT_NOT_EXIST ex ) {
            throw ex;
        } catch ( Exception ex ) {
            throw new OBJ_ADAPTER(MinorCodes.POA_LOOKUP_ERROR,
                                  CompletionStatus.COMPLETED_NO);
        }

        if ( poa == null )
            throw new OBJ_ADAPTER(MinorCodes.POA_LOOKUP_ERROR,
                                  CompletionStatus.COMPLETED_NO);

        return poa;
    }

    // This is used by the Util.isLocal case in rmi-iiop stubs.
    // Called from local stub thru GenericPOAClientSC.
    public ServantObject preinvoke(IOR targetIor, String method,
                                   Class expectedType)
    {
        byte[] objectId = targetIor.getObjectId()  ;
	POAId poaid = targetIor.getPOAId() ;

        // Get servant
        CookieHolder cookieHolder = new CookieHolder();
        POAImpl poaimpl=null;
        Servant servant=null;
        try {
            poaimpl = (POAImpl)getPOA(poaid);
            servant = poaimpl.getServant(objectId, cookieHolder, method, null);
        } catch ( POADestroyed ex ) {
            if (poaimpl != null) {
                poaimpl.returnServantAndRemoveThreadInfo();
            }

            // Destroyed POAs can be recreated by normal adapter activation.
            // So just reinvoke this method.
            return preinvoke(targetIor, method, expectedType);
        } catch ( ForwardRequest ex ) {
            if (poaimpl != null) {
                poaimpl.returnServantAndRemoveThreadInfo();
            }

            // REVISIT: java2idl rtf:
            // The isLocal branch does not have a remarshal loop to
            // handle ForwardRequest.  Therefore, just return null.
            return null;
        } catch ( ThreadDeath ex ) {
	    // Balancing happenings in ORB.process.
            throw ex;
        } catch ( Throwable t ) {
            if (poaimpl != null) {
                poaimpl.returnServantAndRemoveThreadInfo();
            }
            SystemException ex;
            if ( t instanceof SystemException )
                ex = (SystemException)t;
            else
                ex = new OBJ_ADAPTER(MinorCodes.LOCAL_SERVANT_LOOKUP,
                                     CompletionStatus.COMPLETED_NO);

            throw ex;
        }

        // No exceptions.  So handle servant.

        if ( servant == null ||
             (  ! (servant instanceof Tie)
                 && !expectedType.isInstance(servant) ) ) {
            if (poaimpl != null) {
                poaimpl.returnServantAndRemoveThreadInfo();
            }
            return null;
        }

        boolean alternateServant = false;
        if (servant != null && servant instanceof Tie ) {
                if(!expectedType.isInstance(((Tie)servant).getTarget())){
                    if (poaimpl != null) {
                        poaimpl.returnServantAndRemoveThreadInfo();
                    }
                    return null;
                }
                else alternateServant = true;
        }

        ServantObject servantObject = new ServantObject();
        if (alternateServant) {
            //servant is Tie if alternate servant is set
            servantObject.servant = ((Tie)servant).getTarget();
        } else {
            servantObject.servant = servant;
        }
        return servantObject;
    }

    // This is used by the Util.isLocal case in rmi-iiop stubs.
    // Called from local stub thru GenericPOAClientSC.
    // This will always be called if preinvoke returned a non-null value.
    public void postinvoke(IOR targetIor, ServantObject servantobj)
    {
        POAImpl poaimpl = ((POAORB)orb).getCurrent().getPOA();
        // This must be called explicitly since no reply streams are
        // created in this case.
        poaimpl.returnServantAndRemoveThreadInfo();
    }

    //dispatching to servant
    private ServerResponse dispatchToServant( Servant servant,
        com.sun.corba.se.internal.core.ServerRequest req,
        byte[] oid, POAImpl poa, int targetScid )
    {
        if (orb.subcontractDebugFlag)
            dprint( "dispatchToServant entered" ) ;

        POAORB myorb = (POAORB)orb;
        ServerRequestImpl sreq = new ServerRequestImpl(req, orb);
        String operation = req.getOperationName();

        if (SpecialMethod.isSpecialMethod(operation)) {
            if (orb.subcontractDebugFlag)
                dprint( "dispatchToServant: handling special method" ) ;

            SpecialMethod method = SpecialMethod.getSpecialMethod(operation);
            ServerResponse resp = method.invoke(servant, req);
            return resp;
        }

        // COMMENT(Ram J) Null servant checks should happen within the POA contract.
        // Look at the POAImpl.internalGetServant(). This method should either
        // return a valid servant or raise an appropriate exception.
        /*
        // According to CORBA V2.3, section 11.2.6, for null servant
        // OBJ_ADAPTER Exception should be thrown
        if (servant == null)
            throw new OBJ_ADAPTER(MinorCodes.NULL_SERVANT,
                                       CompletionStatus.COMPLETED_NO);
        */

        // POA supports two kinds of servants: those that are instances
        // of DynamicImplementation (the IDL/DSI case), and those that are
        // instances of InvokeHandler (the IDL/Streams and RMI cases).
        boolean isDynamicImplementation =
            servant instanceof DynamicImplementation ;

        DynamicImplementation dynimpl = null ;
        InvokeHandler invhandle = null ;

        if (isDynamicImplementation)
            dynimpl = (DynamicImplementation)servant;
        else
            invhandle = (InvokeHandler)servant ;

        // Invoke the transaction hooks only if this is not a
        // dispatch for a special method.

        // Check if the target objref has non-transactional SCID even
        // though servant is transactional. If so, get new IOR.
        IOR ior = ((POAORB)orb).checkTransactional( servant, oid, poa,
            targetScid ) ;
        if ( ior != null ) {
            if (orb.subcontractDebugFlag) {
                dprint( "dispatchToServant: forwarding to new transactional IOR" ) ;
                ior.dump() ;
            }

            return req.createLocationForward(ior, null);
        }

        // Extract transaction context and deliver it to JTS
        receivedRequest(req, operation);

        // Makes a down call ORB to set the application specific
        // service context fields.
        myorb.receivedRequestServiceContexts(req.getServiceContexts());

        // Invoke on the servant using the portable DSI skeleton
        if (isDynamicImplementation) {
            try {
                if (orb.subcontractDebugFlag)
                    dprint( "dispatchToServant: starting dynamic invocation" ) ;
                // Note: When/if dynimpl.invoke calls arguments() or
                // set_exception() then intermediate points are run.
                dynimpl.invoke(sreq);

                // Check if ServerRequestImpl.result() has been called
                Any excany = sreq.checkResultCalled();

                // Create and marshal the ReplyMessage header.
                // Get the reply transaction context from JTS if necessary
                // and marshal it too.
                ServerResponse resp = sendingReply(req, operation, null,
                                                   excany);

                if ( excany == null ) { // normal return
                    OutputStream os = (OutputStream)resp;

                    // Marshal out/inout/return parameters into the ReplyMessage
                    sreq.marshalReplyParams(os);
                }

                return resp ;
            } catch (SystemException ex) {
                return sendingReply(req, operation, ex, null);
            } catch( InternalRuntimeForwardRequest ex ) {
                // If PI throws a ForwardRequest, it is rethrown as a
                // InternalRuntimeForwardRequest.  We catch this here so
                // we can pass it back to internalDispatch who knows how
                // to handle it appropriately.
                throw ex;
            } catch (RequestCanceledException ex) {
                throw ex;
            } catch (Throwable ex) {

	        SystemException ex2 =
		    orb.convertThrowableToSystemException(
                        ex, 
			CompletionStatus.COMPLETED_MAYBE);

                return sendingReply(req, operation, ex2, null);

            }
        } else {
            try {
                if (orb.subcontractDebugFlag)
                    dprint( "dispatchToServant: starting invhandler invocation" ) ;
                ServerResponse resp =
                    (ServerResponse)invhandle._invoke(
                                                      operation,
                                                      (org.omg.CORBA.portable.InputStream)req,
                                                      new SubcontractResponseHandler(req,orb) ) ;
                return resp;
            } catch (UnknownException ex) {
                // RMIC generated tie skeletons convert all Throwable exception
                // types (including RequestCanceledException, ThreadDeath)
                // thrown during reading fragments into UnknownException.
                // If RequestCanceledException was indeed raised,
                // then rethrow it, which will eventually cause the worker
                // thread to unstack the dispatch and wait for other requests.
                if (ex.originalEx instanceof RequestCanceledException) {
                    throw (RequestCanceledException) ex.originalEx;
                }

                SystemException sys = new UNKNOWN(0,
                                                  CompletionStatus.COMPLETED_MAYBE);

                ServiceContexts contexts = makeServiceContexts( req ) ;

                try {
                    UEInfoServiceContext uesc = new UEInfoServiceContext(
                                                                         ex.originalEx ) ;
                    try {
                        contexts.put( uesc ) ;
                    } catch (DuplicateServiceContext dsc) {
                        // can't happen: contexts only contains a transaction sc, if any
                    }
                } catch (ThreadDeath d) {
                    throw d;
                } catch (Throwable t) {
                }

                return req.createSystemExceptionResponse(sys,contexts);
            } catch (RequestCanceledException ex) {
                // IDLJ generated non-tie based skeletons do not catch the
                // RequestCanceledException. Rethrow the exception, which will
                // cause the worker thread to unwind the dispatch and wait for
                // other requests.
                throw ex;
            } catch (Throwable ex) {
		SystemException sex =
		    orb.convertThrowableToSystemException(
	                ex,
			CompletionStatus.COMPLETED_MAYBE);
	        return sendingReply(req, operation, sex, null);
            }
        }
    }

    protected ClientSubcontract createClientSubcontract(int scid)
    {
        ClientSubcontract clientRep = new GenericPOAClientSC();
        return clientRep;
    }


    /** Create an objref given an IOR. This may be called while an
     *  objref is being unmarshalled. Note: the IOR may have been created by
     *  a foreign ORB.
     */
    public java.lang.Object createObjref(IOR ior)
    {
        ObjectKeyTemplate oktemp = ior.getProfile().getTemplate().getObjectKeyTemplate() ;
        int scid = oktemp.getSubcontractId() ;
        if (scid < ORBConstants.FIRST_POA_SCID)
            return super.createObjref(ior) ;

        SubcontractRegistry scRegistry = orb.getSubcontractRegistry();
        ObjectImpl objref = new CORBAObjectImpl() ;
        ClientSubcontract rep ;

        // Create a subcontract and stick it in the stub
        // XXX Set a transactional subcontract for the foreign objref
        // if objref.is_a("TransactionalObject") returns true.
        if (ior.isLocal())
            rep = createClientSubcontract( scid ) ;
        else
            rep = scRegistry.getClientSubcontract(oktemp) ;

        rep.unmarshal( ior ) ;
        rep.setOrb( orb ) ;
        objref._set_delegate( (org.omg.CORBA.portable.Delegate)rep ) ;
        return objref ;
    }

    private void throw_object_not_exist()
    {
        throw new OBJECT_NOT_EXIST(
            com.sun.corba.se.internal.orbutil.MinorCodes.BAD_SERVER_ID,
            CompletionStatus.COMPLETED_NO);
    }

    /** Called from ORB.locate when a LocateRequest arrives.
     * Need to signal one of OBJECT_HERE, OBJECT_FORWARD, OBJECT_NOT_EXIST.
     */
    public IOR locate(ObjectKey okey)
    {
        ObjectKeyTemplate oktemp = okey.getTemplate() ;
        int sid = oktemp.getServerId() ;
        int scid = oktemp.getSubcontractId() ;

        POAORB poaorb = (POAORB)orb;

        // XXX Check only serverid. For now, POA checking will be done as
        // part of the normal invocation. (i.e. OBJECT_NOT_EXIST may be
        // thrown on the normal invocation even though the locate request
        // is successful).
        if (isTransient( scid ))
            if (sid == poaorb.getTransientServerId())
                return null ;
            else
                throw_object_not_exist() ;

        // At this point, have a persistent subcontact

        if (poaorb.isLocalServerId( scid, sid ))
            return null ;

        // At this point, either the persistent server ID is not initialized,
        // or else the server id of the object key does not match that of the orb.

        if ( poaorb.getBadServerIdHandler() != null ) { // e.g. in ORBD
            try {
                // will always throw an exception: ForwardException
                poaorb.getBadServerIdHandler().handle(okey);
                return null;
            } catch ( ForwardException fex ) {
                return fex.getIOR();
            }
        } else {
            throw_object_not_exist() ;
        }

        // can't get here, but the compiler can't figure that out!
        return null ;
    }


    public Class getClientSubcontractClass()
    {
        return com.sun.corba.se.internal.POA.GenericPOAClientSC.class;
    }

    // servant is not supported in this, therefore return false
    public boolean isServantSupported()
    {
        return false;
    }

    // These methods from ClientSubcontract should not be used for POA objects
    public java.lang.Object getServant(byte[] objKey)
    {
        throw new INTERNAL(MinorCodes.WRONG_CLIENTSC, CompletionStatus.COMPLETED_MAYBE);
    }

    public void destroyObjref(java.lang.Object objref)
    {
        // This method should not be invoked on POA objects, but
        // just ignore it, since it's harmless and it causes problems
        // for EJB currently.
        // throw new INTERNAL(MinorCodes.WRONG_CLIENTSC, CompletionStatus.COMPLETED_MAYBE);
    }

    public java.lang.Object createObjref(byte[] userKey,
                                         java.lang.Object servant)
    {
        throw new INTERNAL(MinorCodes.WRONG_CLIENTSC, CompletionStatus.COMPLETED_MAYBE);
    }


    // hooks that can be overridden by subclasses to do subcontract specific
    // work; This done as part of POA Transactions split.  The JTS or J2EE
    // could override these hooks to do their specific stuff
    // These methods are overridden in com.sun.corba.se.internal.TransactionalPOA.TransactionalServerSC

    public void receivedRequest(com.sun.corba.se.internal.core.ServerRequest req,
                                   String method)
    {
    }

    public ServiceContexts makeServiceContexts(com.sun.corba.se.internal.core.ServerRequest req)
    {
        POAORB myorb = (POAORB)orb;
        ObjectKey okey = req.getObjectKey();
        int tscid = okey.getTemplate().getSubcontractId() ;
        ServiceContexts scs = new ServiceContexts( orb ) ;

        // call any hooks if defined

        try {
            myorb.sendingReplyServiceContexts(scs);
        } catch (Throwable ex) {
            ; //Do not let hook errors escape.
        }

        return scs ;
    }

   /** Must always be called, just after the servant's method returns.
     *  Creates the ReplyMessage header and puts in the transaction context
     *  if necessary.
     */

    public ServerResponse sendingReply(com.sun.corba.se.internal.core.ServerRequest req,
                                       String method,
                                       SystemException exc,
                                       Any excany)
    {
        ServiceContexts scs = makeServiceContexts( req ) ;

        // Create reply msg
        if ( exc == null && excany == null ) { // normal return
            return req.createResponse(scs);
        } else if ( excany != null ) {
            // Check if the servant set a SystemException or
            // UserException
            ServerResponse resp;
            String repId=null;
            try {
                repId = excany.type().id();
            } catch ( org.omg.CORBA.TypeCodePackage.BadKind e ) {}

            if ( ORBUtility.isSystemException(repId) ) {
                // Get the exception object from the Any
                InputStream in = excany.create_input_stream();
                SystemException ex = ORBUtility.readSystemException(in);
                // Marshal the exception back
                resp = req.createSystemExceptionResponse(ex, scs);
            } else {
                resp = req.createUserExceptionResponse(scs);
                OutputStream os = (OutputStream)resp;
                excany.write_value(os);
            }

            return resp;
        } else { // SystemException was thrown
            return req.createSystemExceptionResponse(exc, scs);
        }
    }
}
