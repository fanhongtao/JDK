/*
 * @(#)ReflectiveCopyException.java	1.4 03/12/19
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.sun.corba.se.spi.copyobject ;

public class ReflectiveCopyException extends Exception {
    public ReflectiveCopyException()
    {
	super() ;
    }

    public ReflectiveCopyException( String msg )
    {
	super( msg ) ;
    }

    public ReflectiveCopyException( String msg, Throwable t )
    {
	super( msg, t ) ;
    }
}
