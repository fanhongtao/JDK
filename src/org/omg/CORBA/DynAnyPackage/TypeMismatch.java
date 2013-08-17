/*
 * @(#)TypeMismatch.java	1.9 00/02/02
 *
 * Copyright 1998-2000 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */


package org.omg.CORBA.DynAnyPackage;

/**
 * TypeMismatch is thrown by dynamic any accessor methods when
 * type of the actual contents do not match what is trying to be
 * accessed.  
 */
public final class TypeMismatch
    extends org.omg.CORBA.UserException {

    /**
     * Constructs a <code>TypeMismatch</code> object.
     */
    public TypeMismatch() {
	super();
    }

    /**
     * Constructs a <code>TypeMismatch</code> object.
     * @param reason  a <code>String</code> giving more information
     * regarding the exception.
     */
    public TypeMismatch(String reason) {
	super(reason);
    }
}
