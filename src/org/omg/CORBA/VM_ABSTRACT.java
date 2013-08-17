/*
 * @(#)VM_ABSTRACT.java	1.6 01/12/03
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.omg.CORBA;

/** Defines the code used to represent an Abstract interface in  
* a typecode.
* This is one of the possible results of the <code>type_modified</code>
* method on the <code>TypeCode</code> interface.
* @see org.omg.CORBA.TypeCode
* @version 1.6 12/03/01
*/
public interface VM_ABSTRACT {
    /** The value representing an abstract interface value type in
    * a typecode.
    */
    final short value = (short) (2L);
}
