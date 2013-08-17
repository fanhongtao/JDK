/*
 * @(#)Invalid.java	1.2 00/01/12
 *
 * Copyright 1998-2000 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */


package org.omg.CORBA.DynAnyPackage;

/**
 * Invalid is thrown by dynamic any operations when a bad
 * <code>DynAny</code> or <code>Any</code> is passed as a parameter. 
 */
public final class Invalid
	extends org.omg.CORBA.UserException {
    //	constructor
    public Invalid() {
	super();
    }

    public Invalid(String reason) {
	super(reason);
    }
}
