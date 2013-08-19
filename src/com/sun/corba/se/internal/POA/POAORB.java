/*
 * @(#)POAORB.java	1.116 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.corba.se.internal.POA;

import java.util.*;
import java.net.InetAddress;

import org.omg.PortableServer.*;
import org.omg.CORBA.ORBPackage.InvalidName;
import org.omg.CORBA.Any;
import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.INITIALIZE;
import org.omg.CORBA.INTERNAL;
import org.omg.CORBA.NVList;
import org.omg.CORBA.ORBPackage.InvalidName ;
import org.omg.CORBA.WrongTransaction;
import org.omg.CORBA.portable.ObjectImpl;
import org.omg.CORBA.portable.RemarshalException;
import org.omg.CosNaming.NamingContext;

import com.sun.corba.se.internal.core.*;

import com.sun.corba.se.internal.ior.IIOPProfile;
import com.sun.corba.se.internal.iiop.Connection;
import com.sun.corba.se.internal.ior.IORTemplate;

import com.sun.corba.se.internal.iiop.messages.ReplyMessage;

import com.sun.corba.se.internal.orbutil.ORBUtility; //d11638
import com.sun.corba.se.internal.orbutil.ORBConstants; 
import com.sun.corba.se.internal.orbutil.ORBClassLoader; 

import com.sun.corba.se.internal.corba.ClientDelegate;
import com.sun.corba.se.internal.corba.CORBAObjectImpl;
import com.sun.corba.se.internal.corba.RequestImpl;
import com.sun.corba.se.internal.corba.ServerDelegate;

import com.sun.corba.se.internal.Activation.BootStrapActivation;

import com.sun.corba.se.ActivationIDL.Activator;
import com.sun.corba.se.ActivationIDL.ActivatorHelper;
import com.sun.corba.se.ActivationIDL.Locator;
import com.sun.corba.se.ActivationIDL.LocatorHelper;
import com.sun.corba.se.ActivationIDL.IIOP_CLEAR_TEXT;
import com.sun.corba.se.ActivationIDL.EndPointInfo;

public class POAORB extends com.sun.corba.se.internal.iiop.ORB
{
    public static final int DefaultSCID = ORBConstants.TransientSCID;
    
    // This is the mapping from subcontract-class to subcontract-ids. 
    private String[] scTable[] = {
	{ORBConstants.GenericPOAClient, ORBConstants.GenericPOAServer, 
	    Integer.toString(ORBConstants.TransientSCID)},
	{ORBConstants.GenericPOAClient, ORBConstants.GenericPOAServer, 
	    Integer.toString(ORBConstants.PersistentSCID)},
	{ORBConstants.ServantCachePOAClient, ORBConstants.GenericPOAServer, 
	    Integer.toString(ORBConstants.SCTransientSCID)},
	{ORBConstants.ServantCachePOAClient, ORBConstants.GenericPOAServer, 
	    Integer.toString(ORBConstants.SCPersistentSCID)},
    };

    // Information required for supporting persistence/activation:
    // orbdPort is port at which ORBD listens. This is embedded in persistent 
    // objrefs so that ORBD gets invocations first and has a chance to activate 
    // this server if necessary.
    //private int orbdPort=0;  - Note: orbd ports now live in the GIOP endpoint list
    private boolean orbdPortInitialized=false;  
    private boolean persistentPortInitialized=false;
    private int persistentServerId=0;
    boolean persistentServerIdInitialized=false;
    private String persistentServerName = null;
    private int persistentServerPort;
    private EndPoint serverEndPoint;

    // Stuff for supporting activation thru orbd 
    private boolean serverIsORBActivated = false;
    private BadServerIdHandler badServerIdHandler = null;
    private String badServerIdHandlerClass = null;

    // POA stuff
    POAImpl rootPOA;
    org.omg.PortableServer.Current poaCurrent;
    Set poaManagers = Collections.synchronizedSet(new HashSet(4));
    DelegateImpl delegateImpl;

    // Combined list of initialization props, applet params, commandline args
    protected Properties allProps = new Properties();

    // List of property names recognized by this ORB. 
    private static final String[] POAORBPropertyNames = {
	ORBConstants.PERSISTENT_SERVER_PORT_PROPERTY,
	ORBConstants.SERVER_ID_PROPERTY,
	ORBConstants.BAD_SERVER_ID_HANDLER_CLASS_PROPERTY,
	ORBConstants.ACTIVATED_PROPERTY
    };

    // This method creates the root POA.  Note that this has the crucial
    // side-effect of starting the transport, which allocates server sockets
    // for listening for incoming requests.  This is done indirectly
    // when the root POA creates its ior template, which requires an IIOP
    // address, which gets host and port, which starts the transport.
    protected POAImpl makeRootPOA( )
    {
        POAManagerImpl poaManager = new POAManagerImpl( this ) ;
	POAImpl result = new POAImpl( ORBConstants.ROOT_POA_NAME,
            poaManager, Policies.rootPOAPolicies, null, null, this ) ;
        return result;
    }
  
    /** Default constructor. Called from org.omg.CORBA.ORB.init().
     *  This is the only constructor, and it must be followed by
     *  the appropriate set_parameters() call from org.omg.CORBA.ORB.init().
     */
    public POAORB()
    {
	super();

	// We delay the evaluation of makeRootPOA until
	// a call to resolve_initial_references( "RootPOA" ).
	// The Future guarantees that makeRootPOA is only called once.
	Closure rpClosure = new Closure() {
	    public Object evaluate() {
		return POAORB.this.makeRootPOA() ;
	    }
	} ;
	registerInitialReference( ORBConstants.ROOT_POA_NAME, 
	    new Future( rpClosure ) ) ;

	poaCurrent = new POACurrent(this);
	registerInitialReference( ORBConstants.POA_CURRENT_NAME, 
	    new Constant( poaCurrent ) ) ;
    }

/******************************************************************************
 *  The following methods deal with ORB initialization and property parsing etc.
 *  Only the first two set_parameters() are public; rest are internal methods.
 ******************************************************************************/

    /**
     * Initialize any necessary ORB state by parsing passed parameters/props.
     * Called from org.omg.CORBA.ORB.init(...).
     * @param args String arguments typcially from the main() method
     * @param props Properties that are specific to the application
     */
    protected void set_parameters(String[] args, java.util.Properties props)
    {
	super.set_parameters(args, props);
        initializePOA( );

	// The InetAddress objects are used to compare ORBInitialHost and
	// LocalHost. If these two are equal and persistentServerPort == ORBInitialPort
	// then BootStrap activation is started. ORB will act as ORBD+ORB         
        try{
            if( ( persistentServerPort == ORBInitialPort )
                && ( ORBInitialPort != 0 )
                && getLocalHostName().equals(getHostName( ORBInitialHost )) )
                // This means that the ORB is started without ORBD and is listening
                // both BootStrap and other requests in one port
                {
                    // BootStrapActivation calls getInitialService( "<ServiceName>") 
                    // and initializes all the services for resolve_initial_references to
                    // work
                    BootStrapActivation theActivation =
			new BootStrapActivation( this );
                    theActivation.start( );
                } 
        } catch( Exception e ){
            // If there is any exception here, do pointout it is a Bootstrap
            // initialization error.
            throw new INITIALIZE(MinorCodes.BOOTSTRAP_ERROR, 
                CompletionStatus.COMPLETED_NO );
	}

        // Get all properties and store them for later use.
	if( props != null ) {
	    allProps = (Properties)props.clone();
	}

        initPostProcessing();
    }

    private String getHostName(String host) throws java.net.UnknownHostException {
        return InetAddress.getByName( host ).getHostAddress();
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
     */
    private static String localHostString = null;

    private String getLocalHostName() {
        if (localHostString != null) {
            return localHostString;
        } else {
            try {
                synchronized (com.sun.corba.se.internal.POA.POAORB.class){
                    if ( localHostString == null )
                        localHostString = InetAddress.getLocalHost().getHostAddress();
                    return localHostString;
                }
            } catch (Exception ex) {
                throw new INTERNAL( 
		    com.sun.corba.se.internal.orbutil.MinorCodes.GET_LOCAL_HOST_FAILED,
			CompletionStatus.COMPLETED_NO );
            }
	}
    }

    /**
     * Initialize any necessary ORB state by parsing passed parameters/props.
     * Called from org.omg.CORBA.ORB.init(...).
     * @param app  the applet
     * @param props Properties that are specific to the application/applet
     */
    protected void set_parameters(java.applet.Applet app, java.util.Properties props)
    {
	super.set_parameters(app, props);
	initializePOA() ;

	// We will not support single address mode in Applets,
	// since Applets cannot act as ORBD.

        // Get all properties and store them for use later.
	if( props != null ) {
	    allProps = (Properties)props.clone();
	}

        initPostProcessing();
    }

    /** Return a list of property names that this ORB is interested in.
     *  This may be overridden by subclasses, but subclasses must call
     *  super.getPropertyNames() to get all names.
     *  Called from super.set_parameters() for both application and applets.
     */
    protected String[] getPropertyNames()
    {
        String[] names = super.getPropertyNames();
	String[] result = ORBUtility.concatenateStringArrays( names, 
	    POAORBPropertyNames ) ;

	if (ORBInitDebug)
	    dprint( "getPropertyNames returns " + 
		ORBUtility.objectToString( result ) ) ;

	return result ;
    }

    /** Set ORB internal variables using the properties specified. 
     *  Called from super.set_parameters() for both application and applets.
     */
    protected void parseProperties(java.util.Properties props)
    {
        super.parseProperties(props);

        // get persistent server port
        String serverPortStr = props.getProperty( 
	    ORBConstants.PERSISTENT_SERVER_PORT_PROPERTY ) ;
        if (serverPortStr != null) {
            setPersistentServerPort(Integer.valueOf(serverPortStr).intValue());
        }
    
        // get persistent ServerId
        String persServerIdStr = props.getProperty( ORBConstants.SERVER_ID_PROPERTY ) ;
        if (persServerIdStr != null) {
            setPersistentServerId(Integer.valueOf(persServerIdStr).intValue());
        }
    
	//
        // BadServerIdHandler
	//
        badServerIdHandlerClass = props.getProperty( 
	    ORBConstants.BAD_SERVER_ID_HANDLER_CLASS_PROPERTY );
    
        // determine whether the server was ORB activated 
        String activatedStr = props.getProperty( ORBConstants.ACTIVATED_PROPERTY ) ;
        if (activatedStr != null) {
	    serverIsORBActivated = true;
        }

        // get all properties and store them for use later.
        Enumeration e = props.keys();
        while ( e.hasMoreElements() ) {
            Object key = e.nextElement();
            allProps.put(key, props.getProperty((String)key));
        }
    }

    private void initializePOA( ) 
    {
        // register subcontracts in the subcontractRegistry
        // this is overridden in the derived subclass to
        // register it's own subcontracts if any
        initSubcontractRegistry();

        delegateImpl = new DelegateImpl(this);
    }

    /** Do miscellaneous other initialization for subcontracts, registration
     *  with ORBD, POA, etc.
     */
    protected void initPostProcessing()
    {
	// determine the ORBD port so that persistent objrefs can be
	// created.
	if (serverIsORBActivated) {
	    try {
                Locator locator = LocatorHelper.narrow(
		    resolve_initial_references( ORBConstants.SERVER_LOCATOR_NAME )) ;
		Collection serverEndpoints = getServerEndpoints();
		Iterator iterator = serverEndpoints.iterator();
		while (iterator.hasNext()) {
		    EndPoint ep = (EndPoint) iterator.next();
		    // REVISIT - use exception instead of -1.
		    int port = locator.getEndpoint(ep.getType());
		    if (port == -1) {
			port = locator.getEndpoint(EndPoint.IIOP_CLEAR_TEXT);
			if (port == -1) {
			    throw new Exception("ORBD must support IIOP_CLEAR_TEXT");
			}
		    }
		    ep.setLocatorPort(port);
		}
	        orbdPortInitialized = true;
	    } catch (Exception ex) {
		throw new INITIALIZE(MinorCodes.ORBD_ERROR, 
				     CompletionStatus.COMPLETED_MAYBE);
	    }
	}

	initServices();

	// Register back with the Activator (ORBD).
	// This is done at the end so that ORBD will not reply to the	
	// client until this server has finished all initialization.
	if (serverIsORBActivated) {
	    try {
                Activator activator = ActivatorHelper.narrow(
		    resolve_initial_references( ORBConstants.SERVER_ACTIVATOR_NAME )) ;
		Collection serverEndpoints = 
		    getServerGIOP().getServerEndpoints();

		EndPointInfo[] endpointList = 
		    new EndPointInfo[serverEndpoints.size()];
		Iterator iterator = serverEndpoints.iterator();
		int i = 0;
		while (iterator.hasNext()) {
		    EndPoint ep = (EndPoint) iterator.next();
		    endpointList[i] = 
			new EndPointInfo(ep.getType(), ep.getPort());
		    i++;
		}
	        activator.registerEndpoints(
		    getPersistentServerId(), orbId, endpointList);
	    } catch (Exception ex) {
		throw new INITIALIZE(MinorCodes.ORBD_ERROR, 
				     CompletionStatus.COMPLETED_MAYBE);
	    }
	}
    }

    // Convenience package method for getting poaCurrent
    public POACurrent getCurrent()
    { 
	return (POACurrent)poaCurrent;
    }

/******************************************************************************
 *  The following internal methods are accessors/modifiers for ORB internal 
 *  variables.
 ******************************************************************************/

    /** Return the persistent-server-id of this server. This id is the same
     *  across multiple activations of this server. This is in contrast to
     *  com.sun.corba.se.internal.iiop.ORB.getTransientServerId() which returns a transient
     *  id that is guaranteed to be different across multiple activations of
     *  this server. The user/environment is required to supply the 
     *  persistent-server-id every time this server is started, in 
     *  the ORBServerId parameter, System properties, or other means.
     *  The user is also required to ensure that no two persistent servers
     *  on the same host have the same server-id.
     */
    public int getPersistentServerId()
    {
	if ( persistentServerIdInitialized ) 
            return persistentServerId;
	else
	    throw new INITIALIZE(
		"Persistent Server Id not initialized",
		     MinorCodes.PERSISTENT_SERVERID_NOT_SET,
			 CompletionStatus.COMPLETED_MAYBE);
    }

    /** Set the persistent-server-id of this server. This id is the same
     *  across multiple activations of this server. The id can be set to any
     *  integer value other than 0. This id must be set before any persistent
     *  objects can be created.
     */
    public void setPersistentServerId(int id)
    {
	persistentServerId = id;	
	persistentServerIdInitialized = true;
    }

    public boolean getPersistentServerIdInitialized()
    {
	return persistentServerIdInitialized;
    }

    public void setPersistentServerPort(int sp)
    {
	if ( persistentPortInitialized ) 
	    throw new INTERNAL(MinorCodes.PERSISTENT_SERVERPORT_ERROR,
			       CompletionStatus.COMPLETED_MAYBE);

	// Create an endpoint for the port.
	// Note: for transient servers, a ServerSocket is created lazily,
	// only when an objref is created, which allows the ORB to work in 
        // applets which do not allow ServerSockets to be created.
	// But for persistent servers, it is possible that the server is 
	// servicing a persistent objref which was created on a previous
	// incarnation. Hence we have to create the ServerSocket now. 
	// getEndpoint is defined in com.sun.corba.se.internal.iiop.GIOPImpl;
	// it creates the ServerSocket, listenerThread and sets listenerPort.
	
	// Changed on 11/24 to unify the port for Alliance delivery
	// With this change the POAORB can work without ORBD and it
	// will listen all the requests on one port if ORBInitialPort = ORBServerPort.
	// Start the Listener thread only of the ports are different, otherwise
	// it will be started from the BootStrapServer.
	if( sp != ORBInitialPort ) 
	{
		ServerGIOP sgiop = getServerGIOP();
		serverEndPoint = sgiop.getEndpoint(EndPoint.IIOP_CLEAR_TEXT, sp, null);
	}

	persistentServerPort = sp;  
	persistentPortInitialized = true;
    }

    public org.omg.CORBA.Object getInitialService( String theKey )
    {
	// Simply return Null, If the end user wants to replace 
	// any of the initial Service then this method has to be
	// overridden and a particular service handle shold be
	// returned based upon the Key.
	// This method is useful for replacing Sun's NameService
	// with Vendor's NameService
	return null;
    }

    public int getPersistentServerPort(String socketType)
    {
	if ( orbdPortInitialized ) // this server is activated by orbd
	    return getServerGIOP().getPersistentServerPort(socketType);
	else if ( persistentPortInitialized ) // this is a user-activated server
	    return persistentServerPort;
	else
	    throw new INITIALIZE("Persistent Server Port not initialized",
				 MinorCodes.PERSISTENT_SERVERPORT_NOT_SET,
				 CompletionStatus.COMPLETED_MAYBE);
    }

    // This overrides iiop.ORB.getServerPort.
    public int getServerPort(String socketType)
    {
	if ( serverIsORBActivated ) {
	    return getPersistentServerPort(socketType);
	} else {
	    return super.getServerPort(socketType);
	}
    }

    public EndPoint getServerEndpoint()
    {
	if ( serverEndPoint == null ) { // create one
	    ServerGIOP sgiop = getServerGIOP();
	    sgiop.initEndpoints();
	    serverEndPoint = sgiop.getDefaultEndpoint();
	}
	return serverEndPoint;
    }

    public Collection getServerEndpoints()
    {
	getServerGIOP().initEndpoints();
	return getServerGIOP().getServerEndpoints();
    }

    /**
     * The bad server id handler is used by the Locator to
     * send back the location of a persistant server to the client.
     */
    public BadServerIdHandler getBadServerIdHandler()
    {
	return badServerIdHandler;
    }

    public void setBadServerIdHandler(BadServerIdHandler handler)
    {
	badServerIdHandler = handler;
    }

    public String getBadServerIdHandlerClass()
    {
	return badServerIdHandlerClass;
    }

    protected void removePoaManager(POAManager manager) {
        poaManagers.remove(manager);
    }

    protected void addPoaManager(POAManager manager) {
        poaManagers.add(manager);
    }

    public synchronized POA getRootPOA()
    {
	if (rootPOA == null) {
	    try {
		Object obj = resolve_initial_references(
		    ORBConstants.ROOT_POA_NAME ) ;
		rootPOA = (POAImpl)obj ;
	    } catch (InvalidName inv) {
		throw new INTERNAL() ;
	    } 
	}

	return rootPOA;
    }

    public String getORBId() {
        return orbId;
    }

/******************************************************************************
 *  The following public methods are for ORB shutdown. 
 *
 ******************************************************************************/

    /** This method shuts down the ORB and causes orb.run() to return.
     *	It will cause all POAManagers to be deactivated, which in turn
     *  will cause all POAs to be deactivated.
     */
    protected void shutdownServants(boolean wait_for_completion) {
	// It is important to copy the list of POAManagers first because 
	// pm.deactivate removes itself from poaManagers!
	Iterator managers = (new HashSet(poaManagers)).iterator();
	while ( managers.hasNext() ) {
	    try {
	        ((POAManager)managers.next()).deactivate(true, wait_for_completion);
	    } catch ( org.omg.PortableServer.POAManagerPackage.AdapterInactive e ) {}
	}
        super.shutdownServants(wait_for_completion);
    }

    /** This method always returns false because the ORB never needs the
     *  main thread to do work.
     */
    public boolean work_pending()
    {
        checkShutdownState();
	return false;
    }
  
    /** This method does nothing. It is not required by the spec to do anything!
     */
    public void perform_work()
    {
        checkShutdownState();
    }

    synchronized boolean isProcessingInvocation() {
        return isProcessingInvocation.get() == Boolean.TRUE;
    }

    //
    // Client side service context interceptors
    //

    /** 
     * Called before the arguments are marshalled and before the
     * request is sent. A derived class may override this hook to view the 
     * service contexts that the ORB has prepared for this request.  The 
     * overriding method may also add new service contexts.
     * Note: this hook may be invoked concurrently by multiple threads.
     * However, ServiceContexts objects are not shared between threads.
     */
    protected void sendingRequestServiceContexts( ServiceContexts scs ) 
    {
    }

    /** 
     * Called after the ORB receives a reply, before result is
     * unmarshalled.  A derived class may override this hook to view the
     * service contexts that the ORB has received in this reply.  The
     * overriding method should only view the received service contexts.
     * The contents of scs will be further processed by the ORB after
     * receivedServiceContexts returns.
     * Note: this hook may be invoked concurrently by multiple threads.
     * However, ServiceContexts objects are not shared between threads.
     */
    protected void receivedReplyServiceContexts(ServiceContexts scs)
    {
    }

    //
    // Server side service context interceptors
    //

    /** 
     * Called after the ORB receives a request, before arguments are
     * unmarshalled.  A derived class may override this hook to view the
     * service contexts that the ORB has received in this request.  The
     * overriding method should only view the received service contexts.
     * The contents of scs will be further processed by the ORB after
     * receivedServiceContexts returns.
     * Note: this hook may be invoked concurrently by multiple threads.
     * However, ServiceContexts objects are not shared between threads.
     */
    protected void receivedRequestServiceContexts(ServiceContexts scs) 
    {
    }

    /** 
     * Called before the arguments are marshalled and before the
     * reply is sent. A derived class may override this hook to view the 
     * service contexts that the ORB has prepared for this reply.  The 
     * overriding method may also add new service contexts.
     * Note: this hook may be invoked concurrently by multiple threads.
     * However, ServiceContexts objects are not shared between threads.
     */
    protected void sendingReplyServiceContexts(ServiceContexts scs)
    {
    }

    // Hook that can be overridden in a derived class to add more specific
    // subcontracts to the SubContractRegistry
    protected void initSubcontractRegistry() {
        for (int i =0; i<scTable.length;i++) {
            try {
		int scid = Integer.parseInt(scTable[i][2]);
                Class clientSCclass = ORBClassLoader.loadClass(scTable[i][0]);
                subcontractRegistry.registerClient(clientSCclass, scid);
                String serverSCclass = scTable[i][1];
                ServerSubcontract sc = (ServerSubcontract)
		    ORBClassLoader.loadClass(serverSCclass).newInstance();
                sc.setOrb(this);
                sc.setId(scid);
                subcontractRegistry.registerServer(sc, scid);
            } catch (Exception exc) {
                if (subcontractDebugFlag)
                    exc.printStackTrace();
	    }
        }
    }

    // Hook to enable the subclassed ORBs to initialize their services
    // after POA creation
    protected void initServices()
    {
    }

    // Hook to enable the subclassed ORBs to put additional service
    // contexts while building a reply

    protected void getServiceSpecificServiceContexts(int scid,
                                                        ServerRequest serverRequest,
                                                        ServiceContexts contexts)
    {
    }

    public GenericPOAServerSC getServerSubcontract(POAImpl poa) 
    {
        // here return the GenericPOAServerSC, and it takes care of handling
        // non-transactional cases

	Policies policies = poa.getPolicies();
	ServerSubcontract subcontract;

        if ( !policies.isPersistent() )
            subcontract = subcontractRegistry.getServerSubcontract(
		ORBConstants.TransientSCID);
        else
            subcontract = subcontractRegistry.getServerSubcontract(
		ORBConstants.PersistentSCID);

	return (GenericPOAServerSC) subcontract;
    }

    protected IOR objectReferenceCreated( IOR ior ) 
    {
	return ior ;
    }

    public boolean isLocalServerId( int subcontractId, int serverId )
    {
	if ((subcontractId < ORBConstants.FIRST_POA_SCID) || 
	    (subcontractId > ORBConstants.MAX_POA_SCID))
	    return super.isLocalServerId( subcontractId, serverId ) ;
		
	if (GenericPOAServerSC.isTransient( subcontractId ))
	    return (serverId == getTransientServerId()) ;
	else if (persistentServerIdInitialized)
	    return (serverId == getPersistentServerId()) ;
	else
	    return false ;
    }

    //==============================================================================
    // In support of the Servant to be implemented by org.omg.CORBA_2_3.ORB s.
    //==============================================================================
    public void set_delegate(java.lang.Object servant){
        checkShutdownState();

        ((org.omg.PortableServer.Servant)servant)
            ._set_delegate(new DelegateImpl(this));
    }

    /**************************************************************************
     *
     *  The following method is a hook for Portable IOR Interceptors
     *
     *************************************************************************/

    /**
     * Called when a new POA is created.  This hook is empty in POAORB, 
     * but will be filled in in PIORB.  This allows us to ship both with and
     * without Portable Interceptor support without need to change any code.
     *
     * @param poaImpl The POAImpl associated with the interceptors to be
     *   invoked.
     */
    protected void invokeIORInterceptors( POAImpl poaImpl ) {
    }

    /*
     **************************************************************************
     *  The following methods are hooks for Portable Interceptors.
     *  They have empty method bodies so that we may ship with or without
     *  PI support.  The actual implementations can be found in 
     *  Interceptors.PIORB.  The uppermost implementations can be found
     *  in corba.ORB.  Some are explicitly overridden here in protected 
     *  scope so that we can access them from the classes in the POA package.
     *************************************************************************/

    /*
     *****************
     * Client PI hooks
     *****************/
    
    /**
     * Overridden from corba.ORB.
     * See description in corba.ORB.
     */
    protected void disableInterceptorsThisThread() {
        super.disableInterceptorsThisThread();
    }
    
    /**
     * Overridden from corba.ORB.
     * See description in corba.ORB.
     */
    protected void enableInterceptorsThisThread() {
        super.enableInterceptorsThisThread();
    }
    
    /**
     * Overridden from corba.ORB.
     * See description in corba.ORB.
     */
    protected void invokeClientPIStartingPoint() 
        throws RemarshalException 
    {
        super.invokeClientPIStartingPoint();
    }
    
    /**
     * Overridden from corba.ORB.
     * See description in corba.ORB.
     */
    protected Exception invokeClientPIEndingPoint(
        int replyStatus, Exception exception )
    {
        return super.invokeClientPIEndingPoint( replyStatus, exception );
    }
    
    /**
     * Overridden from corba.ORB.
     * See description in corba.ORB.
     */
    protected void initiateClientPIRequest( boolean diiRequest ) {
        super.initiateClientPIRequest( diiRequest );
    }
    
    /**
     * Overridden from corba.ORB.
     * See description in corba.ORB.
     */
    protected void cleanupClientPIRequest() {
        super.cleanupClientPIRequest();
    }
    
    /**
     * Overridden from corba.ORB.
     * See description in corba.ORB.
     */
    protected void setClientPIInfo( Connection connection,
				    ClientDelegate delegate, 
                                    IOR effectiveTarget,
                                    IIOPProfile profile, 
                                    int requestId,
                                    String opName,
                                    boolean isOneWay,
                                    ServiceContexts svc ) 
    {
        super.setClientPIInfo( connection, delegate, effectiveTarget, profile, 
                               requestId, opName, isOneWay, svc );
    }

    /**
     * Overridden from corba.ORB.
     * See description in corba.ORB.
     */
    protected void setClientPIInfo( ClientResponse response ) {
        super.setClientPIInfo( response );
    }

    /**
     * Overridden from corba.ORB.
     * See description in corba.ORB.
     */
    protected void setClientPIInfo( RequestImpl requestImpl ) {
        super.setClientPIInfo( requestImpl );
    }

    /**
     * Overridden from corba.ORB.
     * See description in corba.ORB.
     */
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

    /*
     **************************************************************************
     * End Portable Interceptors Hooks
     *************************************************************************/


    // Check if the target objref has non-transactional SCID even though
    // servant is transactional. If so, throw return new IOR to be forwarded.
    public IOR checkTransactional( Servant servant, byte[] oid,
	POAImpl poa, int targetScid ) 
    {
	return null ;
    }

    // Hook for client subcontract to obtain Tx service context
    // for sending request
    public ServiceContext getTxServiceContext( int RequestId ) 
    {
	return null ;
    }

    // Hook for client subcontract to handle Tx service context
    // in received reply
    public void handleTxServiceContext( ServiceContexts scs,
	Exception exception, int requestId ) throws WrongTransaction
    {
	// NO-OP here
    }

    public void dump( String type )
    {
	if (type.equals( "serverSubcontract" ) )
	    subcontractRegistry.dumpServers() ;
    }
}
