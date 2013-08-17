/*
 * @(#)IDLType.java	1.2 00/01/12
 *
 * Copyright 1997-2000 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
/*
 * File: ./org/omg/CORBA/IDLType.java
 * From: ./ir.idl
 * Date: Fri Aug 28 16:03:31 1998
 *   By: idltojava Java IDL 1.2 Aug 11 1998 02:00:18
 */

package org.omg.CORBA;
/**
  * An abstract interface inherited by all Interface Repository
  * (IR) objects that represent OMG IDL types. It provides access
  * to the <code>TypeCode</code> object describing the type and is used in defining the
  * other interfaces wherever definitions of <code>IDLType</code> must be referenced.
  */
public interface IDLType
    extends org.omg.CORBA.Object, org.omg.CORBA.portable.IDLEntity,
	    org.omg.CORBA.IRObject {

   /**
    * Retrieves the <code>TypeCode</code> object describing the type of this object.
    * @return the  <code>TypeCode</code> object describing this object
    */
    org.omg.CORBA.TypeCode type();
}
