/*
 * @(#)ExportException.java	1.2 00/01/12
 *
 * Copyright 1996-2000 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */

package java.rmi.server;

/**
 * An <code>ExportException</code> is a <code>RemoteException</code>
 * thrown if an attempt to export a remote object fails.  A remote object is
 * exported via the constructors and <code>exportObject</code> methods of
 * <code>java.rmi.server.UnicastRemoteObject</code> and
 * <code>java.rmi.activation.Activatable</code>.
 *
 * @version 1.7, 09/21/98
 * @author  Ann Wollrath
 * @since   JDK1.1
 * @see java.rmi.server.UnicastRemoteObject
 * @see java.rmi.activation.Activatable
 */
public class ExportException extends java.rmi.RemoteException {

    /* indicate compatibility with JDK 1.1.x version of class */
    private static final long serialVersionUID = -9155485338494060170L;

    /**
     * Constructs an <code>ExportException</code> with the specified
     * detail message.
     *
     * @param s the detail message
     * @since JDK1.1
     */
    public ExportException(String s) {
	super(s);
    }

    /**
     * Constructs an <code>ExportException</code> with the specified
     * detail message and nested exception.
     *
     * @param s the detail message
     * @param ex the nested exception
     * @since JDK1.1
     */
    public ExportException(String s, Exception ex) {
	super(s, ex);
    }

}
