/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
/*
 * File: ./org/omg/CosNaming/NamingContextPackage/NotFoundReasonHelper.java
 * From: nameservice.idl
 * Date: Tue Aug 11 03:12:09 1998
 *   By: idltojava Java IDL 1.2 Aug 11 1998 02:00:18
 */

package org.omg.CosNaming.NamingContextPackage;
public class NotFoundReasonHelper {
    // It is useless to have instances of this class
    private NotFoundReasonHelper() { }

    public static void write(org.omg.CORBA.portable.OutputStream out, org.omg.CosNaming.NamingContextPackage.NotFoundReason that)  {
	out.write_long(that.value());
    }
    public static org.omg.CosNaming.NamingContextPackage.NotFoundReason read(org.omg.CORBA.portable.InputStream in)  {
	return org.omg.CosNaming.NamingContextPackage.NotFoundReason.from_int(in.read_long());
    }
    public static org.omg.CosNaming.NamingContextPackage.NotFoundReason extract(org.omg.CORBA.Any a) {
	org.omg.CORBA.portable.InputStream in = a.create_input_stream();
	return read(in);
    }
    public static void insert(org.omg.CORBA.Any a, org.omg.CosNaming.NamingContextPackage.NotFoundReason that) {
	org.omg.CORBA.portable.OutputStream out = a.create_output_stream();
	write(out, that);
	a.read_value(out.create_input_stream(), type());
    }
    private static org.omg.CORBA.TypeCode _tc;
    private static final int _memberCount = 3;
    private static String[] _members = {
	"missing_node",
	"not_context",
	"not_object"
    };
    synchronized public static org.omg.CORBA.TypeCode type() {
	if (_tc == null)
	    _tc = org.omg.CORBA.ORB.init().create_enum_tc(id(), "NotFoundReason", _members);
	return _tc;
    }
    public static String id() {
	return "IDL:omg.org/CosNaming/NamingContext/NotFoundReason:1.0";
    }
}
