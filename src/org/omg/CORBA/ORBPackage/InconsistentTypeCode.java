/*
 * @(#)InconsistentTypeCode.java	1.5 98/10/11
 *
 * Copyright 1998 by Sun Microsystems, Inc.,
 * 901 San Antonio Road, Palo Alto, California, 94303, U.S.A.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
 */


package org.omg.CORBA.ORBPackage;

/**
 * InconsistentTypeCode is thrown when an attempt is made to create a
 * dynamic any with a type code that does not match the particular
 * subclass of <code>DynAny</code>.
 */
public final class InconsistentTypeCode
	extends org.omg.CORBA.UserException {
    //	constructor
    public InconsistentTypeCode() {
	super();
    }

    public InconsistentTypeCode(String reason) {
	super(reason);
    }
}
