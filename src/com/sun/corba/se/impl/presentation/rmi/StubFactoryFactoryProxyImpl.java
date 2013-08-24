/*
 * @(#)StubFactoryFactoryProxyImpl.java	1.4 05/11/17
 *
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.corba.se.impl.presentation.rmi;

import com.sun.corba.se.spi.presentation.rmi.PresentationManager ;

public class StubFactoryFactoryProxyImpl extends StubFactoryFactoryDynamicBase 
{
    public PresentationManager.StubFactory makeDynamicStubFactory( 
	PresentationManager pm, PresentationManager.ClassData classData, 
	ClassLoader classLoader ) 
    {
	return new StubFactoryProxyImpl( classData, classLoader ) ;
    }
}
