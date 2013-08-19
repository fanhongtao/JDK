/*
 * @(#)POACurrent.java	1.15 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.corba.se.internal.POA;

import java.util.*;
import org.omg.CORBA.INTERNAL;
import org.omg.CORBA.CompletionStatus;
import org.omg.PortableServer.CurrentPackage.NoContext;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.Servant;
import org.omg.PortableServer.ServantLocatorPackage.CookieHolder;

//Needs to be turned into LocalObjectImpl.

public class POACurrent extends org.omg.CORBA.portable.ObjectImpl
    implements org.omg.PortableServer.Current 
{
    private POAORB orb;
    private ThreadLocal threadLocal 
    = new ThreadLocal()
    { 
        protected Object initialValue() {
            return new Stack();
        }
    };


    // This is used for debugging only.
    public int size()
    {
	return ((Stack) threadLocal.get()).size();
    }

    POACurrent(POAORB orb)
    {
	this.orb = orb;
    }

    public String[] _ids()
    {
        String[] ids = new String[1];
        ids[0] = "IDL:omg.org/PortableServer/Current:1.0";
        return ids;
    }

    //
    // Standard OMG operations.
    //

    public POA get_POA()
        throws 
	    NoContext
    {
        POA poa = peekThrowNoContext().poa();
	throwNoContextIfNull(poa);
	return poa;
    }

    public byte[] get_object_id()
        throws 
	    NoContext
    {
	byte[] objectid = peekThrowNoContext().id();
	throwNoContextIfNull(objectid);
	return objectid;
    }

    //
    // Implementation operations used by POA package.
    //

    public POAImpl getPOA()
    {
        POAImpl poa = (POAImpl) peekThrowInternal().poa();
	throwInternalIfNull(poa);
	return poa;
    }

    public byte[] getObjectId()
    {
	byte[] objectid = peekThrowInternal().id();
	throwInternalIfNull(objectid);
	return objectid;
    }

    Servant getServant()
    {
	Servant servant = peekThrowInternal().getServant();
	// If is OK for the servant to be null.
	// This could happen if POAImpl.getServant is called but
	// POAImpl.internalGetServant throws an exception.
	return servant;
    }

    CookieHolder getCookieHolder()
    {
	CookieHolder cookieHolder = peekThrowInternal().getCookieHolder();
	throwInternalIfNull(cookieHolder);
	return cookieHolder;
    }

    // This is public so we can test the stack balance.
    // It is not a security hole since this same info can be obtained from 
    // PortableInterceptors.
    public String getOperation()
    {
	String operation = peekThrowInternal().getOperation();
	throwInternalIfNull(operation);
	return operation;
    }

    void setServant(Servant servant)
    {
	peekThrowInternal().setServant( servant );
    }

    void setPreInvokeCalled()
    {
	peekThrowInternal().setPreInvokeCalled();
    }

    boolean preInvokeCalled()
    {
	return peekThrowInternal().preInvokeCalled();
    }

    void setPostInvokeCalled()
    {
	peekThrowInternal().setPostInvokeCalled();
    }

    boolean postInvokeCalled()
    {
	return peekThrowInternal().postInvokeCalled();
    }

    void addThreadInfo(POA poa, byte[] id, CookieHolder cookieHolder,
		       String operation)
    {
        //The first get() initializes the stack
	Stack stack = (Stack) threadLocal.get();
	stack.push(new InvocationInfo(poa, id, cookieHolder, operation));
    }

    void removeThreadInfo()
    {
        try {
            ((Stack) threadLocal.get()).pop();
        } catch(EmptyStackException e){
	    // REVISIT: It doesn't seem that doing extra pops should be OK.
            //Should not occur. If it does we are still OK.
        }
    }

    //
    // Class utilities.
    //

    private InvocationInfo peekThrowNoContext()
	throws
	    NoContext
    {
	InvocationInfo invocationInfo = null;
	try {
	    invocationInfo = 
		(InvocationInfo) ((Stack) threadLocal.get()).peek();
	} catch (EmptyStackException e) {
	    throw new NoContext();
	}
	return invocationInfo;
    }

    private InvocationInfo peekThrowInternal()
    {
	InvocationInfo invocationInfo = null;
	try {
	    invocationInfo = 
		(InvocationInfo) ((Stack) threadLocal.get()).peek();
	} catch (EmptyStackException e) {
	    // The completion status is maybe because this could happen
	    // after the servant has been invoked.
	    throw new INTERNAL("POACurrent: unbalanced threadLocal stack.",
			       MinorCodes.POACURRENT_UNBALANCED_STACK,
			       CompletionStatus.COMPLETED_MAYBE);

	}
	return invocationInfo;
    }

    private void throwNoContextIfNull(Object o)
	throws
	    NoContext
    {
	if ( o == null ) {
	    throw new NoContext();
	}
    }

    private void throwInternalIfNull(Object o)
    {
	if ( o == null ) {
	    throw new INTERNAL("POACurrent: null field.",
			       MinorCodes.POACURRENT_NULL_FIELD,
			       CompletionStatus.COMPLETED_MAYBE);
	}
    }
}
