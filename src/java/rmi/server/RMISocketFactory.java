/*
 * @(#)RMISocketFactory.java	1.6 98/07/01
 *
 * Copyright 1995-1998 by Sun Microsystems, Inc.,
 * 901 San Antonio Road, Palo Alto, California, 94303, U.S.A.
 * All rights reserved.
 * 
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
 */

package java.rmi.server;

import java.io.*;
import java.net.*;

/**
 * The RMISocketFactory is used by the RMI runtime in order to obtain
 * client and server sockets for RMI calls. The default implementation
 * of the socket factory performs a three-tiered approach to creating
 * client sockets. First, a direct socket connection to the remote VM
 * is attempted.  If that fails (due to a firewall), the runtime uses
 * HTTP with the explicit port number of the server.  If the firewall
 * does not allow this type of communication, then HTTP to a cgi-bin
 * script on the server is used to POST the RMI call.
 *
 * An application may set the source of sockets for RMI. In this case,
 * the application is responsible for offering up sockets that will
 * penetrate a firewall.
 */
public abstract class RMISocketFactory {

    /** Client/server socket factory used by RMI */
    private static RMISocketFactory factory = null;
    /** Handler for socket creation failure */
    private static RMIFailureHandler handler = null;

    /**
     * Create a client socket connected to the specified host and port.
     */
    public abstract Socket createSocket(String host, int port)
	throws IOException;

    /**
     * Create a server socket on the specified port (port 0 represents
     * an anonymous port).
     */
    public abstract ServerSocket createServerSocket(int port)
	throws IOException;

    /**
     * Set the socket factory from which RMI gets sockets. The RMI
     * socket factory can only be set once. Note: The RMISocketFactory
     * may only be set if the current security manager allows setting
     * a socket factory; if disallowed, a SecurityException will be
     * thrown.
     */
    public static void setSocketFactory(RMISocketFactory fac)
	throws IOException
    {
    	if (factory != null) {
	    throw new SocketException("factory already defined");
	}
	SecurityManager security = System.getSecurityManager();
	if (security != null) {
	    security.checkSetFactory();
	}
	factory = fac;
    }

    /**
     * Returns the socket factory used by RMI.
     */
    public static RMISocketFactory getSocketFactory() 
    {
	return factory;
    }

    /**
     * Set the failure handler to be called by the RMI runtime if
     * socket creation fails.  The default implementation of this
     * handler returns false (thus recreation of sockets is not
     * attempted by the runtime).
     */
    public static void setFailureHandler(RMIFailureHandler fh) 
    {
	handler = fh;
    }

    /**
     * Returns the handler for socket creation failure.
     */
    public static RMIFailureHandler getFailureHandler()
    {
	return handler;
    }
}

    
	
