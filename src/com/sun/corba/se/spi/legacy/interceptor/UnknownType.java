/*
 * @(#)UnknownType.java	1.9 05/11/17
 *
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.corba.se.spi.legacy.interceptor;

public class UnknownType
    extends
	Exception
{
    public UnknownType()
    {
	super();
    }

    public UnknownType(String msg)
    {
	super(msg);
    }
}
