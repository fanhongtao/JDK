/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
/*
 * File: ./org/omg/CosNaming/BindingHolder.java
 * From: nameservice.idl
 * Date: Tue Aug 11 03:12:09 1998
 *   By: idltojava Java IDL 1.2 Aug 11 1998 02:00:18
 */

package org.omg.CosNaming;
public final class BindingHolder
    implements org.omg.CORBA.portable.Streamable{
    //	instance variable 
    public org.omg.CosNaming.Binding value;
    //	constructors 
    public BindingHolder() {
	this(null);
    }
    public BindingHolder(org.omg.CosNaming.Binding __arg) {
	value = __arg;
    }

    public void _write(org.omg.CORBA.portable.OutputStream out) {
        org.omg.CosNaming.BindingHelper.write(out, value);
    }

    public void _read(org.omg.CORBA.portable.InputStream in) {
        value = org.omg.CosNaming.BindingHelper.read(in);
    }

    public org.omg.CORBA.TypeCode _type() {
        return org.omg.CosNaming.BindingHelper.type();
    }
}
