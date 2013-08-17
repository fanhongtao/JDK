/*
 * @(#)DynSequence.java	1.4 01/11/29
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
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
