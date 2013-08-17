/*
 * @(#)CTX_RESTRICT_SCOPE.java	1.2 00/01/12
 *
 * Copyright 1997-2000 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
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

