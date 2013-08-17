/*
 * @(#)InvalidValue.java	1.2 00/01/12
 *
 * Copyright 1998-2000 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
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
