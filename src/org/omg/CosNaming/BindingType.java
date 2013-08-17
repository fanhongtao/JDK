/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
/*
 * File: ./org/omg/CosNaming/BindingType.java
 * From: nameservice.idl
 * Date: Tue Aug 11 03:12:09 1998
 *   By: idltojava Java IDL 1.2 Aug 11 1998 02:00:18
 */

package org.omg.CosNaming;
public final class BindingType implements org.omg.CORBA.portable.IDLEntity {
    public static final int _nobject = 0,
	_ncontext = 1;
    public static final BindingType nobject = new BindingType(_nobject);
    public static final BindingType ncontext = new BindingType(_ncontext);
    public int value() {
	return _value;
    }
    public static final BindingType from_int(int i)  throws  org.omg.CORBA.BAD_PARAM {
	switch (i) {
	case _nobject:
	    return nobject;
	case _ncontext:
	    return ncontext;
	default:
	    throw new org.omg.CORBA.BAD_PARAM();
	}
    }
    private BindingType(int _value){
	this._value = _value;
    }
    private int _value;
}
