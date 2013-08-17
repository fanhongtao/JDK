/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
/*
 * File: ./org/omg/CosNaming/BindingTypeHelper.java
 * From: nameservice.idl
 * Date: Tue Aug 11 03:12:09 1998
 *   By: idltojava Java IDL 1.2 Aug 11 1998 02:00:18
 */

package org.omg.CosNaming;
public class BindingTypeHelper {
    // It is useless to have instances of this class
    private BindingTypeHelper() { }

    public static void write(org.omg.CORBA.portable.OutputStream out, org.omg.CosNaming.BindingType that)  {
	out.write_long(that.value());
    }
    public static org.omg.CosNaming.BindingType read(org.omg.CORBA.portable.InputStream in)  {
	return org.omg.CosNaming.BindingType.from_int(in.read_long());
    }
    public static org.omg.CosNaming.BindingType extract(org.omg.CORBA.Any a) {
	org.omg.CORBA.portable.InputStream in = a.create_input_stream();
	return read(in);
    }
    public static void insert(org.omg.CORBA.Any a, org.omg.CosNaming.BindingType that) {
	org.omg.CORBA.portable.OutputStream out = a.create_output_stream();
	write(out, that);
	a.read_value(out.create_input_stream(), type());
    }
    private static org.omg.CORBA.TypeCode _tc;
    private static final int _memberCount = 2;
    private static String[] _members = {
	"nobject",
	"ncontext"
    };
    synchronized public static org.omg.CORBA.TypeCode type() {
	if (_tc == null)
	    _tc = org.omg.CORBA.ORB.init().create_enum_tc(id(), "BindingType", _members);
	return _tc;
    }
    public static String id() {
	return "IDL:omg.org/CosNaming/BindingType:1.0";
    }
}
