/*
 * @(#)DelegateInvocationHandlerImpl.java	1.8 04/07/27
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.corba.se.spi.orbutil.proxy ;

import java.io.Serializable ;

import java.util.Map ;
import java.util.LinkedHashMap ;
  
import java.lang.reflect.Proxy ;
import java.lang.reflect.Method ;
import java.lang.reflect.InvocationHandler ;
import java.lang.reflect.InvocationTargetException ;

public abstract class DelegateInvocationHandlerImpl 
{
    private DelegateInvocationHandlerImpl() {}

    public static InvocationHandler create( final Object delegate )
    {
	return new InvocationHandler() {
	    public Object invoke( Object proxy, Method method, Object[] args )
		throws Throwable
	    {
		// This throws an IllegalArgument exception if the delegate
		// is not assignable from method.getDeclaring class.
		try {
		    return method.invoke( delegate, args ) ;
		} catch (InvocationTargetException ite) {
		    // Propagate the underlying exception as the
		    // result of the invocation
		    throw ite.getCause() ;
		}
	    }
	} ;
    }
}
