/*
 * @(#)ARG_IN.java	1.17 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.omg.CORBA;

/**
 * Signifies an "input" argument to an invocation,
 * meaning that the argument is being passed from the client to
 * the server.
 * <code>ARG_IN.value</code> is one of the possible values used to
 * indicate the direction in
 * which a parameter is being passed during an invocation performed
 * using the Dynamic Invocation Interface (DII).
 * <P>
 * The code fragment below shows a typical usage:
 * <PRE>
 *    ORB orb = ORB.init(args, null);
 *    org.omg.CORBA.NamedValue nv = orb.create_named_value(
 *         "IDLArgumentIdentifier", myAny, org.omg.CORBA.ARG_IN.value);
 * </PRE>
 *
 * @version 1.5, 09/09/97
 * @see     org.omg.CORBA.NamedValue
 * @since   JDK1.2
 */
public interface ARG_IN {

    /**
     * The value indicating an input argument.
     */
    int value = 1;
}

