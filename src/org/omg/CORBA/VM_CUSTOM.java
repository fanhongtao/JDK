/*
 * @(#)VM_CUSTOM.java	1.8 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.omg.CORBA;

/** Defines the code used to represent a custom marshalled value type in
* a typecode.
* This is one of the possible results of the <code>type_modifier</code>
* method on the <code>TypeCode</code> interface.
* @see org.omg.CORBA.TypeCode
* @version 1.8 01/23/03
*/
public interface VM_CUSTOM {
    /** The value representing a custom marshalled value type in
    * a typecode.
    */
    final short value = (short) (1L);
}
