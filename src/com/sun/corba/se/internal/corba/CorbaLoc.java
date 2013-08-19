/*
 * @(#)CorbaLoc.java	1.5 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.corba.se.internal.corba;
/** The corbaloc: URL definitions from the -ORBInitDef and -ORBDefaultInitDef's
 *  will be stored in this object. This object is capable of storing multiple
 *  Host profiles as defined in the CorbaLoc grammer.
 */
public class CorbaLoc
{
    // If rirFlag is set to true that means internal
    // boot strapping technique will be used. If set to
    // false then the HostInfo will be used to create the
    // Service Object reference.
    private boolean rirFlag;
    private java.util.Vector theHostInfo;
    private String theKeyString;

    public CorbaLoc( ) {
	rirFlag = false;
	theHostInfo = null;
	// If no Key string is specified then it means 
	// It is pointing to NameService
	theKeyString = "NameService";
    }

    public void setRIRFlag( ) {
	rirFlag = true;
    }

    public boolean getRIRFlag( ) {
	return rirFlag;
    }

    /** There can be one or more HostInfo in a given Corbaloc and
     *  hence it will be stored in Vector as a list.
     */
    public void addHostInfo( HostInfo element ) {
	if( theHostInfo == null ) {
	    theHostInfo = new java.util.Vector( );
	}
	theHostInfo.addElement( element );
    }

    public java.util.Vector getHostInfo( ) {
	return theHostInfo;
    }

    public void setKeyString( String keyString ) {
	theKeyString = keyString;
    }

    public String getKeyString( ) {
	return theKeyString;
    }

    public void dprint( ) {
	System.out.println("///////////////////////////////////////////////" );	
	System.out.println( " Printing CORBALoc Object ..........." );
	System.out.println( "rirFlag -> " + rirFlag );
	if( theHostInfo != null ) {
	    System.out.println( "Printing all the Host Info .........." );
	    for( int i = 0; i < theHostInfo.size(); i++ ) {
		HostInfo temp = (HostInfo) theHostInfo.elementAt( i );
		temp.dprint( );
	    }
	}
	System.out.println( "KeyString -> " + theKeyString );
	System.out.println("//////////////////////////////////////////////" );	
	System.out.flush( );
    }
}
