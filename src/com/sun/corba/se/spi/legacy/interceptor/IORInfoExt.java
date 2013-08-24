/*
 * @(#)IORInfoExt.java	1.10 05/11/17
 *
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.corba.se.spi.legacy.interceptor;

import com.sun.corba.se.spi.oa.ObjectAdapter;

public interface IORInfoExt
{
    public int getServerPort(String type)
	throws
	    UnknownType;

    public ObjectAdapter getObjectAdapter();
}

// End of file.
