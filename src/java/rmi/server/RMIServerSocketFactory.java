/*
 * @(#)RMIServerSocketFactory.java	1.3 98/07/12
 *
 * Copyright 1998 by Sun Microsystems, Inc.,
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
 * An <code>RMIServerSocketFactory</code> instance is used by the RMI runtime
 * in order to obtain server sockets for RMI calls.  A remote object can be
 * associated with an <code>RMIServerSocketFactory</code> when it is
 * created/exported via the constructors or <code>exportObject</code> methods
 * of <code>java.rmi.server.UnicastRemoteObject</code> and
 * <code>java.rmi.activation.Activatable</code> .
 *
 * <p>An <code>RMIServerSocketFactory</code> instance associated with a remote
 * object is used to obtain the <code>ServerSocket</code> used to accept
 * incoming calls from clients.
 *
 * <p>An <code>RMIServerSocketFactory</code> instance can also be associated
 * with a remote object registry so that clients can use custom socket
 * communication with a remote object registry.
 *
 * @version 1.3, 07/12/98
 * @author  Ann Wollrath
 * @author  Peter Jones
 * @since   JDK1.2
 * @see     java.rmi.server.UnicastRemoteObject
 * @see     java.rmi.activation.Activatable
 * @see     java.rmi.registry.LocateRegistry
 */
public interface RMIServerSocketFactory {

    /**
     * Create a server socket on the specified port (port 0 indicates
     * an anonymous port).
     * @param  port the port number
     * @return the server socket on the specified port
     * @exception IOException if an I/O error occurs during server socket
     * creation
     * @since JDK1.2
     */
    public ServerSocket createServerSocket(int port)
	throws IOException;
}
