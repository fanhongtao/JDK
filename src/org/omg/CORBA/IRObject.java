/*
 * @(#)IRObject.java	1.24 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
/*
 * File: ./org/omg/CORBA/IRObject.java
 * From: ./ir.idl
 * Date: Fri Aug 28 16:03:31 1998
 *   By: idltojava Java IDL 1.2 Aug 11 1998 02:00:18
 */

package org.omg.CORBA;
/**
An IRObject IDL interface represents the most generic interface
from which all other Interface Repository interfaces are derived,
even the Repository itself.
*/

public interface IRObject extends IRObjectOperations, org.omg.CORBA.Object, 
    org.omg.CORBA.portable.IDLEntity
{
}
