/*
 * @(#)VM_NONE.java	1.5 00/02/02
 *
 * Copyright 1995-2000 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */

package org.omg.CORBA;

/**
 * Defines the code used to represent the one of the values of a value type 
 * in a typecode.
 *
 * @author OMG
 * @see org.omg.CORBA.TypeCode
 * @version 1.5 02/02/00
 * @since   JDK1.2
 */

 
public interface VM_NONE {
    /**
     * The value representing the value type in a typecode as per 
     * CORBA 2.3 spec.
     */
    final short value = (short) (0L);
}
