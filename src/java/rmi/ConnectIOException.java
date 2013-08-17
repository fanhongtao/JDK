/*
 * @(#)ConnectIOException.java	1.5 01/12/10
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.rmi;

public class ConnectIOException extends RemoteException {

    private static final long serialVersionUID = -8087809532704668744L;

    /**
     * Create A remote exception with the specified string
     */
    public ConnectIOException(String s) {
	super(s);
    }

    /**
     * Create A remote exception with the specified string, and the
     * exception specified.
     */
    public ConnectIOException(String s, Exception ex) {
	super(s, ex);
    }
}
