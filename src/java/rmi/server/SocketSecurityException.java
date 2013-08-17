/*
 * @(#)SocketSecurityException.java	1.5 01/12/10
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.rmi.server;

public class SocketSecurityException extends ExportException {

    private static final long serialVersionUID = -7622072999407781979L;

    /**
     * Create A remote exception with the specified string
     */
    public SocketSecurityException(String s) {
	super(s);
    }

    /**
     * Create A remote exception with the specified string, and the
     * exception specified.
     */
    public SocketSecurityException(String s, Exception ex) {
	super(s, ex);
    }

}
