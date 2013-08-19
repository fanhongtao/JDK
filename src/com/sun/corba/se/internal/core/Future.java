/*
 * @(#)Future.java	1.6 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.corba.se.internal.core ;

import com.sun.corba.se.internal.core.Closure ;

public class Future implements Closure {
    private boolean evaluated ;
    private Closure closure ;
    private Object value ;

    public Future( Closure value )
    {
	this.evaluated = false ;
	this.closure = (Closure)value ;
	this.value = null ;
    }
    
    public synchronized Object evaluate() 
    {
	if (!evaluated) {
	    evaluated = true ;
	    value = closure.evaluate() ;
	}

	return value ;
    }
}
