/*
 * @(#)InvalidValue.java	1.4 01/11/29
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */


package org.omg.CORBA.DynAnyPackage;
public final class InvalidValue
	extends org.omg.CORBA.UserException {
    //	constructor
    public InvalidValue() {
	super();
    }

    public InvalidValue(String reason) {
	super(reason);
    }
}
