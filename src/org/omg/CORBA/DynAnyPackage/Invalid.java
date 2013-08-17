/*
 * @(#)Invalid.java	1.9 00/02/02
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
