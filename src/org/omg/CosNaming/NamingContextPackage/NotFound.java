/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
/*
 * File: ./org/omg/CosNaming/NamingContextPackage/NotFound.java
 * From: nameservice.idl
 * Date: Tue Aug 11 03:12:09 1998
 *   By: idltojava Java IDL 1.2 Aug 11 1998 02:00:18
 */

package org.omg.CosNaming.NamingContextPackage;
public final class NotFound
    extends org.omg.CORBA.UserException implements org.omg.CORBA.portable.IDLEntity {
    //	instance variables
    public org.omg.CosNaming.NamingContextPackage.NotFoundReason why;
    public org.omg.CosNaming.NameComponent[] rest_of_name;
    //	constructors
    public NotFound() {
	super();
    }
    public NotFound(org.omg.CosNaming.NamingContextPackage.NotFoundReason __why, org.omg.CosNaming.NameComponent[] __rest_of_name) {
	super();
	why = __why;
	rest_of_name = __rest_of_name;
    }
}
