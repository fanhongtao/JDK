/*
 * @(#)Connection.java	1.7 03/12/19
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
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
