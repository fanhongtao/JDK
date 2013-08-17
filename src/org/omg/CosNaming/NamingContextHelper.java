/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
/*
 * File: ./org/omg/CosNaming/NamingContextHelper.java
 * From: nameservice.idl
 * Date: Tue Aug 11 03:12:09 1998
 *   By: idltojava Java IDL 1.2 Aug 11 1998 02:00:18
 */

package org.omg.CosNaming;
public class NamingContextHelper {
    // It is useless to have instances of this class
    private NamingContextHelper() { }

    public static void write(org.omg.CORBA.portable.OutputStream out, org.omg.CosNaming.NamingContext that) {
        out.write_Object(that);
    }
    public static org.omg.CosNaming.NamingContext read(org.omg.CORBA.portable.InputStream in) {
        return org.omg.CosNaming.NamingContextHelper.narrow(in.read_Object());
    }
    public static org.omg.CosNaming.NamingContext extract(org.omg.CORBA.Any a) {
	org.omg.CORBA.portable.InputStream in = a.create_input_stream();
	return read(in);
    }
    public static void insert(org.omg.CORBA.Any a, org.omg.CosNaming.NamingContext that) {
	org.omg.CORBA.portable.OutputStream out = a.create_output_stream();
	write(out, that);
	a.read_value(out.create_input_stream(), type());
    }
    private static org.omg.CORBA.TypeCode _tc;
    synchronized public static org.omg.CORBA.TypeCode type() {
	if (_tc == null)
	    _tc = org.omg.CORBA.ORB.init().create_interface_tc(id(), "NamingContext");
	return _tc;
    }
    public static String id() {
	return "IDL:omg.org/CosNaming/NamingContext:1.0";
    }
    public static org.omg.CosNaming.NamingContext narrow(org.omg.CORBA.Object that)
	throws org.omg.CORBA.BAD_PARAM {
        if (that == null)
            return null;
        if (that instanceof org.omg.CosNaming.NamingContext)
            return (org.omg.CosNaming.NamingContext) that;
	if (!that._is_a(id())) {
	    throw new org.omg.CORBA.BAD_PARAM();
	}
        org.omg.CORBA.portable.Delegate dup = ((org.omg.CORBA.portable.ObjectImpl)that)._get_delegate();
        org.omg.CosNaming.NamingContext result = new org.omg.CosNaming._NamingContextStub(dup);
        return result;
    }
}
