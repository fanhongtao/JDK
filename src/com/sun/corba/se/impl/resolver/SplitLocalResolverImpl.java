/*
 * @(#)SplitLocalResolverImpl.java	1.2 03/12/19
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.corba.se.impl.resolver ;

import com.sun.corba.se.spi.orbutil.closure.Closure ;

import com.sun.corba.se.spi.resolver.Resolver ;
import com.sun.corba.se.spi.resolver.LocalResolver ;

public class SplitLocalResolverImpl implements LocalResolver 
{
    private Resolver resolver ;
    private LocalResolver localResolver ;

    public SplitLocalResolverImpl( Resolver resolver, 
	LocalResolver localResolver ) 
    {
	this.resolver = resolver ;
	this.localResolver = localResolver ;
    }

    public void register( String name, Closure closure ) 
    {
	localResolver.register( name, closure ) ;
    }

    public org.omg.CORBA.Object resolve( String name ) 
    {
	return resolver.resolve( name ) ;
    }

    public java.util.Set list() 
    { 
	return resolver.list() ;
    }
}

