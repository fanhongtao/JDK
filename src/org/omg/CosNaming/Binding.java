/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
/*
 * File: ./org/omg/CosNaming/Binding.java
 * From: nameservice.idl
 * Date: Tue Aug 11 03:12:09 1998
 *   By: idltojava Java IDL 1.2 Aug 11 1998 02:00:18
 */

package org.omg.CosNaming;
public final class Binding implements org.omg.CORBA.portable.IDLEntity {
    //	instance variables
    public org.omg.CosNaming.NameComponent[] binding_name;
    public org.omg.CosNaming.BindingType binding_type;
    //	constructors
    public Binding() { }
    public Binding(org.omg.CosNaming.NameComponent[] __binding_name, org.omg.CosNaming.BindingType __binding_type) {
	binding_name = __binding_name;
	binding_type = __binding_type;
    }
}
