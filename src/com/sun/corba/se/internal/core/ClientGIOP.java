/*
 * @(#)ClientGIOP.java	1.22 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.corba.se.internal.core;

import com.sun.corba.se.internal.corba.ClientDelegate;
import com.sun.corba.se.internal.core.IOR;
import com.sun.corba.se.internal.iiop.Connection ;

/**
 * ClientGIOP contains the client-side GIOP APIs. There can be
 * multiple implementations of this GIOP transport.
 */
public interface ClientGIOP {
    /* (KMC) I have removed the createRequest APIs.  The problem was that
     * this API required the ServiceContext at too early a stage in the 
     * process of constructing a request, since transport level information
     * like the connection and the request ID is required for constructing the
     * ServiceContext.  All of the control logic is now in the subcontract.
     */

    /** Get a Connection that can be used to transmit a request.
     */
    public Connection getConnection( IOR ior ) ;

    /**
     * Allocate a new request id.
     */
    public int allocateRequestId();


    /**
     * Send a locate request.
     */
    public IOR locate(IOR ior);
}

