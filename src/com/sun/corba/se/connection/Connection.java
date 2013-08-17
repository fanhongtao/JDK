/*
 * @(#)Connection.java	1.3 01/12/03
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.corba.se.connection;

/**
 * This interface represents the connection on which a request is made.
 */

public interface Connection
{
    public java.net.Socket getSocket();
}
