/*
 * @(#)HostInfo.java	1.5 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.corba.se.internal.corba;


/** HostInfo is used internally by CorbaLoc object to store the
 *  host information used in creating the Service Object reference
 *  from the -ORBInitDef and -ORBDefaultInitDef definitions.
 */
public class HostInfo
{
    // Version information
    private int major, minor;

    // Host Name and Port Number
    private String hostName;
    private int portNumber;

    public HostInfo( ) {
	// Default IIOP Version is 1.0
	major = 1;
	minor = 0;
	// Default host is localhost
	hostName = "localhost";
	// Default Portnumber is 2089
	portNumber = 2089;
    }

    public void setHostName( String theHostName ) {
	hostName = theHostName;
    }

    public String getHostName( ) {
	return hostName;
    }

    public void setPortNumber( int thePortNumber ) {
	portNumber = thePortNumber;
    }

    public int getPortNumber( ) {
	return portNumber;
    }

    public void setVersion( int theMajor, int theMinor ) {
	major = theMajor;
	minor = theMinor;
    }

    public int getMajorNumber( ) {
	return major;
    }

    public int getMinorNumber( ) {
	return minor;
    }

    /** Internal Debug Method.
     */
    public void dprint( ) {
	System.out.println( " Major -> " + major + " Minor -> " + minor );
	System.out.println( "hostName -> " + hostName );
	System.out.println( "portNumber -> " + portNumber );
    }
}

