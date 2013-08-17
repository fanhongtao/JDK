/*
 * @(#)NotBoundException.java	1.5 01/12/10
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package java.rmi;

public
class NotBoundException extends java.lang.Exception {

    private static final long serialVersionUID = -1857741824849069317L;

    public NotBoundException() {
	super();
    }

    public NotBoundException(String s) {
	super(s);
    }
}
