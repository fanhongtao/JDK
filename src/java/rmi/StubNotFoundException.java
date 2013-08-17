/*
 * @(#)StubNotFoundException.java	1.5 01/12/10
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.rmi;

public class StubNotFoundException extends RemoteException {

    private static final long serialVersionUID = -7088199405468872373L;

    /**
     * Create a new StubNotFoundException with a descriptive string.
     */
    public StubNotFoundException(String s) {
	super(s);
    }

    /**
     * Create a new StubNotFoundException with a descriptive string and an exception.
     */
    public StubNotFoundException(String s, Exception ex) {
	super(s, ex);
    }
}
