/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
/*
 * File: ./org/omg/CosNaming/NamingContextPackage/InvalidNameHelper.java
 * From: nameservice.idl
 * Date: Tue Aug 11 03:12:09 1998
 *   By: idltojava Java IDL 1.2 Aug 11 1998 02:00:18
 */

package org.omg.CosNaming.NamingContextPackage;
public class InvalidNameHelper {
    // It is useless to have instances of this class
    private InvalidNameHelper() { }

    public static void write(org.omg.CORBA.portable.OutputStream out, org.omg.CosNaming.NamingContextPackage.InvalidName that) {
	out.write_string(id());
    }
    public static org.omg.CosNaming.NamingContextPackage.InvalidName read(org.omg.CORBA.portable.InputStream in) {
        org.omg.CosNaming.NamingContextPackage.InvalidName that = new org.omg.CosNaming.NamingContextPackage.InvalidName();
	// read and discard the repository id
        in.read_string();
	return that;
    }
    public static org.omg.CosNaming.NamingContextPackage.InvalidName extract(org.omg.CORBA.Any a) {
	org.omg.CORBA.portable.InputStream in = a.create_input_stream();
	return read(in);
    }
    public static void insert(org.omg.CORBA.Any a, org.omg.CosNaming.NamingContextPackage.InvalidName that) {
	org.omg.CORBA.portable.OutputStream out = a.create_output_stream();
	write(out, that);
	a.read_value(out.create_input_stream(), type());
    }
    private static org.omg.CORBA.TypeCode _tc;
    synchronized public static org.omg.CORBA.TypeCode type() {
	int _memberCount = 0;
	org.omg.CORBA.StructMember[] _members = null;
	if (_tc == null) {
	    _members= new org.omg.CORBA.StructMember[0];
	    _tc = org.omg.CORBA.ORB.init().create_exception_tc(id(), "InvalidName", _members);
	}
	return _tc;
    }
    public static String id() {
	return "IDL:omg.org/CosNaming/NamingContext/InvalidName:1.0";
    }
}
