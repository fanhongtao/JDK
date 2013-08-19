/*
 * @(#)ServerRequest.java	1.25 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.corba.se.internal.core;

import org.omg.CORBA.SystemException;
import org.omg.CORBA.portable.ResponseHandler;
import org.omg.CORBA.portable.UnknownException;

/**
 * ServerRequest represents the incoming request on the server-side.
 */
public interface ServerRequest extends MarshalInputStream, Request, ResponseHandler {

    /**
     * Create a normal (invocation return) response for this request.
     */
    public ServerResponse
	createResponse(ServiceContexts svc);


    /**
     * Create a response that represents an user exception.
     *
     */
    public ServerResponse
	createUserExceptionResponse(ServiceContexts svc);


    /**
     * Create a response that represents an unknown exception.
     *
     */
    public ServerResponse
	createUnknownExceptionResponse(UnknownException ex);


    /**
     * Create a response that represents a system exception.
     */
    public ServerResponse
        createSystemExceptionResponse(SystemException ex,
				      ServiceContexts svc);


    /**
     * Create a response that represents a location forward.
     */
    public ServerResponse
	createLocationForward(IOR ior, ServiceContexts svc);

    public boolean executeReturnServantInResponseConstructor();

    public void setExecuteReturnServantInResponseConstructor(boolean b);

    public boolean executeRemoveThreadInfoInResponseConstructor();

    public void setExecuteRemoveThreadInfoInResponseConstructor(boolean b);

    /**
     * Returns true if we are to execute Portable Interceptors ending
     * points in the ServerResponse constructor.
     */
    public boolean executePIInResponseConstructor();

    /**
     * Pass in whether we are to execute Portable Interceptors ending
     * points in the ServerResponse constructor.
     */
    public void setExecutePIInResponseConstructor( boolean b );
}
