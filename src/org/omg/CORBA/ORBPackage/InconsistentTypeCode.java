/*
 * @(#)InconsistentTypeCode.java	1.16 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */


package org.omg.CORBA.ORBPackage;

/**
 * InconsistentTypeCode is thrown when an attempt is made to create a
 * dynamic any with a type code that does not match the particular
 * subclass of <code>DynAny</code>.
 */
public final class InconsistentTypeCode
    extends org.omg.CORBA.UserException {
    /**
     * Constructs an <code>InconsistentTypeCode</code> user exception
     * with no reason message.
    */
    public InconsistentTypeCode() {
	super();
    }

    /**
    * Constructs an <code>InconsistentTypeCode</code> user exception
    * with the specified reason message.
    * @param reason The String containing a reason message
    */
    public InconsistentTypeCode(String reason) {
	super(reason);
    }
}
