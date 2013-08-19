/*
 * @(#)UnknownType.java	1.5 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.corba.se.interceptor;

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
