/*
 * @(#)GIOPImpl.java	1.49 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.corba.se.internal.iiop;

import java.net.ServerSocket ;

import java.util.Collection ;
import java.util.HashMap ;
import java.util.Vector ;
import java.util.Iterator ;
import java.util.Enumeration ;
import java.util.Map ;

import com.sun.corba.se.internal.core.ClientGIOP;
import com.sun.corba.se.internal.core.ServerGIOP;
import com.sun.corba.se.internal.core.IOR;
import com.sun.corba.se.internal.core.RequestHandler;
import com.sun.corba.se.internal.core.ServiceContext;
import com.sun.corba.se.internal.core.ServiceContexts;
import com.sun.corba.se.internal.core.ClientRequest;
import com.sun.corba.se.internal.core.EndPoint;

import com.sun.corba.se.internal.corba.ClientDelegate;
import com.sun.corba.se.internal.corba.EncapsOutputStream;

import com.sun.corba.se.internal.orbutil.ORBUtility;

import com.sun.corba.se.internal.ior.IIOPProfile ;
import com.sun.corba.se.internal.ior.ObjectKeyTemplate;
import com.sun.corba.se.internal.ior.ObjectId ;

import org.omg.CORBA.INITIALIZE;
import org.omg.CORBA.INTERNAL;
import org.omg.CORBA.CompletionStatus;

import com.sun.corba.se.internal.orbutil.MinorCodes;  //d11638
import java.net.InetAddress;

import java.security.AccessController;
import java.security.PrivilegedAction;

public class GIOPImpl implements ClientGIOP, ServerGIOP {

    // Start at some value other than zero since this is a magic
    // value in some protocols.
    private int requestId = 5;

    protected synchronized int getNextRequestId() {
	return requestId++;
    }

    protected ConnectionTable table;
    protected ORB orb;
    
    public GIOPImpl(ORB orb, RequestHandler handler) {
	this.orb = orb;
	this.table = new ConnectionTable(orb, this);
	setRequestHandler(handler);
    }

    public Connection getConnection( IOR ior ) {
	return table.getConnection( ior ) ;
    }

    public void deleteConnection( EndPoint ep ) 
    {
        try {
	    table.deleteConn( ep ) ;
        } catch( Exception e ) {
	    // Ignore the exception
        }
    }


    /**
     * Allocate a new request id.
     */
    public int allocateRequestId()
    {
	return getNextRequestId();
    }

    private RequestHandler handler;
    
    public void setRequestHandler(RequestHandler handler) {
	this.handler = handler;
    }

    public RequestHandler getRequestHandler() {
	return handler;
    }

    public IOR locate(IOR ior)
    {
	IIOPProfile iop = ior.getProfile();
	ObjectKeyTemplate oktemp = iop.getTemplate().getObjectKeyTemplate() ;
	ObjectId oid = iop.getObjectId() ;
	EncapsOutputStream os = new EncapsOutputStream( orb ) ;
	oktemp.write( oid, os ) ;
	byte[]  objKey = os.toByteArray() ;

        Connection c = table.getConnection(ior);
	IOR newIOR = c.locate(getNextRequestId(), objKey, ior);
	if ( newIOR == null ) // we got an OBJECT_HERE reply
	    newIOR = ior;
	return newIOR;
    }

    /****************************************************************************
    The following methods are implementations of the ServerGIOP's endpoint APIs.
    ****************************************************************************/

    protected HashMap listenerThreads = new HashMap();

    protected Vector endPoints = new Vector();
    private EndPoint bootstrapEndpoint;

    private boolean wasExplicitInitializationDone = false;

    public synchronized void initEndpoints() {
	if (! wasExplicitInitializationDone) {
	    // REVISIT - temporary until getDefaultEndpoint problem fixed.
	    if (endPoints.size() == 0) {
		// Allocate the default listener.
		getEndpoint(EndPoint.IIOP_CLEAR_TEXT, 0, null);
	    }
	    // Allocate user listeners.
	    Iterator types = orb.getUserSpecifiedListenPorts().iterator();
	    while (types.hasNext()) {
		ORB.UserSpecifiedListenPort user =
		    (ORB.UserSpecifiedListenPort) types.next();
		getEndpoint(user.getType(), user.getPort(), null);
	    }
	    wasExplicitInitializationDone = true;
	}
    }

    public synchronized EndPoint getDefaultEndpoint() {
	// REVISIT - do not depend on order.
	// Note: some places in the code just do getEndpoint
	// without calling initEndpoint.  THis causes the endPoints
	// vector to contain entries.  They may depend on their
	// first call to getEndpoint creating what will become
	// the "default" endpoint.
	// E.G.: POAORB.setPersistentServerPort
	if (endPoints.size() == 0)
	    return null;
	return (EndPoint) endPoints.elementAt(0);
    }

    /**
     * Get an EndPoint for the specified type, listenPort and local address.
     *
     * Type must be one which has a socket factory
     * (e.g., EndPoint.IIOP_CLEAR_TEXT default or user supplied "SSL").
     *
     * If listenPort == 0, a listening port will be assigned.
     *
     * If addr == null, InetAddress.getLocalHost() is used.
     *
     * If an EndPoint at the specified port/address does not exist,
     * then it will be created.
     */
    public synchronized EndPoint getEndpoint(String socketType, int port,
				             InetAddress addr)
    {
	if (orb.transportDebugFlag) {
	    ORBUtility.dprint(this, "getEndpoint(" + socketType + ", " + port + ", " + addr +")");
	}

	// Check if this endpoint was already created
        String host = null;
        if (addr != null)
            host = addr.getHostName().toLowerCase();
	Enumeration eps = endPoints.elements();
	while ( eps.hasMoreElements() ) {
	    EndPoint ep = (EndPoint)eps.nextElement();
	    if ( ep.getType().equals(socketType) &&
                 ep.getPort() == port &&
                 ep.getHostName().equals(host) )
            {
		return ep;
            }
	}

	// REVISIT - This needs to go in initEndpoints.
	// However, it seems getEndpoint may be called before
	// init, so it is left here for now.
	if (socketType == EndPoint.IIOP_CLEAR_TEXT &&
	    port == 0 &&
	    orb.getORBServerPort() != 0)
	{
	    port = orb.getORBServerPort();
	}

	// Create the endpoint
	ListenerThread listenerThread = createListener(socketType, port);
	listenerThreads.put(socketType, listenerThread);
	EndPoint ep =
	    new EndPointImpl(socketType, 
			     listenerThread.getSocket().getLocalPort(),
			     orb.getORBServerHost());
	endPoints.addElement(ep);

	return ep;
    }

    /**
     * Get an EndPoint for the bootstrap naming service, at the specified port.
     * If listenPort == 0 and this is the first time getBootstrapEndpoint was
     * invoked, then an EndPoint at the default port of 900 will be created.
     * If listenPort == 0 and this is not the first time getBootstrapEndpoint
     * was called, then the existing bootstrap EndPoint will be returned.
     */
    public synchronized EndPoint getBootstrapEndpoint(int port)
    {
	if (bootstrapEndpoint == null)
	    bootstrapEndpoint =
		getEndpoint(EndPoint.IIOP_CLEAR_TEXT, port, null);

	return bootstrapEndpoint;
    }

    private synchronized ListenerThread createListener(final String socketType,
						       final int port) {
	final ServerSocket ss;
        ListenerThread lis;
        final ConnectionTable finalTable = table;

	if (orb.transportDebugFlag)
	    ORBUtility.dprint( this, "createListener( socketType = " + socketType +
		" port = " + port + " )" ) ;

        try {
	    ss = orb.getSocketFactory().createServerSocket(socketType, port);
            lis = (ListenerThread) AccessController.doPrivileged(new PrivilegedAction() {
                public java.lang.Object run() {
                    ListenerThread thread = new ListenerThread(finalTable, 
							       ss,
							       socketType);
                    thread.setDaemon(true);
                    return thread;
                }
            });
            lis.start();
        }
        catch (java.lang.Exception e) {
            throw new INTERNAL(MinorCodes.CREATE_LISTENER_FAILED,
			       CompletionStatus.COMPLETED_NO);
        }
        return lis;
    }

    public int getServerPort (String socketType)
    {
	return getServerPort(socketType, false);
    }

    public int getPersistentServerPort (String socketType)
    {
	return getServerPort(socketType, true);
    }

    private int getServerPort (String socketType, boolean isPersistent)
    {
	if (endPoints.size() == 0) {
	    throw new INITIALIZE(
	        "GIOPImpl.get*ServerPort called before endpoints initialized.",
		MinorCodes.GET_SERVER_PORT_CALLED_BEFORE_ENDPOINTS_INITIALIZED,
		CompletionStatus.COMPLETED_NO);
	}

	Iterator endpoints = endPoints.iterator();
	while (endpoints.hasNext()) {
	    EndPoint ep = (EndPoint) endpoints.next();
	    if (ep.getType().equals(socketType)) {
		if (isPersistent) {
		    return ep.getLocatorPort();
		} else {
		    return ep.getPort();
		}
	    }
	}
	return -1;
    }

    public Collection getServerEndpoints ()
    {
	return endPoints;
    }

    void destroyConnections()
    {
        // Shut down all Listeners
        Iterator threads = listenerThreads.entrySet().iterator();
        while (threads.hasNext()) {
            // Listener threads always shut down gracefully.
            ((ListenerThread)((Map.Entry)threads.next()).getValue()).shutdown();
        }
        // Shut down all Readers
        table.destroyConnections();
    }
}

