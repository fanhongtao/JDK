/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.omg.CORBA;

/**
 * Defines the code used to represent the one of the values of a value type 
 * in a typecode.
 *
 * @author OMG
 * @see org.omg.CORBA.TypeCode
 * @version 1.6 02/06/02
 * @since   JDK1.2
 */

 
public interface VM_NONE {
    /**
     * The value representing the value type in a typecode as per 
     * CORBA 2.3 spec.
     */
    final short value = (short) (0L);
}
