/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
/*
 * File: ./org/omg/CosNaming/NameComponentHelper.java
 * From: nameservice.idl
 * Date: Tue Aug 11 03:12:09 1998
 *   By: idltojava Java IDL 1.2 Aug 11 1998 02:00:18
 */

package org.omg.CosNaming;
public class NameComponentHelper {
    // It is useless to have instances of this class
    private NameComponentHelper() { }

    public static void write(org.omg.CORBA.portable.OutputStream out, org.omg.CosNaming.NameComponent that) {
	out.write_string(that.id);
	out.write_string(that.kind);
    }
    public static org.omg.CosNaming.NameComponent read(org.omg.CORBA.portable.InputStream in) {
        org.omg.CosNaming.NameComponent that = new org.omg.CosNaming.NameComponent();
	that.id = in.read_string();
	that.kind = in.read_string();
        return that;
    }
    public static org.omg.CosNaming.NameComponent extract(org.omg.CORBA.Any a) {
	org.omg.CORBA.portable.InputStream in = a.create_input_stream();
	return read(in);
    }
    public static void insert(org.omg.CORBA.Any a, org.omg.CosNaming.NameComponent that) {
	org.omg.CORBA.portable.OutputStream out = a.create_output_stream();
	write(out, that);
	a.read_value(out.create_input_stream(), type());
    }
    private static org.omg.CORBA.TypeCode _tc;
    synchronized public static org.omg.CORBA.TypeCode type() {
	int _memberCount = 2;
	org.omg.CORBA.StructMember[] _members = null;
	if (_tc == null) {
	    _members= new org.omg.CORBA.StructMember[2];
	    _members[0] = new org.omg.CORBA.StructMember(
							 "id",
							 org.omg.CORBA.ORB.init().get_primitive_tc(org.omg.CORBA.TCKind.tk_string),
							 null);

	    _members[1] = new org.omg.CORBA.StructMember(
							 "kind",
							 org.omg.CORBA.ORB.init().get_primitive_tc(org.omg.CORBA.TCKind.tk_string),
							 null);
	    _tc = org.omg.CORBA.ORB.init().create_struct_tc(id(), "NameComponent", _members);
	}
	return _tc;
    }
    public static String id() {
	return "IDL:omg.org/CosNaming/NameComponent:1.0";
    }
}
