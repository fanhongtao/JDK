/*
 * @(#)Invalid.java	1.5 01/11/29
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
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
