/*
 * @(#)AccessException.java	1.5 01/12/10
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.rmi;

public class AccessException
	extends java.rmi.RemoteException {

    private static final long serialVersionUID = 6314925228044966088L;

    /**
     * Create a new AccessException with a description.
     */
    public AccessException(String s) {
	super(s);
    }

    /**
     * Create a new AccessException with a description and detail exception.
     */
    public AccessException(String s, Exception ex) {
	super(s, ex);
    }
}
