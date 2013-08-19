/*
 * @(#)RequestHandler.java	1.20 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.corba.se.internal.core;

import com.sun.corba.se.internal.ior.ObjectKey;

/**
 * A RequestHandler is responsible for finding the appropriate
 * server subcontracts to dispatch the incoming request to. There
 * exists one RequestDispatcher for a GIOPServer.
 */
public interface RequestHandler {

    /**
     * Process this request and return the response.
     */
    public ServerResponse process(ServerRequest request)
	throws Throwable;

    /**
     * Process a locate request.
     */
    public IOR locate(ObjectKey key);
}
