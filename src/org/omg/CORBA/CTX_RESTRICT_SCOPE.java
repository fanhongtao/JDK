/*
 * @(#)CTX_RESTRICT_SCOPE.java	1.6 98/06/29
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
 * A flag that can be used as the second parameter to the method
 * <code>Context.get_values</code> to restrict the search scope.
 * When this flag is used, it restricts the search for
 * context values to this particular <code>Context</code> object
 * or to the scope specified in the first parameter to
 * <code>Context.get_values</code>.
 * <P>
 * Usage:
 * <PRE>
 *     NVList props = myContext.get_values("_USER",
 *                     CTX_RESTRICT_SCOPE.value, "id*");
 * </PRE>
 *
 * @see org.omg.CORBA.Context#get_values(String, int, String)
 * @version 1.3, 09/09/97
 * @since   JDK1.2
 */
public interface CTX_RESTRICT_SCOPE {
  int value = 15;
}

