/*
 * @(#)NameValuePair.java	1.2 98/06/29
 *
 * Copyright 1998 by Sun Microsystems, Inc.,
 * 901 San Antonio Road, Palo Alto, California, 94303, U.S.A.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
 */


package org.omg.CORBA;

/** The NameValuePair interface is used to hold names and values of
 *  IDL structs in the DynStruct APIs.
 */

public final class NameValuePair implements org.omg.CORBA.portable.IDLEntity {

    public String id;
    public org.omg.CORBA.Any value;

    public NameValuePair() { }
    public NameValuePair(String __id, org.omg.CORBA.Any __value) {
	id = __id;
	value = __value;
    }
}
