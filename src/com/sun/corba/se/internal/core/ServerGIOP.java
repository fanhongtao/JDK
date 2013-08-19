/*
 * @(#)ServerGIOP.java	1.25 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.corba.se.internal.core;

import java.net.InetAddress;
import java.util.Collection;

/**
 * ServerGIOP contains the server-side GIOP APIs.
 */
public interface ServerGIOP
{

    /**
     * Get the default end point, if you don't care about an end point
     * on a particular port.
     *
     * NOTE: If getDefaultEndpoint returns null, initEndpoints() needs
     *       to be called after which getDefaultEndpoint will return
     *       the default end point.
     */
    public EndPoint getDefaultEndpoint();

    /**
     * Initialize the vector of end points.
     */
    public void initEndpoints();

    /**
     * Get an EndPoint for the specified type, listenPort and local address.
     *
     * Type must be one which has a socket factory
     * (e.g., EndPoint.IIOP_CLEAR_TEXT default or user supplied "SSL").
     *
     * If listenPort == 0, a listening port will be assigned.
     *
     * If addr == null, InetAddress.getLocalHost() is used.
     *
     * If an EndPoint at the specified port/address does not exist,
     * then it will be created.
     */
    public EndPoint getEndpoint(String type, int listenPort, InetAddress addr);

    /**
     * Get an EndPoint for the bootstrap naming service, at the specified port.
     * If listenPort == 0 and this is the first time getBootstrapEndpoint was
     * invoked, then an EndPoint at the default port of 900 will be created.
     * If listenPort == 0 and this is not the first time getBootstrapEndpoint
     * was called, then the existing bootstrap EndPoint will be returned.
     */
    public EndPoint getBootstrapEndpoint(int port);

    /**
     * All incoming requests will be dispatched to the RequestHandler.
     */
    public void setRequestHandler(RequestHandler handler);

    /**
     * Return the RequestHandler.
     */
    public RequestHandler getRequestHandler();

    /**
     * Return port number for given type.
     */
    public int getServerPort (String socketType);

    /**
     * Return port number of locator for the given type.
     */
    public int getPersistentServerPort (String socketType);

    /**
     * Return number of server ports.
     */
    public Collection getServerEndpoints ();

}
    
