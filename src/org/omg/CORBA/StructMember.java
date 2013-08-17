/*
 * @(#)StructMember.java	1.11 98/09/21
 *
 * Copyright 1997, 1998 by Sun Microsystems, Inc.,
 * 901 San Antonio Road, Palo Alto, California, 94303, U.S.A.
 * All rights reserved.
 * 
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
 */
/*
 * File: ./org/omg/CORBA/StructMember.java
 * From: ./ir.idl
 * Date: Fri Aug 28 16:03:31 1998
 *   By: idltojava Java IDL 1.2 Aug 11 1998 02:00:18
 */

package org.omg.CORBA;

/**
 * A description of a member of an IDL <code>struct</code>.
 */
public final class StructMember implements org.omg.CORBA.portable.IDLEntity {

    //	instance variables

/**
 * The name of the struct member described by
 * this <code>StructMember</code> object.
 * @serial
 */
    public String name;

/**
 * The type of the struct member described by
 * this <code>StructMember</code> object.
 * @serial
 */
    public org.omg.CORBA.TypeCode type;

/**
 * The typedef that represents the IDL type of the struct member described by
 * this <code>StructMember</code> object.
 * @serial
 */
    public org.omg.CORBA.IDLType type_def;
    //	constructors

/**
 * Constructs a default <code>StructMember</code> object.
 */
    public StructMember() { }

/**
 * Constructs a <code>StructMember</code> object initialized with the
 * given values.
 */
    public StructMember(String __name, org.omg.CORBA.TypeCode __type, org.omg.CORBA.IDLType __type_def) {
	name = __name;
	type = __type;
	type_def = __type_def;
    }
}
