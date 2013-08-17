/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
/*
 * File: ./org/omg/CosNaming/BindingListHolder.java
 * From: nameservice.idl
 * Date: Tue Aug 11 03:12:09 1998
 *   By: idltojava Java IDL 1.2 Aug 11 1998 02:00:18
 */

package org.omg.CosNaming;
public final class BindingListHolder
    implements org.omg.CORBA.portable.Streamable
{
    //	instance variable 
    public org.omg.CosNaming.Binding[] value;
    //	constructors 
    public BindingListHolder() {
	this(null);
    }
    public BindingListHolder(org.omg.CosNaming.Binding[] __arg) {
	value = __arg;
    }
    public void _write(org.omg.CORBA.portable.OutputStream out) {
        org.omg.CosNaming.BindingListHelper.write(out, value);
    }

    public void _read(org.omg.CORBA.portable.InputStream in) {
        value = org.omg.CosNaming.BindingListHelper.read(in);
    }

    public org.omg.CORBA.TypeCode _type() {
        return org.omg.CosNaming.BindingListHelper.type();
    }
}
