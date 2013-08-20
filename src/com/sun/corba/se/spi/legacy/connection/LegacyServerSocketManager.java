/*
 * @(#)LegacyServerSocketManager.java	1.42 03/12/19
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.corba.se.spi.legacy.connection;

import java.util.Collection;

import com.sun.corba.se.spi.legacy.connection.LegacyServerSocketEndPointInfo;
import com.sun.corba.se.spi.transport.SocketOrChannelAcceptor;

/**
 * @author Harold Carr
 */
public interface LegacyServerSocketManager
{
    public int legacyGetTransientServerPort(String type);
    public int legacyGetPersistentServerPort(String socketType);
    public int legacyGetTransientOrPersistentServerPort(String socketType);

    public LegacyServerSocketEndPointInfo legacyGetEndpoint(String name);

    public boolean legacyIsLocalServerPort(int port);
}
    
// End of file.
