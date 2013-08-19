/*
 * @(#)ServerDelegate.java	1.72 03/01/23
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

import javax.rmi.CORBA.Tie;

import org.omg.CORBA.portable.ApplicationException;
import org.omg.CORBA.SystemException;
import org.omg.CORBA.INTERNAL;
import org.omg.CORBA.UNKNOWN;
import org.omg.CORBA.OBJECT_NOT_EXIST;
import org.omg.CORBA.DATA_CONVERSION;
import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.Any;
import org.omg.CORBA.portable.ObjectImpl;
import org.omg.CORBA.portable.InvokeHandler;
import org.omg.CORBA.DynamicImplementation;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.UnknownException;
import org.omg.CORBA.CompletionStatus;

import java.rmi.Remote;

import com.sun.corba.se.internal.iiop.ORB;
import com.sun.corba.se.internal.core.InternalRuntimeForwardRequest;
import com.sun.corba.se.internal.core.ServerSubcontract;
import com.sun.corba.se.internal.core.IOR;
import com.sun.corba.se.internal.core.ServerRequest;
import com.sun.corba.se.internal.core.ServerResponse;
import com.sun.corba.se.internal.core.ServiceContext;
import com.sun.corba.se.internal.core.DuplicateServiceContext;
import com.sun.corba.se.internal.core.NoSuchServiceContext;
import com.sun.corba.se.internal.core.ServiceContexts;
import com.sun.corba.se.internal.core.UEInfoServiceContext;
import com.sun.corba.se.internal.core.CodeSetServiceContext;
import com.sun.corba.se.internal.core.SendingContextServiceContext;
import com.sun.corba.se.internal.core.ORBVersionServiceContext;
import com.sun.corba.se.internal.core.ORBVersion;
import com.sun.corba.se.internal.core.ORBVersionFactory;
import com.sun.corba.se.internal.core.ORBVersionImpl;
import com.sun.corba.se.internal.core.EndPoint;
import com.sun.corba.se.internal.core.ClientSubcontract;
import com.sun.corba.se.internal.core.ServiceContext;
import com.sun.corba.se.internal.core.MarshalInputStream;
import com.sun.corba.se.internal.core.MarshalOutputStream;
import com.sun.corba.se.internal.core.CodeSetComponentInfo;
import com.sun.corba.se.internal.core.GIOPVersion;
import com.sun.corba.se.internal.core.OSFCodeSetRegistry;

import com.sun.corba.se.internal.orbutil.MinorCodes;
import com.sun.corba.se.internal.orbutil.ORBUtility;
import com.sun.org.omg.SendingContext.CodeBase;

import com.sun.corba.se.internal.orbutil.TransientObjectManager;
import com.sun.corba.se.internal.orbutil.ORBConstants;

import com.sun.corba.se.internal.iiop.Connection;
import com.sun.corba.se.internal.iiop.RequestCanceledException;

import com.sun.corba.se.internal.ior.ObjectId;
import com.sun.corba.se.internal.ior.ObjectKeyFactory;
import com.sun.corba.se.internal.ior.ObjectKey;
import com.sun.corba.se.internal.ior.ObjectKeyTemplate;
import com.sun.corba.se.internal.ior.JIDLObjectKeyTemplate;

public class ServerDelegate implements ServerSubcontract {
    protected void dprint( String msg )
    {
        ORBUtility.dprint( this, msg ) ;
    }

    protected ORB orb; // my ORB instance
    protected int scid;  // my subcontract id
    protected TransientObjectManager servants;

    // Added from last version because it broke the build - RTW
    public static final int UNKNOWN_EXCEPTION_INFO_ID = 9;

    // These offsets are for the object key. Assume the first 8 bytes
    // contain the magic and the sc-id.
    public static final int SERVERID_OFFSET = 8;
    public static final int USERKEYLEN_OFFSET = 12;
    public static final int USERKEY_OFFSET = 16;

    public ServerDelegate() {
        servants = new TransientObjectManager();
    }

    public ServerDelegate(ORB orb) {
        this();
        setOrb( orb ) ;
    }

    public void setId(int id) {
        this.scid = id;
    }

    public int getId()
    {
        return scid ;
    }

    public void setOrb(com.sun.corba.se.internal.core.ORB orb) {
	this.orb = (com.sun.corba.se.internal.iiop.ORB) orb;
        servants.setOrb( (com.sun.corba.se.internal.corba.ORB)orb ) ;
    }

    public Class getClientSubcontractClass() {
        return ClientDelegate.class;
    }

 
    // Need to signal one of OBJECT_HERE, OBJECT_FORWARD, OBJECT_NOT_EXIST.
    public IOR locate(ObjectKey okey) {
        ObjectId id = okey.getId() ;
        ObjectKeyTemplate oktemp = okey.getTemplate() ;
         
        // Check if the serverid matches this server's transientServerId.
        int sid = oktemp.getServerId() ;
        if ( sid != orb.getTransientServerId() )
            throw new OBJECT_NOT_EXIST(MinorCodes.BAD_SERVER_ID,
                                       CompletionStatus.COMPLETED_NO);

        // Get the servant
        java.lang.Object servant = getServant(id);

        if (servant == null)
            throw new OBJECT_NOT_EXIST(MinorCodes.SERVANT_NOT_FOUND,
                                       CompletionStatus.COMPLETED_NO);

        // If we reached here, it means we got the servant,
        // so just return null to signal OBJECT_HERE.
        return null;
    }

    private Object getServant( ObjectId id )
    {
        byte[] userKey = id.getId() ;
        return servants.lookupServant(userKey);
    }

    public Object getServant(IOR ior) {
        if (!ior.isLocal())
            return null;

        ObjectId id = ior.getProfile().getObjectId() ;
        return getServant( id ) ;
    }

    public boolean isServantSupported() {
        return true;
    }

    public void consumeServiceContexts(ServerRequest request) {
        ServiceContexts ctxts = request.getServiceContexts();
        ServiceContext sc ;

        GIOPVersion giopVersion = request.getGIOPVersion();

        // we cannot depend on this since for our local case, we do not send
        // in this service context.  Can we rely on just the CodeSetServiceContext?
        // boolean rtSC = false; // Runtime ServiceContext

        boolean hasCodeSetContext = processCodeSetContext(request, ctxts);

        if (orb.subcontractDebugFlag) {
            dprint("Consuming service contexts, GIOP version: " + giopVersion);
            dprint("Has code set context? " + hasCodeSetContext);
        }

        try {
            sc = ctxts.get(
                SendingContextServiceContext.SERVICE_CONTEXT_ID ) ;

            SendingContextServiceContext scsc =
                (SendingContextServiceContext)sc ;
            IOR ior = scsc.getIOR() ;

            try {
                request.getConnection().setCodeBaseIOR(ior);
            } catch (ThreadDeath td) {
                throw td ;
            } catch (Throwable t) {
                throw new DATA_CONVERSION( MinorCodes.BAD_STRINGIFIED_IOR,
                                           CompletionStatus.COMPLETED_NO);
            }
        } catch (NoSuchServiceContext exc) {
            // ignore: this type not present
        }

        // the RTSC is sent only once during session establishment.  We
        // need to find out if the CodeBaseRef is already set.  If yes,
        // then also the rtSC flag needs to be set to true
        // this is not possible for the LocalCase since there is no
        // IIOPConnection for the LocalCase

        // used for a case where we have JDK 1.3 supporting 1.0 protocol,
        // but sending 2 service contexts, that is not normal as per
        // GIOP rules, based on above information, we figure out that we
        // are talking to the legacy ORB and set the ORB Version Accordingly.

        // this special case tell us that it is legacy SUN orb
        // and not a foreign one
        // rtSC is not available for localcase due to which this generic
        // path would fail if relying on rtSC
        //if (giopVersion.equals(GIOPVersion.V1_0) && hasCodeSetContext && rtSC)
        boolean isForeignORB = false;

        if (giopVersion.equals(GIOPVersion.V1_0) && hasCodeSetContext) {
            if (orb.subcontractDebugFlag)
                dprint("Determined to be an old Sun ORB");
                
            orb.setORBVersion(ORBVersionImpl.OLD) ;
            // System.out.println("setting legacy ORB version");
        } else {
            // If it didn't include our ORB version service context (below),
            // then it must be a foreign ORB.
            isForeignORB = true;
        }

        // try to get the ORBVersion sent as part of the ServiceContext
        // if any
        try {
            sc = ctxts.get( ORBVersionServiceContext.SERVICE_CONTEXT_ID ) ;

            ORBVersionServiceContext ovsc =
               (ORBVersionServiceContext) sc;

            ORBVersion version = ovsc.getVersion();
            orb.setORBVersion( version ) ;

            isForeignORB = false;

        } catch (NoSuchServiceContext exc) {
            // ignore: this type not present
        }

        if (isForeignORB) {
            if (orb.subcontractDebugFlag)
                dprint("Determined to be a foreign ORB");

            orb.setORBVersion(ORBVersionImpl.FOREIGN);
        }
    }

    public ServerResponse dispatch(ServerRequest request) {
        if (orb.subcontractDebugFlag)
            dprint( "Entering dispatch method" ) ;

        ObjectKey okey = request.getObjectKey();
        ObjectKeyTemplate oktemp = okey.getTemplate() ;
        ObjectId oid = okey.getId() ;

        String operation = request.getOperationName();
        Object servant = getServant(oid);

	if (servant == null) {
	    boolean raiseObjectNotExist = true;
	    if (SpecialMethod.isSpecialMethod(operation)) {
		SpecialMethod specialMethod = 
		    SpecialMethod.getSpecialMethod(operation);
		if (specialMethod instanceof NonExistent ||
		    specialMethod instanceof NotExistent)
		{
		    raiseObjectNotExist = false;
		}
	    }
	    if (raiseObjectNotExist) {
		throw new OBJECT_NOT_EXIST(MinorCodes.SERVANT_NOT_FOUND,
					   CompletionStatus.COMPLETED_NO);
	    }
	}

        ServerResponse response = null;

        int sId = oktemp.getServerId() ;

        if (sId != orb.getTransientServerId())
            throw new OBJECT_NOT_EXIST(MinorCodes.BAD_SERVER_ID,
                                       CompletionStatus.COMPLETED_NO);

        consumeServiceContexts(request);

        // Now that we have the service contexts processed and the
        // correct ORBVersion set, we must finish initializing the
        // stream.
        request.performORBVersionSpecificInit();

        if (orb.subcontractDebugFlag)
            dprint( "Dispatching to servant" ) ;

        // This outer try is required for PI so that if an interceptor
        // throws InternalRuntimeForwardRequest in a send_* point,
        // we can still handle it.
        try {
            try {
                // Notify PI of serverRequest, Servant.
                // Note that we call receive_request_service_contexts after
                // setting the servant in the info object.  This is
                // different than in the POA case (see GenericPOAServerSC),
                // but OK since this case is not covered by the spec
                // anyway.
                ((com.sun.corba.se.internal.corba.ORB)orb).
                    initializeServerPIInfo( request, null,
                    oid.getId(), oktemp.getAdapterId( orb ) );

		// Note: we do not know the MDI on a null servant.
		// We only end up in that situation if _non_existent called.
                ((com.sun.corba.se.internal.corba.ORB)orb).
                    setServerPIInfo( servant,
				     (servant == null ?
				      "unknown" :
				      ((ObjectImpl)servant)._ids()[0]) );

                // Invoke server starting interception points:
                ((com.sun.corba.se.internal.corba.ORB)orb).
                    invokeServerPIStartingPoint();

                // Note: org.omg.CORBA.DynamicImplementation is deprecated!
                if( ((servant != null) &&
		     !(servant instanceof DynamicImplementation) ) ||
                    SpecialMethod.isSpecialMethod( operation ) )
                {
                    ((com.sun.corba.se.internal.corba.ORB)orb).
                        invokeServerPIIntermediatePoint();
                }

                if (SpecialMethod.isSpecialMethod(operation)) {
                    if (orb.subcontractDebugFlag)
                        dprint( "Handling special method" ) ;

                    response = SpecialMethod.getSpecialMethod(
                        operation).invoke(servant, request);
                } else if (servant instanceof InvokeHandler) {
                    if (orb.subcontractDebugFlag)
                        dprint( "Handling invoke handler type servant" ) ;

                    response = (ServerResponse)((InvokeHandler)servant)._invoke(
                        operation, (org.omg.CORBA.portable.InputStream) request,
                        request);
                } else {
                    if (orb.subcontractDebugFlag)
                        dprint( "Handling DSI type servant" ) ;

                    // Invoke on servant using DSI
                    ServerRequestImpl sreq = new ServerRequestImpl(request,
                                                                   orb);
                    DynamicImplementation dServant =
                        (DynamicImplementation)servant;
                    OutputStream os;
                    InputStream is = (InputStream) request;

                    dServant.invoke(sreq);

                    // Check if ServerRequestImpl.result() has been called
                    Any excany = sreq.checkResultCalled();

                    if ( excany == null ) { // normal return
                        if (orb.subcontractDebugFlag)
                            dprint( "DSI type servant: normal response" ) ;

                        response = request.createResponse(null);

                        // Marshal out/inout/return parameters into the
                        // ReplyMessage
                        os = (OutputStream) response;
                        sreq.marshalReplyParams(os);
                    } else {
                        if (orb.subcontractDebugFlag)
                            dprint( "DSI type servant: error response" ) ;

                        // Check if the servant set a SystemException or
                        // UserException
                        String repId = excany.type().id();
                        if (ORBUtility.isSystemException(repId)) {
                            if (orb.subcontractDebugFlag)
                                dprint( "DSI type servant: system exception" ) ;

                            // Get the exception object from the Any
                            InputStream in = excany.create_input_stream();
                            SystemException ex =
                                ORBUtility.readSystemException(in);

                            // Marshal the exception back
                            response = request.createSystemExceptionResponse(
                                ex, null);
                        } else {
                            if (orb.subcontractDebugFlag)
                                dprint( "DSI type servant: user exception" ) ;

                            response =
                                request.createUserExceptionResponse(null);
                            os = (OutputStream) response;
                            excany.write_value(os);
                        }
                    }
                }
            } catch (InternalRuntimeForwardRequest ex ) {
                // In case PI throws a ForwardRequest:
                // Get the IOR from the ForwardRequest and send it back.
                response = handleInternalRuntimeForwardRequest(
                    request, (ObjectImpl)ex.forward );
            } catch (UnknownException ex) {
                if (orb.subcontractDebugFlag)
                    dprint( "After dispatch: Unknown exception " + ex ) ;

                // RMIC generated tie skeletons convert all Throwable exception
                // types (including RequestCanceledException, ThreadDeath)
                // thrown during reading fragments into UnknownException.
                // If RequestCanceledException was indeed raised,
                // then rethrow it, which will eventually cause the worker
                // thread to unstack the dispatch and wait for other requests.
                if (ex.originalEx instanceof RequestCanceledException) {
                    throw (RequestCanceledException) ex.originalEx;
                }

                ServiceContexts contexts = new ServiceContexts( orb ) ;
                UEInfoServiceContext usc = new UEInfoServiceContext(
                    ex.originalEx ) ;

                try {
                    contexts.put( usc ) ;
                } catch (DuplicateServiceContext dsc) {
                    // Can't happen, since we are adding usc to an empty
                    // contexts object
                }

                SystemException sys = new UNKNOWN( 0,
                    CompletionStatus.COMPLETED_MAYBE);
                response = request.createSystemExceptionResponse(sys,contexts);
            } catch (RequestCanceledException ex) {
                // IDLJ generated non-tie based skeletons do not catch the
                // RequestCanceledException. Rethrow the exception, which will
                // cause the worker thread to unwind the dispatch and wait for
                // other requests.
                throw ex;
            } catch (Throwable ex) {
                if (orb.subcontractDebugFlag)
                    dprint( "After dispatch: other exception " + ex ) ;

		response = 
		    orb.handleThrowableDuringServerDispatch(
                        request,
			ex,
			CompletionStatus.COMPLETED_MAYBE);
            }
        }
        catch( InternalRuntimeForwardRequest re ) {
            // In case PI throws a ForwardRequest:
            // Get the IOR from the ForwardRequest and send it back.
            response = handleInternalRuntimeForwardRequest(
                request, (ObjectImpl)re.forward );
        }

        return response;
    }

    /**
     * Private utility method to handle PI internal runtime forwardrequest
     * and create the appropriate location forward response for it.
     */
    private ServerResponse handleInternalRuntimeForwardRequest(
        ServerRequest request, ObjectImpl foi )
    {
        ClientSubcontract delegate =
            (ClientSubcontract)foi._get_delegate();
        IOR ior = delegate.marshal();
        return request.createLocationForward( ior, null );
    }

    public void destroyObjref(Object objref) {
        // Get the delegate, then ior, then transientKey, then delete servant
        ObjectImpl oi = (ObjectImpl)objref;
        ClientSubcontract del = (ClientSubcontract)oi._get_delegate();
        IOR ior = del.marshal();
        ObjectId id = ior.getProfile().getObjectId() ;
        byte[] transientKey = id.getId() ;
        servants.deleteServant(transientKey);
        del.unexport();
    }

    public Object createObjref(IOR ior) {
        throw new INTERNAL( MinorCodes.WRONG_CLIENTSC,
            CompletionStatus.COMPLETED_MAYBE);
    }

    public Object createObjref(byte[] key, Object servant) {
        // Note that the key parameter is never used here.

        // First, make sure this is an ObjectImpl.
        ObjectImpl objectImpl = (ObjectImpl) servant;

        // Store it and get a userkey allocated by the transient
        // object manager.
        key = servants.storeServant(objectImpl, null);

        ObjectId oid = new ObjectId( key ) ;

        ObjectKeyTemplate oktemp = new JIDLObjectKeyTemplate( scid,
            orb.getTransientServerId() ) ;

        ObjectKey okey = new ObjectKey( oktemp, oid ) ;

        // Find out the repository ID for this servant.
        String id = getId(objectImpl);

        // Find out the port number to put in the IOR.
        EndPoint endpoint = orb.getServerGIOP().getDefaultEndpoint();
        if (endpoint == null) {
            orb.getServerGIOP().initEndpoints();
            endpoint = orb.getServerGIOP().getDefaultEndpoint();
        }

        int port = endpoint.getPort();
        String host = endpoint.getHostName();

        IOR ior = new IOR( orb, id, host, port, okey, servant ) ;

        // Create the delegate and set it in the tie
        ClientDelegate delegate = new ClientDelegate( orb,
            ior, this.scid, servant ) ;

        objectImpl._set_delegate( delegate ) ;

        return objectImpl ;
    }

    public static String getId(ObjectImpl theTie) {
        return theTie._ids()[0];
    }

    /**
     * Handles setting the connection's code sets if required.
     * Returns true if the CodeSetContext was in the request, false
     * otherwise.
     */
    private boolean processCodeSetContext(ServerRequest request,
                                          ServiceContexts contexts) {

        try {
            ServiceContext sc = contexts.get(CodeSetServiceContext.SERVICE_CONTEXT_ID);

            // Somehow a code set service context showed up in the local case.
            if (request.getConnection() == null) {
                return true;
            }

            // If it's GIOP 1.0, it shouldn't have this context at all.  Our legacy
            // ORBs sent it and we need to know if it's here to make ORB versioning
            // decisions, but we don't use the contents.
            if (request.getGIOPVersion().equals(GIOPVersion.V1_0)) {
                return true;
            }

            CodeSetServiceContext cssc = (CodeSetServiceContext)sc ;
            CodeSetComponentInfo.CodeSetContext csctx = cssc.getCodeSetContext();

            // Note on threading:
            //
            // getCodeSetContext and setCodeSetContext are synchronized
            // on the Connection.  At worst, this will result in 
            // multiple threads entering this block and calling 
            // setCodeSetContext but not actually changing the
            // values on the Connection.
            //
            // Alternative would be to lock the connection for the
            // whole block, but it's fine either way.
            //

            // The connection's codeSetContext is null until we've received a
            // request with a code set context with the negotiated code sets.
            if (request.getConnection().getCodeSetContext() == null) {

                // Use these code sets on this connection
                if (orb.subcontractDebugFlag)
                    dprint("Setting code sets to: " + csctx);

                request.getConnection().setCodeSetContext(csctx);

                // We had to read the method name using ISO 8859-1
                // (which is the default in the CDRInputStream for
                // char data), but now we may have a new char
                // code set.  If it isn't ISO8859-1, we must tell
                // the CDR stream to null any converter references
                // it has created so that it will reacquire
                // the code sets again using the new info.
                //
                // This should probably compare with the stream's
                // char code set rather than assuming it's ISO8859-1.
                // (However, the operation name is almost certainly
                // ISO8859-1 or ASCII.)
                if (csctx.getCharCodeSet()
                    != OSFCodeSetRegistry.ISO_8859_1.getNumber()) {

                    request.resetCodeSetConverters();
                }
            }

            return true;

        } catch (NoSuchServiceContext exc) {
            // If no code set information is ever sent from the client,
            // the server will use ISO8859-1 for char and throw an
            // exception for any wchar transmissions.
            //
            // In the local case, we use ORB provided streams for
            // marshaling and unmarshaling.  Currently, they use
            // ISO8859-1 for char/string and UTF16 for wchar/wstring.
            return false;
        }
    }
}
