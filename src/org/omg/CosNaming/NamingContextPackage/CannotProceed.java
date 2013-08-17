/*
 * @(#)CannotProceed.java	1.5 98/09/21
 *
 * Copyright 1997, 1998 by Sun Microsystems, Inc.,
 * 901 San Antonio Road, Palo Alto, California, 94303, U.S.A.
 * All rights reserved.
 * 
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
 */
/*
 * File: ./org/omg/CosNaming/NamingContextPackage/CannotProceed.java
 * From: nameservice.idl
 * Date: Tue Aug 11 03:12:09 1998
 *   By: idltojava Java IDL 1.2 Aug 11 1998 02:00:18
 */

package org.omg.CosNaming.NamingContextPackage;
public final class CannotProceed
	extends org.omg.CORBA.UserException implements org.omg.CORBA.portable.IDLEntity {
    //	instance variables
    public org.omg.CosNaming.NamingContext cxt;
    public org.omg.CosNaming.NameComponent[] rest_of_name;
    //	constructors
    public CannotProceed() {
	super();
    }
    public CannotProceed(org.omg.CosNaming.NamingContext __cxt, org.omg.CosNaming.NameComponent[] __rest_of_name) {
	super();
	cxt = __cxt;
	rest_of_name = __rest_of_name;
    }
}
