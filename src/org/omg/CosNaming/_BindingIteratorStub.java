/*
 * @(#)_BindingIteratorStub.java	1.6 98/09/21
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
 * File: ./org/omg/CosNaming/_BindingIteratorStub.java
 * From: nameservice.idl
 * Date: Tue Aug 11 03:12:09 1998
 *   By: idltojava Java IDL 1.2 Aug 11 1998 02:00:18
 */

package org.omg.CosNaming;
public class _BindingIteratorStub
	extends org.omg.CORBA.portable.ObjectImpl
    	implements org.omg.CosNaming.BindingIterator {

    public _BindingIteratorStub(org.omg.CORBA.portable.Delegate d) {
          super();
          _set_delegate(d);
    }

    private static final String _type_ids[] = {
        "IDL:omg.org/CosNaming/BindingIterator:1.0"
    };

    public String[] _ids() { return (String[]) _type_ids.clone(); }

    //	IDL operations
    //	    Implementation of ::CosNaming::BindingIterator::next_one
    public boolean next_one(org.omg.CosNaming.BindingHolder b)
 {
           org.omg.CORBA.Request r = _request("next_one");
           r.set_return_type(org.omg.CORBA.ORB.init().get_primitive_tc(org.omg.CORBA.TCKind.tk_boolean));
           org.omg.CORBA.Any _b = r.add_out_arg();
           _b.type(org.omg.CosNaming.BindingHelper.type());
           r.invoke();
           b.value = org.omg.CosNaming.BindingHelper.extract(_b);
           boolean __result;
           __result = r.return_value().extract_boolean();
           return __result;
   }
    //	    Implementation of ::CosNaming::BindingIterator::next_n
    public boolean next_n(int how_many, org.omg.CosNaming.BindingListHolder bl)
 {
           org.omg.CORBA.Request r = _request("next_n");
           r.set_return_type(org.omg.CORBA.ORB.init().get_primitive_tc(org.omg.CORBA.TCKind.tk_boolean));
           org.omg.CORBA.Any _how_many = r.add_in_arg();
           _how_many.insert_ulong(how_many);
           org.omg.CORBA.Any _bl = r.add_out_arg();
           _bl.type(org.omg.CosNaming.BindingListHelper.type());
           r.invoke();
           bl.value = org.omg.CosNaming.BindingListHelper.extract(_bl);
           boolean __result;
           __result = r.return_value().extract_boolean();
           return __result;
   }
    //	    Implementation of ::CosNaming::BindingIterator::destroy
    public void destroy()
 {
           org.omg.CORBA.Request r = _request("destroy");
           r.invoke();
   }

};
