/*
 * @(#)IIOPAddressBase.java	1.3 03/01/23
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
abstract class IIOPAddressBase implements IIOPAddress
{
    // Ports are marshalled as shorts on the wire.  The IDL
    // type is unsigned short, which lacks a convenient representation
    // in Java in the 32768-65536 range.  So, we treat ports as
    // ints throught this code, except that marshalling requires a
    // scaling conversion.  intToShort and shortToInt are provided
    // for this purpose.
    protected short intToShort( int value ) 
    {
	if (value > 32767)
	    return (short)(value - 65536) ;
	return (short)value ;
    }

    protected int shortToInt( short value )
    {
	if (value < 0)
	    return value + 65536 ;
	return value ;
    }

    public void write( OutputStream os )
    {
	os.write_string( getHost() ) ;
	int port = getPort() ;
	os.write_short( intToShort( port ) ) ;
    }

    public boolean equals( Object obj )
    {
	if (!(obj instanceof IIOPAddress))
	    return false ;

	IIOPAddress other = (IIOPAddress)obj ;

	return getHost().equals(other.getHost()) && 
	    (getPort() == other.getPort()) ;
    }

    public String toString()
    {
	return "IIOPAddress[" + getHost() + "," + getPort() + "]" ;
    }
}
