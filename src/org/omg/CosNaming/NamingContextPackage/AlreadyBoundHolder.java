/*
 * @(#)AlreadyBoundHolder.java	1.10 00/02/02
 *
 * Copyright 1997-2000 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
/*
 * File: ./org/omg/CosNaming/NamingContextPackage/AlreadyBoundHolder.java
 * From: nameservice.idl
 * Date: Tue Aug 11 03:12:09 1998
 *   By: idltojava Java IDL 1.2 Aug 11 1998 02:00:18
 */

package org.omg.CosNaming.NamingContextPackage;
public final class AlreadyBoundHolder
    implements org.omg.CORBA.portable.Streamable{
    //	instance variable 
    public org.omg.CosNaming.NamingContextPackage.AlreadyBound value;
    //	constructors 
    public AlreadyBoundHolder() {
	this(null);
    }
    public AlreadyBoundHolder(org.omg.CosNaming.NamingContextPackage.AlreadyBound __arg) {
	value = __arg;
    }

    public void _write(org.omg.CORBA.portable.OutputStream out) {
        org.omg.CosNaming.NamingContextPackage.AlreadyBoundHelper.write(out, value);
    }

    public void _read(org.omg.CORBA.portable.InputStream in) {
        value = org.omg.CosNaming.NamingContextPackage.AlreadyBoundHelper.read(in);
    }

    public org.omg.CORBA.TypeCode _type() {
        return org.omg.CosNaming.NamingContextPackage.AlreadyBoundHelper.type();
    }
}
