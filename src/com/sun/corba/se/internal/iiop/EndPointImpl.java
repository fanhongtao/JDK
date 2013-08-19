/*
 * @(#)EndPointImpl.java	1.26 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.corba.se.internal.iiop;

import com.sun.corba.se.connection.EndPointInfo;
import com.sun.corba.se.internal.core.EndPoint;

public class EndPointImpl 
    implements 
	EndPoint,
	EndPointInfo
{
    public EndPointImpl(String type, int port, String hostname) {
	this.type = type;
	this.port = port;
	this.hostname = hostname;
	this.locatorPort = -1;
    }

    public String getType() {
	return type;
    }

    public int getPort() {
	return port;
    }

    public int getLocatorPort ()
    {
	return locatorPort;
    }

    public void setLocatorPort (int port)
    {
	locatorPort = port;
    }

    public String getHostName() {
	return hostname;
    }

    public String getHost() {
	return hostname;
    }

    public int hashCode() {
        return type.hashCode() ^ hostname.hashCode() ^ port;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof EndPointImpl)) {
            return false;
	}
        EndPointImpl other = (EndPointImpl)obj;
	if (type == null) {
	    if (other.type != null) {
		return false;
	    }
	} else if (!type.equals(other.type)) {
	    return false;
	}
        if (port != other.port) {
            return false;
	}
        if (!hostname.equals(other.hostname)) {
            return false;
        }
        return true;
    }

    public String toString ()
    {
	return
	    hostname + " " +
	    port     + " " +
	    type;
    }

    private String type;
    private int port;
    private int locatorPort;
    private String hostname;
}
