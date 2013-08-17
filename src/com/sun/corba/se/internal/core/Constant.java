/*
 * @(#)Constant.java	1.3 01/12/04
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
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

