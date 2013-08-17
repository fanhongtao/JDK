/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
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
