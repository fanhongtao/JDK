/*
 * @(#)Response.java	1.21 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.corba.se.internal.core;

import org.omg.CORBA.SystemException;

/**
 * Response represents a GIOP response received from the server
 * object.
 */
public interface Response {

    /**
     * Is this response a system exception?
     */
    public boolean isSystemException();

    /**
     * Is this response a user exception?
     */
    public boolean isUserException();

    /**
     * Is this a location forward?
     */
    public boolean isLocationForward();

    /**
     * IOR from the forward request
     */
    IOR getForwardedIOR();

    /**
     * Get the request id corresponding to this response.
     */
    public int getRequestId();

    /**
     * Get the service context information for this response.
     */
    public ServiceContexts getServiceContexts();

    /**
     * Get the SystemException object if this response unmarshalled
     * off the wire was a SystemException.
     */
    public SystemException getSystemException();

    /**
     * Check to see if the response is local.
     */
    public boolean isLocal();

    /**
     * Set Connection for the Request
     */
    // COMMENT(Ram J) connections are not allowed to be reset.
    //public void setConnection(com.sun.corba.se.internal.iiop.Connection conn);

    /**
     * Obtain connection for the Request.
     */
    public com.sun.corba.se.internal.iiop.Connection getConnection();


}
