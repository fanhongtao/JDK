/*
 * @(#)AlreadyBoundException.java	1.5 01/12/10
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package java.rmi;

public
class AlreadyBoundException extends java.lang.Exception {

    private static final long serialVersionUID = 9218657361741657110L;

    public AlreadyBoundException() {
	super();
    }

    public AlreadyBoundException(String s) {
	super(s);
    }
}
