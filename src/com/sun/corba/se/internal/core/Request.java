/*
 * @(#)Request.java	1.21 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.corba.se.internal.core;

/**
 * Request represents a method invocation request. There are two types
 * of Request -- one that is used on the client side to invoke into
 * the GIOP layer and one that is used to dispatch up to the
 * subcontract.
 */
public interface Request {

    /**
     * Get the id assigned to this request object. These ids
     * are used by the transaction service hooks.
     */
    public int getRequestId();

    /**
     * Is this request object for a oneway operation?
     */
    public boolean isOneWay();

    /**
     * Get the service context information for this request.
     */
    public ServiceContexts getServiceContexts();

    /**
     * Get the operation name for this request object.
     */
    public String getOperationName();

    /**
     * Get the object key.
     */
    public com.sun.corba.se.internal.ior.ObjectKey getObjectKey();

    /**
     * Check to see if the request is local.
     */
    public boolean isLocal();


    /**
     * Obtain connection for the Request.
     */
    public com.sun.corba.se.internal.iiop.Connection getConnection();

    /**
     * Obtain the GIOP version of the Request
     */
    public GIOPVersion getGIOPVersion();

}
