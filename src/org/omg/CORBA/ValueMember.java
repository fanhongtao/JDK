/*
 * @(#)ValueMember.java	1.8 98/10/11
 *
 * Copyright 1998-1998 by Sun Microsystems, Inc.,
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
 * File: ./org/omg/CORBA/ValueMember.java
 * From: ./ir.idl
 * Date: Fri Aug 28 16:03:31 1998
 *   By: idltojava Java IDL 1.2 Aug 11 1998 02:00:18
 */

package org.omg.CORBA;

/**
 * A description in the Interface Repository of
 * a member of a <code>value</code> object.
 */
public final class ValueMember implements org.omg.CORBA.portable.IDLEntity {

    //	instance variables

/**
 * The name of the <code>value</code> member described by this
 * <code>ValueMember</code> object.
 * @serial
 */
    public String name;

/**
 * The name of the <code>value</code> member described by this
 * <code>ValueMember</code> object.
 * @serial
 */
    public String id;

/**
 * The name of the <code>value</code> member described by this
 * <code>ValueMember</code> object.
 * @serial
 */
    public String defined_in;

/**
 * The name of the <code>value</code> member described by this
 * <code>ValueMember</code> object.
 * @serial
 */
    public String version;

/**
 * The type of the <code>value</code> member described by this
 * <code>ValueMember</code> object.
 * @serial
 */
    public org.omg.CORBA.TypeCode type;

/**
 * The typedef that represents the IDL type of the <code>value</code> 
 * member described by this <code>ValueMember</code> object.
 * @serial
 */
    public org.omg.CORBA.IDLType type_def;

/**
 * The type of access (public, private) for the <code>value</code> 
 * member described by this <code>ValueMember</code> object.
 * @serial
 */
    public short access;
    //	constructors

/**
 * Constructs a default <code>ValueMember</code> object.
 */
    public ValueMember() { }

/**
 * Constructs a <code>ValueMember</code> object initialized with
 * the given values.
 */
    public ValueMember(String __name, String __id, String __defined_in, String __version, org.omg.CORBA.TypeCode __type, org.omg.CORBA.IDLType __type_def, short __access) {
	name = __name;
	id = __id;
	defined_in = __defined_in;
	version = __version;
	type = __type;
	type_def = __type_def;
	access = __access;
    }
}
