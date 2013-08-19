/*
 * @(#)Policies.java	1.21 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.corba.se.internal.POA;

import java.util.HashMap ;
import java.util.Iterator ;

import com.sun.corba.se.internal.orbutil.ORBConstants ;

import org.omg.CORBA.*;
import org.omg.PortableServer.*;
import org.omg.PortableServer.POAPackage.*;

public final class Policies {
    private boolean cachingServantAllowed = false ;
    private int threadModel;
    private int lifespan;
    private int idUniqueness;
    private int idAssignment;
    private int retention;
    private int requestProcessing;
    private int implicitActivation;
    private HashMap policies = new HashMap() ;	// Maps Integer(policy type) to Policy

    public static final Policies defaultPolicies 
	= new Policies(ThreadPolicyValue._ORB_CTRL_MODEL,
		       LifespanPolicyValue._TRANSIENT,
		       IdUniquenessPolicyValue._UNIQUE_ID,
		       IdAssignmentPolicyValue._SYSTEM_ID,
		       ServantRetentionPolicyValue._RETAIN,
		       RequestProcessingPolicyValue._USE_ACTIVE_OBJECT_MAP_ONLY,
		       ImplicitActivationPolicyValue._NO_IMPLICIT_ACTIVATION);

    public static final Policies rootPOAPolicies
        = new Policies(ThreadPolicyValue._ORB_CTRL_MODEL,
                       LifespanPolicyValue._TRANSIENT,
                       IdUniquenessPolicyValue._UNIQUE_ID,
                       IdAssignmentPolicyValue._SYSTEM_ID,
                       ServantRetentionPolicyValue._RETAIN,
                       RequestProcessingPolicyValue._USE_ACTIVE_OBJECT_MAP_ONLY,                       
                       ImplicitActivationPolicyValue._IMPLICIT_ACTIVATION);
					  
    private Policies(int threadModel, int lifespan, int idUniqueness, int idAssignment,
		     int retention, int requestProcessing, int implicitActivation) {
	this.threadModel        = threadModel;
	this.lifespan           = lifespan;
	this.idUniqueness       = idUniqueness;
	this.idAssignment       = idAssignment;
	this.retention          = retention;
	this.requestProcessing  = requestProcessing;
	this.implicitActivation = implicitActivation;
    }

    Policies() {
	this.threadModel        = defaultPolicies.threadModel;
	this.lifespan           = defaultPolicies.lifespan;
	this.idUniqueness       = defaultPolicies.idUniqueness;
	this.idAssignment       = defaultPolicies.idAssignment;
	this.retention          = defaultPolicies.retention;
	this.requestProcessing  = defaultPolicies.requestProcessing;
	this.implicitActivation = defaultPolicies.implicitActivation;
    }

    Policies(int bitmap) {
	this.threadModel        = (bitmap & 0x0080) >> 7;
	this.lifespan           = (bitmap & 0x0040) >> 6;
	this.idUniqueness       = (bitmap & 0x0020) >> 5;
	this.idAssignment       = (bitmap & 0x0010) >> 4;
	this.implicitActivation = (bitmap & 0x0008) >> 3;
	this.retention          = (bitmap & 0x0004) >> 2;
	this.requestProcessing  = bitmap & 0x0003;
    }

    public String toString() {
	StringBuffer buffer = new StringBuffer();
	buffer.append( "Policies[" ) ;
	boolean first = true ;
	Iterator iter = policies.values().iterator() ;
	while (iter.hasNext()) {
	    if (first)
		first = false ;
	    else
		buffer.append( "," ) ;

	    buffer.append( iter.next().toString() ) ;
	} 
	buffer.append( "]" ) ;
	return buffer.toString() ;
    }

    Policies(Policy[] policies) throws InvalidPolicy {
	// Make sure the defaults are set according to the POA spec
	this();			

	if ( policies == null )
	    return;

	// XXX: Check for duplicates ?
	for(short i = 0; i < policies.length; i++) {
	    Policy policy = policies[i];

	    // Save the policy in the HashMap to support POA.get_effective_policy
	    Integer key = new Integer( policy.policy_type() ) ;
	    this.policies.put( key, policy ) ;

	    // Set up the local copy of standard POA policy information
	    if (policy instanceof ThreadPolicy) {
		threadModel = ((ThreadPolicy) policy).value().value();
	    } else if (policy instanceof LifespanPolicy) {
		lifespan = ((LifespanPolicy) policy).value().value();
	    } else if (policy instanceof IdUniquenessPolicy) {
		idUniqueness = ((IdUniquenessPolicy) policy).value().value();
	    } else if (policy instanceof IdAssignmentPolicy) {
		idAssignment = ((IdAssignmentPolicy) policy).value().value();
	    } else if (policy instanceof ServantRetentionPolicy) {
		retention = ((ServantRetentionPolicy) policy).value().value();
	    } else if (policy instanceof RequestProcessingPolicy) {
		requestProcessing = ((RequestProcessingPolicy) policy).value().value();
	    } else if (policy instanceof ImplicitActivationPolicy) {
		implicitActivation = ((ImplicitActivationPolicy) policy).value().value();
	    } else if (policy.policy_type() == ORBConstants.SERVANT_CACHING_POLICY)
		cachingServantAllowed = true ;
	}

        short firstIndexOfBadCombination = 
            POAPolicyCombinationValidator.checkForInvalidPolicyCombinations(policies);
        if ( !( firstIndexOfBadCombination <= -1 ) )
            invalidPolicy(firstIndexOfBadCombination);
    }

    public Policy get_effective_policy( int type )
    {
	Integer key = new Integer( type ) ;
	Policy result = (Policy)(policies.get(key)) ;
	return result ;
    }

    public final int getBitMap() {
	return (threadModel << 7 | lifespan << 6 | idUniqueness << 5
	        | idAssignment << 4 | implicitActivation << 3
	        | retention << 2 | requestProcessing);
    }

    public final boolean servantCachingAllowed()
    {
	return cachingServantAllowed ;
    }

    /* Thread Policies */
    public final boolean isOrbControlledThreads() {
	return threadModel == ThreadPolicyValue._ORB_CTRL_MODEL;
    }
    public final boolean isSingleThreaded() {
	return threadModel == ThreadPolicyValue._SINGLE_THREAD_MODEL;
    }

    /* Lifespan */
    public final boolean isTransient() {
	return lifespan == LifespanPolicyValue._TRANSIENT;
    }
    public final boolean isPersistent() {
	return lifespan == LifespanPolicyValue._PERSISTENT;
    }

    /* ID Uniqueness */
    public final boolean isUniqueIds() {
	return idUniqueness == IdUniquenessPolicyValue._UNIQUE_ID;
    }
    public final boolean isMultipleIds() {
	return idUniqueness == IdUniquenessPolicyValue._MULTIPLE_ID;
    }

    /* ID Assignment */
    public final boolean isUserAssignedIds() {
	return idAssignment == IdAssignmentPolicyValue._USER_ID;
    }
    public final boolean isSystemAssignedIds() {
	return idAssignment == IdAssignmentPolicyValue._SYSTEM_ID;
    }

    /* Servant Rentention */
    public final boolean retainServants() {
	return retention == ServantRetentionPolicyValue._RETAIN;
    }

    /* Request Processing */
    public final boolean useActiveMapOnly() {
	return
	    requestProcessing == RequestProcessingPolicyValue._USE_ACTIVE_OBJECT_MAP_ONLY;
    }
    public final boolean useDefaultServant() {
	return
	    requestProcessing == RequestProcessingPolicyValue._USE_DEFAULT_SERVANT;
    }
    public final boolean useServantManager() {
	return
	    requestProcessing == RequestProcessingPolicyValue._USE_SERVANT_MANAGER;
    }

    /* Implicit Activation */
    public final boolean isImplicitlyActivated() {
	return implicitActivation == ImplicitActivationPolicyValue._IMPLICIT_ACTIVATION;
    }

    private void invalidPolicy(short index) throws InvalidPolicy {
	throw new InvalidPolicy(index);
    }
}
