/*
 * @(#)UnknownHostException.java	1.6 01/12/10
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.rmi;

public class UnknownHostException extends RemoteException {

    private static final long serialVersionUID = -8152710247442114228L;

    /**
     * Create a new UnknownHostException with a description.
     */
    public UnknownHostException(String s) {
	super(s);
    }

    /**
     * Create a new UnknownHostException with a description and
     * detail exception.
     */
    public UnknownHostException(String s, Exception ex) {
	super(s, ex);
    }
}
