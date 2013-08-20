/*
 * @(#)ClosureFactory.java	1.2 03/12/19
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.corba.se.spi.orbutil.closure ;

import com.sun.corba.se.impl.orbutil.closure.Constant ;
import com.sun.corba.se.impl.orbutil.closure.Future ;

public abstract class ClosureFactory {
    private ClosureFactory() {}

    public static Closure makeConstant( Object value )
    {
	return new Constant( value ) ;
    }

    public static Closure makeFuture( Closure value )
    {
	return new Future( value ) ;
    }
}
