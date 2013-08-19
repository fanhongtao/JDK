/*
 * @(#)ServiceContextRegistry.java	1.12 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.corba.se.internal.core;

import java.util.Vector ;
import java.util.Enumeration ;
import com.sun.corba.se.internal.core.ServiceContext ;
import com.sun.corba.se.internal.core.ServiceContextData ;
import com.sun.corba.se.internal.core.NoSuchServiceContext ;
import com.sun.corba.se.internal.core.DuplicateServiceContext ;
import com.sun.corba.se.internal.corba.ORB ;
import com.sun.corba.se.internal.orbutil.ORBUtility ;

public class ServiceContextRegistry {
    private ORB orb ;
    private Vector scCollection ;

    private void dprint( String msg ) 
    {
	ORBUtility.dprint( this, msg ) ;
    }

    public ServiceContextRegistry( ORB orb )
    {
	scCollection = new Vector() ;
	this.orb = orb ;
    }		

    /** Register the ServiceContext class so that it will be recognized
     * by the read method.
     * Class cls must have the following properties:
     * <ul>
     * <li>It must derive from com.sun.corba.se.internal.core.ServiceContext.</li>
     * <li>It must have a public static final int SERVICE_CONTEXT_ID 
     * member.</li>
     * <li>It must implement a constructor that takes a 
     * org.omg.CORBA_2_3.portable.InputStream argument.</li>
     * </ul>
     */
    public void register( Class cls ) throws DuplicateServiceContext,
    NoSuchServiceContext
    {
	if (ORB.ORBInitDebug)
	    dprint( "Registering service context class " + cls ) ;
	
	ServiceContextData scd = new ServiceContextData( cls ) ;

	if (findServiceContextData(scd.getId()) == null)
	    scCollection.addElement( scd ) ;
	else
	    throw new DuplicateServiceContext() ;
    }

    public ServiceContextData findServiceContextData( int scId )
    {
	if (ORB.ORBInitDebug)
	    dprint( "Searching registry for service context id " + scId ) ;
	
	Enumeration enum = scCollection.elements() ;
	while (enum.hasMoreElements()) {
	    ServiceContextData scd = (ServiceContextData)(enum.nextElement()) ;
	    if (scd.getId() == scId) {
		if (ORB.ORBInitDebug)
		    dprint( "Service context data found: " + scd ) ;

		return scd ;
	    }
	}

	if (ORB.ORBInitDebug)
	    dprint( "Service context data not found" ) ;

	return null ;
    }
}
