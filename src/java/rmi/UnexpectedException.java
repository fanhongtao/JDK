/*
 * @(#)UnexpectedException.java	1.5 01/12/10
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.rmi;

public class UnexpectedException extends RemoteException {

    private static final long serialVersionUID = 1800467484195073863L;

    /**
     * Create a new UnexpectedException with a description.
     */
    public UnexpectedException(String s) {
	super(s);
    }

    /**
     * Create a new UnexpectedException with a description, and detail
     * exception.
     */
    public UnexpectedException(String s, Exception ex) {
	super(s, ex);
    }
}
