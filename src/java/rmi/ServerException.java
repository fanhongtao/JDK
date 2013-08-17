/*
 * @(#)ServerException.java	1.7 98/09/21
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
 * A <code>ServerException</code> is thrown as a result of a remote method call
 * if the execution of the remote method on the server machine throws a
 * <code>RemoteException</code>.
 * 
 * @version 1.7, 09/21/98
 * @author  Ann Wollrath
 * @since   JDK1.1
 */
public class ServerException extends RemoteException {

    /* indicate compatibility with JDK 1.1.x version of class */
    private static final long serialVersionUID = -4775845313121906682L;

    /**
     * Constructs a <code>ServerException</code> with the specified
     * detail message.
     *
     * @param s the detail message
     * @since JDK1.1
     */
    public ServerException(String s) {
	super(s);
    }

    /**
     * Constructs a <code>ServerException</code> with the specified
     * detail message and nested exception.
     *
     * @param s the detail message
     * @param ex the nested exception
     * @since JDK1.1
     */
    public ServerException(String s, Exception ex) {
	super(s, ex);
    }
}
