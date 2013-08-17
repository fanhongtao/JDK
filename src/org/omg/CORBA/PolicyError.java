/*
 * @(#)PolicyError.java	1.6 99/04/22
 *
 * Copyright 1995-1999 by Sun Microsystems, Inc.,
 * 901 San Antonio Road, Palo Alto, California, 94303, U.S.A.
 * All rights reserved.
 * 
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
 */

package org.omg.CORBA;

/**
* An exception carrying one of the following policy error reason codes
* defined in the org.omg.CORBA package: 
* BAD_POLICY, BAD_POLICY_TYPE, BAD_POLICY_VALUE, UNSUPPORTED_POLICY, UNSUPPORTED_POLICY_VALUE
*/

public final class PolicyError extends org.omg.CORBA.UserException {

/** 
 * The reason for the <code>PolicyError</code> exception being thrown.
 * @serial
 */
    public short reason;

    /**
    * Constructs a default <code>PolicyError</code> user exception
    * with no reason code and an empty reason detail message.
    */
    public PolicyError() {
	super();
    }

    /**
    * Constructs a <code>PolicyError</code> user exception
    * initialized with the given reason code and an empty reason detail message.
    * @param __reason the reason code.
    */
    public PolicyError(short __reason) {
	super();
	reason = __reason;
    }

    /**
    * Constructs a <code>PolicyError</code> user exception
    * initialized with the given reason detail message and reason code.
    * @param reason_string the reason detail message.
    * @param __reason the reason code.
    */
    public PolicyError(String reason_string, short __reason) {
	super(reason_string);
	reason = __reason;
    }
}
