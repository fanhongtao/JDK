/*
 * @(#)PIORB.java	1.63 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.sun.corba.se.internal.Interceptors;
             
import org.omg.CORBA.Any;
import org.omg.CORBA.BAD_PARAM;
import org.omg.CORBA.BAD_POLICY;
import org.omg.CORBA.BAD_INV_ORDER;
import org.omg.CORBA.COMM_FAILURE;
import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.INTERNAL;
import org.omg.CORBA.NVList;
import org.omg.CORBA.OBJECT_NOT_EXIST;
import org.omg.CORBA.ORBPackage.InvalidName;
import org.omg.CORBA.SystemException;
import org.omg.CORBA.UserException;
import org.omg.CORBA.UNKNOWN;
import org.omg.CORBA.portable.ApplicationException;
import org.omg.CORBA.portable.Delegate;
import org.omg.CORBA.portable.ObjectImpl;
import org.omg.CORBA.portable.RemarshalException;

import com.sun.corba.se.internal.corba.ClientDelegate;
import com.sun.corba.se.internal.corba.RequestImpl;
import com.sun.corba.se.internal.core.ClientResponse;
import com.sun.corba.se.internal.core.ClientRequest;
import com.sun.corba.se.internal.core.ClientSubcontract;
import com.sun.corba.se.internal.core.InternalRuntimeForwardRequest;
import com.sun.corba.se.internal.core.IOR;
import com.sun.corba.se.internal.core.ServerRequest;
import com.sun.corba.se.internal.core.ServiceContexts;
import com.sun.corba.se.internal.core.Constant;
import com.sun.corba.se.internal.iiop.Connection;
import com.sun.corba.se.internal.iiop.IIOPConnection;
import com.sun.corba.se.internal.iiop.IIOPOutputStream;
import com.sun.corba.se.internal.iiop.messages.ReplyMessage;
import com.sun.corba.se.internal.ior.IIOPProfile;
import com.sun.corba.se.internal.ior.IORTemplate;

import com.sun.corba.se.internal.orbutil.ORBConstants;
import com.sun.corba.se.internal.orbutil.ORBClassLoader;

import com.sun.corba.se.internal.POA.POAImpl;
import com.sun.corba.se.internal.POA.POAORB;

import com.sun.corba.se.internal.orbutil.ORBUtility;
import org.omg.IOP.CodecFactory;

import org.omg.PortableInterceptor.ForwardRequest;
import org.omg.PortableInterceptor.Interceptor;
import org.omg.PortableInterceptor.LOCATION_FORWARD;
import org.omg.PortableInterceptor.ORBInitializer;
import org.omg.PortableInterceptor.ORBInitInfo;
import org.omg.PortableInterceptor.ORBInitInfoPackage.DuplicateName;
import org.omg.PortableInterceptor.SUCCESSFUL;
import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;
import org.omg.PortableInterceptor.TRANSPORT_RETRY;
import org.omg.PortableInterceptor.USER_EXCEPTION;
import org.omg.PortableInterceptor.PolicyFactory;

import java.util.*;
import java.io.IOException;

/** 
 * Extends the POAORB to provide portable interceptor functionality.  
 */
public class PIORB 
    extends POAORB
{
    // A unique id used in ServerRequestInfo.
    // This does not correspond to the GIOP request id.
    private int serverRequestIdCounter = 0;

    // Stores the codec factory for producing codecs
    CodecFactory codecFactory = null;

    // The actual instances of the initializers.  Some elements mat be null
    // if there were errors finding or instantiating intializers.  These
    // entries should be ignored.
    ORBInitializer[] orbInitializers = null;

    // The arguments passed to the application's main method.  May be null.
    // This is used for ORBInitializers and set from set_parameters.
    String[] arguments = null;

    // List of property name prefixes recognized by this ORB.
    private static final String[] PIORBPropertyNamePrefixes = {
	ORBConstants.PI_ORB_INITIALIZER_CLASS_PREFIX
    };

    // The list of portable interceptors, organized by type:
    private InterceptorList interceptorList;

    // Cached information for optimization - do we have any interceptors
    // registered of the given types?  Set during ORB initialization.
    private boolean hasIORInterceptors;
    private boolean hasClientInterceptors;
    private boolean hasServerInterceptors;

    // The class responsible for invoking interceptors
    private InterceptorInvoker interceptorInvoker;

    // There will be one PICurrent instantiated for every ORB.
    private PICurrent current;

    // This table contains a list of PolicyFactories registered using
    // ORBInitInfo.registerPolicyFactory() method.
    // Key for the table is PolicyType which is an Integer
    // Value is PolicyFactory.
    private HashMap policyFactoryTable;
    
    // Table to convert from a ReplyMessage.? to a PI replyStatus short.
    // Note that this table relies on the order and constants of 
    // ReplyMessage not to change.
    private final static short REPLY_MESSAGE_TO_PI_REPLY_STATUS[] = {
        SUCCESSFUL.value,       // = ReplyMessage.NO_EXCEPTION
        USER_EXCEPTION.value,   // = ReplyMessage.USER_EXCEPTION
        SYSTEM_EXCEPTION.value, // = ReplyMessage.SYSTEM_EXCEPTION
        LOCATION_FORWARD.value, // = ReplyMessage.LOCATION_FORWARD
        LOCATION_FORWARD.value, // = ReplyMessage.LOCATION_FORWARD_PERM
        TRANSPORT_RETRY.value   // = ReplyMessage.NEEDS_ADDRESSING_MODE
    };
            
    // ThreadLocal containing a stack to store client request info objects
    // and a disable count.
    private ThreadLocal threadLocalClientRequestInfoStack = 
        new ThreadLocal() {
            protected Object initialValue() {
                return new RequestInfoStack();
            }
        };

    // ThreadLocal containing the current server request info object.
    private ThreadLocal threadLocalServerRequestInfoStack =
	new ThreadLocal() {
	    protected Object initialValue() {
		return new RequestInfoStack();
	    }
	};
     
    // Class to contain all ThreadLocal data for ClientRequestInfo
    // maintenance.
    //
    // We simulate a Stack here instead of using java.util.Stack because
    // java.util.Stack is thread-safe, negatively impacting performance.
    // We use an ArrayList instead since it is not thread-safe.  
    // RequestInfoStack is used quite frequently.
    private final class RequestInfoStack {
        // The stack for RequestInfo objects.  
        private final ArrayList stack = new ArrayList();

        // Number of times a request has been made to disable interceptors.
        // When this reaches 0, interception hooks are disabled.  Any higher
        // value indicates they are enabled.
	// NOTE: The is only currently used on the client side.
        public int disableCount = 0;

	// Tests if this stack is empty.
	public final boolean empty() {
	    return stack.size() == 0;
	}

	// Looks at the object at the top of this stack without removing it
	// from the stack.
	public final Object peek() {
	    int len = stack.size();

	    if( len == 0 ) throw new EmptyStackException();

	    return stack.get( len - 1 );
	}

	// Removes the object at the top of this stack and returns that 
	// object as the value of this function.
	public final Object pop() {
	    Object obj;
	    int len = stack.size();

	    if( len == 0 ) throw new EmptyStackException();
	    obj = stack.get( len - 1 );
	    stack.remove( len - 1 );

	    return obj;
	}

	// Pushes an item onto the top of the stack
	public final Object push( Object item ) {
	    stack.add( item );
	    return item;
	}
    }
        
    /**
     * Default constructor.  
     * This is the only constructor, and it must be followed by
     * the appropriate set_parameters() call.
     */
    public PIORB() {
	super();

	// Create codec factory:
	codecFactory = new CodecFactoryImpl( this );

	// Create new interceptor list:
	interceptorList = new InterceptorList();

        // Create a new PICurrent.
        current = new PICurrent( this );

	// Create new interceptor invoker, initially disabled:
	interceptorInvoker = new InterceptorInvoker( this, interceptorList, 
                                                     current );

	// Register the PI current and Codec factory objects
	registerInitialReference( ORBConstants.PI_CURRENT_NAME, 
	    new Constant( current ) ) ;
	registerInitialReference( ORBConstants.CODEC_FACTORY_NAME, 
	    new Constant( codecFactory ) ) ;
    }

    /**
     * Do miscellaneous initialization for subcontracts.  This is called
     * after set_parameters() in the POAORB.  It is used here to create
     * the CodecFactory.
     */
    protected void initPostProcessing() {
	// Allow POAORB to do post-processing initialization
	super.initPostProcessing();
     
	// If we have any orb initializers, make use of them:
	if( orbInitializers != null ) {
	    // Create the ORBInitInfo object to pass to ORB intializers:
	    ORBInitInfoImpl orbInitInfo = createORBInitInfo();

	    // Make sure get_slot and set_slot are not called from within
	    // ORB initializers:
	    current.setORBInitializing( true );

	    // Call pre_init on all ORB initializers:
	    preInitORBInitializers( orbInitInfo );

	    // Call post_init on all ORB initializers:
	    postInitORBInitializers( orbInitInfo );

	    // Proprietary: sort interceptors:
	    interceptorList.sortInterceptors();

	    // Re-enable get_slot and set_slot to be called from within
	    // ORB initializers:
	    current.setORBInitializing( false );

	    // Ensure nobody makes any more calls on this object.
            orbInitInfo.setStage( ORBInitInfoImpl.STAGE_CLOSED );

	    // Set cached flags indicating whether we have interceptors
	    // registered of a given type.
	    hasIORInterceptors = interceptorList.hasInterceptorsOfType(
		InterceptorList.INTERCEPTOR_TYPE_IOR );
	    hasClientInterceptors = interceptorList.hasInterceptorsOfType(
		InterceptorList.INTERCEPTOR_TYPE_CLIENT );
	    hasServerInterceptors = interceptorList.hasInterceptorsOfType(
		InterceptorList.INTERCEPTOR_TYPE_SERVER );

	    // Enable interceptor invoker (not necessary if no interceptors 
	    // are registered).  This should be the last stage of ORB
	    // initialization.
	    interceptorInvoker.setEnabled( true );
	}
    }

    /**
     *
     *	ptc/00-08-06 p 205: "When an application calls ORB::destroy, the ORB
     *	1) waits for all requests in progress to complete
     *	2) calls the Interceptor::destroy operation for each interceptor
     *	3) completes destruction of the ORB"
     */
    public void destroy() {
	super.destroy();
        interceptorList.destroyAll();
    }

    /* 
     **********************************************************************
     *  The following methods deal with ORB initialization and property 
     *  parsing, etc.
     ************************************************************************/

    /**
     * Return a list of property names prefixes that this ORB is interested in.
     * This may be overridden by subclasses, but subclasses must call
     * super.getPropertyNamePrefixes() to get all names.
     * Called from super.set_parameters() for both application and applets.
     */
    protected String[] getPropertyNamePrefixes() {
        String[] names = super.getPropertyNamePrefixes();

        // Add names that we are interested in
        int supercount = names.length;
        String[] allnames = new String[supercount + 
				       PIORBPropertyNamePrefixes.length];
        for ( int i=0; i<supercount; i++ )
            allnames[i] = names[i];
        for ( int i=0; i<PIORBPropertyNamePrefixes.length; i++ )
            allnames[i+supercount] = PIORBPropertyNamePrefixes[i];

        if (ORBInitDebug) {
            dprint( "getPropertyNamePrefixes returns " +
		    ORBUtility.objectToString( allnames ) ) ;
        }

        return allnames;
    }

    /**
     * Initialize any necessary ORB state; get attributes if possible.
     * Called from org.omg.CORBA.ORB.init().  This method is overridden
     * so that we may remember the commandline arguments passed to the
     * application instantiating this ORB.  This is later available by a call
     * to ORBInitInfo.arguments() for ORBInitializers.
     *
     * @param params command-line arguments for the application's main method.
     *     may be <code>null</code>.
     * "-param-name" and "param-value" strings.
     * @param props the application properties
     */
    protected void set_parameters (String[] params, Properties props) {
	this.arguments = params;
	super.set_parameters( params, props );
    }
 
    /**
     * Set ORB internal variables using the properties specified.
     * Called from super.set_parameters() for both applications and applets.
     */
    protected void parseProperties( Properties props ) {
	super.parseProperties( props );

	// Read in PI orb initializers and instantiate them:
	registerORBInitializers( props );
    }

    /**
     * Register and instantiate ORB initializers, as per PI spec, 
     * ptc/00-08-06, section 21.7.3.1
     */
    private void registerORBInitializers( Properties props ) {
	// Find all property names that begin with the orb init prefix
	// and add to the orb initializers list.
	String orbInitPrefix = ORBConstants.PI_ORB_INITIALIZER_CLASS_PREFIX;
	Enumeration propertyNames = props.propertyNames();
	ArrayList initializerList = new ArrayList();

	while( propertyNames.hasMoreElements() ) {
	    String propertyName = (String)propertyNames.nextElement();
	    if( propertyName.startsWith( orbInitPrefix ) ) {
		// Extract orb initializer name from property:
		String initClassName = propertyName.substring( 
		    orbInitPrefix.length() );
		try {
                    Class initClass = ORBClassLoader.loadClass( initClassName );

		    // For security reasons avoid creating an instance 
		    // if this class is one that would fail the class cast 
		    // to ORBInitializer anyway.
		    if( ORBInitializer.class.isAssignableFrom( initClass ) ) {
			// Now that we have a class object, instantiate one and
			// remember it:
			if( initClass != null ) {
			    ORBInitializer initializer = 
				(ORBInitializer)initClass.newInstance();
			    initializerList.add( initializer );
			}
		    }
		}
		catch( Exception e ) {
		    // As per ptc/00-08-06, section 21.7.3.1., "If there
		    // are any exceptions the ORB shall ignore them and
		    // proceed."
		}
	    } // end if this property starts with prefix
	} // end while there are more properties

	if( initializerList.size() > 0 ) {
	    orbInitializers = (ORBInitializer[])initializerList.toArray( 
		new ORBInitializer[0] );
	}
	else {
	    orbInitializers = null;
	}
    }

    /*
     **************************************************************************
     *  The following methods are the implementations for the Portable
     *  Interceptors hooks found in POAORB and corba.ORB.  
     *
     *  They are made final for security purposes - someone could otherwise 
     *  derive from PIORB and get sensitive information and intercept
     *  the interceptors themselves.  This way, there is
     *  no convenient way to get sensitive information and have PI 
     *  functionality at the same time (though they could inherit from POAORB
     *  and get sensitive information, they would have to re-implement 
     *  Portable Interceptors to get sensitive information and still have
     *  PI functionality).
     *************************************************************************/

    /**
     * Overridden from POAORB.
     * Called when a new POA is created.
     *
     * @param poaImpl The POAImpl associated with these IOR Interceptor.
     */
    final protected void invokeIORInterceptors( POAImpl poaImpl ) {
	if( !hasIORInterceptors ) return;

	// Forward request to interceptor invoker:
	interceptorInvoker.invokeIORInterceptors( poaImpl );
    }
    
    /*
     *****************
     * Client PI hooks
     *****************/

    /**
     * Overridden from corba.ORB.
     * Called for pseudo-ops to temporarily disable portable interceptor
     * hooks for calls on this thread.  Keeps track of the number of
     * times this is called and increments the disabledCount.
     */
    final protected void disableInterceptorsThisThread() {
	if( !hasClientInterceptors ) return;

        RequestInfoStack infoStack = 
            (RequestInfoStack)threadLocalClientRequestInfoStack.get();
        infoStack.disableCount++;
    }
    
    /**
     * Overridden from corba.ORB.
     * Called for pseudo-ops to re-enable portable interceptor
     * hooks for calls on this thread.  Decrements the disabledCount.
     * If disabledCount is 0, interceptors are re-enabled.
     */
    final protected void enableInterceptorsThisThread() {
	if( !hasClientInterceptors ) return;

        RequestInfoStack infoStack = 
            (RequestInfoStack)threadLocalClientRequestInfoStack.get();
        infoStack.disableCount--;
    }
    
    /**
     * Overridden from corba.ORB.
     * Called when the send_request or send_poll portable interception point 
     * is to be invoked for all appropriate client-side request interceptors.
     * Happens on any orb-mediated call.
     *
     * @exception RemarhsalException - Thrown when this request needs to
     *     be retried.
     */
    final protected void invokeClientPIStartingPoint() 
        throws RemarshalException
    {
	if( !hasClientInterceptors ) return;
        if( !isClientPIEnabledForThisThread() ) return;

        // Invoke the starting interception points and record exception
        // and reply status info in the info object:
        ClientRequestInfoImpl info = peekClientRequestInfoImplStack();
        interceptorInvoker.invokeClientInterceptorStartingPoint( info );
        
        // Check reply status.  If we will not have another chance later
        // to invoke the client ending points, do it now.
        short replyStatus = info.getReplyStatus();
        if( (replyStatus == SYSTEM_EXCEPTION.value) ||
            (replyStatus == LOCATION_FORWARD.value) )
        {
	    // Note: Transport retry cannot happen here since this happens
	    // before the request hits the wire.

            Exception exception = invokeClientPIEndingPoint( 
                convertPIReplyStatusToReplyMessage( replyStatus ),
                info.getException() );
            if( exception == null ) {
                // Do not throw anything.  Otherwise, it must be a
                // SystemException, UserException or RemarshalException.
            }
            if( exception instanceof SystemException ) {
                throw (SystemException)exception;
            }
            else if( exception instanceof RemarshalException ) {
                throw (RemarshalException)exception;
            }
            else if( (exception instanceof UserException) ||
		     (exception instanceof ApplicationException) ) 
	    {
                // It should not be possible for an interceptor to throw 
		// a UserException.  By asserting instead of throwing the
		// UserException, we need not declare anything but 
		// RemarshalException in the throws clause.
		throw new INTERNAL( 
		    "Assertion failed: Interceptor set exception to " +
		    "UserException or ApplicationException.", 
		    MinorCodes.EXCEPTION_INVALID, 
		    CompletionStatus.COMPLETED_NO );
            }
        }
	else if( replyStatus != ClientRequestInfoImpl.UNINITIALIZED ) {
	    throw new INTERNAL( 
		"Assertion failed: Reply status is initialized but not " +
		"SYSTEM_EXCEPTION, or LOCATION_FORWARD_*.", 
		MinorCodes.REPLY_STATUS_NOTINIT, 
		CompletionStatus.COMPLETED_NO );
	}
    }
    
    /**
     * Overridden from corba.ORB.
     * Called when the appropriate client ending interception point is
     * to be invoked for all apporpriate client-side request interceptors.
     *
     * @param replyStatus One of the constants in iiop.messages.ReplyMessage
     *     indicating which reply status to set.
     * @param exception The exception before ending interception points have
     *     been invoked, or null if no exception at the moment.
     * @return The exception to be thrown, after having gone through
     *     all ending points, or null if there is no exception to be
     *     thrown.  Note that this exception can be either the same or
     *     different from the exception set using setClientPIException.
     *     There are four possible return types: null (no exception), 
     *     SystemException, UserException, or RemarshalException.
     */
    final protected Exception invokeClientPIEndingPoint(
        int replyStatus, Exception exception )
    {
	if( !hasClientInterceptors ) return exception;
        if( !isClientPIEnabledForThisThread() ) return exception;

        // Translate ReplyMessage.replyStatus into PI replyStatus:
	// Note: this is also an assertion to make sure a valid replyStatus
	// is passed in (IndexOutOfBoundsException will be thrown otherwise)
        short piReplyStatus = REPLY_MESSAGE_TO_PI_REPLY_STATUS[replyStatus];
        
        // Invoke the ending interception points and record exception
        // and reply status info in the info object:
        ClientRequestInfoImpl info = peekClientRequestInfoImplStack();
        info.setReplyStatus( piReplyStatus );
        info.setException( exception );
        interceptorInvoker.invokeClientInterceptorEndingPoint( info );
	piReplyStatus = info.getReplyStatus();

        // Check reply status:
        if( (piReplyStatus == LOCATION_FORWARD.value) ||
            (piReplyStatus == TRANSPORT_RETRY.value) ) 
        {
            // If this is a forward or a retry, reset and reuse 
            // info object:
            info.reset();
            info.setRetryRequest( true );

            // ... and return a RemarshalException so the orb internals know
            exception = new RemarshalException();
        }
        else if( (piReplyStatus == SYSTEM_EXCEPTION.value) ||
                 (piReplyStatus == USER_EXCEPTION.value) ) 
	{
            exception = info.getException();
        }
        
        return exception;
    }
    
    /**
     * Overridden from corba.ORB.
     * <p>
     * Invoked when a request is about to be created.  Must be called before
     * any of the setClientPI* methods so that a new info object can be
     * prepared for information collection.
     * <p>
     * Happens on any orb-mediated call.  Note that this method may be
     * called multiple times if a request is retried.
     * <p>
     * This call is essentially required so we can maintain the same info
     * object across invocations.  This is not required by the current spec,
     * but we will eventually need this functionality for things like
     * invocation ids.
     * <p>
     * On a DII request, initiate is called twice (once in doInvocation so
     * we can set the RequestImpl object, and once in 
     * ClientDelegate.createRequest.  In this case, we must ignore the second
     * initiate call.
     */
    final protected void initiateClientPIRequest( boolean diiRequest ) {
	if( !hasClientInterceptors ) return;
        if( !isClientPIEnabledForThisThread() ) return;

	// Get the most recent info object from the thread local 
	// ClientRequestInfoImpl stack:
	RequestInfoStack infoStack = 
	    (RequestInfoStack)threadLocalClientRequestInfoStack.get();
	ClientRequestInfoImpl info = null;
	if( !infoStack.empty() ) info = 
	    (ClientRequestInfoImpl)infoStack.peek();

	if( !diiRequest && (info != null) && info.isDIIInitiate() ) {
	    // In RequestImpl.doInvocation we already called 
	    // initiateClientPIRequest( true ), so ignore this initiate.
	    info.setDIIInitiate( false );
	}
	else {
	    // If there is no info object or if we are not retrying a request,
	    // push a new ClientRequestInfoImpl on the stack:
	    if( (info == null) || !info.getRetryRequest() ) {
		info = new ClientRequestInfoImpl( this );
		infoStack.push( info );
		// Note: the entry count is automatically initialized to 0.
	    }
	    
	    // Reset the retry request flag so that recursive calls will
	    // push a new info object, and bump up entry count so we know
	    // when to pop this info object:
	    info.setRetryRequest( false );
	    info.incrementEntryCount();

	    // If this is a DII request, make sure we ignore the next initiate.
	    if( diiRequest ) {
		info.setDIIInitiate( true );
	    }
	}
    }
    
    /**
     * Overridden from corba.ORB.
     * Invoked when a request is about to be cleaned up.  Must be called
     * after ending points are called so that the info object on the stack
     * can be deinitialized and popped from the stack at the appropriate
     * time.
     */
    final protected void cleanupClientPIRequest() {
	if( !hasClientInterceptors ) return;
        if( !isClientPIEnabledForThisThread() ) return;

        ClientRequestInfoImpl info = peekClientRequestInfoImplStack();
        
        // If the replyStatus has not yet been set, this is an indication
        // that the ORB threw an exception before we had a chance to
        // invoke the client interceptor ending points.
        //
        // _REVISIT_ We cannot handle any exceptions or ForwardRequests
        // flagged by the ending points here because there is no way
        // to gracefully handle this in any of the calling code.  
        // This is a rare corner case, so we will ignore this for now.
        short replyStatus = info.getReplyStatus();
        if( replyStatus == info.UNINITIALIZED ) {
            invokeClientPIEndingPoint( 
                ReplyMessage.SYSTEM_EXCEPTION,
                new UNKNOWN( "Problem between _request and _invoke.",
			     MinorCodes.UNKNOWN_REQUEST_INVOKE, 
			     CompletionStatus.COMPLETED_MAYBE ) );
        }
        
        // Decrement entry count, and if it is zero, pop it from the stack.
        info.decrementEntryCount();
        if( info.getEntryCount() == 0 ) {
	    RequestInfoStack infoStack = 
		(RequestInfoStack)threadLocalClientRequestInfoStack.get();
            infoStack.pop();
        }
    }
    
    /**
     * Overridden from corba.ORB.
     * Notifies PI of the information for client-side interceptors.  
     * PI will use this information as a source of information for the 
     * ClientRequestInfo object.
     */
    final protected void setClientPIInfo( Connection connection,
				    ClientDelegate delegate, 
                                    IOR effectiveTarget,
                                    IIOPProfile profile, 
                                    int requestId,
                                    String opName,
                                    boolean isOneWay,
                                    ServiceContexts svc ) 
    {
	if( !hasClientInterceptors ) return;
        if( !isClientPIEnabledForThisThread() ) return;
        
        peekClientRequestInfoImplStack().setInfo(
            connection, delegate, effectiveTarget, profile,
	    requestId, opName, isOneWay, svc );
    }
    
    /**
     * Overridden from corba.ORB.
     * Notifies PI of additional information for client-side interceptors.
     * PI will use this information as a source of information for the
     * ClientRequestInfo object.
     */
    final protected void setClientPIInfo( ClientResponse response ) {
	if( !hasClientInterceptors ) return;
        if( !isClientPIEnabledForThisThread() ) return;
        
        peekClientRequestInfoImplStack().setInfo( response );
    }

    /**
     * Overridden from corba.ORB.
     * Notifies PI of additional information for client-side 
     * interceptors.  PI will use this information as a source of information 
     * for the ClientRequestInfo object.
     */
    final protected void setClientPIInfo( RequestImpl requestImpl ) {
	if( !hasClientInterceptors ) return;
        if( !isClientPIEnabledForThisThread() ) return;
        
        peekClientRequestInfoImplStack().setDIIRequest( requestImpl );
    }

    final protected void sendCancelRequestIfFinalFragmentNotSent()
    {
	// The stack only has something in it if interceptors are registered.
	if( !hasClientInterceptors ) return;
        if( !isClientPIEnabledForThisThread() ) return;

        ClientRequestInfoImpl info = peekClientRequestInfoImplStack();
	int requestID = info.request_id();
	IIOPConnection connection = (IIOPConnection) info.connection();
	IIOPOutputStream existingOutputStream = null;
	if (connection != null) {
	    // If it is a colocated request the connection will be null.
	    // We do not (and cannot) fragment on colocated requests.
	    existingOutputStream =
		connection.getIdToFragmentedOutputStreamEntry(requestID);
	    if (existingOutputStream != null) {
		try {
		    connection.sendCancelRequestWithLock(
	                existingOutputStream.getMessage().getGIOPVersion(),
			requestID);
		} catch (IOException e) {
		    // REVISIT: put in logging here.
		    // REVISIT: we could attempt to send a final incomplete
		    // fragment in this case.
		    throw new COMM_FAILURE(
                        e.getClass().getName(),
                        MinorCodes.IOEXCEPTION_DURING_CANCEL_REQUEST,
			CompletionStatus.COMPLETED_MAYBE);
		} finally {
		    connection
			.removeIdToFragmentedOutputStreamEntry(requestID);
		    // Since we canceled the request we remove the out
		    // call descriptor too, since the client thread will never
		    // be waiting for a reply.
		    connection.removeOutCallDescriptor(requestID);
		}
	    }
	}
    }
    
    /*
     *****************
     * Server PI hooks
     *****************/
    
    /**
     * Overridden from corba.ORB.
     * Called when the appropriate server starting interception point is
     * to be invoked for all appropriate server-side request interceptors.
     */
    final protected void invokeServerPIStartingPoint() 
        throws InternalRuntimeForwardRequest
    {
	if( !hasServerInterceptors ) return;

        ServerRequestInfoImpl info = peekServerRequestInfoImplStack();
        interceptorInvoker.invokeServerInterceptorStartingPoint( info );

	// Handle SystemException or ForwardRequest:
	serverPIHandleExceptions( info );
    }

    
    /**
     * Overridden from corba.ORB.
     * Called when the appropriate server intermediate interception point is
     * to be invoked for all appropriate server-side request interceptors.
     */
    final protected void invokeServerPIIntermediatePoint() 
        throws InternalRuntimeForwardRequest
    {
	if( !hasServerInterceptors ) return;

        ServerRequestInfoImpl info = peekServerRequestInfoImplStack();
        interceptorInvoker.invokeServerInterceptorIntermediatePoint( info );
        
        // Clear servant from info object so that the user has control over
        // its lifetime:
        info.releaseServant();

	// Handle SystemException or ForwardRequest:
	serverPIHandleExceptions( info );
    }
    
    /**
     * Overridden from corba.ORB.
     * Called when the appropriate server ending interception point is
     * to be invoked for all appropriate server-side request interceptors.
     */
    final protected void invokeServerPIEndingPoint( ReplyMessage replyMessage ) 
	throws InternalRuntimeForwardRequest
    {
	if( !hasServerInterceptors ) return;
        ServerRequestInfoImpl info = peekServerRequestInfoImplStack();

	// REVISIT: This needs to be done "early" for the following workaround.
	info.setReplyMessage( replyMessage );

	// REVISIT: This was done inside of invokeServerInterceptorEndingPoint
	// but needs to be here for now.  See comment in that method for why.
	info.setCurrentExecutionPoint( info.EXECUTION_POINT_ENDING );

        // It is possible we might have entered this method more than
        // once (e.g. if an ending point threw a SystemException, then
        // a new ServerResponseImpl is created).
        if( !info.getAlreadyExecuted() ) {
	    int replyStatus = replyMessage.getReplyStatus();

            // Translate ReplyMessage.replyStatus into PI replyStatus:
            // Note: this is also an assertion to make sure a valid 
            // replyStatus is passed in (IndexOutOfBoundsException will be 
            // thrown otherwise)
            short piReplyStatus = 
                REPLY_MESSAGE_TO_PI_REPLY_STATUS[replyStatus];

	    // Make forwarded IOR available to interceptors, if applicable:
	    if( ( piReplyStatus == LOCATION_FORWARD.value ) ||
		( piReplyStatus == TRANSPORT_RETRY.value ) ) 
	    {
		info.setForwardRequest( replyMessage.getIOR() );
	    }

	    // REVISIT: Do early above for now.
	    // Make reply message available to interceptors:
	    //info.setReplyMessage( replyMessage );

            // Remember exception so we can tell if an interceptor changed it.
            Exception prevException = info.getException();

	    // _REVISIT_ We do not have access to the User Exception at
	    // this point, so treat it as an UNKNOWN for now.
	    // Note that if this is a DSI call, we do have the user exception.
	    if( !info.isDynamic() && 
		(piReplyStatus == USER_EXCEPTION.value) ) 
	    {
		info.setException( 
		    new UNKNOWN( "Cannot access user exception",
			     MinorCodes.UNKNOWN_USER_EXCEPTION, 
			     CompletionStatus.COMPLETED_MAYBE ) );
	    }

            
            // Invoke the ending interception points:
            info.setReplyStatus( piReplyStatus );
            interceptorInvoker.invokeServerInterceptorEndingPoint( info );
            short newPIReplyStatus = info.getReplyStatus();
            Exception newException = info.getException();
            
            // Check reply status.  If an interceptor threw a SystemException
            // and it is different than the one that we came in with,
            // rethrow it so the proper response can be constructed:
            if( ( newPIReplyStatus == SYSTEM_EXCEPTION.value ) &&
                ( newException != prevException ) ) 
            {
                throw (SystemException)newException;
            }

	    // If we are to forward the location:
	    if( newPIReplyStatus == LOCATION_FORWARD.value ) {
	        if( piReplyStatus != LOCATION_FORWARD.value ) {
	            // Treat a ForwardRequest as an 
		    // InternalRuntimeForwardRequest.
                    ForwardRequest newForwardRequest = 
			info.getForwardRequestException();
		    throw new InternalRuntimeForwardRequest( 
			newForwardRequest.forward );
		}
		else if( info.isForwardRequestRaisedInEnding() ) {
	            // Treat a ForwardRequest by changing the IOR.
                    replyMessage.setIOR( info.getForwardRequestIOR() );
		}
	    }
        }
    }
    
    /**
     * Overridden from corba.ORB.
     * Notifies PI of additional information required for ServerRequestInfo.
     */
    final protected void setServerPIInfo( Exception exception ) {
	if( !hasServerInterceptors ) return;

        ServerRequestInfoImpl info = peekServerRequestInfoImplStack();
        info.setException( exception );
    }

    /**
     * Overridden from corba.ORB.
     * Notifies PI of additional information required for ServerRequestInfo.
     */
    final protected void setServerPIInfo( NVList arguments )
    {
	if( !hasServerInterceptors ) return;

        ServerRequestInfoImpl info = peekServerRequestInfoImplStack();
        info.setDSIArguments( arguments );
    }

    /**
     * Overridden from corba.ORB.
     * Notifies PI of additional information required for ServerRequestInfo.
     */
    final protected void setServerPIExceptionInfo( Any exception )
    {
	if( !hasServerInterceptors ) return;

        ServerRequestInfoImpl info = peekServerRequestInfoImplStack();
        info.setDSIException( exception );
    }

    /**
     * Overridden from corba.ORB.
     * Notifies PI of additional information required for ServerRequestInfo.
     */
    final protected void setServerPIInfo( Any result )
    {
	if( !hasServerInterceptors ) return;

        ServerRequestInfoImpl info = peekServerRequestInfoImplStack();
        info.setDSIResult( result );
    }

    /**
     * Overridden from corba.ORB.
     * Notifies PI to start a new server request and set initial
     * information for server-side interceptors.
     * PI will use this information as a source of information for the
     * ServerRequestInfo object.
     */
    final protected void initializeServerPIInfo( ServerRequest request,
	java.lang.Object poaimpl, byte[] objectId, byte[] adapterId ) 
    {
	if( !hasServerInterceptors ) return;

        RequestInfoStack infoStack = 
            (RequestInfoStack)threadLocalServerRequestInfoStack.get();
        ServerRequestInfoImpl info = new ServerRequestInfoImpl( this );
	infoStack.push( info );

	// Notify request object that once response is constructed, make
	// sure we execute ending points.
        request.setExecutePIInResponseConstructor( true );

        info.setInfo( request, (POAImpl)poaimpl, objectId, adapterId );
    }
    
    /**
     * Notifies PI of additional information reqired for ServerRequestInfo.
     */
    final protected void setServerPIInfo( java.lang.Object servant, 
				          String targetMostDerivedInterface ) 
    {
	if( !hasServerInterceptors ) return;

        ServerRequestInfoImpl info = peekServerRequestInfoImplStack();
        info.setInfo( servant, targetMostDerivedInterface );
    }

    /**
     * Overridden from corba.ORB.
     * See description in corba.ORB.
     */                                                     
    final protected void cleanupServerPIRequest() {
	if( !hasServerInterceptors ) return;

        RequestInfoStack infoStack = 
            (RequestInfoStack)threadLocalServerRequestInfoStack.get();
	infoStack.pop();
    }
    
    /* 
     **********************************************************************
     *  The following methods are private utility methods for the PIORB's
     *  use.
     ************************************************************************/

    /**
     * Handles exceptions for the starting and intermediate points for
     * server request interceptors.  This is common code that has been
     * factored out into this utility method.
     * <p>
     * This method will NOT work for ending points.
     */
    private void serverPIHandleExceptions( ServerRequestInfoImpl info ) 
      throws InternalRuntimeForwardRequest
    {
        int endingPointCall = info.getEndingPointCall();
        if(endingPointCall == ServerRequestInfoImpl.CALL_SEND_EXCEPTION) {
            // If a system exception was thrown, throw it to caller:
            throw (SystemException)info.getException();
        }
        else if( (endingPointCall == ServerRequestInfoImpl.CALL_SEND_OTHER) &&
                 (info.getForwardRequestException() != null) )
        {
            // If an interceptor throws a forward request, convert it
            // into an InternalRuntimeForwardRequest for easier handling:
            ForwardRequest forwardRequest = info.getForwardRequestException();
	    throw new InternalRuntimeForwardRequest( 
		forwardRequest.forward );
        }
    }

    /**
     * Utility method to convert a PI reply status short to a ReplyMessage
     * constant.  This is a reverse lookup on the table defined in
     * REPLY_MESSAGE_TO_PI_REPLY_STATUS.  The reverse lookup need not be
     * performed as quickly since it is only executed in exception
     * conditions.
     */
    private int convertPIReplyStatusToReplyMessage( short replyStatus ) {
        int result = 0;
        for( int i = 0; i < REPLY_MESSAGE_TO_PI_REPLY_STATUS.length; i++ ) {
            if( REPLY_MESSAGE_TO_PI_REPLY_STATUS[i] == replyStatus ) {
                result = i;
		break;
            }
        }
        return result;
    }
    
    /** 
     * Convenience method to get the ClientRequestInfoImpl object off the 
     * top of the ThreadLocal stack.  Throws an INTERNAL exception if 
     * the Info stack is empty.
     */
    private ClientRequestInfoImpl peekClientRequestInfoImplStack() {
        RequestInfoStack infoStack = 
            (RequestInfoStack)threadLocalClientRequestInfoStack.get();
        ClientRequestInfoImpl info = null;
        if( !infoStack.empty() ) {
	    info = (ClientRequestInfoImpl)infoStack.peek();
	}
	else {
	    throw new INTERNAL(
		"Assertion failed: Client Request Info Stack is null.",
		MinorCodes.CLIENT_INFO_STACK_NULL,
		CompletionStatus.COMPLETED_NO );
	}
        
        return info;
    }

    /** 
     * Convenience method to get the ServerRequestInfoImpl object off the 
     * top of the ThreadLocal stack.  Returns null if there are none.
     */
    private ServerRequestInfoImpl peekServerRequestInfoImplStack() {
        RequestInfoStack infoStack = 
            (RequestInfoStack)threadLocalServerRequestInfoStack.get();
        ServerRequestInfoImpl info = null;

        if( !infoStack.empty() ) {
            info = (ServerRequestInfoImpl)infoStack.peek();
        }
        else {
            throw new INTERNAL(
                "Assertion failed: Server Request Info Stack is null.",
                MinorCodes.SERVER_INFO_STACK_NULL,
                CompletionStatus.COMPLETED_NO );
        }

        return info;
    }
    
    /**
     * Convenience method to determine whether Client PI is enabled
     * for requests on this thread. 
     */
    private boolean isClientPIEnabledForThisThread() {
        RequestInfoStack infoStack = 
            (RequestInfoStack)threadLocalClientRequestInfoStack.get();
        return (infoStack.disableCount == 0);
    }
    
    /**
     * Call pre_init on all ORB initializers
     */
    private void preInitORBInitializers( ORBInitInfoImpl info ) {

	// Inform ORBInitInfo we are in pre_init stage
        info.setStage( ORBInitInfoImpl.STAGE_PRE_INIT );

	// Step through each initializer instantiation and call its 
	// pre_init.  Ignore any exceptions.
	for( int i = 0; i < orbInitializers.length; i++ ) {
	    ORBInitializer init = orbInitializers[i];
	    if( init != null ) {
		try {
		    init.pre_init( info );
		}
		catch( Exception e ) {
		    // As per orbos/99-12-02, section 9.3.1.2, "If there are 
		    // any exceptions, the ORB shall ignore them and proceed."
		}
	    }
	}
    }

    /**
     * Call post_init on all ORB initializers
     */
    private void postInitORBInitializers( ORBInitInfoImpl info ) {

	// Inform ORBInitInfo we are in post_init stage
        info.setStage( ORBInitInfoImpl.STAGE_POST_INIT );

	// Step through each initializer instantiation and call its post_init.
	// Ignore any exceptions.
	for( int i = 0; i < orbInitializers.length; i++ ) {
	    ORBInitializer init = orbInitializers[i];
	    if( init != null ) {
		try {
		    init.post_init( info );
		}
		catch( Exception e ) {
		    // As per orbos/99-12-02, section 9.3.1.2, "If there are 
		    // any exceptions, the ORB shall ignore them and proceed."
		}
	    }
	}
    }

    /** 
     * Creates the ORBInitInfo object to be passed to ORB intializers'
     * pre_init and post_init methods
     */
    private ORBInitInfoImpl createORBInitInfo() {
	ORBInitInfoImpl result = null;
	
	// arguments comes from set_parameters.  May be null.

	// _REVISIT_ The spec does not specify which ID this is to be.
	// We currently get this from the corba.ORB, which reads it from
	// the ORB_ID_PROPERTY property.
	String orbId = this.orbId;

	// codecFactory comes from constructor of PIORB.

	result = new ORBInitInfoImpl( this, arguments, orbId, codecFactory );

	return result;
    }

    /**
     * Called by ORBInitInfo when an interceptor needs to be registered.
     * The type is one of:
     * <ul>
     *   <li>INTERCEPTOR_TYPE_CLIENT - ClientRequestInterceptor
     *   <li>INTERCEPTOR_TYPE_SERVER - ServerRequestInterceptor
     *   <li>INTERCEPTOR_TYPE_IOR - IORInterceptor
     * </ul>
     *
     * @exception DuplicateName Thrown if an interceptor of the given
     *     name already exists for the given type.
     */
    void register_interceptor( Interceptor interceptor, int type ) 
	throws DuplicateName
    {
	// We will assume interceptor is not null, since it is called
	// internally.
	if( (type >= InterceptorList.NUM_INTERCEPTOR_TYPES) || (type < 0) ) {
	    throw new BAD_PARAM( "Interceptor type out of range: " + type );
	}

        String interceptorName = interceptor.name();

        if( interceptorName == null ) {
            throw new BAD_PARAM(
                "Interceptor's name is null.  " +
                "Use empty string for anonymous interceptors." );
        }

	// Register with interceptor list:
	interceptorList.register_interceptor( interceptor, type );
    }

    /**
     * return the PICurrent instance created during ORB initialization.
     */
    PICurrent getPICurrent( ) {
        return current;
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

   
    /** This is the implementation of standard API defined in org.omg.CORBA.ORB
     *  class. This method finds the Policy Factory for the given Policy Type 
     *  and instantiates the Policy object from the Factory. It will throw 
     *  PolicyError exception, If the PolicyFactory for the given type is
     *  not registered.
     *  _REVISIT_, Once Policy Framework work is completed, Reorganize
     *  this method to com.sun.corba.se.internal.core.ORB. 
     */
    public org.omg.CORBA.Policy create_policy(int type, org.omg.CORBA.Any val)
        throws org.omg.CORBA.PolicyError
    {
        checkShutdownState();

        if( val == null ) {
            nullParam( );
        }
        if( policyFactoryTable == null ) {
            throw new org.omg.CORBA.PolicyError(
                "There is no PolicyFactory Registered for type " + type, 
		BAD_POLICY.value );
        }
        PolicyFactory factory = (PolicyFactory)policyFactoryTable.get(
            new Integer(type) );
        if( factory == null ) {
            throw new org.omg.CORBA.PolicyError(
                " Could Not Find PolicyFactory for the Type " + type, 
                BAD_POLICY.value);
        }
        org.omg.CORBA.Policy policy = factory.create_policy( type, val );
        return policy;
    }


    /** This method registers the Policy Factory in the policyFactoryTable,
     *  which is a HashMap. This method is made package private, because
     *  it is used internally by the  Interceptors.
     */
    void registerPolicyFactory( int type, PolicyFactory factory ) {
        if( policyFactoryTable == null ) {
            policyFactoryTable = new HashMap();
        }
        Integer key = new Integer( type );
        java.lang.Object val = policyFactoryTable.get( key );
        if( val == null ) {
            policyFactoryTable.put( key, factory );
        }
        else {
	    throw new BAD_INV_ORDER( 
                "Failure in registerPolicyFactory, Trying to register a " +
                "PolicyFactory with the Type " + type + " which is already " +
                " registered", MinorCodes.POLICY_FACTORY_REG_FAILED, 
		CompletionStatus.COMPLETED_NO );
        }
   }

    
    synchronized int allocateServerRequestId ()
    {
	return serverRequestIdCounter++;
    }

}
