/*
 * @(#)UnknownType.java	1.8 03/12/19
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
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
