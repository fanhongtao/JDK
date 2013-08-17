/*
 * @(#)TypeMismatch.java	1.2 00/01/12
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
    //	constructor
    public TypeMismatch() {
	super();
    }

    public TypeMismatch(String reason) {
	super(reason);
    }
}
