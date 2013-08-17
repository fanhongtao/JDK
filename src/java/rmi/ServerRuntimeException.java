/*
 * @(#)ServerRuntimeException.java	1.8 98/09/21
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
 * A <code>ServerRuntimeException</code> is thrown as a result of a remote
 * method call if the execution of the remote method on the server machine
 * throws a <code>java.lang.RuntimeException</code>.
 * A <code>ServerRuntimeException</code> is not thrown from servers executing
 * in JDK1.2 or later versions.
 * 
 * @version 1.8, 09/21/98
 * @author  Ann Wollrath
 * @since   JDK1.1
 * @deprecated no replacement
 */
public class ServerRuntimeException extends RemoteException {

    /* indicate compatibility with JDK 1.1.x version of class */
    private static final long serialVersionUID = 7054464920481467219L;

    /**
     * Constructs a <code>ServerRuntimeException</code> with the specified
     * detail message and nested exception.
     *
     * @param s the detail message
     * @param ex the nested exception
     * @deprecated no replacement
     * @since JDK1.1
     */
    public ServerRuntimeException(String s, Exception ex) {
	super(s, ex);
    }
}
