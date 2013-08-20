/*
 * @(#)VM_NONE.java	1.9 03/12/19
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.omg.CORBA;

/**
 * Defines the code used to represent the one of the values of a value type 
 * in a typecode.
 *
 * @author OMG
 * @see org.omg.CORBA.TypeCode
 * @version 1.9 12/19/03
 * @since   JDK1.2
 */

 
public interface VM_NONE {
    /**
     * The value representing the value type in a typecode as per 
     * CORBA 2.3 spec.
     */
    final short value = (short) (0L);
}
