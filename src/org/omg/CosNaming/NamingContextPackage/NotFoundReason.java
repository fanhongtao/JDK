/*
 * @(#)NotFoundReason.java	1.5 98/09/21
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
