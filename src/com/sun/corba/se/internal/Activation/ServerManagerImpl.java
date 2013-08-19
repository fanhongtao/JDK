/*
 * @(#)ServerManagerImpl.java	1.45 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.corba.se.internal.Activation;

/**
 *
 * @version     1.3, 97/10/19
 * @author      Rohit Garg
 * @since       JDK1.2
 */

import java.lang.reflect.Constructor;

import java.util.HashMap;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.NoSuchElementException;

import org.omg.CORBA.INTERNAL;
import org.omg.CORBA.OBJECT_NOT_EXIST;
import org.omg.CORBA.CompletionStatus;

import com.sun.corba.se.internal.core.*;
import com.sun.corba.se.internal.util.Utility;
import com.sun.corba.se.internal.orbutil.ORBConstants;
import com.sun.corba.se.internal.orbutil.ORBClassLoader;
import com.sun.corba.se.internal.POA.POAORB;
import com.sun.corba.se.internal.POA.BadServerIdHandler;
import com.sun.corba.se.internal.POA.ForwardException;
import com.sun.corba.se.ActivationIDL.EndPointInfo;
import com.sun.corba.se.ActivationIDL.IIOP_CLEAR_TEXT;
import com.sun.corba.se.ActivationIDL.ORBPortInfo;
import com.sun.corba.se.ActivationIDL.Repository;
import com.sun.corba.se.ActivationIDL.LocatorPackage.ServerLocation;
import com.sun.corba.se.ActivationIDL.LocatorPackage.ServerLocationPerORB;
import com.sun.corba.se.ActivationIDL.RepositoryPackage.ServerDef;
import com.sun.corba.se.ActivationIDL._ServerManagerImplBase;
import com.sun.corba.se.ActivationIDL.ServerAlreadyActive;
import com.sun.corba.se.ActivationIDL.ServerAlreadyInstalled;
import com.sun.corba.se.ActivationIDL.ServerAlreadyUninstalled;
import com.sun.corba.se.ActivationIDL.ServerNotRegistered;
import com.sun.corba.se.ActivationIDL.ORBAlreadyRegistered;
import com.sun.corba.se.ActivationIDL.ServerHeldDown;
import com.sun.corba.se.ActivationIDL.ServerNotActive;
import com.sun.corba.se.ActivationIDL.NoSuchEndPoint;
import com.sun.corba.se.ActivationIDL.InvalidORBid;
import com.sun.corba.se.ActivationIDL.Server;
import com.sun.corba.se.ActivationIDL.IIOP_CLEAR_TEXT;

import com.sun.corba.se.internal.ior.ObjectKeyFactory ;
import com.sun.corba.se.internal.ior.ObjectKey ;
import com.sun.corba.se.internal.ior.POAObjectKeyTemplate ;

public class ServerManagerImpl extends _ServerManagerImplBase
    implements BadServerIdHandler
{
    // Using HashMap, since synchronization should be done by the calling
    // routines
    HashMap serverTable;
    Repository repository;

    ServerGIOP sgiop;
    int initialPort;
    POAORB orb;
    String dbDirName;
    boolean debug = false ;
 
    private int serverStartupDelay;

    ServerManagerImpl(POAORB orb, ServerGIOP sgiop, Repository repository,
		      String dbDirName, boolean debug)
    {
        this.orb        = orb;
        this.sgiop      = sgiop;
	this.repository = repository;
	this.dbDirName = dbDirName;
	this.debug = debug ;

	initialPort = orb.getServerGIOP().getBootstrapEndpoint(0).getPort();
        serverTable = new HashMap(256);

        // The ServerStartupDelay is the delay added after the Server registers
        // end point information. This is to allow the server to completely
        // initialize after ORB is instantiated.
        serverStartupDelay = ORBConstants.DEFAULT_SERVER_STARTUP_DELAY;
        String  delay = System.getProperty( ORBConstants.SERVER_STARTUP_DELAY);
        if( delay != null ) {
            try {
                serverStartupDelay = Integer.parseInt( delay );
            } catch ( Exception e ) {
                // Just use the default 1000 milliseconds as the default
            }
        }


	BadServerIdHandler handler = null;
	if (orb.getBadServerIdHandlerClass() == null) {
	    handler = this;
	} else {
	    try {
		Class[] params =
		    new Class[] { org.omg.CORBA.ORB.class };
		java.lang.Object[] args = new java.lang.Object[]{orb};
                Class badServerIdHandlerClass
                    = ORBClassLoader.loadClass(orb.getBadServerIdHandlerClass());
                Constructor cons = badServerIdHandlerClass.getConstructor(params);
		handler = (BadServerIdHandler) cons.newInstance(args);
	    } catch (Exception e) {
		throw new org.omg.CORBA.INITIALIZE(
		    "Error while creating BadServerIdHandler: " + e.getMessage());
	    }
	}
        orb.setBadServerIdHandler(handler);

        ((com.sun.corba.se.internal.corba.ORB)orb).connect(this);
        ProcessMonitorThread.start( serverTable );
    }

    public void activate(int serverId)
        throws ServerAlreadyActive, ServerNotRegistered, ServerHeldDown
    {

        ServerLocation   location;
        ServerTableEntry entry;
        Integer key = new Integer(serverId);

	synchronized(serverTable) {
            entry = (ServerTableEntry) serverTable.get(key);
	}

        if (entry != null && entry.isActive()) {
	    if (debug)
		System.out.println( "ServerManagerImpl: activate for server Id " +
				    serverId + " failed because server is already active. " +
				    "entry = " + entry ) ;

            throw new ServerAlreadyActive( serverId );
	}

        // locate the server
        try {

	    // We call getEntry here so that state of the entry is
	    // checked for validity before we actually go and locate a server

	    entry = getEntry(serverId);

            if (debug)
                System.out.println( "ServerManagerImpl: locateServer called with " +
                                " serverId=" + serverId + " endpointType="
                                + IIOP_CLEAR_TEXT.value + " block=false" ) ;

	    location = locateServer(entry, IIOP_CLEAR_TEXT.value, false);

	    if (debug)
		System.out.println( "ServerManagerImpl: activate for server Id " +
				    serverId + " found location " +
				    location.hostname + " and activated it" ) ;
        } catch (NoSuchEndPoint ex) {
            if (debug)
                System.out.println( "ServerManagerImpl: activate for server Id " +
                                    " threw NoSuchEndpoint exception, which was ignored" );
	}
    }

    public void active(int serverId, Server server) throws ServerNotRegistered
    {
        ServerTableEntry entry;
        Integer key = new Integer(serverId);

        synchronized (serverTable) {
            entry = (ServerTableEntry) serverTable.get(key);

            if (entry == null) {
		if (debug)
		    System.out.println( "ServerManagerImpl: active for server Id " +
					serverId + " called, but no such server is registered." ) ;

                throw (new INTERNAL(MinorCodes.SERVER_NOT_EXPECTED_TO_REGISTER,
				    CompletionStatus.COMPLETED_NO));
            } else {
		if (debug)
		    System.out.println( "ServerManagerImpl: active for server Id " +
					serverId + " called.  This server is now active." ) ;

		entry.register(server);
	    }
        }
    }

    public void registerEndpoints( int serverId, String orbId,
	EndPointInfo [] endpointList ) throws NoSuchEndPoint, ServerNotRegistered,
	ORBAlreadyRegistered
    {
	// orbId is ignored for now
        ServerTableEntry entry;
        Integer key = new Integer(serverId);

        synchronized (serverTable) {
            entry = (ServerTableEntry) serverTable.get(key);

            if (entry == null) {
		if (debug)
		    System.out.println(
			"ServerManagerImpl: registerEndpoint for server Id " +
			serverId + " called, but no such server is registered." ) ;

                throw (new INTERNAL(MinorCodes.SERVER_NOT_EXPECTED_TO_REGISTER,
				    CompletionStatus.COMPLETED_NO));
            } else {
		if (debug)
		    System.out.println(
			"ServerManagerImpl: registerEndpoints for server Id " +
			serverId + " called.  This server is now active." ) ;

		entry.registerPorts( orbId, endpointList );
               
	    }
	}
    }

    // XXX should look at the state of the server as well
    public int[] getActiveServers()
    {
	ServerTableEntry entry;
        int[] list = null;

        synchronized (serverTable) {
	    // unlike vectors, list is not synchronized

	    ArrayList servers = new ArrayList(0);

	    Iterator serverList = serverTable.keySet().iterator();

	    try {
	        while (serverList.hasNext()) {
	            Integer key = (Integer) serverList.next();
	            // get an entry
	            entry = (ServerTableEntry) serverTable.get(key);

		    if (entry.isValid() && entry.isActive()) {
		        servers.add(entry);
	            }
	        }
	    } catch (NoSuchElementException e) {
	        // all done
	    }

	    // collect the active entries
	    list = new int[servers.size()];
	    for (int i = 0; i < servers.size(); i++) {
	        entry = (ServerTableEntry) servers.get(i);
		list[i] = entry.getServerId();
	    }
        }

	if (debug) {
	    StringBuffer sb = new StringBuffer() ;
	    for (int ctr=0; ctr<list.length; ctr++) {
		sb.append( ' ' ) ;
		sb.append( list[ctr] ) ;
	    }

	    System.out.println( "ServerManagerImpl: getActiveServers returns" +
				sb.toString() ) ;
	}

        return list;
    }

    public void shutdown(int serverId) throws ServerNotActive
    {
        ServerTableEntry entry;
        Integer key = new Integer(serverId);

	synchronized(serverTable) {
            entry = (ServerTableEntry) serverTable.remove(key);

            if (entry == null) {
	        if (debug)
		    System.out.println( "ServerManagerImpl: shutdown for server Id " +
				    serverId + " throws ServerNotActive." ) ;

	        throw new ServerNotActive( serverId );
	    }

            try {
                entry.destroy();

	        if (debug)
		    System.out.println( "ServerManagerImpl: shutdown for server Id " +
				    serverId + " completed." ) ;
            } catch (Exception e) {
	        if (debug)
		    System.out.println( "ServerManagerImpl: shutdown for server Id " +
				    serverId + " threw exception " + e ) ;
	    }
	}
    }

    private ServerTableEntry getEntry( int serverId )
	throws ServerNotRegistered
    {
        Integer key = new Integer(serverId);
	ServerTableEntry entry = null ;

	synchronized (serverTable) {
            entry = (ServerTableEntry) serverTable.get(key);

	    if (debug)
		if (entry == null) {
		    System.out.println( "ServerManagerImpl: getEntry: " +
					"no active server found." ) ;
		} else {
		    System.out.println( "ServerManagerImpl: getEntry: " +
					" active server found " + entry + "." ) ;
		}

            if ((entry != null) && (!entry.isValid())) {
                serverTable.remove(key);
                entry = null;
            }

            if (entry == null) {
		ServerDef serverDef = repository.getServer(serverId);

                entry = new ServerTableEntry(
		    serverId, serverDef, initialPort, dbDirName, false, debug);
		serverTable.put(key, entry);
		entry.activate() ;
            }
        }

	return entry ;
    }

    private ServerLocation locateServer (ServerTableEntry entry, String endpointType,
					boolean block)
        throws NoSuchEndPoint, ServerNotRegistered, ServerHeldDown
    {
	ServerLocation location = new ServerLocation() ;

	// if server location is desired, then wait for the server
	// to register back, then return location

	ORBPortInfo [] serverORBAndPortList;
	if (block) {
	    try {
	            serverORBAndPortList = entry.lookup(endpointType);
            } catch (Exception ex) {
		if (debug)
		    System.out.println( "ServerManagerImpl: locateServer: " +
					"server held down" ) ;

	        throw new ServerHeldDown( entry.getServerId() );
	    }

            String host = ((POAORB)orb).getServerEndpoint().getHostName();
            location.hostname = host ;
	    int listLength;
	    if (serverORBAndPortList != null) {
	        listLength = serverORBAndPortList.length;
	    } else {
	        listLength = 0;
	    }
	    location.ports = new ORBPortInfo[listLength];
	    for (int i = 0; i < listLength; i++) {
	        location.ports[i] = new ORBPortInfo(serverORBAndPortList[i].orbId,
			serverORBAndPortList[i].port) ;

	        if (debug)
	            System.out.println( "ServerManagerImpl: locateServer: " +
				    "server located at location " +
				    location.hostname + " ORBid  " +
	                            serverORBAndPortList[i].orbId +
	                            " Port " + serverORBAndPortList[i].port) ;
	    }
	}

	return location;
    }

    private ServerLocationPerORB locateServerForORB (ServerTableEntry entry, String orbId,
					boolean block)
        throws InvalidORBid, ServerNotRegistered, ServerHeldDown
    {
	ServerLocationPerORB location = new ServerLocationPerORB() ;

	// if server location is desired, then wait for the server
	// to register back, then return location

	EndPointInfo [] endpointInfoList;
	if (block) {
	    try {
	        endpointInfoList = entry.lookupForORB(orbId);
	    } catch (InvalidORBid ex) {
		throw ex;
            } catch (Exception ex) {
		if (debug)
		    System.out.println( "ServerManagerImpl: locateServerForORB: " +
					"server held down" ) ;

	        throw new ServerHeldDown( entry.getServerId() );
	    }

            String host = ((POAORB)orb).getServerEndpoint().getHostName();
            location.hostname = host ;
	    int listLength;
	    if (endpointInfoList != null) {
	        listLength = endpointInfoList.length;
	    } else {
	        listLength = 0;
	    }
	    location.ports = new EndPointInfo[listLength];
	    for (int i = 0; i < listLength; i++) {
	        location.ports[i] = new EndPointInfo(endpointInfoList[i].endpointType,
			endpointInfoList[i].port) ;

	        if (debug)
	            System.out.println( "ServerManagerImpl: locateServer: " +
				    "server located at location " +
				    location.hostname + " endpointType  " +
	                            endpointInfoList[i].endpointType +
	                            " Port " + endpointInfoList[i].port) ;
	    }
	}

	return location;
    }

    public String[] getORBNames(int serverId)
	throws ServerNotRegistered
    {
	try {
	    ServerTableEntry entry = getEntry( serverId ) ;
	    return (entry.getORBList());
	} catch (Exception ex) {
	    throw new ServerNotRegistered(serverId);
	}
    }

    private ServerTableEntry getRunningEntry( int serverId )
	throws ServerNotRegistered
    {
	ServerTableEntry entry = getEntry( serverId ) ;

	try {
	    // this is to see if the server has any listeners
	    ORBPortInfo [] serverORBAndPortList = entry.lookup(IIOP_CLEAR_TEXT.value) ;
	} catch (Exception exc) {
	    return null ;
	}
	return entry;

    }

    public void install( int serverId )
	throws ServerNotRegistered, ServerHeldDown, ServerAlreadyInstalled
    {
	ServerTableEntry entry = getRunningEntry( serverId ) ;
	if (entry != null) {
	    repository.install( serverId ) ;
	    entry.install() ;
	}
    }

    public void uninstall( int serverId )
	throws ServerNotRegistered, ServerHeldDown, ServerAlreadyUninstalled
    {
	ServerTableEntry entry = 
            (ServerTableEntry) serverTable.get( new Integer(serverId) );
        
	if (entry != null) {

            entry = 
                (ServerTableEntry) serverTable.remove(new Integer(serverId));

            if (entry == null) {
                if (debug)
                    System.out.println( "ServerManagerImpl: shutdown for server Id " +
                                    serverId + " throws ServerNotActive." ) ;

                throw new ServerHeldDown( serverId );
            }

	    entry.uninstall();
	}
    }

    public ServerLocation locateServer (int serverId, String endpointType)
        throws NoSuchEndPoint, ServerNotRegistered, ServerHeldDown
    {
	ServerTableEntry entry = getEntry( serverId ) ;
        if (debug)
            System.out.println( "ServerManagerImpl: locateServer called with " +
                                " serverId=" + serverId + " endpointType=" +
				endpointType + " block=true" ) ;

	// passing in entry to eliminate multiple lookups for
	// the same entry in some cases

	return locateServer(entry, endpointType, true);
    }

    /** This method is used to obtain the registered ports for an ORB.
    * This is useful for custom Bad server ID handlers in ORBD.
    */
    public ServerLocationPerORB locateServerForORB (int serverId, String orbId)
        throws InvalidORBid, ServerNotRegistered, ServerHeldDown
    {
	ServerTableEntry entry = getEntry( serverId ) ;

	// passing in entry to eliminate multiple lookups for
	// the same entry in some cases

	if (debug)
	    System.out.println( "ServerManagerImpl: locateServerForORB called with " +
				" serverId=" + serverId + " orbId=" + orbId +
				" block=true" ) ;
	return locateServerForORB(entry, orbId, true);
    }


    public void handle(ObjectKey okey) throws ForwardException
    {
	IOR newIOR = null;
	ServerLocationPerORB location;

	// we need to get the serverid and the orbid from the object key
	POAObjectKeyTemplate poktemp = (POAObjectKeyTemplate)(okey.getTemplate());
	int serverId = poktemp.getServerId() ;
	String orbId = poktemp.getORBId() ;

	try {
	    // get the ORBName corresponding to the orbMapid, that was
	    // first registered by the server
	    ServerTableEntry entry = getEntry( serverId ) ;
	    location = locateServerForORB(entry, orbId, true);
             


	    if (debug)
	        System.out.println( "ServerManagerImpl: handle called for server id" +
			serverId + "  orbid  " + orbId) ;


	    // we received a list of ports corresponding to an ORB in a
            // particular server, now retrieve the one corresponding
	    // to IIOP_CLEAR_TEXT, and for other created the tagged
	    // components to be added to the IOR

	    int clearPort = 0;
	    EndPointInfo[] listenerPorts = location.ports;
	    for (int i = 0; i < listenerPorts.length; i++) {
	        if ((listenerPorts[i].endpointType).equals(IIOP_CLEAR_TEXT.value)) {
	            clearPort = listenerPorts[i].port;
	            break;
                }
	    }
	    // create a new IOR with the correct port and correct tagged
            // components
	    newIOR = new IOR(orb, "IDL:org/omg/CORBA/Object:1.0",
			     location.hostname, clearPort,
			     okey);
	} catch (Exception e) {
	    if (debug)
		System.out.println( "ServerManagerImpl: handle " +
				    "throws OBJECT_NOT_EXIST" ) ;

	    throw new OBJECT_NOT_EXIST();
	}

	if (debug)
	    System.out.println( "ServerManagerImpl: handle " +
				"throws ForwardException" ) ;

        
        try {
            // This delay is required in case of Server is activated or 
            // re-activated the first time. Server needs some time before 
            // handling all the requests. 
            // (Talk to Ken to see whether there is a better way of doing this).
            Thread.sleep( serverStartupDelay );
        } catch ( Exception e ) {
            System.out.println( "Exception = " + e );
            e.printStackTrace();
        } 

	throw new ForwardException(newIOR);
    }

    public int getEndpoint(String endpointType) throws NoSuchEndPoint
    {
        return sgiop.getServerPort(endpointType);
    }

    public int getServerPortForType(ServerLocationPerORB location,
				    String endPointType)
	throws NoSuchEndPoint
    {
	EndPointInfo[] listenerPorts = location.ports;
	for (int i = 0; i < listenerPorts.length; i++) {
	    if ((listenerPorts[i].endpointType).equals(endPointType)) {
		return listenerPorts[i].port;
	    }
	}
	throw new NoSuchEndPoint();
    }

}
