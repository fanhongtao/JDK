/*
 * @(#)InvalidSeq.java	1.4 98/09/10
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
 * The InvalidSeq exception is thrown by all operations on dynamic
 * anys that take a sequence (Java array) as an argument, when that
 * sequence is invalid.
 */
public final class InvalidSeq
	extends org.omg.CORBA.UserException {
    public InvalidSeq() {
	super();
    }

    public InvalidSeq(String reason) {
	super(reason);
    }
}
