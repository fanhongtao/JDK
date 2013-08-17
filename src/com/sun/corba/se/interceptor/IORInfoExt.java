/*
 * @(#)IORInfoExt.java	1.4 01/12/03
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.corba.se.interceptor;

public interface IORInfoExt
{
    int getServerPort(String type)
	throws
	    UnknownType;
}
