/*
 * @(#)EndPoint.java	1.23 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.corba.se.internal.core;

/**
 * EndPoint is an abstraction of a GIOP listening port.
 */
public interface EndPoint
{
    // Endpoint types known in advance.
    // If you change the value of this constant then update
    // activation.idl accordingly.  It has a duplicate definition
    // to avoid a compilation dependency.
    public static final String IIOP_CLEAR_TEXT = "IIOP_CLEAR_TEXT";

    /**
     * Get the type of this end point (e.g., "CLEAR_TEXT", "SSL", ...)
     */
    public String getType();

    /**
     * Get the port number of this end point.
     */
    public int getPort();

    /**
     * Get the ORBD's proxy port of this end point.
     */
    public int getLocatorPort();

    /**
     * Set the ORBD's proxy port of this end point.
     */
    public void setLocatorPort(int port);

    /**
     * Get the host name of this end point. Subcontracts must use this
     * instead of InetAddress.getHostName() because this would take
     * into account the value of the ORBServerHost property.
     */
    public String getHostName();

}
