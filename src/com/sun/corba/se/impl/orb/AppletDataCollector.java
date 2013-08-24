/*
 * @(#)AppletDataCollector.java	1.10 05/11/17
 * 
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.corba.se.impl.orb ;

import java.applet.Applet ;
import java.util.Properties ;

public class AppletDataCollector extends DataCollectorBase {
    private Applet applet ;

    AppletDataCollector( Applet app, Properties props, String localHostName,
	String configurationHostName ) 
    {
	super( props, localHostName, configurationHostName ) ;
	this.applet = app ;
    }

    public boolean isApplet() 
    {
	return true ;
    }

    protected void collect( )
    {
	checkPropertyDefaults() ;

        findPropertiesFromFile() ;

	// We do not use system properties for applets in order to 
	// avoid security exceptions.

        findPropertiesFromProperties() ;
        findPropertiesFromApplet( applet ) ;
    }
}
