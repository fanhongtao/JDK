/*
 * @(#)ORB.java	1.216 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.corba.se.internal.iiop;

// Import JDK stuff
import java.net.*;
import java.util.*;

// Import our stuff

import org.omg.CORBA.Any;
import org.omg.CORBA.SystemException;
import org.omg.CORBA.OBJ_ADAPTER;
import org.omg.CORBA.INTERNAL;
import org.omg.CORBA.NVList;
import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.TRANSIENT;
import org.omg.CORBA.UNKNOWN;
import org.omg.CORBA.portable.ObjectImpl;

import com.sun.corba.se.internal.core.ClientSubcontract;
import com.sun.corba.se.internal.core.ServerSubcontract;
import com.sun.corba.se.internal.core.SubcontractRegistry;
import com.sun.corba.se.internal.core.ClientGIOP;
import com.sun.corba.se.internal.core.ServerGIOP;
import com.sun.corba.se.internal.core.InternalRuntimeForwardRequest;
import com.sun.corba.se.internal.core.MarshalInputStream;
import com.sun.corba.se.internal.core.MarshalOutputStream;
import com.sun.corba.se.internal.core.RequestHandler;
import com.sun.corba.se.internal.core.ServerResponse;
import com.sun.corba.se.internal.core.ServerRequest;
import com.sun.corba.se.internal.core.ServiceContextRegistry;
import com.sun.corba.se.internal.core.ServiceContexts;
import com.sun.corba.se.internal.core.DuplicateServiceContext;
import com.sun.corba.se.internal.core.NoSuchServiceContext;
import com.sun.corba.se.internal.core.SendingContextServiceContext;
import com.sun.corba.se.internal.core.CodeSetServiceContext;
import com.sun.corba.se.internal.core.UEInfoServiceContext;
import com.sun.corba.se.internal.core.ORBVersionServiceContext;
import com.sun.corba.se.internal.core.EndPoint;
import com.sun.corba.se.internal.core.IOR;

import com.sun.corba.se.internal.iiop.messages.ReplyMessage;

import com.sun.corba.se.internal.orbutil.MinorCodes; //d11638
import com.sun.corba.se.internal.orbutil.SubcontractList; 

import com.sun.corba.se.internal.ior.ObjectKey  ; 
import com.sun.corba.se.internal.ior.ObjectKeyFactory  ; 
import com.sun.corba.se.internal.ior.ObjectKeyTemplate ; 
import com.sun.corba.se.internal.ior.TaggedComponentFactories ;

import com.sun.corba.se.internal.corba.EncapsInputStream;
import com.sun.corba.se.internal.corba.EncapsOutputStream; 
import com.sun.corba.se.internal.corba.ClientDelegate; 
import com.sun.corba.se.internal.corba.ServerDelegate; 


/** 
 * The JavaIDL IIOP ORB implementation.
 */
public class ORB extends com.sun.corba.se.internal.corba.ORB implements RequestHandler {
    // The following fields form our special little collection of global state. 
    // We keep it bottled up here in the ORB class and attach a reference to
    // ourselves to every object reference that we create and handle.
    protected SubcontractRegistry subcontractRegistry;
    protected GIOPImpl giopTransport;

    // This is the unique id of this server (JVM). Multiple incarnations
    // of this server will get different ids.
    protected int transientServerId=0;

    // The thread group of the main thread (for applications) or applet.
    protected ThreadGroup threadGroup;

    protected ServiceContextRegistry scr ;

    private static final int GENERIC = SubcontractList.Generic ;

    /** 
     * Create a new ORB. Should only be invoked from the
     * CORBA ORB superclass. Should be followed by the appropriate
     * set_parameters() call.
     */
    public ORB() 
    {
	// Register the tagged component factories so that 
	// tagged components can be fully decoded when an IOR
	// is unmarshalled.
	TaggedComponentFactories.registerFactories() ;

        //
        // We attempt to create new threads in this thread group, if
        // possible. This avoids problems if the application/applet
        // creates a thread group, makes JavaIDL calls which create a new
        // connection and ReaderThread, and then destroys the thread
        // group. If our ReaderThread were part of this destroyed thread
        // group then it might get killed and cause other invoking threads
        // sharing the same connection to get a non-restartable
        // CommunicationFailure. We'd like to avoid that.
        //
        // Our solution is to create all of our threads in the same
        // thread group that we were initialized under.
        //
        threadGroup = Thread.currentThread().getThreadGroup();

        // Compute transientServerId = (milliseconds since Jan 1, 1970)/10.
	// Note: transientServerId will wrap in about 2^32 / 8640000 = 497 days.
        // If two ORBS are started at the same time then there is a possibility
        // of having the same transientServerId. This may result in collision 
        // and may be a problem in ior.isLocal() check to see if the object 
        // belongs to the current ORB. This problem is taken care of by checking
        // to see if the IOR port matches ORB server port in isLocalServerPort()
        // method.
        transientServerId = (int)System.currentTimeMillis();

	if (ORBInitDebug)
	    dprint( "Creating service context registry" ) ;

	scr = new ServiceContextRegistry( this ) ;

	if (ORBInitDebug)
	    dprint( "Registering service context classes" ) ;

	// Register service contexts 
	try {
	    scr.register( UEInfoServiceContext.class ) ;
	    scr.register( CodeSetServiceContext.class ) ;
	    scr.register( SendingContextServiceContext.class ) ;

	    // register the service context class to handle ORB versioning
	    scr.register( ORBVersionServiceContext.class ) ;

	} catch (DuplicateServiceContext dsc) {
	    if (ORBInitDebug)
		dprint( "Duplicate service context registration error" ) ;

	    throw new INTERNAL( "Duplicate Service Context error" ) ;
	} catch (NoSuchServiceContext dsc) {
	    if (ORBInitDebug)
		dprint( "No such service context registration error" ) ;

	    throw new INTERNAL( 
			       "Error in service context class definition" ) ;
	}

	// Register subcontracts in subcontractRegistry
        subcontractRegistry = new SubcontractRegistry( this, GENERIC ) ;

	ServerSubcontract ssc = new ServerDelegate( this ) ;
	ssc.setId( GENERIC ) ;

	subcontractRegistry.registerServer( ssc, GENERIC ) ;

	subcontractRegistry.registerBootstrapServer( ssc ) ;

	// register the server subcontract *instance*
	subcontractRegistry.registerClient( ClientDelegate.class, GENERIC ) ;	
	    
	// Initialize the GIOP transport
	giopTransport = new GIOPImpl(this, this);
    }

    public ClientGIOP getClientGIOP()
    {
	return giopTransport;
    }

    public ServerGIOP getServerGIOP()
    {
	return giopTransport;
    }

    public MarshalInputStream newInputStream() {
	return new EncapsInputStream(this);
    }

    public MarshalInputStream newInputStream(byte[] buffer, int size) {
	return new EncapsInputStream(this, buffer, size);
    }

    public MarshalInputStream newInputStream(byte[] buffer, int size, boolean littleEndian) {
	return new EncapsInputStream(this, buffer, size, littleEndian);
    }
    

    public MarshalOutputStream newOutputStream() {
	return new EncapsOutputStream(this);
    }

    public IIOPOutputStream newOutputStream(Connection c) {
        //REVISIT: Unlike Request/Reply based IIOPOutputStreams
        //this IIOPOutputStream pickes version from the ORB.
        //This should be OK.
	return new IIOPOutputStream(getGIOPVersion(), this, c);
    }

    /**
     * Used to determine the listen port assigned to the given type.
     * Useful when ports are ephemeral.
     */
    public int getServerPort (String socketType)
    {
	return giopTransport.getServerPort(socketType);
    }

    public int getTransientServerId()
    {
        return transientServerId;
    }

    public SubcontractRegistry getSubcontractRegistry()
    {
        return subcontractRegistry;
    }

    public ServiceContextRegistry getServiceContextRegistry()
    {
	return scr ;
    } 

    public ServerResponse process(ServerRequest request) {
        checkShutdownState();

        if (subcontractDebugFlag) {
	    ObjectKey okey = request.getObjectKey();
	    ObjectKeyTemplate oktemp = okey.getTemplate() ;
	    dprint( "process: dispatching to scid " + oktemp.getSubcontractId() ) ;
	}

	ServerSubcontract sc =
	    subcontractRegistry.getServerSubcontract(request.getObjectKey());

	if (subcontractDebugFlag)
	    dprint( "dispatching to sc " + sc ) ;

	if (sc == null) {
	    SystemException ex =
		new OBJ_ADAPTER(MinorCodes.NO_SERVER_SC_IN_DISPATCH,
				CompletionStatus.COMPLETED_NO);
			    
	    return request.createSystemExceptionResponse(ex, null);
	}
        try {
            startingDispatch();
	    try {
		return sc.dispatch(request);
	    } catch (Throwable throwable) {
		return handleThrowableDuringServerDispatch(
                    request,
		    throwable,
		    CompletionStatus.COMPLETED_MAYBE);
	    }
        } finally {
            finishedDispatch();
        }
    }

    public IOR locate(ObjectKey key) {
	ServerSubcontract sc =
	    subcontractRegistry.getServerSubcontract(key);
	if (sc == null)
	    return null;
	return sc.locate(key);
    }

    public boolean isLocalHost( String hostName ) {
	return hostName.equals( getORBServerHost() ) ||
	    hostName.equals( getLocalHostName() ) ;
    }

    public boolean isLocalServerId( int subcontractId, int serverId )
    {
	// If we instantiated this ORB version, we only have a transient subcontract.
	return serverId == transientServerId ; 
    }

    // Check to see if the given port is equal to any of the ORB Server Ports.
    public boolean isLocalServerPort( int port ) {
        Collection serverEndPoints = giopTransport.getServerEndpoints( );
        if( serverEndPoints != null ) {
            Iterator iterator = serverEndPoints.iterator( );
            EndPoint endPoint;
            while( iterator.hasNext( ) ) { 
                endPoint = (EndPoint) iterator.next( );
                if( endPoint.getPort() == port ) {
                    return true;
                }
            }
        }
        return false;
    }


    /* keeping a copy of the getLocalHostName so that it can only be called 
     * internally and the unauthorized clients cannot have access to the
     * localHost information, originally, the above code was calling getLocalHostName
     * from Connection.java.  If the hostname is cached in Connection.java, then
     * it is a security hole, since any unauthorized client has access to
     * the host information.  With this change it is used internally so the
     * security problem is resolved.  Also in Connection.java, the getLocalHost()
     * implementation has changed to always call the 
     * InetAddress.getLocalHost().getHostAddress()
     * The above mentioned method has been removed from the connection class
     */

    private static String localHostString = null;


    private synchronized String getLocalHostName() {
        if (localHostString == null) {
            try {
		localHostString = 
		    InetAddress.getLocalHost().getHostAddress();
            } catch (Exception ex) {
                throw new INTERNAL( MinorCodes.GET_LOCAL_HOST_FAILED,
		    CompletionStatus.COMPLETED_NO );
            }
	}

	return localHostString ;
    }

    //
    // Handle Throwable.
    //

    public SystemException convertThrowableToSystemException(
        Throwable throwable,
	CompletionStatus completionStatus)
    {
	if (throwable instanceof SystemException) {
	    return (SystemException)throwable;
	}

	if (throwable instanceof RequestCanceledException) {
	    return new TRANSIENT("RequestCanceled",
				 MinorCodes.REQUEST_CANCELED,
				 CompletionStatus.COMPLETED_NO);
	}

	// If user code throws a non-SystemException report it generically.

	String errorString =
	    "Unknown Application exception on server: " 
	    + throwable.getClass().getName();

	return new UNKNOWN(errorString,
			   MinorCodes.RUNTIMEEXCEPTION,
			   completionStatus);
    }

    public ServerResponse handleThrowableDuringServerDispatch(
        ServerRequest serverRequest,
	Throwable throwable,
	CompletionStatus completionStatus)
    {
	return handleThrowableDuringServerDispatch(serverRequest,
						   throwable,
						   completionStatus,
						   1);
    }

    private ServerResponse handleThrowableDuringServerDispatch(
        ServerRequest serverRequest,
	Throwable throwable,
	CompletionStatus completionStatus,
	int iteration)

    {
	if (iteration > 10) {
	    throw new RuntimeException(
	        "handleThrowableDuringServerDispatch: " +
		"cannot create response.");
	}

	try {

	    if (throwable instanceof InternalRuntimeForwardRequest) {
		ObjectImpl objectImpl = (ObjectImpl)
		    ((InternalRuntimeForwardRequest)throwable).forward;
		ClientSubcontract delegate = (ClientSubcontract)
		    objectImpl._get_delegate();
		return serverRequest.createLocationForward(delegate.marshal(),
							   null);
	    }

	    SystemException sex =
		convertThrowableToSystemException(throwable, completionStatus);

	    return serverRequest.createSystemExceptionResponse(sex, null);

	} catch (Throwable throwable2) {

	    // User code (e.g., postinvoke, interceptors) may change
	    // the exception, so we end up back here.
	    // Report the changed exception.

	    return handleThrowableDuringServerDispatch(serverRequest,
						       throwable2,
						       completionStatus,
						       iteration + 1);
	}
    }

    /******************************************************************************
     *  The following public methods are for ORB shutdown.
     ******************************************************************************/

    protected void shutdownServants(boolean wait_for_completion) {
        if (ShutdownUtilDelegate.instance != null) {
            ShutdownUtilDelegate.instance.unregisterTargetsForORB(this);
        }
    }

    protected void destroyConnections() {
        giopTransport.destroyConnections();
    }

    /*
     **************************************************************************
     *  The following methods are hooks for Portable Interceptors.
     *  They have empty method bodies so that we may ship with or without
     *  PI support.  The actual implementations can be found in 
     *  Interceptors.PIORB.  The uppermost implementations can be found
     *  in corba.ORB.  Some are explicitly overridden here in protected 
     *  scope so that we can access them from the classes in the iiop package.
     *************************************************************************/

    /*
     *****************
     * Client PI hooks
     *
     *****************/

    /**
     * Overridden from corba.ORB.
     * See description in corba.ORB.
     */
    // REVISIT: not sure if this is necessary in this package.
    protected void sendCancelRequestIfFinalFragmentNotSent() {
        super.sendCancelRequestIfFinalFragmentNotSent();
    }
    
    /*
     *****************
     * Server PI hooks
     *****************/
    
    /**
     * Overridden from corba.ORB.
     * See description in corba.ORB.
     */
    protected void invokeServerPIStartingPoint() 
        throws InternalRuntimeForwardRequest
    {
        super.invokeServerPIStartingPoint();
    }
    
    /**
     * Overridden from corba.ORB.
     * See description in corba.ORB.
     */
    protected void invokeServerPIIntermediatePoint() 
        throws InternalRuntimeForwardRequest
    {
        super.invokeServerPIIntermediatePoint();
    }
    
    /**
     * Overridden from corba.ORB.
     * See description in corba.ORB.
     */
    protected void invokeServerPIEndingPoint( ReplyMessage replyMessage )
        throws InternalRuntimeForwardRequest
    {
        super.invokeServerPIEndingPoint( replyMessage );
    }
    
    /**
     * Overridden from corba.ORB.
     * See description in corba.ORB.
     */
    protected void initializeServerPIInfo( ServerRequest request, 
	java.lang.Object poaimpl, byte[] objectId, byte[] adapterId ) 
    {
        super.initializeServerPIInfo( request, poaimpl, objectId, adapterId );
    }
   
    /**
     * Overridden from corba.ORB.
     * See description in corba.ORB.
     */
    protected void setServerPIInfo( java.lang.Object servant, 
				    String targetMostDerivedInterface ) 
    {
        super.setServerPIInfo( servant, targetMostDerivedInterface );
    }
    
    /**
     * Overridden from corba.ORB.
     * See description in corba.ORB.
     */
    protected void setServerPIInfo( Exception exception ) {
        super.setServerPIInfo( exception );
    }

    /**
     * Overridden from corba.ORB.
     * See description in corba.ORB.
     */
    protected void setServerPIInfo( NVList arguments ) {
        super.setServerPIInfo( arguments );
    }
    
    /**
     * Overridden from corba.ORB.
     * See description in corba.ORB.
     */
    protected void setServerPIExceptionInfo( Any exception ) {
        super.setServerPIExceptionInfo( exception );
    }

    /**
     * Overridden from corba.ORB.
     * See description in corba.ORB.
     */
    protected void setServerPIInfo( Any result ) {
        super.setServerPIInfo( result );
    }

    /**
     * Overridden from corba.ORB.
     * See description in corba.ORB.
     */
    protected void cleanupServerPIRequest() {
	super.cleanupServerPIRequest();
    }

} // Class ORB
