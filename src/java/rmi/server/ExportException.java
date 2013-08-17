/*
 * @(#)ExportException.java	1.5 01/12/10
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.rmi.server;

public class ExportException extends java.rmi.RemoteException {

    private static final long serialVersionUID = -9155485338494060170L;

    /**
     * Create A remote exception with the specified string
     */
    public ExportException(String s) {
	super(s);
    }

    /**
     * Create A remote exception with the specified string, and the
     * exception specified.
     */
    public ExportException(String s, Exception ex) {
	super(s, ex);
    }

}
