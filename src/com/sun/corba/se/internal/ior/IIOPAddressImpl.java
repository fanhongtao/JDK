/*
 * @(#)IIOPAddressImpl.java	1.3 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

//Source file: J:/ws/serveractivation/src/share/classes/com.sun.corba.se.internal.ior/IIOPAddress.java

package com.sun.corba.se.internal.ior;

import org.omg.CORBA.BAD_PARAM ;

import org.omg.CORBA_2_3.portable.InputStream ;
import org.omg.CORBA_2_3.portable.OutputStream ;

/**
 * @author 
 */
public final class IIOPAddressImpl extends IIOPAddressBase
{
    private String host;
    private int port;
    
    public IIOPAddressImpl( String host, int port ) 
    {
	if ((port < 0) || (port > 65535))
	    throw new BAD_PARAM() ;

	this.host = host ;
	this.port = port ;
    }

    public IIOPAddressImpl( InputStream is )
    {
	host = is.read_string() ;
	short thePort = is.read_short() ;
	port = shortToInt( thePort ) ;
    }

    public String getHost()
    {
	return host ;
    }

    public int getPort()
    {
	return port ;
    }
}
