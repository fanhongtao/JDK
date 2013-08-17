/*
 * @(#)InvalidSeq.java	1.5 01/11/29
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */


package org.omg.CORBA.DynAnyPackage;

/**
 * The InvalidSeq exception is thrown by all operations on dynamic
 * anys that take a sequence (Java array) as an argument, when that
 * sequence is invalid.
 */
public final class InvalidSeq
	extends org.omg.CORBA.UserException {
    public InvalidSeq() {
	super();
    }

    public InvalidSeq(String reason) {
	super(reason);
    }
}
