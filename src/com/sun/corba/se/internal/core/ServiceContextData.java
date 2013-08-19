/*
 * @(#)ServiceContextData.java	1.12 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.corba.se.internal.core;

import org.omg.CORBA_2_3.portable.InputStream ;
import com.sun.corba.se.internal.core.ServiceContext ;
import com.sun.corba.se.internal.core.NoSuchServiceContext ;
import java.lang.reflect.InvocationTargetException ;
import java.lang.reflect.Modifier ;
import java.lang.reflect.Field ;
import java.lang.reflect.Constructor ;
import com.sun.corba.se.internal.corba.ORB ;
import com.sun.corba.se.internal.orbutil.ORBUtility ;

/** Internal class used to hold data about a service context class.
*/
public class ServiceContextData {
    private void dprint( String msg ) 
    {
	ORBUtility.dprint( this, msg ) ;
    }
    
    public ServiceContextData( Class cls ) throws NoSuchServiceContext
    {
	if (ORB.ORBInitDebug)
	    dprint( "ServiceContextData constructor called for class " + cls ) ;

	scClass = cls ;

	try {
	    if (ORB.ORBInitDebug)
		dprint( "Finding constructor for " + cls ) ;

	    // Find the appropriate constructor in cls
	    Class[] args = new Class[2] ;
	    args[0] = InputStream.class ;
        args[1] = GIOPVersion.class;
	    try {
		scConstructor = cls.getConstructor( args ) ;
	    } catch (NoSuchMethodException nsme) {
		throw new NoSuchServiceContext( 
					       "Class does not have an InputStream constructor" ) ;
	    }

	    if (ORB.ORBInitDebug)
		dprint( "Finding SERVICE_CONTEXT_ID field in " + cls ) ;

	    // get the ID from the public static final int SERVICE_CONTEXT_ID
	    Field fld ;
	    try {
		fld = cls.getField( "SERVICE_CONTEXT_ID" ) ;
	    } catch (NoSuchFieldException nsfe) {
		throw new NoSuchServiceContext(
					       "Class does not have a SERVICE_CONTEXT_ID member" ) ;
	    } catch (SecurityException se) {
		throw new NoSuchServiceContext(
					       "Could not access SERVICE_CONTEXT_ID member" ) ;
	    }

	    if (ORB.ORBInitDebug)
		dprint( "Checking modifiers of SERVICE_CONTEXT_ID field in " + cls ) ;

	    int mod = fld.getModifiers() ;
	    if (!Modifier.isPublic(mod) || !Modifier.isStatic(mod) ||
		!Modifier.isFinal(mod) )
		throw new NoSuchServiceContext(
					       "SERVICE_CONTEXT_ID field is not public static final" ) ;

	    if (ORB.ORBInitDebug)
		dprint( "Getting value of SERVICE_CONTEXT_ID in " + cls ) ;

	    try {
		scId = fld.getInt( null ) ;
	    } catch (IllegalArgumentException iae) {
		throw new NoSuchServiceContext(
					       "SERVICE_CONTEXT_ID not convertible to int" ) ;
	    } catch (IllegalAccessException iae2) {
		throw new NoSuchServiceContext(
					       "Could not access value of SERVICE_CONTEXT_ID" ) ;
	    }
	} catch (NoSuchServiceContext nssc) {
	    if (ORB.ORBInitDebug)
		dprint( "Exception in ServiceContextData constructor: " + nssc ) ;
	    throw nssc ;
	} catch (Throwable thr) {
	    if (ORB.ORBInitDebug)
		dprint( "Unexpected Exception in ServiceContextData constructor: " + 
			thr ) ;
	}	

	if (ORB.ORBInitDebug)
	    dprint( "ServiceContextData constructor completed" ) ;
    }

    /** Factory method used to create a ServiceContext object by
     * unmarshalling it from the InputStream.
     */
    public ServiceContext makeServiceContext(InputStream is, GIOPVersion gv)
	throws NoSuchServiceContext
    {
	Object[] args = new Object[2];
	args[0] = is ;
    args[1] = gv;
	ServiceContext sc = null ;

	try {
	    sc = (ServiceContext)(scConstructor.newInstance( args )) ;
	} catch (IllegalArgumentException iae) {
	    throw new NoSuchServiceContext(
					   "InputStream constructor argument error" ) ;
	} catch (IllegalAccessException iae2) {
	    throw new NoSuchServiceContext(
					   "InputStream constructor argument error" ) ;
	} catch (InstantiationException ie) {
	    throw new NoSuchServiceContext(
					   "InputStream constructor called for abstract class" ) ;
	} catch (InvocationTargetException ite) {
	    throw new NoSuchServiceContext(
					   "InputStream constructor threw exception " + 
					   ite.getTargetException() ) ;
	}
	   
	return sc ;
    }

    int getId() 
    {
	return scId ;
    }

    public String toString()
    {
	return "ServiceContextData[ scClass=" + scClass + " scConstructor=" +
	    scConstructor + " scId=" + scId + " ]" ;
    }

    private Class	scClass ;
    private Constructor	scConstructor ;	
    private int		scId ;
}

