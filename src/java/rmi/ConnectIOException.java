/*
 * @(#)ConnectIOException.java	1.4 98/08/12
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
