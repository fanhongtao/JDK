/*
 * @(#)ConnectionTable.java	1.80 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.corba.se.internal.iiop;

import java.io.*;
import java.util.Hashtable;
import java.util.Enumeration;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.BindException;
import java.net.ConnectException;
import java.net.NoRouteToHostException;

import org.omg.CORBA.COMM_FAILURE;
import org.omg.CORBA.CompletionStatus;

import com.sun.corba.se.connection.EndPointInfo;
import com.sun.corba.se.connection.GetEndPointInfoAgainException;
import com.sun.corba.se.internal.core.EndPoint;
import com.sun.corba.se.internal.core.IOR;
import com.sun.corba.se.internal.core.ServerGIOP;
import com.sun.corba.se.internal.orbutil.MinorCodes;
import com.sun.corba.se.internal.orbutil.ORBUtility;

/**
 * One instance of this class is created per ORB object. 
 */
public class ConnectionTable {
    protected ORB orb;
    protected Hashtable connectionCache = new Hashtable();

    protected long globalCounter=0;

    private int MAX_SOCKET_RETRIES=5;

    protected ServerGIOP server;

    public ConnectionTable(ORB orb, ServerGIOP server) {
	this.orb = orb;
	this.server = server;
    }

    private void dprint(String msg) {
        ORBUtility.dprint(this, msg);
    }


    /**
     * Called only on client side.
     *
     * Get a connection from the connection table, or create a new one.
     *
     * @param ior The IOR whic is being invoked.
     * @return The connection, pre-existing or new.
     */
    public Connection getConnection(IOR ior)
    {
	return getConnection(ior, null);
    }

    private Connection getConnection(IOR ior, EndPointInfo endPointInfo)
    {
	endPointInfo =
	    orb.getSocketFactory().getEndPointInfo(orb, ior, endPointInfo);

	EndPoint key;

	if (endPointInfo instanceof EndPointImpl) {
	    key = (EndPointImpl) endPointInfo;
	} else {
	    // Convert the implmentation to one of ours to make sure
	    // we control hashCode and equals.
	    key = new EndPointImpl(endPointInfo.getType(),
				   endPointInfo.getPort(),
				   endPointInfo.getHost());
	}


	if (orb.transportDebugFlag)
            dprint( "Client get called: host = " + key.getHostName() +
                    " port = " + key.getPort() ) ;

        Connection c = null;
        // Create a new connection object. Even though Hashtable.get/put are
        // synchronized, we need to synchronize on "this" to keep the
        // get+put atomic and prevent two threads from creating connections
        // to the same destination at the same time.
        synchronized(this) {
            // Always recheck the entry condition after getting the lock !!
            c = (Connection) connectionCache.get(key);
            if (c != null) {
		if (orb.transportDebugFlag)
		    dprint( "Returning connection " + c + " from table" ) ;
                return c;
	    }

        }

        // Try to open a socket to the server
        int tries = 0;
        while (true)  {
	    try {
		Socket socket = 
		    orb.getSocketFactory().createSocket(endPointInfo);

		// set socket to disable Nagle's algorithm
		// (always send immediately)
		try {
		    socket.setTcpNoDelay(true);
		} catch (Exception e) {
		}
                // Create an entry in the connection table
                synchronized( this ) {
                    c = new IIOPConnection(orb, server, this, key);
                    stampTime(c);
                    connectionCache.put(key, c);
		    c.setConnection(socket, this);
	            if (orb.transportDebugFlag)
	                dprint( "Creating new connection " + c ) ;
                }
		checkConnectionTable();
		break;
	    } catch (GetEndPointInfoAgainException ex) {
                if( c != null ) {
		    c.abortConnection();
                }
		deleteConn(key);
		return getConnection(ior, ex.getEndPointInfo());
	    } catch (SocketException ex) {
		if (orb.transportDebugFlag)
		    dprint( "SocketException " + ex + 
			    " while creating socket for new connection" ) ;

		if ( ex instanceof BindException
		     || ex instanceof ConnectException
		     || ex instanceof NoRouteToHostException )
		{
		    if (orb.transportDebugFlag)
			dprint( "Serious error: aborting connection" ) ;

		    throw new COMM_FAILURE(MinorCodes.CONNECT_FAILURE,
					   CompletionStatus.COMPLETED_NO);
		}

		if (orb.transportDebugFlag)
		    dprint( "Attempting resource cleanup and retry on socket creation" ) ;

		// Probably the system's file descriptor limit has been reached
		// Try cleaning up some connections and retry.
		if ((tries == MAX_SOCKET_RETRIES) || (!cleanUp())) {
		    if (orb.transportDebugFlag)
			dprint( "Out of resources: aborting connection" ) ;

		    throw new COMM_FAILURE(MinorCodes.CONNECT_FAILURE,
					   CompletionStatus.COMPLETED_NO);
		}
		tries++;
	    } catch ( Exception ex ) {
		if (orb.transportDebugFlag)
		    dprint( "Exception " + ex + 
			    " while creating socket for new connection: aborting connection" ) ;
                if( c != null ) {
	    	    c.abortConnection();
                }
		deleteConn(key);
		throw new COMM_FAILURE(MinorCodes.CONNECT_FAILURE,
				       CompletionStatus.COMPLETED_NO);
	    }
	}

	if (orb.transportDebugFlag)
	    dprint( "Succesfully created socket for new connection" ) ;

        return c;
    }

    /**
     * Called only on server side when a connection has been accepted.
     * Always creates a new Connection instance, which starts the upcall.
     * @param sock The socket for the connection.
     * @param socketType The user defined type of socket (e.g., clear, ssl, etc).
     */
    public synchronized Connection getConnection(Socket sock, 
						 String socketType)
    {
        try {
	    if (orb.transportDebugFlag)
		dprint( "Server getConnection(" + sock + ", " + socketType + ")");

	    InputStream in = null;
	    OutputStream out = null;
	    try {
		in = sock.getInputStream();
		out = sock.getOutputStream();
	    } catch ( Exception ex ) {
		throw new COMM_FAILURE(MinorCodes.CONNECT_FAILURE,
				       CompletionStatus.COMPLETED_NO);
	    }

	    String host = sock.getInetAddress().getHostName();
	    int port = sock.getPort();
            EndPoint key = new EndPointImpl(socketType, port, host);

	    if (orb.transportDebugFlag) 
		dprint( "host = " + host + " port = " + port ) ;
	    
	    // Create a new Connection instance, and start the upcall.
	    // We explicitly give the input/output streams to IIOPConnection
	    // so that there is the flexibility of substituting the streams
	    // e.g. for handling multiple protocols later.
	    Connection c = new IIOPConnection(orb, server, key, sock, 
					      in, out, this);

	    connectionCache.put(key, c);

	    stampTime(c);

	    if (orb.transportDebugFlag) 
		dprint( "Created connection " + c ) ;
	    return c;

        } catch ( Exception ex ) {
	    if (orb.transportDebugFlag) 
		dprint( "Exception " + ex + " on creating connection" ) ;

	    // We should not throw an exception back to the ListenerThread.
	    // Just close the socket and return silently.
	    try {
		sock.close();
	    } catch ( Exception ex2 ) {}
	    return null;
        }
    }


    /**
     * Delete a connection from the connection table.
     * @param key The Endpoint of the connection.
     * @see Connection
     */
    public synchronized void deleteConn(EndPoint key) {
	if (orb.transportDebugFlag) 
	    dprint( "DeleteConn called: host = " + key.getHostName() +
                    " port = " + key.getPort() ) ;

        connectionCache.remove( key );
    }

    /**
     * CleanUp by discarding least recently used Connections that
     * are not busy
     */
    //    private static final int LOW_WATER_MARK = 100;
    // On Solaris there seems to be a max of 256 connections per process.
    //    private static final int HIGH_WATER_MARK = 248;
    // How many connections to clean at a time.
    //    private static final int NCLEAN = 5;

    public boolean cleanUp()
    {
	if (orb.transportDebugFlag) 
	    dprint( "Cleanup called" ) ;

	if (connectionCache.size() < orb.getLowWaterMark()) {
	    if (orb.transportDebugFlag) 
		dprint( "Cleanup returns false: not enough connections open to start cleanup" ) ;
	    return false;
	}

	for ( int i=0; i<orb.getNumberToReclaim(); i++ ) {
	    Connection toClean = null;
	    long lru = java.lang.Long.MAX_VALUE;
	    Enumeration e = connectionCache.elements();

	    // Find least recently used and not busy connection in cache
	    while ( e.hasMoreElements() )
		{
		    Connection c = (Connection) e.nextElement();
		    if ( !c.isBusy() && c.timeStamp < lru )
			{
			    toClean = c; 
			    lru = c.timeStamp;
			}
		}

	    if ( toClean == null ) {
		if (orb.transportDebugFlag) 
		    dprint( "Cleanup returns false: all connections busy" ) ;
		return false;
	    }

	    // Clean connection
	    try {
		if (orb.transportDebugFlag) 
		    dprint( "Cleanup is cleaning connection " + toClean ) ;
		toClean.cleanUp();
	    } catch (Exception ex) {}
	}

	// XXX is necessary to do a GC to reclaim closed network connections ??
        // java.lang.System.gc();

	return true;
    }

    /**
     * Check if number of connections has exceeded high watermark
     */
    public void checkConnectionTable()
    {
	// note: Hashtable.size() is not synchronized
        if (connectionCache.size() > orb.getHighWaterMark())
            cleanUp();
    }

    // Need to worry about wrap around some day
    public synchronized void stampTime(Connection c)
    {
	// _REVISIT_ Need to worry about wrap around some day
        c.timeStamp = globalCounter++;
    }

    void destroyConnections()
    {
        // Shut down all connections
        Enumeration e = connectionCache.elements();
        while (e.hasMoreElements()) {
            Connection c = (Connection)e.nextElement();
            c.shutdown();
        }
    }

    public synchronized void print()
    {
        System.out.println("***ConnectionTable***");
	int siz = connectionCache.size();
        System.out.println("  SIZE=" + siz);
	if ( siz < 10 ) {
            Enumeration e = connectionCache.elements(); 
            while ( e.hasMoreElements() )
		{
		    Connection c = (Connection) e.nextElement();
		    c.print();
		}
	}
    }
 
}
