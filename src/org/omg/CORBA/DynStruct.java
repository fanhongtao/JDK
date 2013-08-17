/*
 * @(#)DynStruct.java	1.4 98/09/10
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

/** The DynStruct interface represents a DynAny object which is associated
 *  with an IDL struct.
 */

public interface DynStruct extends org.omg.CORBA.Object, org.omg.CORBA.DynAny
{
    /**
     * During a traversal, return the name of the current member.
     *
     * @return the string name of the current member.
     */
    public String current_member_name();

    /**
     * Return the <code>TypeCode</code> kind of the current member.
     *
     * @return the TCKind.
     */
    public org.omg.CORBA.TCKind current_member_kind();

    /**
     * Return an array of all members of the stored struct.
     *
     * @return the array of name-value pairs.
     */
    public org.omg.CORBA.NameValuePair[] get_members();

    /**
     * Set the members of the struct.
     *
     * @param value the array of name-value pairs.
     */
    public void set_members(org.omg.CORBA.NameValuePair[] value)
        throws org.omg.CORBA.DynAnyPackage.InvalidSeq;
}
