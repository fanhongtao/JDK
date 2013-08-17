/*
 * @(#)ServerNotActiveException.java	1.8 98/09/21
 *
 * Copyright 1996-1998 by Sun Microsystems, Inc.,
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

/**
 * An <code>ServerNotActiveException</code> is an <code>Exception</code>
 * thrown during a call to <code>RemoteServer.getClientHost</code> if
 * the getClientHost method is called outside of servicing a remote
 * method call.
 *
 * @version 1.8, 09/21/98
 * @author  Roger Riggs
 * @since   JDK1.1
 * @see java.rmi.server.RemoteServer#getClientHost()
 */
public class ServerNotActiveException extends java.lang.Exception {

    /* indicate compatibility with JDK 1.1.x version of class */
    private static final long serialVersionUID = 4687940720827538231L;

    /**
     * Constructs an <code>ServerNotActiveException</code> with no specified
     * detail message.
     * @since JDK1.1
     */
    public ServerNotActiveException() {}

    /**
     * Constructs an <code>ServerNotActiveException</code> with the specified
     * detail message.
     *
     * @param s the detail message.
     * @since JDK1.1
     */
    public ServerNotActiveException(String s) 
    {
	super(s);
    }
}
