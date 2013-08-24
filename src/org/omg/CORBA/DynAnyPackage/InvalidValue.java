/*
 * @(#)InvalidValue.java	1.14 05/11/17
 *
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.omg.CORBA.DynAnyPackage;

/**
 * @author unattributed
 * @version 1.14 11/17/05
 *
 * Dynamic Any insert operations raise the <code>InvalidValue</code>
 * exception if the value inserted is not consistent with the type
 * of the accessed component in the <code>DynAny</code> object.
 */
public final class InvalidValue
    extends org.omg.CORBA.UserException {

    /**
     * Constructs an <code>InvalidValue</code> object.
     */
    public InvalidValue() {
	super();
    }

    /**
     * Constructs an <code>InvalidValue</code> object.
     * @param reason  a <code>String</code> giving more information
     * regarding the exception.
     */
    public InvalidValue(String reason) {
	super(reason);
    }
}
