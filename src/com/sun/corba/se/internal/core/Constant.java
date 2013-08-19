/*
 * @(#)Constant.java	1.4 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.corba.se.internal.core ;

import com.sun.corba.se.internal.core.Closure ;

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

