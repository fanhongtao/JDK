/*
 * @(#)VM_NONE.java	1.11 10/03/23
 *
 * Copyright (c) 2006, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.omg.CORBA;

/**
 * Defines the code used to represent the one of the values of a value type 
 * in a typecode.
 *
 * @author OMG
 * @see org.omg.CORBA.TypeCode
 * @version 1.11 03/23/10
 * @since   JDK1.2
 */

 
public interface VM_NONE {
    /**
     * The value representing the value type in a typecode as per 
     * CORBA 2.3 spec.
     */
    final short value = (short) (0L);
}
