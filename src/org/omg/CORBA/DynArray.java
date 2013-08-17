/*
 * @(#)DynArray.java	1.3 98/09/10
 *
 * Copyright 1998 by Sun Microsystems, Inc.,
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


/** The DynArray interface represents a DynAny object which is associated
 *  with an array.
 */

public interface DynArray extends org.omg.CORBA.Object, org.omg.CORBA.DynAny
{
    /**
     * Return the value of all the elements of the array.
     *
     * @return an array of <code>Any</code>s.
     */
    public org.omg.CORBA.Any[] get_elements();

    /**
     * Set the values of all elements of an array represented by this
     * <code>DynArray</code>.
     *
     * @param value the array of <code>Any</code>s.
     * @exception InvalidSeq if the sequence is bad.
     */
    public void set_elements(org.omg.CORBA.Any[] value)
        throws org.omg.CORBA.DynAnyPackage.InvalidSeq;
}
