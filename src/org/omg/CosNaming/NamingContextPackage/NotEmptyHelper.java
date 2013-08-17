/*
 * @(#)NotEmptyHelper.java	1.5 98/09/21
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
 * File: ./org/omg/CosNaming/NamingContextPackage/NotEmptyHelper.java
 * From: nameservice.idl
 * Date: Tue Aug 11 03:12:09 1998
 *   By: idltojava Java IDL 1.2 Aug 11 1998 02:00:18
 */

package org.omg.CosNaming.NamingContextPackage;
public class NotEmptyHelper {
     // It is useless to have instances of this class
     private NotEmptyHelper() { }

    public static void write(org.omg.CORBA.portable.OutputStream out, org.omg.CosNaming.NamingContextPackage.NotEmpty that) {
    out.write_string(id());
 }
    public static org.omg.CosNaming.NamingContextPackage.NotEmpty read(org.omg.CORBA.portable.InputStream in) {
        org.omg.CosNaming.NamingContextPackage.NotEmpty that = new org.omg.CosNaming.NamingContextPackage.NotEmpty();
         // read and discard the repository id
        in.read_string();
    return that;
    }
   public static org.omg.CosNaming.NamingContextPackage.NotEmpty extract(org.omg.CORBA.Any a) {
     org.omg.CORBA.portable.InputStream in = a.create_input_stream();
     return read(in);
   }
   public static void insert(org.omg.CORBA.Any a, org.omg.CosNaming.NamingContextPackage.NotEmpty that) {
     org.omg.CORBA.portable.OutputStream out = a.create_output_stream();
     write(out, that);
     a.read_value(out.create_input_stream(), type());
   }
   private static org.omg.CORBA.TypeCode _tc;
   synchronized public static org.omg.CORBA.TypeCode type() {
       int _memberCount = 0;
       org.omg.CORBA.StructMember[] _members = null;
          if (_tc == null) {
               _members= new org.omg.CORBA.StructMember[0];
             _tc = org.omg.CORBA.ORB.init().create_exception_tc(id(), "NotEmpty", _members);
          }
      return _tc;
   }
   public static String id() {
       return "IDL:omg.org/CosNaming/NamingContext/NotEmpty:1.0";
   }
}
