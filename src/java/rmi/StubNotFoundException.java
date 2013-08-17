/*
 * @(#)StubNotFoundException.java	1.7 98/09/21
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

package java.rmi;

/**
 * A <code>StubNotFoundException</code> is thrown if a valid stub class
 * could not be found for a remote object when it is exported or when
 * a remote object is passed in a remote method call as a parameter or
 * return value.  A <code>StubNotFoundException</code> may also be
 * thrown when an activatable object is registered via the
 * <code>java.rmi.activation.Activatable.register</code> method.
 * 
 * @version 1.7, 09/21/98
 * @author  Roger Riggs
 * @since   JDK1.1
 * @see	    java.rmi.server.UnicastRemoteObject
 * @see     java.rmi.activation.Activatable
 */
public class StubNotFoundException extends RemoteException {

    /* indicate compatibility with JDK 1.1.x version of class */
    private static final long serialVersionUID = -7088199405468872373L;

    /**
     * Constructs a <code>StubNotFoundException</code> with the specified
     * detail message and nested exception.
     *
     * @param s the detail message
     * @param ex the nested exception
     * @since JDK1.1
     */
    public StubNotFoundException(String s) {
	super(s);
    }

    /**
     * Constructs a <code>StubNotFoundException</code> with the specified
     * detail message and nested exception.
     *
     * @param s the detail message
     * @param ex the nested exception
     * @since JDK1.1
     */
    public StubNotFoundException(String s, Exception ex) {
	super(s, ex);
    }
}
