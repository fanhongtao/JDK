/*
 * @(#)InvalidSeq.java	1.2 00/01/12
 *
 * Copyright 1998-2000 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
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
