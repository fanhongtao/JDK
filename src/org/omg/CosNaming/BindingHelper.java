/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
/*
 * File: ./org/omg/CosNaming/BindingHelper.java
 * From: nameservice.idl
 * Date: Tue Aug 11 03:12:09 1998
 *   By: idltojava Java IDL 1.2 Aug 11 1998 02:00:18
 */

package org.omg.CosNaming;
public class BindingHelper {
    // It is useless to have instances of this class
    private BindingHelper() { }

    public static void write(org.omg.CORBA.portable.OutputStream out, org.omg.CosNaming.Binding that) {
	{
	    out.write_long(that.binding_name.length);
	    for (int __index = 0 ; __index < that.binding_name.length ; __index += 1) {
	        org.omg.CosNaming.NameComponentHelper.write(out, that.binding_name[__index]);
	    }
	}
	org.omg.CosNaming.BindingTypeHelper.write(out, that.binding_type);
    }
    public static org.omg.CosNaming.Binding read(org.omg.CORBA.portable.InputStream in) {
        org.omg.CosNaming.Binding that = new org.omg.CosNaming.Binding();
	{
	    int __length = in.read_long();
	    that.binding_name = new org.omg.CosNaming.NameComponent[__length];
	    for (int __index = 0 ; __index < that.binding_name.length ; __index += 1) {
	        that.binding_name[__index] = org.omg.CosNaming.NameComponentHelper.read(in);
	    }
	}
	that.binding_type = org.omg.CosNaming.BindingTypeHelper.read(in);
        return that;
    }
    public static org.omg.CosNaming.Binding extract(org.omg.CORBA.Any a) {
	org.omg.CORBA.portable.InputStream in = a.create_input_stream();
	return read(in);
    }
    public static void insert(org.omg.CORBA.Any a, org.omg.CosNaming.Binding that) {
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
							 "binding_name",
							 org.omg.CORBA.ORB.init().create_sequence_tc(0, org.omg.CosNaming.NameComponentHelper.type()),
							 null);

	    _members[1] = new org.omg.CORBA.StructMember(
							 "binding_type",
							 org.omg.CosNaming.BindingTypeHelper.type(),
							 null);
	    _tc = org.omg.CORBA.ORB.init().create_struct_tc(id(), "Binding", _members);
	}
	return _tc;
    }
    public static String id() {
	return "IDL:omg.org/CosNaming/Binding:1.0";
    }
}
