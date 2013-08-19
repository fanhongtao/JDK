/*
 * @(#)CorbaName.java	1.5 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.corba.se.internal.corba;
/** The corbaname: URL definitions from the -ORBInitDef and -ORBDefaultInitDef's
 *  will be stored in this object. This object is capable of storing CorbaLoc
 *  profiles as defined in the CorbaName grammer.
 */
public class CorbaName
{
    // CorbaLoc Object contains all the information to resolve
    // Root Naming context
    private CorbaLoc theCorbaLocObject;
   
    // Stringified Name is the compound anem which will be used to resolve
    // the reference from the root naming context
    private String theStringifiedName;

    public CorbaName( ) {
        theCorbaLocObject = null;
	theStringifiedName = null;
    }
	
    public void setCorbaLoc( CorbaLoc CorbaLocObject ) {
	theCorbaLocObject = CorbaLocObject;
    }

    public CorbaLoc getCorbaLoc( ) {
	return theCorbaLocObject;
    }
		
    public void setStringifiedName( String StringifiedName ) {
	theStringifiedName = StringifiedName;
    }

    public String getStringifiedName( ) {
	return theStringifiedName;
    }
	
    /** Internal Debug Method.  
     */
    public void dprint( ) {
	System.out.println( "/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-" );
	System.out.println( "CORBAName Print ....." );
	System.out.println( "PrintingCorbaLoc Object First ..." );
	if( theCorbaLocObject != null ) {
	    theCorbaLocObject.dprint( );
	}
	System.out.println( "StringifiedName -> " + theStringifiedName );
	System.out.flush( );
    }
	
}
