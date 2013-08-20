/*
 * @(#)UnknownHostException.java	1.13 03/12/19
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.rmi;

/**
 * An <code>UnknownHostException</code> is thrown if a
 * <code>java.net.UnknownHostException</code> occurs while creating
 * a connection to the remote host for a remote method call.
 * 
 * @version 1.13, 12/19/03
 * @since   JDK1.1
 */
public class UnknownHostException extends RemoteException {

    /* indicate compatibility with JDK 1.1.x version of class */
     private static final long serialVersionUID = -8152710247442114228L;

    /**
     * Constructs an <code>UnknownHostException</code> with the specified
     * detail message.
     *
     * @param s the detail message
     * @since JDK1.1
     */
    public UnknownHostException(String s) {
	super(s);
    }

    /**
     * Constructs an <code>UnknownHostException</code> with the specified
     * detail message and nested exception.
     *
     * @param s the detail message
     * @param ex the nested exception
     * @since JDK1.1
     */
    public UnknownHostException(String s, Exception ex) {
	super(s, ex);
    }
}
