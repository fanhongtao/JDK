/*
 * @(#)ServerRuntimeException.java	1.6 01/12/10
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
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
