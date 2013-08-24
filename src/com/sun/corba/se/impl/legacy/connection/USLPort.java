/*
 * @(#)USLPort.java	1.7 05/11/17
 * 
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.corba.se.impl.legacy.connection;

public class USLPort
{
    private String type;
    private int    port;

    public USLPort (String type, int port)
    {
	this.type = type;
	this.port = port;
    }

    public String getType  () { return type; }
    public int    getPort  () { return port; }
    public String toString () { return type + ":" + port; }
}

// End of file.

