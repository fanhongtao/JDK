/*
 * @(#)CopierManagerImpl.java	1.9 03/12/19
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.corba.se.impl.copyobject ;

import com.sun.corba.se.spi.orb.ORB ;

import com.sun.corba.se.impl.orbutil.DenseIntMapImpl ;

import com.sun.corba.se.spi.copyobject.ObjectCopierFactory ;
import com.sun.corba.se.spi.copyobject.CopierManager ;
import com.sun.corba.se.spi.copyobject.ObjectCopierFactory ;

public class CopierManagerImpl implements CopierManager
{
    private int defaultId ;
    private DenseIntMapImpl map ;
    private ORB orb ;

    public CopierManagerImpl( ORB orb )
    {
	defaultId = 0 ;
	map = new DenseIntMapImpl() ;
	this.orb = orb ;
    }

    public void setDefaultId( int id ) 
    {
	defaultId = id ;
    }

    public int getDefaultId() 
    {
	return defaultId ;
    }

    public ObjectCopierFactory getObjectCopierFactory( int id ) 
    {
	return (ObjectCopierFactory)(map.get( id )) ;
    }

    public ObjectCopierFactory getDefaultObjectCopierFactory()
    {
	return (ObjectCopierFactory)(map.get( defaultId )) ;
    }

    public void registerObjectCopierFactory( ObjectCopierFactory factory, int id ) 
    {
	map.set( id, factory ) ;
    }
}

