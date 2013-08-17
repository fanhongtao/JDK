/*
 * @(#)ServerError.java	1.5 01/12/10
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.rmi;

public class ServerError extends RemoteException {

    private static final long serialVersionUID = 8455284893909696482L;

    /**
     * Create a new "server error" exception with the string and
     * the specified exception.
     */
    public ServerError(String s, Error err) {
	super(s, err);
    }
}
