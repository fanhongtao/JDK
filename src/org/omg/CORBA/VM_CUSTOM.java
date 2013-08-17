/*
 * @(#)VM_CUSTOM.java	1.5 00/02/02
 *
 * Copyright 1995-2000 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */

package org.omg.CORBA;

/** Defines the code used to represent a custom marshalled value type in
* a typecode.
* This is one of the possible results of the <code>type_modifier</code>
* method on the <code>TypeCode</code> interface.
* @see org.omg.CORBA.TypeCode
* @version 1.5 02/02/00
*/
public interface VM_CUSTOM {
    /** The value representing a custom marshalled value type in
    * a typecode.
    */
    final short value = (short) (1L);
}
