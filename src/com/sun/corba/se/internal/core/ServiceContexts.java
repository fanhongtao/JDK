/*
 * @(#)ServiceContexts.java	1.15 01/12/03
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.corba.se.internal.core;

import java.util.Enumeration ;
import java.util.NoSuchElementException ;
import java.util.Vector ;
import org.omg.CORBA.SystemException;
import org.omg.CORBA.INTERNAL;
import org.omg.CORBA_2_3.portable.OutputStream ;
import org.omg.CORBA_2_3.portable.InputStream ;
import com.sun.corba.se.internal.corba.ORB ;
import com.sun.corba.se.internal.util.Utility ;
import com.sun.corba.se.internal.orbutil.ORBUtility ;
import com.sun.corba.se.internal.core.ServiceContext ;
import com.sun.corba.se.internal.core.ServiceContextRegistry ;
import com.sun.corba.se.internal.core.ServiceContextData ;
import com.sun.corba.se.internal.core.UnknownServiceContext ;
import com.sun.corba.se.internal.core.NoSuchServiceContext ;
import com.sun.corba.se.internal.core.DuplicateServiceContext ;
import com.sun.corba.se.internal.iiop.CDRInputStream ;
import com.sun.corba.se.internal.iiop.CDROutputStream ;
import java.lang.reflect.InvocationTargetException ;
import java.lang.reflect.Modifier ;
import java.lang.reflect.Field ;
import java.lang.reflect.Constructor ;

public class ServiceContexts {
    private static boolean isDebugging( OutputStream os )
    {
	CDROutputStream cos = (CDROutputStream)os ;
	ORB orb = (ORB)(cos.orb()) ;
	if (orb==null)
	    return false ;
	return orb.serviceContextDebugFlag ;
    }

    private static boolean isDebugging( InputStream is )
    {
	CDRInputStream cis = (CDRInputStream)is ;
	ORB orb = (ORB)(cis.orb()) ;
	if (orb==null) 
	    return false ;
	return orb.serviceContextDebugFlag ;
    }

    private void dprint( String msg ) 
    {
	ORBUtility.dprint( this, msg ) ;
    }

    public static void writeNullServiceContext( OutputStream os ) 
    {
	if (isDebugging(os))
	    ORBUtility.dprint( "ServiceContexts", "Writing null service context" ) ;
	os.write_long( 0 ) ;
    }

    /** Read the Service contexts from the input stream.
     * Constructs each element according to its type, as indicated
     * by the ServiceContextId value in the stream.
     */
    public ServiceContexts(InputStream s, GIOPVersion gv)
    {
        CDRInputStream cis = (CDRInputStream)s ;
        orb = (com.sun.corba.se.internal.corba.ORB)(cis.orb()) ;
        if (orb.serviceContextDebugFlag)
            dprint( "Constructing ServiceContexts from input stream" ) ;

        ServiceContextRegistry scr = orb.getServiceContextRegistry() ;

        numValid = s.read_long() ;

        if (orb.serviceContextDebugFlag)
            dprint( "Number of service contexts = " + numValid ) ;

        scList = new Vector( numValid ) ;
        addAlignmentOnWrite = false ;

        for (int ctr=0; ctr<numValid; ctr++ ) {
            int scId = s.read_long() ;

            if (orb.serviceContextDebugFlag) {
                dprint( "Read service context id " + scId );
            }

            ServiceContextData scd = scr.findServiceContextData(scId);
            ServiceContext sc = null ;

            if (scd == null) {
                if (orb.serviceContextDebugFlag) {
                    dprint("Could not find ServiceContextData: using UnknownServiceContext");
                }

                sc = new UnknownServiceContext(scId, s) ;
            } else {
                if (orb.serviceContextDebugFlag) {
                    dprint("Found " + scd);
                }

                try {
                    sc = scd.makeServiceContext(s, gv) ;
                } catch (NoSuchServiceContext nssc) {
                    throw new INTERNAL() ;
                }
            }

            scList.addElement( sc ) ;
        }
    }

    public ServiceContexts( ORB orb )
    {
	this.orb = orb ;
	numValid = 0 ;
	scList = new Vector() ;
	addAlignmentOnWrite = false ;
    }

    public void addAlignmentPadding() 
    {
	// Make service context 12 bytes longer by adding 
	// JAVAIDL_ALIGN_SERVICE_ID service context at end.
	// The exact length
	// must be >8 (minimum service context size) and 
	// =4 mod 8, so 12 is the minimum.
	addAlignmentOnWrite = true ;
    }

    /** Hopefully unused scid:  This should be changed to a proper
     * VMCID aligned value.
     */
    private static final int JAVAIDL_ALIGN_SERVICE_ID = 0xbe1345cd ;

    /** Write the service contexts to the output stream.
     */
    public void write(OutputStream os, GIOPVersion gv)
    {
	if (isDebugging(os)) {
	    dprint( "Writing service contexts to output stream" ) ;
	    Utility.printStackTrace() ;
  	}

	int numsc = numValid ;
	if (addAlignmentOnWrite) {
	    if (isDebugging(os))
		dprint( "Adding alignment padding" ) ;

	    numsc++ ;
	}

	if (isDebugging(os))
	    dprint( "Service context has " + numsc + " components"  ) ;

	os.write_long( numsc ) ;
	Enumeration enum = scList.elements() ;
	while (enum.hasMoreElements()) {
	    ServiceContext sc = (ServiceContext)(enum.nextElement()) ;

	    if (isDebugging(os))
		dprint( "Writing service context " + sc ) ;

	    sc.write(os, gv) ;
	}

	if (addAlignmentOnWrite) {
	    if (isDebugging(os))
		dprint( "Writing alignment padding" ) ;

	    os.write_long( JAVAIDL_ALIGN_SERVICE_ID ) ;
	    os.write_long( 4 ) ;
	    os.write_octet( (byte)0 ) ;
	    os.write_octet( (byte)0 ) ;
	    os.write_octet( (byte)0 ) ;
	    os.write_octet( (byte)0 ) ;
	}

	if (isDebugging(os))
	    dprint( "Service context writing complete" ) ;
    }

    /** Add a service context to the stream, if there is not already
     * a service context in this object with the same id as sc.
     * If there is already such a service context, throw the 
     * DuplicateServiceContext exception.
     */
    public void put( ServiceContext sc ) throws DuplicateServiceContext
    {
	int index = findServiceContextIndex( sc.getId() ) ;

	if (index >= 0)
	    throw new DuplicateServiceContext() ;
	else {
	    index = findFirstNullIndex() ;

	    if (index < 0)
		scList.addElement( sc ) ;
	    else
		scList.setElementAt( sc, index ) ;

	    numValid++ ;
	}
    }

    public void delete( int scId ) throws NoSuchServiceContext
    {
	int index = findServiceContextIndex( scId ) ;

	if (index < 0)
	    throw new NoSuchServiceContext() ;
	else {
	    scList.setElementAt( null, index ) ;
	    numValid-- ;
	}
    }

    public ServiceContext get( int scId ) throws NoSuchServiceContext
    {
	int index = findServiceContextIndex( scId ) ;
	
	if (index < 0)
	    throw new NoSuchServiceContext() ;
	else
	    return (ServiceContext)(scList.elementAt(index)) ;
    }

    /** Return an enumeration of all of the service contexts in this
     * object.
     */
    public Enumeration list() 
    {
	return new ServiceContextEnumeration( scList ) ;
    }

    private int findFirstNullIndex()
    {
	for ( int ctr=0; ctr<scList.size(); ctr++ ) 
	    if (scList.elementAt( ctr ) == null)
		return ctr ;

	return -1 ;
    }

    private int findServiceContextIndex( int scId ) 
    {
	for ( int ctr=0; ctr<scList.size(); ctr++ ) {
	    Object obj = scList.elementAt(ctr) ;
	    if (obj != null) {
		ServiceContext sc = (ServiceContext)obj ;
		if (sc.getId() == scId)
		    return ctr ;
	    }
	}

	return -1 ;
    }

    private ORB orb ;

    /** List of all ServiceContext objects in this container.
     * Unused slots freed by deletion are null.
     */
    private Vector scList ;

    /** Number of non-null slots in scList.
     */
    private int numValid ;

    /** If true, write out a special alignment service context to force the
     * correct alignment on re-marshalling.
     */
    private boolean addAlignmentOnWrite ;
}
	
class ServiceContextEnumeration implements Enumeration {
    private int current ;
    private Vector scList ;

    ServiceContextEnumeration( Vector scList ) 
    { 
	current = -1 ;
	this.scList = scList ;
	advance() ; 
    }

    private void advance() 
    {
	current++ ;
	while ((current < scList.size()) &&
	       (scList.elementAt( current ) == null))
	    current++ ;
}

    public boolean hasMoreElements() { return current < scList.size() ; }

public Object nextElement() throws NoSuchElementException 
{
    if (hasMoreElements()) {
	Object result = scList.elementAt( current ) ;
	advance() ;
	return result ;
    } else
	throw new NoSuchElementException() ;
}
}
