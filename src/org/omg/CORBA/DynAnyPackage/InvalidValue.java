/*
 * @(#)InvalidValue.java	1.3 98/07/17
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
