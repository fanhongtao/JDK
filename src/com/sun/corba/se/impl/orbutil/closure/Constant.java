/*
 * @(#)Constant.java	1.7 04/06/21
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
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

