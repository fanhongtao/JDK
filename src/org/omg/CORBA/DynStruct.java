/*
 * @(#)DynStruct.java	1.5 01/11/29
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
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
