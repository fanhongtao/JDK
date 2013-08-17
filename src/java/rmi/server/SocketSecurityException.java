/*
 * @(#)SocketSecurityException.java	1.7 98/09/21
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
 * A <code>SocketSecurityException</code> is thrown during remote object
 * export if the code exporting the remote object (either by construction
 * or by explicit call to the <code>exportObject</code> method of
 * <code>UnicastRemoteObject</code> or
 * <code>java.rmi.activation.Activatable</code>) does not have permission
 * to create a <code>java.net.ServerSocket</code> on the port number
 * specified during remote object export.
 *
 * @version 1.7, 09/21/98
 * @author  Ann Wollrath
 * @since   JDK1.1
 * @see     java.rmi.server.UnicastRemoteObject
 * @see     java.rmi.activation.Activatable
 */
public class SocketSecurityException extends ExportException {

    /* indicate compatibility with JDK 1.1.x version of class */
    private static final long serialVersionUID = -7622072999407781979L;

    /**
     * Constructs an <code>SocketSecurityException</code> with the specified
     * detail message.
     *
     * @param s the detail message.
     * @since JDK1.1
     */
    public SocketSecurityException(String s) {
	super(s);
    }

    /**
     * Constructs an <code>SocketSecurityException</code> with the specified
     * detail message and nested exception.
     *
     * @param s the detail message.
     * @param ex the nested exception
     * @since JDK1.1
     */
    public SocketSecurityException(String s, Exception ex) {
	super(s, ex);
    }

}
