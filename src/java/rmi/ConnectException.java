/*
 * @(#)ConnectException.java	1.6 01/12/10
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.rmi;

public class ConnectException extends RemoteException {

    private static final long serialVersionUID = 4863550261346652506L;

    /**
     * Create A remote exception with the specified string
     */
    public ConnectException(String s) {
	super(s);
    }

    /**
     * Create A remote exception with the specified string, and the
     * exception specified.
     */
    public ConnectException(String s, Exception ex) {
	super(s, ex);
    }
}
