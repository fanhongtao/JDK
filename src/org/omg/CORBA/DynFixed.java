/*
 * @(#)DynFixed.java	1.3 98/09/10
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
