/*
 * @(#)StubFactoryBase.java	1.7 04/07/27
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.corba.se.impl.presentation.rmi ;

import javax.rmi.CORBA.Tie ;

import java.lang.reflect.InvocationHandler ;
import java.lang.reflect.Proxy ;

import com.sun.corba.se.spi.presentation.rmi.PresentationManager ;
import com.sun.corba.se.spi.presentation.rmi.DynamicStub ;
import com.sun.corba.se.spi.presentation.rmi.StubAdapter ;

import com.sun.corba.se.spi.orbutil.proxy.InvocationHandlerFactory ;
import com.sun.corba.se.spi.orbutil.proxy.LinkedInvocationHandler ;

public abstract class StubFactoryBase implements PresentationManager.StubFactory 
{
    private String[] typeIds = null ;

    protected final PresentationManager.ClassData classData ;

    protected StubFactoryBase( PresentationManager.ClassData classData ) 
    {
	this.classData = classData ;
    }

    public synchronized String[] getTypeIds()
    {
	if (typeIds == null) {
	    if (classData == null) {
		org.omg.CORBA.Object stub = makeStub() ;
		typeIds = StubAdapter.getTypeIds( stub ) ;
	    } else {
		typeIds = classData.getTypeIds() ;
	    }
	}

	return typeIds ;
    }
}
