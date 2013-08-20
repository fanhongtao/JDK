/*
 * @(#)LocalResolverImpl.java	1.7 04/06/21
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.corba.se.impl.resolver ;

import com.sun.corba.se.spi.resolver.LocalResolver ;
import com.sun.corba.se.spi.orbutil.closure.Closure ;

public class LocalResolverImpl implements LocalResolver {
    java.util.Map nameToClosure = new java.util.HashMap() ;

    public synchronized org.omg.CORBA.Object resolve( String name ) 
    {
	Closure cl = (Closure)nameToClosure.get( name ) ;
	if (cl == null)
	    return null ;

	return (org.omg.CORBA.Object)(cl.evaluate()) ;
    }

    public synchronized java.util.Set list() 
    {
	return nameToClosure.keySet() ;
    }

    public synchronized void register( String name, Closure closure ) 
    {
	nameToClosure.put( name, closure ) ;
    }
}
