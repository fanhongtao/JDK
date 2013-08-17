/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
/*
 * File: ./org/omg/CosNaming/_NamingContextStub.java
 * From: nameservice.idl
 * Date: Tue Aug 11 03:12:09 1998
 *   By: idltojava Java IDL 1.2 Aug 11 1998 02:00:18
 */

package org.omg.CosNaming;
public class _NamingContextStub
    extends org.omg.CORBA.portable.ObjectImpl
    implements org.omg.CosNaming.NamingContext {

    public _NamingContextStub(org.omg.CORBA.portable.Delegate d) {
	super();
	_set_delegate(d);
    }

    private static final String _type_ids[] = {
        "IDL:omg.org/CosNaming/NamingContext:1.0"
    };

    public String[] _ids() { return (String[]) _type_ids.clone(); }

    //	IDL operations
    //	    Implementation of ::CosNaming::NamingContext::bind
    public void bind(org.omg.CosNaming.NameComponent[] n, org.omg.CORBA.Object obj)
        throws org.omg.CosNaming.NamingContextPackage.NotFound, org.omg.CosNaming.NamingContextPackage.CannotProceed, org.omg.CosNaming.NamingContextPackage.InvalidName, org.omg.CosNaming.NamingContextPackage.AlreadyBound {
	org.omg.CORBA.Request r = _request("bind");
	org.omg.CORBA.Any _n = r.add_in_arg();
	org.omg.CosNaming.NameHelper.insert(_n, n);
	org.omg.CORBA.Any _obj = r.add_in_arg();
	_obj.insert_Object(obj);
	r.exceptions().add(org.omg.CosNaming.NamingContextPackage.NotFoundHelper.type());
	r.exceptions().add(org.omg.CosNaming.NamingContextPackage.CannotProceedHelper.type());
	r.exceptions().add(org.omg.CosNaming.NamingContextPackage.InvalidNameHelper.type());
	r.exceptions().add(org.omg.CosNaming.NamingContextPackage.AlreadyBoundHelper.type());
	r.invoke();
	java.lang.Exception __ex = r.env().exception();
	if (__ex instanceof org.omg.CORBA.UnknownUserException) {
	    org.omg.CORBA.UnknownUserException __userEx = (org.omg.CORBA.UnknownUserException) __ex;
	    if (__userEx.except.type().equals(org.omg.CosNaming.NamingContextPackage.NotFoundHelper.type())) {
		throw org.omg.CosNaming.NamingContextPackage.NotFoundHelper.extract(__userEx.except);
	    }
	    if (__userEx.except.type().equals(org.omg.CosNaming.NamingContextPackage.CannotProceedHelper.type())) {
		throw org.omg.CosNaming.NamingContextPackage.CannotProceedHelper.extract(__userEx.except);
	    }
	    if (__userEx.except.type().equals(org.omg.CosNaming.NamingContextPackage.InvalidNameHelper.type())) {
		throw org.omg.CosNaming.NamingContextPackage.InvalidNameHelper.extract(__userEx.except);
	    }
	    if (__userEx.except.type().equals(org.omg.CosNaming.NamingContextPackage.AlreadyBoundHelper.type())) {
		throw org.omg.CosNaming.NamingContextPackage.AlreadyBoundHelper.extract(__userEx.except);
	    }
	}
    }
    //	    Implementation of ::CosNaming::NamingContext::bind_context
    public void bind_context(org.omg.CosNaming.NameComponent[] n, org.omg.CosNaming.NamingContext nc)
        throws org.omg.CosNaming.NamingContextPackage.NotFound, org.omg.CosNaming.NamingContextPackage.CannotProceed, org.omg.CosNaming.NamingContextPackage.InvalidName, org.omg.CosNaming.NamingContextPackage.AlreadyBound {
	org.omg.CORBA.Request r = _request("bind_context");
	org.omg.CORBA.Any _n = r.add_in_arg();
	org.omg.CosNaming.NameHelper.insert(_n, n);
	org.omg.CORBA.Any _nc = r.add_in_arg();
	org.omg.CosNaming.NamingContextHelper.insert(_nc, nc);
	r.exceptions().add(org.omg.CosNaming.NamingContextPackage.NotFoundHelper.type());
	r.exceptions().add(org.omg.CosNaming.NamingContextPackage.CannotProceedHelper.type());
	r.exceptions().add(org.omg.CosNaming.NamingContextPackage.InvalidNameHelper.type());
	r.exceptions().add(org.omg.CosNaming.NamingContextPackage.AlreadyBoundHelper.type());
	r.invoke();
	java.lang.Exception __ex = r.env().exception();
	if (__ex instanceof org.omg.CORBA.UnknownUserException) {
	    org.omg.CORBA.UnknownUserException __userEx = (org.omg.CORBA.UnknownUserException) __ex;
	    if (__userEx.except.type().equals(org.omg.CosNaming.NamingContextPackage.NotFoundHelper.type())) {
		throw org.omg.CosNaming.NamingContextPackage.NotFoundHelper.extract(__userEx.except);
	    }
	    if (__userEx.except.type().equals(org.omg.CosNaming.NamingContextPackage.CannotProceedHelper.type())) {
		throw org.omg.CosNaming.NamingContextPackage.CannotProceedHelper.extract(__userEx.except);
	    }
	    if (__userEx.except.type().equals(org.omg.CosNaming.NamingContextPackage.InvalidNameHelper.type())) {
		throw org.omg.CosNaming.NamingContextPackage.InvalidNameHelper.extract(__userEx.except);
	    }
	    if (__userEx.except.type().equals(org.omg.CosNaming.NamingContextPackage.AlreadyBoundHelper.type())) {
		throw org.omg.CosNaming.NamingContextPackage.AlreadyBoundHelper.extract(__userEx.except);
	    }
	}
    }
    //	    Implementation of ::CosNaming::NamingContext::rebind
    public void rebind(org.omg.CosNaming.NameComponent[] n, org.omg.CORBA.Object obj)
        throws org.omg.CosNaming.NamingContextPackage.NotFound, org.omg.CosNaming.NamingContextPackage.CannotProceed, org.omg.CosNaming.NamingContextPackage.InvalidName {
	org.omg.CORBA.Request r = _request("rebind");
	org.omg.CORBA.Any _n = r.add_in_arg();
	org.omg.CosNaming.NameHelper.insert(_n, n);
	org.omg.CORBA.Any _obj = r.add_in_arg();
	_obj.insert_Object(obj);
	r.exceptions().add(org.omg.CosNaming.NamingContextPackage.NotFoundHelper.type());
	r.exceptions().add(org.omg.CosNaming.NamingContextPackage.CannotProceedHelper.type());
	r.exceptions().add(org.omg.CosNaming.NamingContextPackage.InvalidNameHelper.type());
	r.invoke();
	java.lang.Exception __ex = r.env().exception();
	if (__ex instanceof org.omg.CORBA.UnknownUserException) {
	    org.omg.CORBA.UnknownUserException __userEx = (org.omg.CORBA.UnknownUserException) __ex;
	    if (__userEx.except.type().equals(org.omg.CosNaming.NamingContextPackage.NotFoundHelper.type())) {
		throw org.omg.CosNaming.NamingContextPackage.NotFoundHelper.extract(__userEx.except);
	    }
	    if (__userEx.except.type().equals(org.omg.CosNaming.NamingContextPackage.CannotProceedHelper.type())) {
		throw org.omg.CosNaming.NamingContextPackage.CannotProceedHelper.extract(__userEx.except);
	    }
	    if (__userEx.except.type().equals(org.omg.CosNaming.NamingContextPackage.InvalidNameHelper.type())) {
		throw org.omg.CosNaming.NamingContextPackage.InvalidNameHelper.extract(__userEx.except);
	    }
	}
    }
    //	    Implementation of ::CosNaming::NamingContext::rebind_context
    public void rebind_context(org.omg.CosNaming.NameComponent[] n, org.omg.CosNaming.NamingContext nc)
        throws org.omg.CosNaming.NamingContextPackage.NotFound, org.omg.CosNaming.NamingContextPackage.CannotProceed, org.omg.CosNaming.NamingContextPackage.InvalidName {
	org.omg.CORBA.Request r = _request("rebind_context");
	org.omg.CORBA.Any _n = r.add_in_arg();
	org.omg.CosNaming.NameHelper.insert(_n, n);
	org.omg.CORBA.Any _nc = r.add_in_arg();
	org.omg.CosNaming.NamingContextHelper.insert(_nc, nc);
	r.exceptions().add(org.omg.CosNaming.NamingContextPackage.NotFoundHelper.type());
	r.exceptions().add(org.omg.CosNaming.NamingContextPackage.CannotProceedHelper.type());
	r.exceptions().add(org.omg.CosNaming.NamingContextPackage.InvalidNameHelper.type());
	r.invoke();
	java.lang.Exception __ex = r.env().exception();
	if (__ex instanceof org.omg.CORBA.UnknownUserException) {
	    org.omg.CORBA.UnknownUserException __userEx = (org.omg.CORBA.UnknownUserException) __ex;
	    if (__userEx.except.type().equals(org.omg.CosNaming.NamingContextPackage.NotFoundHelper.type())) {
		throw org.omg.CosNaming.NamingContextPackage.NotFoundHelper.extract(__userEx.except);
	    }
	    if (__userEx.except.type().equals(org.omg.CosNaming.NamingContextPackage.CannotProceedHelper.type())) {
		throw org.omg.CosNaming.NamingContextPackage.CannotProceedHelper.extract(__userEx.except);
	    }
	    if (__userEx.except.type().equals(org.omg.CosNaming.NamingContextPackage.InvalidNameHelper.type())) {
		throw org.omg.CosNaming.NamingContextPackage.InvalidNameHelper.extract(__userEx.except);
	    }
	}
    }
    //	    Implementation of ::CosNaming::NamingContext::resolve
    public org.omg.CORBA.Object resolve(org.omg.CosNaming.NameComponent[] n)
        throws org.omg.CosNaming.NamingContextPackage.NotFound, org.omg.CosNaming.NamingContextPackage.CannotProceed, org.omg.CosNaming.NamingContextPackage.InvalidName {
	org.omg.CORBA.Request r = _request("resolve");
	r.set_return_type(org.omg.CORBA.ORB.init().get_primitive_tc(org.omg.CORBA.TCKind.tk_objref));
	org.omg.CORBA.Any _n = r.add_in_arg();
	org.omg.CosNaming.NameHelper.insert(_n, n);
	r.exceptions().add(org.omg.CosNaming.NamingContextPackage.NotFoundHelper.type());
	r.exceptions().add(org.omg.CosNaming.NamingContextPackage.CannotProceedHelper.type());
	r.exceptions().add(org.omg.CosNaming.NamingContextPackage.InvalidNameHelper.type());
	r.invoke();
	java.lang.Exception __ex = r.env().exception();
	if (__ex instanceof org.omg.CORBA.UnknownUserException) {
	    org.omg.CORBA.UnknownUserException __userEx = (org.omg.CORBA.UnknownUserException) __ex;
	    if (__userEx.except.type().equals(org.omg.CosNaming.NamingContextPackage.NotFoundHelper.type())) {
		throw org.omg.CosNaming.NamingContextPackage.NotFoundHelper.extract(__userEx.except);
	    }
	    if (__userEx.except.type().equals(org.omg.CosNaming.NamingContextPackage.CannotProceedHelper.type())) {
		throw org.omg.CosNaming.NamingContextPackage.CannotProceedHelper.extract(__userEx.except);
	    }
	    if (__userEx.except.type().equals(org.omg.CosNaming.NamingContextPackage.InvalidNameHelper.type())) {
		throw org.omg.CosNaming.NamingContextPackage.InvalidNameHelper.extract(__userEx.except);
	    }
	}
	org.omg.CORBA.Object __result;
	__result = r.return_value().extract_Object();
	return __result;
    }
    //	    Implementation of ::CosNaming::NamingContext::unbind
    public void unbind(org.omg.CosNaming.NameComponent[] n)
        throws org.omg.CosNaming.NamingContextPackage.NotFound, org.omg.CosNaming.NamingContextPackage.CannotProceed, org.omg.CosNaming.NamingContextPackage.InvalidName {
	org.omg.CORBA.Request r = _request("unbind");
	org.omg.CORBA.Any _n = r.add_in_arg();
	org.omg.CosNaming.NameHelper.insert(_n, n);
	r.exceptions().add(org.omg.CosNaming.NamingContextPackage.NotFoundHelper.type());
	r.exceptions().add(org.omg.CosNaming.NamingContextPackage.CannotProceedHelper.type());
	r.exceptions().add(org.omg.CosNaming.NamingContextPackage.InvalidNameHelper.type());
	r.invoke();
	java.lang.Exception __ex = r.env().exception();
	if (__ex instanceof org.omg.CORBA.UnknownUserException) {
	    org.omg.CORBA.UnknownUserException __userEx = (org.omg.CORBA.UnknownUserException) __ex;
	    if (__userEx.except.type().equals(org.omg.CosNaming.NamingContextPackage.NotFoundHelper.type())) {
		throw org.omg.CosNaming.NamingContextPackage.NotFoundHelper.extract(__userEx.except);
	    }
	    if (__userEx.except.type().equals(org.omg.CosNaming.NamingContextPackage.CannotProceedHelper.type())) {
		throw org.omg.CosNaming.NamingContextPackage.CannotProceedHelper.extract(__userEx.except);
	    }
	    if (__userEx.except.type().equals(org.omg.CosNaming.NamingContextPackage.InvalidNameHelper.type())) {
		throw org.omg.CosNaming.NamingContextPackage.InvalidNameHelper.extract(__userEx.except);
	    }
	}
    }
    //	    Implementation of ::CosNaming::NamingContext::list
    public void list(int how_many, org.omg.CosNaming.BindingListHolder bl, org.omg.CosNaming.BindingIteratorHolder bi)
    {
	org.omg.CORBA.Request r = _request("list");
	org.omg.CORBA.Any _how_many = r.add_in_arg();
	_how_many.insert_ulong(how_many);
	org.omg.CORBA.Any _bl = r.add_out_arg();
	_bl.type(org.omg.CosNaming.BindingListHelper.type());
	org.omg.CORBA.Any _bi = r.add_out_arg();
	_bi.type(org.omg.CosNaming.BindingIteratorHelper.type());
	r.invoke();
	bl.value = org.omg.CosNaming.BindingListHelper.extract(_bl);
	bi.value = org.omg.CosNaming.BindingIteratorHelper.extract(_bi);
    }
    //	    Implementation of ::CosNaming::NamingContext::new_context
    public org.omg.CosNaming.NamingContext new_context()
    {
	org.omg.CORBA.Request r = _request("new_context");
	r.set_return_type(org.omg.CosNaming.NamingContextHelper.type());
	r.invoke();
	org.omg.CosNaming.NamingContext __result;
	__result = org.omg.CosNaming.NamingContextHelper.extract(r.return_value());
	return __result;
    }
    //	    Implementation of ::CosNaming::NamingContext::bind_new_context
    public org.omg.CosNaming.NamingContext bind_new_context(org.omg.CosNaming.NameComponent[] n)
        throws org.omg.CosNaming.NamingContextPackage.NotFound, org.omg.CosNaming.NamingContextPackage.AlreadyBound, org.omg.CosNaming.NamingContextPackage.CannotProceed, org.omg.CosNaming.NamingContextPackage.InvalidName {
	org.omg.CORBA.Request r = _request("bind_new_context");
	r.set_return_type(org.omg.CosNaming.NamingContextHelper.type());
	org.omg.CORBA.Any _n = r.add_in_arg();
	org.omg.CosNaming.NameHelper.insert(_n, n);
	r.exceptions().add(org.omg.CosNaming.NamingContextPackage.NotFoundHelper.type());
	r.exceptions().add(org.omg.CosNaming.NamingContextPackage.AlreadyBoundHelper.type());
	r.exceptions().add(org.omg.CosNaming.NamingContextPackage.CannotProceedHelper.type());
	r.exceptions().add(org.omg.CosNaming.NamingContextPackage.InvalidNameHelper.type());
	r.invoke();
	java.lang.Exception __ex = r.env().exception();
	if (__ex instanceof org.omg.CORBA.UnknownUserException) {
	    org.omg.CORBA.UnknownUserException __userEx = (org.omg.CORBA.UnknownUserException) __ex;
	    if (__userEx.except.type().equals(org.omg.CosNaming.NamingContextPackage.NotFoundHelper.type())) {
		throw org.omg.CosNaming.NamingContextPackage.NotFoundHelper.extract(__userEx.except);
	    }
	    if (__userEx.except.type().equals(org.omg.CosNaming.NamingContextPackage.AlreadyBoundHelper.type())) {
		throw org.omg.CosNaming.NamingContextPackage.AlreadyBoundHelper.extract(__userEx.except);
	    }
	    if (__userEx.except.type().equals(org.omg.CosNaming.NamingContextPackage.CannotProceedHelper.type())) {
		throw org.omg.CosNaming.NamingContextPackage.CannotProceedHelper.extract(__userEx.except);
	    }
	    if (__userEx.except.type().equals(org.omg.CosNaming.NamingContextPackage.InvalidNameHelper.type())) {
		throw org.omg.CosNaming.NamingContextPackage.InvalidNameHelper.extract(__userEx.except);
	    }
	}
	org.omg.CosNaming.NamingContext __result;
	__result = org.omg.CosNaming.NamingContextHelper.extract(r.return_value());
	return __result;
    }
    //	    Implementation of ::CosNaming::NamingContext::destroy
    public void destroy()
        throws org.omg.CosNaming.NamingContextPackage.NotEmpty {
	org.omg.CORBA.Request r = _request("destroy");
	r.exceptions().add(org.omg.CosNaming.NamingContextPackage.NotEmptyHelper.type());
	r.invoke();
	java.lang.Exception __ex = r.env().exception();
	if (__ex instanceof org.omg.CORBA.UnknownUserException) {
	    org.omg.CORBA.UnknownUserException __userEx = (org.omg.CORBA.UnknownUserException) __ex;
	    if (__userEx.except.type().equals(org.omg.CosNaming.NamingContextPackage.NotEmptyHelper.type())) {
		throw org.omg.CosNaming.NamingContextPackage.NotEmptyHelper.extract(__userEx.except);
	    }
	}
    }

};
