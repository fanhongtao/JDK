/*
 * @(#)ServerRuntimeException.java	1.5 98/08/12
 *
 * Copyright 1995-1998 by Sun Microsystems, Inc.,
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

public class ServerRuntimeException extends RemoteException {

    private static final long serialVersionUID = 7054464920481467219L;

    /**
     * Create a new runtime exception with the strings and
     * the specified exception.
     */
    public ServerRuntimeException(String s, Exception ex) {
	super(s, ex);
    }
}
