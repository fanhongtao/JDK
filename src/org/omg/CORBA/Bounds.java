/*
 * @(#)Bounds.java	1.8 98/09/10
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

/**
 * A user exception thrown when a parameter is not within
 * the legal bounds for the object that a method is trying
 * to access.
 *
 * @see <A href="../guide/idl/jidlExceptions.html">documentation on
 * Java&nbsp;IDL exceptions</A>
 */

package org.omg.CORBA;

public final class Bounds extends org.omg.CORBA.UserException {

    /**
     * Constructs an <code>Bounds</code> with no specified detail message. 
     */
    public Bounds() {
	super();
    }

    /**
     * Constructs an <code>Bounds</code> with the specified detail message. 
     *
     * @param   reason   the detail message.
     */
    public Bounds(String reason) {
	super(reason);
    }
}
