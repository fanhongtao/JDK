/*
 * @(#)RequestInfoExt.java	1.5 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.corba.se.interceptor;

import com.sun.corba.se.connection.Connection;

/**
 * This interface is implemented by our implementation of 
 * PortableInterceptor.ClientRequestInfo and
 * PortableInterceptor.ServerRequestInfo. <p>
 *
 */

public interface RequestInfoExt
{
    /**
     * @return The connection on which the request is made.
     *         The return value will be null when a local transport 
     *         is used.
     */
    public Connection connection();
}
