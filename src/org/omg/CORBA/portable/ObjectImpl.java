/*
 * @(#)ObjectImpl.java	1.26 00/09/27
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
package org.omg.CORBA.portable;

import org.omg.CORBA.Request;
import org.omg.CORBA.NamedValue;
import org.omg.CORBA.NVList;
import org.omg.CORBA.ExceptionList;
import org.omg.CORBA.ContextList;
import org.omg.CORBA.Context;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.BAD_OPERATION;
import org.omg.CORBA.SystemException;


/** The ObjectImpl class provides default implementations of the
 *  org.omg.CORBA.Object methods. All method implementations are forwarded to
 *  a Delegate object stored in the ObjectImpl instance.
 *  ObjectImpl is the common base class for all stub classes.
 *  ObjectImpl allows for portable stubs because the Delegate can be
 *  implemented by a different vendor-specific ORB.
 */

abstract public class ObjectImpl implements org.omg.CORBA.Object
{
    private transient Delegate __delegate;


    /** return the Delegate contained in this ObjectImpl instance. */
    public Delegate _get_delegate() {
        if (__delegate == null)
	    throw new BAD_OPERATION("The delegate has not been set!");
        return __delegate;
    }


    /** set the Delegate contained in this ObjectImpl instance. */
    public void _set_delegate(Delegate delegate) {
        __delegate = delegate;
    }

    /** return the array of all repository identifiers supported by this
        ObjectImpl instance (e.g. For a stub, _ids() will return information
        about all interfaces supported by the stub).
    */
    public abstract String[] _ids();


    /** default implementation of the org.omg.CORBA.Object method. */
    public org.omg.CORBA.Object _duplicate() {
        return _get_delegate().duplicate(this);
    }

    /** default implementation of the org.omg.CORBA.Object method. */
    public void _release() {
        _get_delegate().release(this);
    }

    /** default implementation of the org.omg.CORBA.Object method. */
    public boolean _is_a(String repository_id) {
        return _get_delegate().is_a(this, repository_id);
    }

    /** default implementation of the org.omg.CORBA.Object method. */
    public boolean _is_equivalent(org.omg.CORBA.Object that) {
        return _get_delegate().is_equivalent(this, that);
    }

    /** default implementation of the org.omg.CORBA.Object method. */
    public boolean _non_existent() {
        return _get_delegate().non_existent(this);
    }

    /** default implementation of the org.omg.CORBA.Object method. */
    public int _hash(int maximum) {
        return _get_delegate().hash(this, maximum);
    }

    /** default implementation of the org.omg.CORBA.Object method. */
    public Request _request(String operation) {
        return _get_delegate().request(this, operation);
    }

    /** default implementation of the org.omg.CORBA.Object method. */
    public Request _create_request(Context ctx,
				 String operation,
				 NVList arg_list,
				 NamedValue result) {
        return _get_delegate().create_request(this,
					  ctx,
					  operation,
					  arg_list,
					  result);
    }

    /** default implementation of the org.omg.CORBA.Object method. */
    public Request _create_request(Context ctx,
				 String operation,
				 NVList arg_list,
				 NamedValue result,
				 ExceptionList exceptions,
				 ContextList contexts) {
        return _get_delegate().create_request(this,
					  ctx,
					  operation,
					  arg_list,
					  result,
					  exceptions,
					  contexts);
    }

    /** default implementation of the org.omg.CORBA.Object method. */
    public org.omg.CORBA.Object _get_interface_def() 
    {
	// First try to call the delegate implementation class's
	// "Object get_interface_def(..)" method (will work for JDK1.2 ORBs).
	// Else call the delegate implementation class's
	// "InterfaceDef get_interface(..)" method using reflection
	// (will work for pre-JDK1.2 ORBs).

        org.omg.CORBA.portable.Delegate delegate = _get_delegate();         
        try {
	    // If the ORB's delegate class does not implement 
	    // "Object get_interface_def(..)", this will call 
	    // get_interface_def(..) on portable.Delegate. 
            return delegate.get_interface_def(this);
        } 
	catch( org.omg.CORBA.NO_IMPLEMENT ex ) {
	    // Call "InterfaceDef get_interface(..)" method using reflection.
            try {
		Class[] argc = { org.omg.CORBA.Object.class };
	        java.lang.reflect.Method meth = 
                     delegate.getClass().getMethod("get_interface", argc);
		Object[] argx = { this };
                return (org.omg.CORBA.Object)meth.invoke(delegate, argx);
	    }
            catch( java.lang.reflect.InvocationTargetException exs ) {
                Throwable t = exs.getTargetException();
                if (t instanceof Error) {
                    throw (Error) t;
                }
                else if (t instanceof RuntimeException) {
                    throw (RuntimeException) t;
                }
                else {
                    throw new org.omg.CORBA.NO_IMPLEMENT();
                }
            } catch( RuntimeException rex ) {
		throw rex;
	    } catch( Exception exr ) {
                throw new org.omg.CORBA.NO_IMPLEMENT();
            }
        }
    }

    /** return the ORB instance which created the Delegate contained in
     *  this ObjectImpl.
     */
    public org.omg.CORBA.ORB _orb() {
        return _get_delegate().orb(this);
    }


    /**
     *
     */
    public org.omg.CORBA.Policy _get_policy(int policy_type) {
        return _get_delegate().get_policy(this, policy_type);
    }

    /**
     *
     */
    public org.omg.CORBA.DomainManager[] _get_domain_managers() {
        return _get_delegate().get_domain_managers(this);
    }

    /**
     *
     */
    public org.omg.CORBA.Object
	_set_policy_override(org.omg.CORBA.Policy[] policies,
			     org.omg.CORBA.SetOverrideType set_add) {
	    return _get_delegate().set_policy_override(this, policies,
						       set_add);
    }

    /**
     *
     */
    public boolean _is_local() {
        return _get_delegate().is_local(this);
    }

    /**
     *
     */
    public ServantObject _servant_preinvoke(String operation,
                                            Class expectedType) {
        return _get_delegate().servant_preinvoke(this, operation,
						 expectedType);
    }

    /**
     *
     */
    public void _servant_postinvoke(ServantObject servant) {
        _get_delegate().servant_postinvoke(this, servant);
    }

    /*
     * The following methods were added by orbos/98-04-03: Java to IDL
     * Mapping. These are used by RMI over IIOP.
     */

    /**
     * _request is called by a stub to obtain an OutputStream for
     * marshaling arguments. The stub must supply the operation name,
     * and indicate if a response is expected (i.e is this a oneway
     * call).
     */
    public OutputStream _request(String operation,
				 boolean responseExpected) {
	return _get_delegate().request(this, operation, responseExpected);
    }

    /**
     * _invoke is called to invoke an operation. The stub provides an
     * OutputStream that was previously returned by a _request()
     * call. _invoke returns an InputStream which contains the
     * marshaled reply. If an exception occurs, _invoke may throw an
     * ApplicationException object which contains an InputStream from
     * which the user exception state may be unmarshaled.
     */
    public InputStream _invoke(OutputStream output)
	throws ApplicationException, RemarshalException {
	return _get_delegate().invoke(this, output);
    }

    /**
     * _releaseReply may optionally be called by a stub to release a
     * reply stream back to the ORB when the unmarshaling has
     * completed. The stub passes the InputStream returned by
     * _invoke() or ApplicationException.getInputStream(). A null
     * value may also be passed to _releaseReply, in which case the
     * method is a noop.
     */
    public void _releaseReply(InputStream input) {
	_get_delegate().releaseReply(this, input);
    }

    public String toString() {
		if ( __delegate != null )
			return __delegate.toString(this);
		else
			return getClass().getName() + ": no delegate set";
    }

    public int hashCode() {
		if ( __delegate != null )
			return __delegate.hashCode(this);
		else
			return System.identityHashCode(this);
    }

    public boolean equals(java.lang.Object obj) {
		if ( __delegate != null )
			return __delegate.equals(this, obj);
		else
			return (this==obj);
    }
}

