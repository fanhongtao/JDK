/*
 * @(#)TypeMismatch.java	1.4 98/09/10
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

/**
 * TypeMismatch is thrown by dynamic any accessor methods when
 * type of the actual contents do not match what is trying to be
 * accessed.  
 */
public final class TypeMismatch
	extends org.omg.CORBA.UserException {
    //	constructor
    public TypeMismatch() {
	super();
    }

    public TypeMismatch(String reason) {
	super(reason);
    }
}
