/*
 * @(#)DynSequence.java	1.3 98/09/10
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
 *  with an IDL sequence.
 */

public interface DynSequence extends org.omg.CORBA.Object, org.omg.CORBA.DynAny
{
    /**
     * Return the length of the sequence represented in this
     * <code>DynFixed</code>.
     *
     * @return an integer length.
     */
    public int length();

    /**
     * Set the length of the sequence represented in this
     * <code>DynFixed</code>.
     *
     * @param arg the length.
     */
    public void length(int arg);

    /**
     * Return all the value of all elements in this sequence.
     *
     * @return an array of <code>Any</code>s.
     */
    public org.omg.CORBA.Any[] get_elements();

    /**
     * Set the values of all elements in this sequence.
     *
     * @param value the array of <code>Any</code>s.
     * @exception InvalidSeq is thrown if the array of values is bad.
     */
    public void set_elements(org.omg.CORBA.Any[] value)
        throws org.omg.CORBA.DynAnyPackage.InvalidSeq;
}
