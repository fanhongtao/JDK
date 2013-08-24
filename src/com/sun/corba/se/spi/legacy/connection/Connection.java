/*
 * @(#)Connection.java	1.8 05/11/17
 *
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.corba.se.spi.legacy.connection;

/**
 * This interface represents the connection on which a request is made.
 */

public interface Connection
{
    public java.net.Socket getSocket();
}
