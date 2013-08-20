/*
 * @(#)Invalid.java	1.13 03/12/19
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */


package org.omg.CORBA.DynAnyPackage;

/**
 * Invalid is thrown by dynamic any operations when a bad
 * <code>DynAny</code> or <code>Any</code> is passed as a parameter. 
 */
public final class Invalid
    extends org.omg.CORBA.UserException {

    /**
     * Constructs an <code>Invalid</code> object.
     */
    public Invalid() {
	super();
    }

    /**
     * Constructs an <code>Invalid</code> object.
     * @param reason a <code>String</code> giving more information
     * regarding the bad parameter passed to a dynamic any operation.
     */
    public Invalid(String reason) {
	super(reason);
    }
}
