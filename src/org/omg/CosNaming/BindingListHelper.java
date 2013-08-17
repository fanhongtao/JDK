/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
/*
 * File: ./org/omg/CosNaming/BindingListHelper.java
 * From: nameservice.idl
 * Date: Tue Aug 11 03:12:09 1998
 *   By: idltojava Java IDL 1.2 Aug 11 1998 02:00:18
 */

package org.omg.CosNaming;
public class BindingListHelper {
    // It is useless to have instances of this class
    private BindingListHelper() { }

    public static void write(org.omg.CORBA.portable.OutputStream out, org.omg.CosNaming.Binding[] that)  {
	{
	    out.write_long(that.length);
	    for (int __index = 0 ; __index < that.length ; __index += 1) {
		org.omg.CosNaming.BindingHelper.write(out, that[__index]);
	    }
	}
    }
    public static org.omg.CosNaming.Binding[] read(org.omg.CORBA.portable.InputStream in) {
	org.omg.CosNaming.Binding[] that;
	{
	    int __length = in.read_long();
	    that = new org.omg.CosNaming.Binding[__length];
	    for (int __index = 0 ; __index < that.length ; __index += 1) {
		that[__index] = org.omg.CosNaming.BindingHelper.read(in);
	    }
	}
	return that;
    }
    public static org.omg.CosNaming.Binding[] extract(org.omg.CORBA.Any a) {
	org.omg.CORBA.portable.InputStream in = a.create_input_stream();
	return read(in);
    }
    public static void insert(org.omg.CORBA.Any a, org.omg.CosNaming.Binding[] that) {
	org.omg.CORBA.portable.OutputStream out = a.create_output_stream();
	a.type(type());
	write(out, that);
	a.read_value(out.create_input_stream(), type());
    }
    private static org.omg.CORBA.TypeCode _tc;
    synchronized public static org.omg.CORBA.TypeCode type() {
	if (_tc == null)
	    _tc = org.omg.CORBA.ORB.init().create_alias_tc(id(), "BindingList", org.omg.CORBA.ORB.init().create_sequence_tc(0, org.omg.CosNaming.BindingHelper.type()));
	return _tc;
    }
    public static String id() {
	return "IDL:omg.org/CosNaming/BindingList:1.0";
    }
}
