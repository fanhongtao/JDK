/*
 * @(#)ConnectIOException.java	1.7 98/09/21
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
 * A <code>ConnectIOException</code> is thrown if an
 * <code>IOException</code> occurs while making a connection
 * to the remote host for a remote method call.
 * 
 * @version 1.7, 09/21/98
 * @author  Ann Wollrath
 * @since   JDK1.1
 */
public class ConnectIOException extends RemoteException {

    /* indicate compatibility with JDK 1.1.x version of class */
    private static final long serialVersionUID = -8087809532704668744L;

    /**
     * Constructs a <code>ConnectIOException</code> with the specified
     * detail message.
     *
     * @param s the detail message
     * @since JDK1.1
     */
    public ConnectIOException(String s) {
	super(s);
    }


    /**
     * Constructs a <code>ConnectIOException</code> with the specified
     * detail message and nested exception.
     *
     * @param s the detail message
     * @param ex the nested exception
     * @since JDK1.1
     */
    public ConnectIOException(String s, Exception ex) {
	super(s, ex);
    }
}
