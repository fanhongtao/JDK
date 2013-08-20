/*
 * @(#)INSURL.java	1.4 03/12/19
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.corba.se.impl.naming.namingutil;

/**
 * INS URL is a generic interface for two different types of URL's specified 
 * in INS spec. 
 *
 * @Author Hemanth
 */
public interface INSURL {
    public boolean getRIRFlag( );

    // There can be one or more Endpoint's in the URL, so the return value is
    // a List
    public java.util.List getEndpointInfo( );

    public String getKeyString( );

    public String getStringifiedName( );

    // This method will return true only in CorbanameURL, It is provided because
    // corbaname: URL needs special handling. 
    public boolean isCorbanameURL( );

    // A debug method, which is not required for normal operation
    public void dPrint( );
}

    
