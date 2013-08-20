/*
 * @(#)POAPolicyMediatorFactory.java	1.21 03/12/19
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.corba.se.impl.oa.poa ;

abstract class POAPolicyMediatorFactory {
    // create an appropriate policy mediator based on the policies.
    // Note that the policies object has already been validated before
    // this call, so it can only contain valid combinations of POA policies.
    static POAPolicyMediator create( Policies policies, POAImpl poa )
    {
	if (policies.retainServants()) {
	    if (policies.useActiveMapOnly())
		return new POAPolicyMediatorImpl_R_AOM( policies, poa ) ;
	    else if (policies.useDefaultServant()) 
		return new POAPolicyMediatorImpl_R_UDS( policies, poa ) ;
	    else if (policies.useServantManager())
		return new POAPolicyMediatorImpl_R_USM( policies, poa ) ;
	    else
		throw poa.invocationWrapper().pmfCreateRetain() ;
	} else {
	    if (policies.useDefaultServant()) 
		return new POAPolicyMediatorImpl_NR_UDS( policies, poa ) ;
	    else if (policies.useServantManager())
		return new POAPolicyMediatorImpl_NR_USM( policies, poa ) ;
	    else
		throw poa.invocationWrapper().pmfCreateNonRetain() ;
	}
    }
}
