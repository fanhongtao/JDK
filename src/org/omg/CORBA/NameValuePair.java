/*
 * @(#)NameValuePair.java	1.5 99/04/22
 *
 * Copyright 1998, 1999 by Sun Microsystems, Inc.,
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

/**
 * The NameValuePair interface associates  a name with a value that is an
 * attribute of an IDL struct.  This interface is used in the DynStruct APIs.
 */

public final class NameValuePair implements org.omg.CORBA.portable.IDLEntity {

   /**
    * The name to be associated with a value by this <code>NameValuePair</code> object.
    */
    public String id;

   /**
    * The value to be associated with a name by this <code>NameValuePair</code> object.
    */
    public org.omg.CORBA.Any value;

   /**
    * Constructs an empty <code>NameValuePair</code> object.
    * To associate a name with a value after using this constructor, the fields
    * of this object have to be accessed individually.
    */
    public NameValuePair() { }
	
   /**
    * Constructs a <code>NameValuePair</code> object that associates
	* the given name with the given <code>org.omg.CORBA.Any</code> object.
    * @param __id the name to be associated with the given <code>Any</code> object
    * @param __value the <code>Any</code> object to be associated with the given name
    */
    public NameValuePair(String __id, org.omg.CORBA.Any __value) {
	id = __id;
	value = __value;
    }
}
