/*
 * @(#)InvalidName.java	1.10 98/08/20
 *
 * Copyright 1997, 1998 by Sun Microsystems, Inc.,
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
 * The <code>InvalidName</code> exception is raised when
 * <code>ORB.resolve_initial_references</code> is passed a name
 * for which there is no initial reference.
 *
 * @see org.omg.CORBA.ORB#resolve_initial_references(String)
 * @version 1.6, 03/18/98
 * @since   JDK1.2
 */

public class InvalidName extends org.omg.CORBA.UserException {
    /**
     * Constructs an <code>InvalidName</code> exception with no reason message.
     */
    public InvalidName() {
	super();
    }

    /**
     * Constructs an <code>InvalidName</code> exception with the specified 
     * reason message.
     * @param reason the String containing a reason message
     */
    public InvalidName(String reason) {
	super(reason);
    }
}
