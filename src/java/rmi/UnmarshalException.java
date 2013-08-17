/*
 * @(#)UnmarshalException.java	1.7 98/09/21
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
 * An <code>UnmarshalException</code> can be thrown while unmarshalling the
 * parameters or results of a remote method call if any of the following
 * conditions occur:
 * <ul>
 * <li> if an exception occurs while unmarshalling the call header
 * <li> if the protocol for the return value is invalid
 * <li> if a <code>java.io.IOException</code> occurs unmarshalling
 * parameters (on the server side) or the return value (on the client side).
 * <li> if a <code>java.lang.ClassNotFoundException</code> occurs during
 * unmarshalling parameters or return values
 * <li> if no skeleton can be loaded on the server-side; note that skeletons
 * are required in the 1.1 stub protocol, but not in the 1.2 stub protocol.
 * <li> if the method hash is invalid (i.e., missing method).
 * <li> if there is a failure to create a remote reference object for
 * a remote object's stub when it is unmarshalled.
 * </ul>
 * 
 * @version 1.7, 09/21/98
 * @author  Ann Wollrath
 * @since   JDK1.1
 */
public class UnmarshalException extends RemoteException {

    /* indicate compatibility with JDK 1.1.x version of class */
    private static final long serialVersionUID = 594380845140740218L;

    /**
     * Constructs an <code>UnmarshalException</code> with the specified
     * detail message.
     *
     * @param s the detail message
     * @since JDK1.1
     */
    public UnmarshalException(String s) {
	super(s);
    }

    /**
     * Constructs an <code>UnmarshalException</code> with the specified
     * detail message and nested exception.
     *
     * @param s the detail message
     * @param ex the nested exception
     * @since JDK1.1
     */
    public UnmarshalException(String s, Exception ex) {
	super(s, ex);
    }
}
