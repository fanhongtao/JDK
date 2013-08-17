/*
 * @(#)InvalidSeq.java	1.9 00/02/02
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

    /**
     * Constructs an <code>InvalidSeq</code> object.
     */
    public InvalidSeq() {
	super();
    }

    /**
     * Constructs an <code>InvalidSeq</code> object.
     * @param reason  a <code>String</code> giving more information
     * regarding the exception.
     */
    public InvalidSeq(String reason) {
	super(reason);
    }
}
