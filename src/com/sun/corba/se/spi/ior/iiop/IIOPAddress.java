/*
 * @(#)IIOPAddress.java	1.7 03/12/19 
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.corba.se.spi.ior.iiop;

import com.sun.corba.se.spi.ior.Writeable ;

/** IIOPAddress represents the host and port used to establish a
 * TCP connection for an IIOP request.
 */
public interface IIOPAddress extends Writeable 
{
    public String getHost() ;

    public int getPort() ;
}
