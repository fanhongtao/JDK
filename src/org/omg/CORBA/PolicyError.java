/*
 * @(#)PolicyError.java	1.3 98/10/11
 *
 * Copyright 1995-1998 by Sun Microsystems, Inc.,
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

public final class PolicyError extends org.omg.CORBA.UserException {

/** 
 * The reason for the <code>PolicyError</code> exception being thrown.
 * @serial
 */
    public short reason;

/**
 * Constructs a default <code>PolicyError</code> user exception.
 */
    public PolicyError() {
	super();
    }


/**
 * Constructs a <code>PolicyError</code> user exception initialized
 * with the given reason code.
 */
    public PolicyError(short __reason) {
	super();
	reason = __reason;
    }


/**
 * Constructs a <code>PolicyError</code> user exception initialized
 * with the given reason string and reason code.
 */
    public PolicyError(String reason_string, short __reason) {
	super(reason_string);
	reason = __reason;
    }
}
