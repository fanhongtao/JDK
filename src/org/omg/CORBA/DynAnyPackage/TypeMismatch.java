/*
 * @(#)TypeMismatch.java	1.5 01/11/29
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
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
