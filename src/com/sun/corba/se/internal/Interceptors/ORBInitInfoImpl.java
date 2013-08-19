/*
 * @(#)ORBInitInfoImpl.java	1.23 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.corba.se.internal.Interceptors;

import org.omg.PortableInterceptor.ORBInitInfo;
import org.omg.IOP.CodecFactory;
import org.omg.PortableInterceptor.ClientRequestInterceptor;
import org.omg.PortableInterceptor.IORInterceptor;
import org.omg.PortableInterceptor.PolicyFactory;
import org.omg.PortableInterceptor.ServerRequestInterceptor;
import org.omg.PortableInterceptor.ORBInitInfoPackage.DuplicateName;
import org.omg.PortableInterceptor.ORBInitInfoPackage.InvalidName;

import org.omg.CORBA.BAD_PARAM;
import org.omg.CORBA.BAD_INV_ORDER;
import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.NO_IMPLEMENT;
import org.omg.CORBA.OBJECT_NOT_EXIST;
import org.omg.CORBA.ORB;
import org.omg.CORBA.LocalObject;

import org.omg.CORBA.portable.Delegate;
import org.omg.CORBA.portable.ObjectImpl;

import com.sun.corba.se.internal.core.ClientSubcontract;
import com.sun.corba.se.internal.core.IOR;


/**
 * ORBInitInfoImpl is the implementation of the ORBInitInfo class to be
 * passed to ORBInitializers, as described in orbos/99-12-02.  
 */
public final class ORBInitInfoImpl 
    extends org.omg.CORBA.LocalObject
    implements ORBInitInfo
{

    // The ORB we are initializing
    private PIORB orb;

    // The arguments passed to ORB_init
    private String[] args;

    // The ID of the ORB being initialized
    private String orbId;

    // The CodecFactory 
    private CodecFactory codecFactory;

    // The current stage of initialization
    private int stage = STAGE_PRE_INIT;

    // The pre-initialization stage (pre_init() being called)
    public static final int STAGE_PRE_INIT = 0;

    // The post-initialization stage (post_init() being called)
    public static final int STAGE_POST_INIT = 1;

    // Reject all calls - this object should no longer be around.
    public static final int STAGE_CLOSED = 2;

    // The description for the OBJECT_NOT_EXIST exception in STAGE_CLOSED
    private static final String MESSAGE_ORBINITINFO_INVALID =
	"ORBInitInfo object is only valid during ORB_init";
 
    /**
     * Creates a new ORBInitInfoImpl object (scoped to package)
     *
     * @param args The arguments passed to ORB_init.
     */
    ORBInitInfoImpl( PIORB orb, String[] args, String orbId, 
	             CodecFactory codecFactory ) 
    {
	this.orb = orb;
	this.args = args;
	this.orbId = orbId;
	this.codecFactory = codecFactory;
    }

    /**
     * Sets the current stage we are in.  This limits access to certain
     * functionality.
     */
    void setStage( int stage ) {
	this.stage = stage;
    }

    /**
     * Throws an exception if the current stage is STAGE_CLOSED.
     * This is called before any method is invoked to ensure that
     * no method invocations are attempted after all calls to post_init()
     * are completed.
     */
    private void checkStage() {
	if( stage == STAGE_CLOSED ) {
	    throw new OBJECT_NOT_EXIST( MESSAGE_ORBINITINFO_INVALID );
	}
    }

    /*
     *******************************************************************
     * The following are implementations of the ORBInitInfo operations.
     *******************************************************************/

    /**
     * This attribute contains the arguments passed to ORB_init.  They may
     * or may not contain the ORB's arguments
     */
    public String[] arguments () {
	checkStage();
	return args;
    }

    /**
     * This attribute is the ID of the ORB being initialized
     */
    public String orb_id () {
	checkStage();
	return orbId;
    }

    /**
     * This attribute is the IOP::CodecFactory.  The CodecFactory is normally
     * obtained via a call to ORB::resolve_initial_references( "CodecFactory" )
     * but since the ORB is not yet available and Interceptors, particularly
     * when processing service contexts, will require a Codec, a means of
     * obtaining a Codec is necessary during ORB intialization.
     */
    public CodecFactory codec_factory () {
	checkStage();
	return codecFactory;
    }

    /**
     * See orbos/99-12-02, Chapter 11, Dynamic Initial References on page
     * 11-81.  This operation is identical to ORB::register_initial_reference
     * described there.  This same functionality exists here because the ORB,
     * not yet fully initialized, is not yet available but initial references
     * may need to be registered as part of Interceptor registration.  
     * <p>
     * This method may not be called during post_init.
     */
    public void register_initial_reference( String id, 
	                                    org.omg.CORBA.Object obj )
        throws InvalidName
    {
	checkStage();
	if( id == null ) nullParam();

	// As per ptc/00-08-06, if null is passed as the obj parameter,
	// throw BAD_PARAM with minor code MinorCodes.RIR_WITH_NULL_OBJECT.
	// Though the spec is talking about IDL null, we will address both
	// Java null and IDL null:
	// Note: Local Objects can never be nil!
	boolean isNil = false;
	if( obj == null ) {
	    isNil = true;
	}
	else if( obj instanceof ObjectImpl ) {
	    Delegate delegate = ((ObjectImpl)obj)._get_delegate();
	    ClientSubcontract csub = (ClientSubcontract)delegate;
	    IOR ior = csub.marshal();
	    isNil = ior.is_nil();
	}

	if( isNil ) {
	    throw new BAD_PARAM( 
		"register_initial_reference called with nil Object.",
		MinorCodes.RIR_WITH_NULL_OBJECT, 
		CompletionStatus.COMPLETED_NO );
	}

	// Delegate to ORB.  If ORB version throws InvalidName, convert to
	// equivalent Portable Interceptors InvalidName.
	try {
	    orb.register_initial_reference( id, obj );
	}
	catch( org.omg.CORBA.ORBPackage.InvalidName e ) {
	    throw new InvalidName( e.getMessage() );
	}
    }

    /**
     * This operation is only valid during post_init.  It is identical to
     * ORB::resolve_initial_references.  This same functionality exists here
     * because the ORB, not yet fully initialized, is not yet available,
     * but initial references may be required from the ORB as part
     * of Interceptor registration.
     * <p>
     * (incorporates changes from errata in orbos/00-01-01)
     * <p>
     * This method may not be called during pre_init.
     */
    public org.omg.CORBA.Object resolve_initial_references (String id) 
	throws InvalidName
    {
	checkStage();
	if( id == null ) nullParam();

	if( stage == STAGE_PRE_INIT ) {
	    // Initializer is not allowed to invoke this method during 
	    // this stage.

	    // _REVISIT_ Spec issue: What exception should really be 
	    // thrown here?
	    throw new BAD_INV_ORDER( 
		"Resolve Initial References cannot be called in pre_init",
		MinorCodes.RIR_INVALID_PRE_INIT, 
		CompletionStatus.COMPLETED_NO );
	}

	org.omg.CORBA.Object objRef = null;
	
	try {
	    objRef = orb.resolve_initial_references( id );
        }
	catch( org.omg.CORBA.ORBPackage.InvalidName e ) {
	    // Convert PIDL to IDL exception:
	    throw new InvalidName();
	}

	return objRef;
    }

    /**
     * This operation is used to add a client-side request Interceptor to
     * the list of client-side request Interceptors.
     * <p>
     * If a client-side request Interceptor has already been registered
     * with this Interceptor's name, DuplicateName is raised.
     */
    public void add_client_request_interceptor ( 
	ClientRequestInterceptor interceptor) 
        throws DuplicateName
    {
	checkStage();
	if( interceptor == null ) nullParam();

	orb.register_interceptor( interceptor, 
	    InterceptorList.INTERCEPTOR_TYPE_CLIENT );
    }

    /**
     * This operation is used to add a server-side request Interceptor to
     * the list of server-side request Interceptors.
     * <p>
     * If a server-side request Interceptor has already been registered
     * with this Interceptor's name, DuplicateName is raised.
     */
    public void add_server_request_interceptor (
	ServerRequestInterceptor interceptor) 
        throws DuplicateName
    {
	checkStage();
	if( interceptor == null ) nullParam();
	
	orb.register_interceptor( interceptor, 
	    InterceptorList.INTERCEPTOR_TYPE_SERVER );
    }

    /**
     * This operation is used to add an IOR Interceptor to
     * the list of IOR Interceptors.
     * <p>
     * If an IOR Interceptor has already been registered
     * with this Interceptor's name, DuplicateName is raised.
     */
    public void add_ior_interceptor (
	IORInterceptor interceptor ) 
        throws DuplicateName
    {
	checkStage();
	if( interceptor == null ) nullParam();

	orb.register_interceptor( interceptor, 
	    InterceptorList.INTERCEPTOR_TYPE_IOR );
    }

    /**
     * A service calls allocate_slot_id to allocate a slot on
     * PortableInterceptor::Current.
     *
     * @return The index to the slot which has been allocated.
     */
    public int allocate_slot_id () {
	checkStage();

        return orb.getPICurrent().allocateSlotId( );

    }

    /**
     * Register a PolicyFactory for the given PolicyType.
     * <p>
     * If a PolicyFactory already exists for the given PolicyType, 
     * BAD_INV_ORDER is raised with a minor code of TBD_BIO+2.
     */
    public void register_policy_factory( int type, 
	                                 PolicyFactory policy_factory )
    {
	checkStage();
	if( policy_factory == null ) nullParam();
        orb.registerPolicyFactory( type, policy_factory );
    }


    /**
     * Called when an invalid null parameter was passed.  Throws a
     * BAD_PARAM with a minor code of 1
     */
    private void nullParam() 
        throws BAD_PARAM 
    {
        throw new BAD_PARAM( 
	    com.sun.corba.se.internal.orbutil.MinorCodes.NULL_PARAM,
	    CompletionStatus.COMPLETED_NO );
    }

}
