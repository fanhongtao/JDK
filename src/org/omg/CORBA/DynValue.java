/*
 * @(#)DynValue.java	1.7 98/09/10
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

public interface DynValue extends org.omg.CORBA.Object, org.omg.CORBA.DynAny {

    /**
     * Return the name of the current member while traversing a
     * <code>DynAny</code> which represents a Value object.
     *
     * @return the name of the current member.
     */
    String current_member_name();

    /**
     * Return the <code>TypeCode</code> kind of the current member.
     *
     * @return the <code>TCKind</code> corresponding to the current
     * member.
     */
    TCKind current_member_kind();

    /**
     * Return an array containing all the members of the value object
     * stored in this <code>DynValue</code>.
     *
     * @return an array of name-value pairs.
     */
    org.omg.CORBA.NameValuePair[] get_members();

    /**
     * Set the members of a value object this <code>DynValue</code>
     * represents.
     *
     * @param value the array of name-value pairs.
     */
    void set_members(NameValuePair[] value)
	throws org.omg.CORBA.DynAnyPackage.InvalidSeq;
}
