/*
 * @(#)Bounds.java	1.9 01/11/29
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
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
