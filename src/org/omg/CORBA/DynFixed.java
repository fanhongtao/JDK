/*
 * @(#)DynFixed.java	1.2 00/01/12
 *
 * Copyright 1998-2000 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
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
