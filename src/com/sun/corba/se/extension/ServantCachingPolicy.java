/*
 * @(#)ServantCachingPolicy.java	1.3 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.corba.se.extension ;

import org.omg.CORBA.Policy ;
import org.omg.CORBA.LocalObject ;
import com.sun.corba.se.internal.orbutil.ORBConstants ;

/** Policy used to implement servant caching optimization in the POA.
*/
public class ServantCachingPolicy extends LocalObject implements Policy
{
    private static ServantCachingPolicy policy = null ;

    private ServantCachingPolicy() 
    {
    }

    public synchronized static ServantCachingPolicy getPolicy()
    {
	if (policy == null)
	    policy = new ServantCachingPolicy() ;

	return policy ;
    }

    public int policy_type ()
    {
	return ORBConstants.SERVANT_CACHING_POLICY ;
    }

    public org.omg.CORBA.Policy copy ()
    {
	return this ;
    }

    public void destroy ()
    {
    }
}
