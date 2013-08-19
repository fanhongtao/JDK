/*
 * @(#)ServantCachePOAClientSC.java	1.14 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */


package com.sun.corba.se.internal.POA;

import org.omg.CORBA.*;
import org.omg.CORBA.portable.*;
import com.sun.corba.se.internal.core.*;
import com.sun.corba.se.internal.core.NoSuchServiceContext ;
import com.sun.corba.se.internal.core.DuplicateServiceContext ;
import com.sun.corba.se.internal.orbutil.ORBUtility; //d11638
import com.sun.corba.se.internal.orbutil.ORBConstants;
import com.sun.corba.se.internal.corba.ClientDelegate;
import com.sun.corba.se.internal.corba.ServerDelegate;
import com.sun.corba.se.internal.iiop.Connection ;
import com.sun.corba.se.internal.iiop.ClientRequestImpl;
import com.sun.corba.se.internal.iiop.messages.ReplyMessage;
import com.sun.corba.se.internal.ior.ObjectKeyTemplate ;
import com.sun.corba.se.internal.ior.POAObjectKeyTemplate ;
import com.sun.corba.se.internal.ior.POAId ;
import com.sun.corba.se.internal.POA.POAORB ;
import com.sun.corba.se.internal.orbutil.MinorCodes;

public class ServantCachePOAClientSC extends GenericPOAClientSC implements ClientSC 
{
    protected ServantObject servant = null ;
    protected boolean servantIsLocal = false ;
    protected ClassLoader   servantClassLoader = null ;
    protected POAImpl servantPOA = null ;

    // If isNextIsLocalValid.get() == null, the next call to isLocal should be
    // valid
    protected ThreadLocal isNextIsLocalValid = new ThreadLocal() ;

    private boolean useLocalInvocation_ClassLoader( org.omg.CORBA.Object self ) 
    {
	// Access to ClassLoader is quite slow
	ClassLoader myClassLoader = self.getClass().getClassLoader() ;
	return 
	    (myClassLoader == servantClassLoader) && 
	    servantIsLocal ;
    }

    private boolean useLocalInvocation_reflection( org.omg.CORBA.Object self ) 
    {
	Class[] interfaces = self.getClass().getInterfaces() ;
	// Since self is an RMI-IIOP stub, interfaces should contain exactly
	// one element, which is the remote interface that the servant
	// must implement.
	return interfaces[0].isInstance( servant.servant ) && servantIsLocal ;
    }

    /*
    * Possible paths through useLocalInvocation/servant_preinvoke/servant_postinvoke:
    *
    * A: call useLocalInvocation
    * If useLocalInvocation returns false, servant_preinvoke is not called.
    * If useLocalInvocation returns true,
    * call servant_preinvoke
    *	If servant_preinvoke returns null,
    *	    goto A
    *   else
    *	    (local invocation proceeds normally)
    *	    servant_postinvoke is called
    *
    */
    private boolean useLocalInvocation_tlcache( org.omg.CORBA.Object self )
    {
	if (isNextIsLocalValid.get() == null)
	    return servantIsLocal ;
	else
	    isNextIsLocalValid.set( null ) ;

	return false ;
    }

    public boolean useLocalInvocation( org.omg.CORBA.Object self ) 
    {
	return useLocalInvocation_tlcache( self ) ;
    }

    public void setOrb( com.sun.corba.se.internal.core.ORB orb )
    {
	super.setOrb( orb ) ;

	initServant() ;
    }

    public void unmarshal( IOR ior )
    {
	super.unmarshal( ior ) ;

	initServant() ;
    }

    private void initServant() 
    {
	if ((ior != null) && (orb != null)) {
	    servantIsLocal = orb.allowLocalOptimization && ior.isLocal() ;

	    if (servantIsLocal) {
		servant = serversc.preinvoke( ior, "", java.lang.Object.class ) ;
		serversc.postinvoke( ior, servant ) ;
	
		POAId poaid = ior.getPOAId() ;
		servantPOA = (POAImpl)(serversc.getPOA( poaid ) ) ;
		servantClassLoader = servant.servant.getClass().getClassLoader() ;
	    }
	}
    }

    public ServantObject servant_preinvoke( org.omg.CORBA.Object self,
	String operation, Class expectedType )
    {
	// Normally, this test will never fail.  However, if the servant
	// and the stub were loaded in different class loaders, this test
	// will fail.
	if (!expectedType.isInstance( servant.servant )) {
	    // set the flag to a non-null object.  "this" is always
	    // available and requires no allocation.
	    isNextIsLocalValid.set( this ) ;

	    // When servant_preinvoke returns null, the RMI-IIOP stub will
	    // recursively re-invoke itself.  Thus, the next call made from 
	    // the stub is another useLocalInvocation call.
	    return null ;
	}

	try {
	    servantPOA.enter() ;
	} catch (Exception exc) {}

	return servant ;
    }

    public void servant_postinvoke(org.omg.CORBA.Object self,
                                   ServantObject servantobj) 
    {
	// NOP
	servantPOA.exit() ;
    }
}
