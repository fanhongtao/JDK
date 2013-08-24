/*
 * @(#)Constant.java	1.8 05/11/17
 *
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.corba.se.impl.orbutil.closure ;

import com.sun.corba.se.spi.orbutil.closure.Closure ;

public class Constant implements Closure {
    private Object value ;

    public Constant( Object value ) 
    {
	this.value = value ;
    }

    public Object evaluate() 
    {
	return value ;
    }
}

