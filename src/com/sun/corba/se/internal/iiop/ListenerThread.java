/*
 * @(#)ListenerThread.java	1.40 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.corba.se.internal.iiop;

import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.ServerSocket;
import java.io.IOException;

public class ListenerThread extends Thread 
{
    protected static final int MAX_CLEANUP_RETRIES=5;
    protected ServerSocket serverSocket;
    protected ConnectionTable connectionTable;
    protected String socketType;
    private boolean keepRunning = true;

    ListenerThread(ConnectionTable connectionTable,
		   ThreadGroup g,
		   ServerSocket serverSocket,
		   String socketType) 
    {
	super(g, "JavaIDL Listener");
	this.serverSocket = serverSocket;
	this.connectionTable = connectionTable;
	this.socketType = socketType;
    }

    public ListenerThread(ConnectionTable connectionTable, 
			  ServerSocket serverSocket,
			  String socketType)
    {
	super("JavaIDL Listener");
	this.serverSocket = serverSocket;
	this.connectionTable = connectionTable;
	this.socketType = socketType;
    }

    public ServerSocket getSocket()
    {
	return serverSocket;
    }

    public void run() 
    {
        int tries = 0;

	// Loop forever listening for incoming connections.
        while (keepRunning) {
	    try {
		// Accept an incoming connection request
	        Socket socket = serverSocket.accept();

		// set socket to disable Nagle's algorithm (always send immediately)
		try {
		    socket.setTcpNoDelay(true);
		} catch (Exception e) {
		}

		// Create a Connection instance and process incoming request
		Connection conn = connectionTable.getConnection(socket, 
								socketType);

                tries = 0;

		// Check if #connections > HighWaterMark, if so cleanup.
		// This prevents incoming connections from being rejected
		// because of TCP/IP or OS network connection limits.
                connectionTable.checkConnectionTable();
            }
            catch (SocketException ex) {
		// An exception was thrown by the accept() call.
		// This probably means that we have run out of network 
		// connections. 

                if (tries == MAX_CLEANUP_RETRIES) {
		    // We tried closing existing connections but that didnt help
		    // So it must be some unknown networking problem.
                    continue;
		}

		// Clean up some existing connections.
                if (!connectionTable.cleanUp()) {
		    // Could not close any connections :-(
                    continue;
		}

                tries++;
            }
            catch (Exception ex) { }
	}
    }

    synchronized void shutdown() {
        keepRunning = false;
        try {
            serverSocket.close();
        } catch (IOException ioex) {}
    }
}


