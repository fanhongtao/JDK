/*
 * @(#)ObjectImpl.java	1.32 00/07/19
 *
 * Copyright 1997-2000 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
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


    /**
     * Provides a reference to the vendor-specific Delegate for this ObjectImpl. 
     * @return the Delegate contained in this ObjectImpl instance. 
     */
    public Delegate _get_delegate() {
        if (__delegate == null)
	    throw new BAD_OPERATION("The delegate has not been set!");
        return __delegate;
    }


    /** 
     * Sets the Delegate contained in this ObjectImpl instance. 
     * @delegate implementation delegate used for an instance of this object.
     */
    public void _set_delegate(Delegate delegate) {
        __delegate = delegate;
    }

    /** 
     * Provides a string array containing the repository ids for this object.
     * @return the array of all repository identifiers supported by this
     * ObjectImpl instance (e.g. For a stub, _ids() will return information
     * about all interfaces supported by the stub).
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

    /** 
     * Provides reference to the ORB associated with this object and its delegate.
     * @return the ORB instance which created the Delegate contained in
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
     * Checks whether this object is process-local.
     * @return true if this object is process-local.
     */
    public boolean _is_local() {
        return _get_delegate().is_local(this);
    }

    /**
     * Returns a Java reference to the servant which should be used for this 
     * request. _servant_preinvoke() is invoked by a local stub.
     * If a ServantObject object is returned, then its servant field 
     * has been set to an object of the expected type (Note: the object may 
     * or may not be the actual servant instance). The local stub may cast 
     * the servant field to the expected type, and then invoke the operation 
     * directly. The ServantRequest object is valid for only one invocation, 
     * and cannot be used for more than one invocation.
     *
     * @param operation a string containing the operation name.
     * The operation name corresponds to the operation name as it would be 
     * encoded in a GIOP request.
     *
     * @param expectedType a Class object representing the expected type of the servant.
     * The expected type is the Class object associated with the operations 
     * class of the stub's interface (e.g. A stub for an interface Foo, 
     * would pass the Class object for the FooOperations interface).
     *
     * @return a ServantObject object.
     * The method may return a null value if it does not wish to support 
     * this optimization (e.g. due to security, transactions, etc). 
     * The method must return null if the servant is not of the expected type.
     */
    public ServantObject _servant_preinvoke(String operation,
                                            Class expectedType) {
        return _get_delegate().servant_preinvoke(this, operation,
						 expectedType);
    }

    /**
     * Is invoked by the local stub after the operation 
     * has been invoked on the local servant.
     * This method must be called if _servant_preinvoke() returned a non-null 
     * value, even if an exception was thrown by the servant's method. 
     * For this reason, the call to _servant_postinvoke() should be placed 
     * in a Java finally clause.
     *
     *
     * @param servant the instance of the ServantObject returned from 
     *  the servant_preinvoke() method.
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
     *
     * @param operation         a String giving the name of the object.
     * @param responseExpected  a boolean -- true if request is not one way.
     * @return an OutputStream object for dispatching the request.
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
     *
     * @param output  an OutputStream object for dispatching the request.
     * @return an InputStream object for reading the response.
     * @throws ApplicationException an exception -- thrown if the invocation meets application-defined exception.
     * @throws RemarshalException an exception -- thrown if the invocation leads to a remarshalling error.
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
     *
     * @param input  an InputStream object that represents handle to the reply.
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

