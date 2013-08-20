/*
 * @(#)IIOPEndpointInfo.java	1.4 03/12/19
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.corba.se.impl.naming.namingutil;


/** 
 *  EndpointInfo is used internally by CorbaLoc object to store the
 *  host information used in creating the Service Object reference
 *  from the -ORBInitDef and -ORBDefaultInitDef definitions.
 *
 *  @Author Hemanth
 */
public class IIOPEndpointInfo
{
    // Version information
    private int major, minor;

    // Host Name and Port Number
    private String host;
    private int port;

    IIOPEndpointInfo( ) {
	// Default IIOP Version 
	major = NamingConstants.DEFAULT_INS_GIOP_MAJOR_VERSION;
	minor = NamingConstants.DEFAULT_INS_GIOP_MINOR_VERSION;
	// Default host is localhost
	host = "localhost";
	// Default INS Port
	port = NamingConstants.DEFAULT_INS_PORT;
    }

    public void setHost( String theHost ) {
	host = theHost;
    }

    public String getHost( ) {
	return host;
    }

    public void setPort( int thePort ) {
	port = thePort;
    }

    public int getPort( ) {
	return port;
    }

    public void setVersion( int theMajor, int theMinor ) {
	major = theMajor;
	minor = theMinor;
    }

    public int getMajor( ) {
	return major;
    }

    public int getMinor( ) {
	return minor;
    }

    /** Internal Debug Method.
     */
    public void dump( ) {
	System.out.println( " Major -> " + major + " Minor -> " + minor );
	System.out.println( "host -> " + host );
	System.out.println( "port -> " + port );
    }
}

