/*
 * @(#)SocketOrChannelAcceptor.java	1.4 10/03/23
 * 
 * Copyright (c) 2006, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
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
