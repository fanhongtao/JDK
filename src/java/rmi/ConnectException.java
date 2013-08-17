/*
 * @(#)ConnectException.java	1.8 98/09/21
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
 * A <code>ConnectException</code> is thrown if a connection is refused
 * to the remote host for a remote method call.
 * 
 * @version 1.8, 09/21/98
 * @author  Ann Wollrath
 * @since   JDK1.1
 */
public class ConnectException extends RemoteException {

    /* indicate compatibility with JDK 1.1.x version of class */
     private static final long serialVersionUID = 4863550261346652506L;

    /**
     * Constructs a <code>ConnectException</code> with the specified
     * detail message.
     *
     * @param s the detail message
     * @since JDK1.1
     */
    public ConnectException(String s) {
	super(s);
    }

    /**
     * Constructs a <code>ConnectException</code> with the specified
     * detail message and nested exception.
     *
     * @param s the detail message
     * @param ex the nested exception
     * @since JDK1.1
     */
    public ConnectException(String s, Exception ex) {
	super(s, ex);
    }
}
