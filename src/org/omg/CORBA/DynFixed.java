/*
 * @(#)DynFixed.java	1.4 01/11/29
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */


package org.omg.CORBA;

/** The DynFixed interface represents a DynAny object which is associated
 *  with an IDL fixed type.
 */

public interface DynFixed extends org.omg.CORBA.Object, org.omg.CORBA.DynAny
{
    /**
     * Return the value of the fixed type represented in the
     * <code>DynFixed</code.
     *
     * @return the value as a byte array.
     */
    public byte[] get_value();

    /**
     * Set the value of the fixed type instance into this
     * <code>DynFixed</code>.
     *
     * @param val the value as a byte array.
     */
    public void set_value(byte[] val)
        throws org.omg.CORBA.DynAnyPackage.InvalidValue;
}
