/*
 * @(#)NoSuchObjectException.java	1.5 01/12/10
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.rmi;

public
class NoSuchObjectException extends java.rmi.RemoteException {

    private static final long serialVersionUID = 6619395951570472985L;

    /**
     * Create a new NoSuchObjectException with a description.
     */
    public NoSuchObjectException(String s) {
	super(s);
    }
}
