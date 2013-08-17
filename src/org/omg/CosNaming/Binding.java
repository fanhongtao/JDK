/*
 * @(#)Binding.java	1.5 98/09/21
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
