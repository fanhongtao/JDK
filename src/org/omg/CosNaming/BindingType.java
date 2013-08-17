/*
 * @(#)BindingType.java	1.5 98/09/21
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
