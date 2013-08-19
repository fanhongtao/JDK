/*
 * @(#)IIOPAddressFutureImpl.java	1.3 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

//Source file: J:/ws/serveractivation/src/share/classes/com.sun.corba.se.internal.ior/IIOPAddress.java

package com.sun.corba.se.internal.ior;

import org.omg.CORBA.BAD_PARAM ;

import org.omg.CORBA_2_3.portable.InputStream ;
import org.omg.CORBA_2_3.portable.OutputStream ;

import com.sun.corba.se.internal.core.Future ;

/**
 * @author 
 */
public final class IIOPAddressFutureImpl extends IIOPAddressBase
{
    private Future host;
    private Future port;
    
    public IIOPAddressFutureImpl( Future host, Future port ) 
    {
	this.host = host ;
	this.port = port ;
    }

    public String getHost()
    {
	return (String)(host.evaluate()) ;
    }

    public int getPort()
    {
	Integer value = (Integer)(port.evaluate()) ;
	return value.intValue() ;
    }
}
