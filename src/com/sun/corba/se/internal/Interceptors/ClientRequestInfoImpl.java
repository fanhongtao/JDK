/*
 * @(#)ClientRequestInfoImpl.java	1.42 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.corba.se.internal.Interceptors;

import java.io.*;
import java.lang.reflect.*;

import org.omg.CORBA.Any;
import org.omg.CORBA.BAD_INV_ORDER;
import org.omg.CORBA.BAD_PARAM;
import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.Context;
import org.omg.CORBA.ContextList;
import org.omg.CORBA.CTX_RESTRICT_SCOPE;
import org.omg.CORBA.ExceptionList;
import org.omg.CORBA.LocalObject;
import org.omg.CORBA.NamedValue;
import org.omg.CORBA.NO_IMPLEMENT;
import org.omg.CORBA.NO_RESOURCES;
import org.omg.CORBA.NVList;
import org.omg.CORBA.Object;
import org.omg.CORBA.ParameterMode;
import org.omg.CORBA.Policy;
import org.omg.CORBA.SystemException;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.INTERNAL;
import org.omg.CORBA.UserException;
import org.omg.CORBA.portable.ApplicationException;
import org.omg.CORBA.portable.InputStream;

import com.sun.corba.se.internal.corba.ClientDelegate;
import com.sun.corba.se.internal.core.ClientResponse;
import com.sun.corba.se.internal.core.ClientRequest;
import com.sun.corba.se.internal.core.DuplicateServiceContext;
import com.sun.corba.se.internal.core.GIOPVersion;
import com.sun.corba.se.internal.core.IOR;
import com.sun.corba.se.internal.core.NoSuchServiceContext;
import com.sun.corba.se.internal.core.ServiceContexts;
import com.sun.corba.se.internal.core.UnknownServiceContext;
import com.sun.corba.se.internal.iiop.CDROutputStream;
import com.sun.corba.se.internal.iiop.Connection;
import com.sun.corba.se.internal.iiop.CDRInputStream_1_0;
import com.sun.corba.se.internal.ior.IIOPProfile;
import com.sun.corba.se.internal.orbutil.ORBUtility;
import com.sun.corba.se.internal.util.RepositoryId;

import org.omg.IOP.ServiceContext;
import org.omg.IOP.ServiceContextHelper;
import org.omg.IOP.TaggedProfile;
import org.omg.IOP.TaggedProfileHelper;
import org.omg.IOP.TaggedComponent;
import org.omg.IOP.TaggedComponentHelper;
import org.omg.IOP.TAG_INTERNET_IOP;
import org.omg.PortableInterceptor.ClientRequestInfo;
import org.omg.PortableInterceptor.LOCATION_FORWARD;
import org.omg.Dynamic.Parameter;
import org.omg.PortableInterceptor.SUCCESSFUL;
import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;
import org.omg.PortableInterceptor.TRANSPORT_RETRY;
import org.omg.PortableInterceptor.USER_EXCEPTION;

import java.util.*;

/**
 * Implementation of the ClientRequestInfo interface as specified in
 * orbos/99-12-02 section 5.4.2.
 */
public final class ClientRequestInfoImpl 
    extends RequestInfoImpl 
    implements ClientRequestInfo 
{

    // The available constants for startingPointCall
    static final int CALL_SEND_REQUEST = 0;
    static final int CALL_SEND_POLL = 1;
    
    // The available constants for endingPointCall
    static final int CALL_RECEIVE_REPLY = 0;
    static final int CALL_RECEIVE_EXCEPTION = 1;
    static final int CALL_RECEIVE_OTHER = 2;

    //////////////////////////////////////////////////////////////////////
    //
    // NOTE: IF AN ATTRIBUTE IS ADDED, PLEASE UPDATE RESET();
    //
    //////////////////////////////////////////////////////////////////////
    
    // The current retry request status.  True if this request is being 
    // retried and this info object is to be reused, or false otherwise.
    private boolean retryRequest;
    
    // The number of times this info object has been (re)used.  This is
    // incremented every time a request is retried, and decremented every
    // time a request is complete.  When this reaches zero, the info object
    // is popped from the ClientRequestInfoImpl ThreadLocal stack in PIORB.
    private int entryCount = 0;

    // The RequestImpl is set when the call is DII based.
    // The DII query calls like ParameterList, ExceptionList,
    // ContextList will be delegated to RequestImpl.
    private org.omg.CORBA.Request request;

    // Sources of client request information
    private IIOPProfile profile;
    private IOR targetIOR;
    private IOR effectiveTargetIOR;
    private int requestId;
    private String opName;
    private boolean isOneWay;
    private boolean diiInitiate;
    private ClientDelegate clientDelegate;
    private ClientResponse response;

    // Cached information:
    private org.omg.CORBA.Object cachedTargetObject;
    private org.omg.CORBA.Object cachedEffectiveTargetObject;
    private Parameter[] cachedArguments;
    private TypeCode[] cachedExceptions;
    private String[] cachedContexts;
    private String[] cachedOperationContext;
    private String cachedReceivedExceptionId;
    private Any cachedResult;
    private Any cachedReceivedException;
    private TaggedProfile cachedEffectiveProfile;
    // key = Integer, value = IOP.ServiceContext.
    private HashMap cachedRequestServiceContexts;
    // key = Integer, value = IOP.ServiceContext.
    private HashMap cachedReplyServiceContexts;
    // key = Integer, value = TaggedComponent
    private HashMap cachedEffectiveComponents;


    protected boolean piCurrentPushed;
    
    //////////////////////////////////////////////////////////////////////
    //
    // NOTE: IF AN ATTRIBUTE IS ADDED, PLEASE UPDATE RESET();
    //
    //////////////////////////////////////////////////////////////////////
    
    /**
     * Reset the info object so that it can be reused for a retry,
     * for example.
     */
    void reset() {
        super.reset();

	// Please keep these in the same order that they're declared above.
        
        retryRequest = false;

        // Do not reset entryCount because we need to know when to pop this
        // from the stack.

        request = null;
        profile = null;
	targetIOR = null;
        effectiveTargetIOR = null;
	requestId = 0;
	opName = null;
        isOneWay = false;
	diiInitiate = false;
	clientDelegate = null;
	response = null;

	// Clear cached attributes:
	cachedTargetObject = null;
	cachedEffectiveTargetObject = null;
	cachedArguments = null;
	cachedExceptions = null;
	cachedContexts = null;
	cachedOperationContext = null;
	cachedReceivedExceptionId = null;
	cachedResult = null;
	cachedReceivedException = null;
	cachedEffectiveProfile = null;
	cachedRequestServiceContexts = null;
	cachedReplyServiceContexts = null;
        cachedEffectiveComponents = null;

	piCurrentPushed = false;

        startingPointCall = CALL_SEND_REQUEST;
        endingPointCall = CALL_RECEIVE_REPLY;

    }
    
    /*
     **********************************************************************
     * Access protection
     **********************************************************************/
    
    // Method IDs for all methods in ClientRequestInfo.  This allows for a 
    // convenient O(1) lookup for checkAccess().
    protected static final int MID_TARGET                  = MID_RI_LAST + 1;
    protected static final int MID_EFFECTIVE_TARGET        = MID_RI_LAST + 2;
    protected static final int MID_EFFECTIVE_PROFILE       = MID_RI_LAST + 3;
    protected static final int MID_RECEIVED_EXCEPTION      = MID_RI_LAST + 4;
    protected static final int MID_RECEIVED_EXCEPTION_ID   = MID_RI_LAST + 5;
    protected static final int MID_GET_EFFECTIVE_COMPONENT = MID_RI_LAST + 6;
    protected static final int MID_GET_EFFECTIVE_COMPONENTS
                                                           = MID_RI_LAST + 7;
    protected static final int MID_GET_REQUEST_POLICY      = MID_RI_LAST + 8;
    protected static final int MID_ADD_REQUEST_SERVICE_CONTEXT 
                                                           = MID_RI_LAST + 9;
    
    // ClientRequestInfo validity table (see ptc/00-08-06 table 21-1).
    // Note: These must be in the same order as specified in contants.
    protected static final boolean validCall[][] = {
        // LEGEND:
        // s_req = send_request     r_rep = receive_reply
        // s_pol = send_poll        r_exc = receive_exception
        //                          r_oth = receive_other
        //
        // A true value indicates call is valid at specified point.  
        // A false value indicates the call is invalid.
        //
        //
        // NOTE: If the order or number of columns change, update 
        // checkAccess() accordingly.
        //
        //                              { s_req, s_pol, r_rep, r_exc, r_oth }
        // RequestInfo methods:
        /*request_id*/                  { true , true , true , true , true  },
        /*operation*/                   { true , true , true , true , true  },
        /*arguments*/                   { true , false, true , false, false },
        /*exceptions*/                  { true , false, true , true , true  },
        /*contexts*/                    { true , false, true , true , true  },
        /*operation_context*/           { true , false, true , true , true  },
        /*result*/                      { false, false, true , false, false },
        /*response_expected*/           { true , true , true , true , true  },
        /*sync_scope*/                  { true , false, true , true , true  },
        /*reply_status*/                { false, false, true , true , true  },
        /*forward_reference*/           { false, false, false, false, true  },
        /*get_slot*/                    { true , true , true , true , true  },
        /*get_request_service_context*/ { true , false, true , true , true  },
        /*get_reply_service_context*/   { false, false, true , true , true  },
        //
        // ClientRequestInfo methods::
        /*target*/                      { true , true , true , true , true  },
        /*effective_target*/            { true , true , true , true , true  },
        /*effective_profile*/           { true , true , true , true , true  },
        /*received_exception*/          { false, false, false, true , false },
        /*received_exception_id*/       { false, false, false, true , false },
        /*get_effective_component*/     { true , false, true , true , true  },
        /*get_effective_components*/    { true , false, true , true , true  },
        /*get_request_policy*/          { true , false, true , true , true  },
        /*add_request_service_context*/ { true , false, false, false, false }
    };
    

    /*
     **********************************************************************
     * Public ClientRequestInfo interfaces
     **********************************************************************/
    
    /**
     * Creates a new ClientRequestInfo implementation.
     * The constructor is package scope since no other package need create
     * an instance of this class.
     */
    protected ClientRequestInfoImpl( PIORB piOrb ) { 
        super( piOrb ); 
        startingPointCall = CALL_SEND_REQUEST;
        endingPointCall = CALL_RECEIVE_REPLY;
    }
    
    /**
     * The object which the client called to perform the operation.
     */
    public org.omg.CORBA.Object target (){
	// access is currently valid for all states:
        //checkAccess( MID_TARGET );
	if (cachedTargetObject == null) {
	    // No synchronization since not worth it here.
	    cachedTargetObject = iorToObject(targetIOR);
	}
	return cachedTargetObject;
    }
    
    /**
     * The actual object on which the operation will be invoked.  If the 
     * reply_status is LOCATION_FORWARD, then on subsequent requests, 
     * effective_target will contain the forwarded IOR while target will 
     * remain unchanged.  
     */
    public org.omg.CORBA.Object effective_target() {
	// access is currently valid for all states:
        //checkAccess( MID_EFFECTIVE_TARGET );

        // Note: This is not necessarily the same as locatedIOR.
        // Reason: See the way we handle COMM_FAILURES in 
        // ClientDelegate.createRequest, v1.32

	if (cachedEffectiveTargetObject == null) {
	    // No synchronization since not worth it here.
	    cachedEffectiveTargetObject = iorToObject(effectiveTargetIOR);
	}
	return cachedEffectiveTargetObject;
    }
    
    /**
     * The profile that will be used to send the request.  If a location 
     * forward has occurred for this operation's object and that object's 
     * profile change accordingly, then this profile will be that located 
     * profile.
     */
    public TaggedProfile effective_profile (){
        // access is currently valid for all states:
        //checkAccess( MID_EFFECTIVE_PROFILE );

	if( cachedEffectiveProfile == null ) {
	    cachedEffectiveProfile = profile.getIOPProfile( piOrb ) ;
	}

	// Good citizen: In the interest of efficiency, we assume interceptors
	// will not modify the returned TaggedProfile in any way so we need
	// not make a deep copy of it.

	return cachedEffectiveProfile;
    }
    
    /**
     * Contains the exception to be returned to the client.
     */
    public Any received_exception (){
        checkAccess( MID_RECEIVED_EXCEPTION );

	if( cachedReceivedException == null ) {
	    cachedReceivedException = exceptionToAny( exception );
	}

	// Good citizen: In the interest of efficiency, we assume interceptors
	// will not modify the returned Any in any way so we need
	// not make a deep copy of it.

	return cachedReceivedException;
    }
    
    /**
     * The CORBA::RepositoryId of the exception to be returned to the client.
     */
    public String received_exception_id (){
        checkAccess( MID_RECEIVED_EXCEPTION_ID );

	if( cachedReceivedExceptionId == null ) {
	    String result = null;
	    
	    if( exception == null ) {
		// Note: exception should never be null here since we will 
		// throw a BAD_INV_ORDER if this is not called from 
		// receive_exception.
		throw new INTERNAL( 
		    "Exception was null in received_exception_id",
		    MinorCodes.EXCEPTION_WAS_NULL, 
		    CompletionStatus.COMPLETED_NO );
	    }
	    else if( exception instanceof SystemException ) {
		String name = exception.getClass().getName();
		result = ORBUtility.repositoryIdOf(name);
	    }
	    else if( exception instanceof ApplicationException ) {
		result = ((ApplicationException)exception).getId();
	    }

	    // _REVISIT_ We need to be able to handle a UserException in the 
	    // DII case.  How do we extract the ID from a UserException?
	    
	    cachedReceivedExceptionId = result;
	}

	return cachedReceivedExceptionId;
    }
    
    /**
     * Returns the IOP::TaggedComponent with the given ID from the profile 
     * selected for this request.  IF there is more than one component for a 
     * given component ID, it is undefined which component this operation 
     * returns (get_effective_component should be called instead).
     */
    public TaggedComponent get_effective_component (int id){
        checkAccess( MID_GET_EFFECTIVE_COMPONENT );
        
        return get_effective_components( id )[0];
    }
    
    /**
     * Returns all the tagged components with the given ID from the profile 
     * selected for this request.
     */
    public TaggedComponent[] get_effective_components (int id){
        checkAccess( MID_GET_EFFECTIVE_COMPONENTS );
	Integer integerId = new Integer( id );
	TaggedComponent[] result = null;
	boolean justCreatedCache = false;

	if( cachedEffectiveComponents == null ) {
	    cachedEffectiveComponents = new HashMap();
	    justCreatedCache = true;
	}
	else {
	    // Look in cache:
	    result = (TaggedComponent[])cachedEffectiveComponents.get( 
		integerId );
	}
        
	// null could mean we cached null or not in cache.
	if( (result == null) &&
	    (justCreatedCache ||
	    !cachedEffectiveComponents.containsKey( integerId ) ) )
	{
	    // Not in cache.  Get it from the profile:
            result = profile.getIOPComponents( piOrb, id ) ;
	    cachedEffectiveComponents.put( integerId, result );
	}
        
        // As per ptc/00-08-06, section 21.3.13.6., If not found, raise 
        // BAD_PARAM with minor code INVLID_COMPONENT_ID.
        if( (result == null) || (result.length == 0) ) {
            throw new BAD_PARAM( "Could not find component with provided id.",
                                 MinorCodes.INVALID_COMPONENT_ID,
                                 CompletionStatus.COMPLETED_NO );
        }

	// Good citizen: In the interest of efficiency, we will assume 
	// interceptors will not modify the returned TaggedCompoent[], or
	// the TaggedComponents inside of it.  Otherwise, we would need to
	// clone the array and make a deep copy of its contents.
        
        return result;
    }
    
    /**
     * Returns the given policy in effect for this operation.
     */
    public Policy get_request_policy (int type){
        checkAccess( MID_GET_REQUEST_POLICY );
	// _REVISIT_ Our ORB is not policy-based at this time.
	throw new NO_IMPLEMENT( "This ORB is not policy-based.",
	    MinorCodes.PI_ORB_NOT_POLICY_BASED, 
	    CompletionStatus.COMPLETED_NO );
    }
    
    /**
     * Allows interceptors to add service contexts to the request.
     * <p>
     * There is no declaration of the order of the service contexts.  They 
     * may or may not appear in the order they are added.
     */
    public void add_request_service_context (ServiceContext service_context, 
                                             boolean replace)
    {
        checkAccess( MID_ADD_REQUEST_SERVICE_CONTEXT );

	if( cachedRequestServiceContexts == null ) {
	    cachedRequestServiceContexts = new HashMap();
	}

	addServiceContext( cachedRequestServiceContexts, serviceContexts, 
			   service_context, replace );
    }
    
    // NOTE: When adding a method, be sure to:
    // 1. Add a MID_* constant for that method
    // 2. Call checkAccess at the start of the method
    // 3. Define entries in the validCall[][] table for interception points.

    /*
     **********************************************************************
     * Public RequestInfo interfaces
     *
     * These are implemented here because they have differing 
     * implementations depending on whether this is a client or a server
     * request info object.
     **********************************************************************/
   
    /**
     * See RequestInfoImpl for javadoc.
     */
    public int request_id (){
        // access is currently valid for all states:
        //checkAccess( MID_REQUEST_ID );
	/* 
	 * NOTE: The requestId in client interceptors is the same as the
	 * GIOP request id.  This works because both interceptors and
	 * request ids are scoped by the ORB on the client side.
	 */
        return requestId;
    }

    /**
     * See RequestInfoImpl for javadoc.
     */
    public String operation (){
        // access is currently valid for all states:
        //checkAccess( MID_OPERATION );
        return opName;
    }

    /**
     * See RequestInfoImpl for javadoc.
     */
    public Parameter[] arguments (){
        checkAccess( MID_ARGUMENTS );

	if( cachedArguments == null ) {
	    if( request == null ) {
		throw new NO_RESOURCES(
		    "The Portable Java Bindings do not support arguments()",
		    MinorCodes.PI_OPERATION_NOT_SUPPORTED,
		    CompletionStatus.COMPLETED_NO );
	    }

	    // If it is DII request then get the arguments from the DII req
	    // and convert that into parameters.
	    cachedArguments = nvListToParameterArray( request.arguments() );
	}

        // Good citizen: In the interest of efficiency, we assume 
        // interceptors will be "good citizens" in that they will not 
        // modify the contents of the Parameter[] array.  We also assume 
        // they will not change the values of the containing Anys.

	return cachedArguments;
    }

    /**
     * See RequestInfoImpl for javadoc.
     */
    public TypeCode[] exceptions (){
        checkAccess( MID_EXCEPTIONS );

	if( cachedExceptions == null ) {
	    if( request == null ) {
	       throw new NO_RESOURCES(
		   "The Portable Java Bindings do not support exceptions()",
		   MinorCodes.PI_OPERATION_NOT_SUPPORTED,
		   CompletionStatus.COMPLETED_NO );
	    }

	    // Get the list of exceptions from DII request data, If there are
	    // no exceptions raised then this method will return null.
	    ExceptionList excList = request.exceptions( );
	    int count = excList.count();
	    TypeCode[] excTCList = new TypeCode[count];
	    try {
		for( int i = 0; i < count; i++ ) {
		    excTCList[i] = excList.item( i );
		}
	    } catch( Exception e ) {
		throw new INTERNAL( "Exception in Requestnfo.exceptions()",
		    MinorCodes.EXCEPTION_IN_EXCEPTIONS,
		    CompletionStatus.COMPLETED_NO );
	    }

	    cachedExceptions = excTCList;
	}

        // Good citizen: In the interest of efficiency, we assume 
        // interceptors will be "good citizens" in that they will not 
        // modify the contents of the TypeCode[] array.  We also assume 
        // they will not change the values of the containing TypeCodes.

	return cachedExceptions;
    }

    /**
     * See RequestInfoImpl for javadoc.
     */
    public String[] contexts (){
        checkAccess( MID_CONTEXTS );

	if( cachedContexts == null ) {
	    if( request == null ) {
		throw new NO_RESOURCES(
		    "The Portable Java Bindings do not support contexts()",
		    MinorCodes.PI_OPERATION_NOT_SUPPORTED,
		    CompletionStatus.COMPLETED_NO );
	    }
	    // Get the list of contexts from DII request data, If there are
	    // no contexts then this method will return null.
	    ContextList ctxList = request.contexts( );
	    int count = ctxList.count();
	    String[] ctxListToReturn = new String[count];
	    try {
		for( int i = 0; i < count; i++ ) {
		    ctxListToReturn[i] = ctxList.item( i );
		}
	    } catch( Exception e ) {
		throw new INTERNAL( "Exception in RequestInfo.contexts()",
		    MinorCodes.EXCEPTION_IN_CONTEXTS,
		    CompletionStatus.COMPLETED_NO );
	    }

            cachedContexts = ctxListToReturn;
	}

        // Good citizen: In the interest of efficiency, we assume 
        // interceptors will be "good citizens" in that they will not 
        // modify the contents of the String[] array.  

	return cachedContexts;
    }

    /**
     * See RequestInfoImpl for javadoc.
     */
    public String[] operation_context (){
        checkAccess( MID_OPERATION_CONTEXT );

	if( cachedOperationContext == null ) {
	    if( request == null ) {
		throw new NO_RESOURCES(
		    "The Portable Java Bindings do not support contexts()",
		    MinorCodes.PI_OPERATION_NOT_SUPPORTED,
		    CompletionStatus.COMPLETED_NO );
	    }
	    // Get the list of contexts from DII request data, If there are
	    // no contexts then this method will return null.
	    Context ctx = request.ctx( );
	    // _REVISIT_ The API for get_values is not compliant with the spec,
	    // Revisit this code once it's fixed.
	    // _REVISIT_ Our ORB doesn't support Operation Context, This code
	    // will not be excerscised until it's supported.
	    // The first parameter in get_values is the start_scope which 
	    // if blank makes it as a global scope.
	    // The second parameter is op_flags which is set to RESTRICT_SCOPE
	    // As there is only one defined in the spec.
	    // The Third param is the pattern which is '*' requiring it to 
	    // get all the contexts.
	    NVList nvList = ctx.get_values( "", CTX_RESTRICT_SCOPE.value,"*" );
	    String[] context = new String[(nvList.count() * 2) ];
	    if( ( nvList != null ) &&( nvList.count() != 0 ) ) {
		// The String[] array will contain Name and Value for each
		// context and hence double the size in the array.
		int index = 0;
		for( int i = 0; i < nvList.count(); i++ ) {
		    NamedValue nv;
		    try {
			nv = nvList.item( i );
		    }
		    catch (Exception e ) {
			return (String[]) null;
		    }
		    context[index] = nv.name();
		    index++;
		    context[index] = nv.value().extract_string();
		    index++;
		}
	    }

	    cachedOperationContext = context;
	}

        // Good citizen: In the interest of efficiency, we assume 
        // interceptors will be "good citizens" in that they will not 
        // modify the contents of the String[] array.  

	return cachedOperationContext;
    }

    /**
     * See RequestInfoImpl for javadoc.
     */
    public Any result (){
        checkAccess( MID_RESULT );

	if( cachedResult == null ) {
	    if( request == null ) {
		throw new NO_RESOURCES(
		    "The Portable Java Bindings do not support result()",
		    MinorCodes.PI_OPERATION_NOT_SUPPORTED,
		    CompletionStatus.COMPLETED_NO );
	    }
	    // Get the result from the DII request data.
	    NamedValue nvResult = request.result( );

	    if( nvResult == null ) {
		throw new INTERNAL(
		    "DII nvResult should not be null.",
		    MinorCodes.PI_DII_RESULT_IS_NULL,
		    CompletionStatus.COMPLETED_NO );
	    }

	    cachedResult = nvResult.value();
	}

	// Good citizen: In the interest of efficiency, we assume that
	// interceptors will not modify the contents of the result Any.
	// Otherwise, we would need to create a deep copy of the Any.

        return cachedResult;
    }

    /**
     * See RequestInfoImpl for javadoc.
     */
    public boolean response_expected (){
	// access is currently valid for all states:
	//checkAccess( MID_RESPONSE_EXPECTED );
	return !isOneWay;
    }

    /**
     * See RequestInfoImpl for javadoc.
     */
    public Object forward_reference (){
        checkAccess( MID_FORWARD_REFERENCE );
        // Check to make sure we are in LOCATION_FORWARD
        // state as per ptc/00-08-06, table 21-1
        // footnote 2.
        if( replyStatus != LOCATION_FORWARD.value ) {
            throw new BAD_INV_ORDER(
                "Invalid to query forward_reference() when reply status " +
                "is not LOCATION_FORWARD.",
                MinorCodes.INVALID_PI_CALL, CompletionStatus.COMPLETED_NO );
        }

	// Do not cache this value since if an interceptor raises
	// forward request then the next interceptor in the
	// list should see the new value.
	IOR ior = clientDelegate.getLocatedIOR();
	return iorToObject(ior);
    }

    /**
     * See RequestInfoImpl for javadoc.
     */
    public org.omg.IOP.ServiceContext get_request_service_context( int id ) {
        checkAccess( MID_GET_REQUEST_SERVICE_CONTEXT );

	if( cachedRequestServiceContexts == null ) {
	    cachedRequestServiceContexts = new HashMap();
	}

        return getServiceContext( cachedRequestServiceContexts, 
				  serviceContexts, id );
    }

    /**
     * does not contain an etry for that ID, BAD_PARAM with a minor code of
     * TBD_BP is raised.
     */
    public org.omg.IOP.ServiceContext get_reply_service_context( int id ) {
        checkAccess( MID_GET_REPLY_SERVICE_CONTEXT );       

	// In the event this is called from a oneway, we will have no
	// response object.
	//
	// In the event this is called after a IIOPConnection.purge_calls,
	// we will have a response object, but that object will
	// not contain a header (which would hold the service context
	// container).  See bug 4624102.
	//
	// REVISIT: this is the only thing used
	// from response at this time.  However, a more general solution
	// would avoid accessing other parts of response's header.
	//
	// Instead of throwing a NullPointer, we will
	// "gracefully" handle these with a BAD_PARAM with minor code 25.
	if( response == null ||
	    response.getServiceContexts() == null )
	{                     
	    throw new BAD_PARAM( "No such service context " +
				 "(note: inaccessible from oneway calls)",
				 MinorCodes.INVALID_SERVICE_CONTEXT_ID,
				 CompletionStatus.COMPLETED_NO );
	}

	if( cachedReplyServiceContexts == null ) {
	    cachedReplyServiceContexts = new HashMap();
	}

        return getServiceContext( cachedReplyServiceContexts,
				  response.getServiceContexts(), id );
    }

    /*
     **********************************************************************
     * Package-scope interfaces
     **********************************************************************/

    /** 
     * Stores the various sources of information used for this info object.
     */
    protected void setInfo( Connection connection,
			    ClientDelegate delegate, 
                            IOR effectiveTargetIOR,
                            IIOPProfile profile, 
                            int requestId,
                            String opName,
                            boolean isOneWay,
                            ServiceContexts svc ) 
    {
	this.connection = connection;
        this.clientDelegate = delegate;
	this.targetIOR = delegate.getIOR();
        this.effectiveTargetIOR = effectiveTargetIOR;
        this.profile = profile;
        this.requestId = requestId;
        this.opName = opName;
        this.isOneWay = isOneWay;
        this.serviceContexts = svc;
    }
    
    /** 
     * Stores the various sources of information used for this info object.
     */
    protected void setInfo( ClientResponse response ) {
        this.response = response;
    }

    /**
     * Set or reset the retry request flag.  
     */
    void setRetryRequest( boolean retryRequest ) {
        this.retryRequest = retryRequest;
    }
    
    /**
     * Retrieve the current retry request status.
     */
    boolean getRetryRequest() {
        return this.retryRequest;
    }
    
    /**
     * Increases the entry count by 1.
     */
    void incrementEntryCount() {
        this.entryCount++;
    }
    
    /**
     * Decreases the entry count by 1.
     */
    void decrementEntryCount() {
        this.entryCount--;
    }
    
    /**
     * Retrieve the current entry count
     */
    int getEntryCount() {
        return this.entryCount;
    }
    
    /**
     * Overridden from RequestInfoImpl.  Calls the super class, then
     * sets the ending point call depending on the reply status.
     */
    protected void setReplyStatus( short replyStatus ) {
        super.setReplyStatus( replyStatus );
        switch( replyStatus ) {
        case SUCCESSFUL.value:
            endingPointCall = CALL_RECEIVE_REPLY;
            break;
        case SYSTEM_EXCEPTION.value:
        case USER_EXCEPTION.value:
            endingPointCall = CALL_RECEIVE_EXCEPTION;
            break;
        case LOCATION_FORWARD.value:
        case TRANSPORT_RETRY.value:
            endingPointCall = CALL_RECEIVE_OTHER;
            break;
        }
    }

    /**
     * Sets DII request object in the RequestInfoObject.
     */
    protected void setDIIRequest(org.omg.CORBA.Request req) {
         request = req;
    }

    /**
     * Keeps track of whether initiate was called for a DII request.  The ORB
     * needs to know this so it knows whether to ignore a second call to
     * initiateClientPIRequest or not.
     */
    protected void setDIIInitiate( boolean diiInitiate ) {
	this.diiInitiate = diiInitiate;
    }

    /**
     * See comment for setDIIInitiate 
     */
    protected boolean isDIIInitiate() {
	return this.diiInitiate;
    }

    /**
     * The PICurrent stack should only be popped if it was pushed.
     * This is generally the case.  But exceptions which occur
     * after the stub's entry to _request but before the push
     * end up in _releaseReply which will try to pop unless told not to.
     */
    protected void setPICurrentPushed( boolean piCurrentPushed ) {
	this.piCurrentPushed = piCurrentPushed;
    }

    protected boolean isPICurrentPushed() {
	return this.piCurrentPushed;
    }

    /**
     * Overridden from RequestInfoImpl.
     */
    protected void setException( Exception exception ) {
        super.setException( exception );

	// Clear cached values:
	cachedReceivedException = null;
	cachedReceivedExceptionId = null;
    }

    protected boolean getIsOneWay() {
	return this.isOneWay;
    }

    /**
     * Returns the reference to the ClientDelegate object
     */
    protected ClientDelegate getClientDelegate() {
        return this.clientDelegate;
    }

    /**
     * See description for RequestInfoImpl.checkAccess
     */
    protected void checkAccess( int methodID ) 
        throws BAD_INV_ORDER 
    {
        // Make sure currentPoint matches the appropriate index in the
        // validCall table:
        int validCallIndex = 0;
        switch( currentExecutionPoint ) {
        case EXECUTION_POINT_STARTING:
            switch( startingPointCall ) {
            case CALL_SEND_REQUEST:
                validCallIndex = 0;
                break;
            case CALL_SEND_POLL:
                validCallIndex = 1;
                break;
            }
            break;
        case EXECUTION_POINT_ENDING:
            switch( endingPointCall ) {
            case CALL_RECEIVE_REPLY:
                validCallIndex = 2;
                break;
            case CALL_RECEIVE_EXCEPTION:
                validCallIndex = 3;
                break;
            case CALL_RECEIVE_OTHER:
                validCallIndex = 4;
                break;
            }
            break;
        }
        
        // Check the validCall table:
        if( !validCall[methodID][validCallIndex] ) {
            throw new BAD_INV_ORDER( 
                "Cannot access this info attribute/method at this point.",
                MinorCodes.INVALID_PI_CALL, CompletionStatus.COMPLETED_NO );
        }
    }
    
}
