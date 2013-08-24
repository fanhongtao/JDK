/*
 * @(#)SocketOrChannelAcceptor.java	1.3 05/11/17
 * 
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.corba.se.spi.transport;

import java.net.ServerSocket;

/**
 * @author Harold Carr
 */
public interface SocketOrChannelAcceptor
{
    public ServerSocket getServerSocket();
}

// End of file.
