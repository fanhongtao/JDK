/*
 * @(#)ARG_IN.java	1.10 98/08/31
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

