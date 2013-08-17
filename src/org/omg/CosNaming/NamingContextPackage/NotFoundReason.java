/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
/*
 * File: ./org/omg/CosNaming/NamingContextPackage/NotFoundReason.java
 * From: nameservice.idl
 * Date: Tue Aug 11 03:12:09 1998
 *   By: idltojava Java IDL 1.2 Aug 11 1998 02:00:18
 */

package org.omg.CosNaming.NamingContextPackage;
public final class NotFoundReason implements org.omg.CORBA.portable.IDLEntity {
    public static final int _missing_node = 0,
	_not_context = 1,
	_not_object = 2;
    public static final NotFoundReason missing_node = new NotFoundReason(_missing_node);
    public static final NotFoundReason not_context = new NotFoundReason(_not_context);
    public static final NotFoundReason not_object = new NotFoundReason(_not_object);
    public int value() {
	return _value;
    }
    public static final NotFoundReason from_int(int i)  throws  org.omg.CORBA.BAD_PARAM {
	switch (i) {
	case _missing_node:
	    return missing_node;
	case _not_context:
	    return not_context;
	case _not_object:
	    return not_object;
	default:
	    throw new org.omg.CORBA.BAD_PARAM();
	}
    }
    private NotFoundReason(int _value){
	this._value = _value;
    }
    private int _value;
}
