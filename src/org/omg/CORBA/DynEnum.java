/*
 * @(#)DynEnum.java	1.10 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */


package org.omg.CORBA;

/** Represents a <tt>DynAny</tt> object  associated
 *  with an IDL enum.
 */

public interface DynEnum extends org.omg.CORBA.Object, org.omg.CORBA.DynAny
{
    /**
     * Return the value of the IDL enum stored in this
     * <code>DynEnum</code> as a string.
     *
     * @return the stringified value.
     */
    public String value_as_string();

    /**
     * Set a particular enum in this <code>DynEnum</code>.
     *
     * @param arg the string corresponding to the value.
     */
    public void value_as_string(String arg);

    /**
     * Return the value of the IDL enum as a Java int.
     *
     * @return the integer value.
     */
    public int value_as_ulong();

    /**
     * Set the value of the IDL enum.
     *
     * @param arg the int value of the enum.
     */
    public void value_as_ulong(int arg);
}
