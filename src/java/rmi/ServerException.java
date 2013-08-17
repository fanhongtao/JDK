/*
 * @(#)ServerException.java	1.5 01/12/10
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.rmi;

public class ServerException extends RemoteException {

    private static final long serialVersionUID = -4775845313121906682L;

    /**
     * Create a new server exception with a descriptive string.
     */
    public ServerException(String s) {
	super(s);
    }

    /**
     * Create a new server exception with a descriptive string and an
     * exception.
     */
    public ServerException(String s, Exception ex) {
	super(s, ex);
    }
}
