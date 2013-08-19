/*
 * @(#)GenericPOAClientSC.java	1.71 03/01/23
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

/** The general-purpose client-side subcontract (delegate) for the POA ORB. 
 *  It handles client ORB processing for transient, persistent,
 *  non-transactional and transactional server objects.
 *  Its main functionality (in addition to the JavaIDL client subcontract) is:
 *      - on each invocation, call the JTS Sender methods if the server object
 *        is transactional.
 *      - for persistent objects, first do a locate then invoke
 *  The quality of service of the server object is known from the subcontract-id
 *  variable ("scid"). 
 */

public class GenericPOAClientSC extends ClientDelegate implements ClientSC
{
    protected GenericPOAServerSC serversc ;

    public void setOrb( com.sun.corba.se.internal.core.ORB orb )
    {
	super.setOrb( orb ) ;
	serversc = (GenericPOAServerSC)orb.getSubcontractRegistry().getServerSubcontract(this.scid);
    }

    public boolean useLocalInvocation( org.omg.CORBA.Object self)
    {
	return orb.allowLocalOptimization && is_local( self ) ;
    }

    /** Return the objectid of the target POA objref associated with this
     *	Delegate. 
     */
    public byte[] getObjectId()
    {
	return getIOR().getProfile().getObjectId().getId() ;
    }

    /** Return the POAid of the target POA objref associated with this
     *	Delegate. 
     */
    public POAId getPOAId()
    {
	ObjectKeyTemplate oktemp = getIOR().getProfile().getTemplate().
	    getObjectKeyTemplate() ;
	if (oktemp instanceof POAObjectKeyTemplate) {
	    POAObjectKeyTemplate poktemp = (POAObjectKeyTemplate)oktemp ;
	    POAId poaid = poktemp.getPOAId() ;
	    if (orb.subcontractDebugFlag)
		dprint( "getPOAId() returns " + poaid ) ;
	    return poaid ;
	} else
	    throw new INTERNAL() ;
    }

    //Called by rmi-iiop's stream-based CORBA stubs.
    public InputStream invoke(org.omg.CORBA.Object self, OutputStream out)
        throws ApplicationException, RemarshalException 
    {
	if (orb.subcontractDebugFlag)
	    dprint( "invoke(org.omg.CORBA.Object,OutputStream) called" ) ;
        // We need to cast the orb to a POAORB so that we can invoke the
        // PI interception points:
        POAORB orb = (POAORB)this.orb;
        
        ClientRequest request = null;
        ClientResponse response = null;
        Exception exception = null;
        try {
            request = (ClientRequest) out;
            response = this.invoke(request);
            
	    if (orb.subcontractDebugFlag)
		dprint( "invoke: response returned" ) ;

            // for oneway, since there is no response, there is no data to
            // be unmarhalled therefore skip to the releaseReply hooks. The
            // releaseReply hooks should take care of checking for null response

            if (request.isOneWay()) {
                // Invoke Portable Interceptors with receive_other
                exception = orb.invokeClientPIEndingPoint(
                    ReplyMessage.NO_EXCEPTION, exception );
                continueOrThrowSystemOrRemarshal( exception );
                return null;
            }

            // COMMENT(Ram J) 10/01/2000 There is no need to set the response
            // stream's connection to that of the request stream. The response
            // stream will have a connection associated if the response
            // had been built out of the server response; else if the
            // response was built out of SystemException arising out of 
            // response handling, then there will be no connection
            // or service context available with the response stream. 
            // In such a case, the consumeServiceContexts has to return 
            // gracefully.
            /*
              Connection c = request.getConnection();
              if (response != null && c != null )
	        response.setConnection(c);
            */

            if (response.isSystemException()) {
		if (orb.subcontractDebugFlag)
		    dprint( "invoke: reponse is system exception" ) ;

                SystemException se = response.getSystemException();

		if (orb.subcontractDebugFlag)
		    dprint( "invoke: system exception " + se ) ;

                boolean doRemarshal = false;

                // FIX(Ram J) (04/28/2000) added  locatedIOR = ior
                // and throw RemarshallException to force retrying
                // from root ior, if system exception is COMM_FAILURE.
                // WARNING: There is a risk of infinite loopback if the requests on
                // location forwarded ior result in system exception (COMM_FAILURE)
                if (se instanceof org.omg.CORBA.COMM_FAILURE
                    && se.completed == CompletionStatus.COMPLETED_NO) {
                    if (locatedIOR != ior) {
                        locatedIOR = ior; // retry from root ior
                        
                        doRemarshal = true;
                        
			if (orb.subcontractDebugFlag)
			    dprint( "invoke: COMM_FAILURE retry" ) ;
                    }
                }

		/* This used to live in a loop in IIOPOutputStream.invoke.
		   That loop had a retry count to bound the number of tries.
		   However that loop did not work once fragmentation was
		   put in.  So it was moved here.  However, there is no
		   place to hang the count since this throws out to the Stub.
		   We need invocation level (i.e., thread local) date like
		   in PI to put this count. That is scheduled for Taggart.
		   For now, just turn this off.
                if (se.minor == MinorCodes.CONN_CLOSE_REBIND &&
                    (se instanceof org.omg.CORBA.COMM_FAILURE)) {
                    
                    doRemarshal = true;
                }
		*/

                if (doRemarshal) {

                    // Invoke Portable Interceptors with receive_exception:
                    exception = orb.invokeClientPIEndingPoint(
                                                              ReplyMessage.SYSTEM_EXCEPTION, se );
                    
                    // If PI did not change the exception, throw a 
                    // Remarshal.  Otherwise, throw the exception PI
                    // wants thrown.
                    if( se == exception ) {
                        // The code before PI was added has exception null
                        // at this point, so we maintain those semantics.
                        exception = null;
                        if (orb.subcontractDebugFlag)
                            dprint( "invoke: throwing ReMarshal exception"  ) ;
                        throw new RemarshalException();
                    }
                    else {
                        continueOrThrowSystemOrRemarshal( exception );
                        throw new INTERNAL( 
                                           "Assertion failed. " +
                                           "exception should not be null." );
                    }
                }

		if (orb.subcontractDebugFlag)
		    dprint( "invoke: processing response service contexts"  ) ;

                ServiceContexts contexts = response.getServiceContexts();
                if (contexts != null) {
                    try {
                        UEInfoServiceContext usc =
                            (UEInfoServiceContext)(contexts.get(
                            UEInfoServiceContext.SERVICE_CONTEXT_ID )) ;
                        Throwable unknown = usc.getUE() ;
                        UnknownException ue = new UnknownException(unknown);

                        // Invoke Portable Interceptors with receive_exception:
                        exception = orb.invokeClientPIEndingPoint(
                            ReplyMessage.SYSTEM_EXCEPTION, ue );

                        continueOrThrowSystemOrRemarshal( exception );

			throw new INTERNAL( 
			    "Assertion failed.  exception should not be null.");
                    } catch (NoSuchServiceContext exc) {
                        // NO-OP: handled by system exception below
                    }
                }

                // Invoke Portable Interceptors with receive_exception:
                exception = orb.invokeClientPIEndingPoint(
                    ReplyMessage.SYSTEM_EXCEPTION, se );
                
                continueOrThrowSystemOrRemarshal( exception );
                
                // Note: We should never need to execute this line, but
                // we should assert in case exception was set to null somehow.
                throw new INTERNAL( 
		    "Assertion failed: exception should not be null." );
            } else if (response.isUserException()) {
		if (orb.subcontractDebugFlag)
		    dprint( "invoke: response is user exception"  ) ;

                ApplicationException appException =
                    new ApplicationException(response.peekUserExceptionId(),
                                 (org.omg.CORBA.portable.InputStream)response);
                
                // Invoke Portable Interceptors with receive_exception
                // (user exception):
                exception = orb.invokeClientPIEndingPoint(
                    ReplyMessage.USER_EXCEPTION, appException );
                
                if( exception != appException ) {
                    continueOrThrowSystemOrRemarshal( exception );
                }
                
                throw (ApplicationException)exception;
            } else if (response.isLocationForward()) {
		if (orb.subcontractDebugFlag)
		    dprint( "invoke: response is location forward"  ) ;

                // In this case the exception is not handed to JTS sender.
                // Need to check spec on this!
                // FIX(Ram J) (04/28/2000) added setting locatedIOR. Also the
                // above assertion "exception is not handled to JTS sender.."
                // is not true anymore. The JTS sender hook is called for
                // every location forwarded ior if the ior is transactional.
                locatedIOR = response.getForwardedIOR();
                
                // Invoke Portable Interceptors with receive_other:
	        Exception newException = orb.invokeClientPIEndingPoint(
		    ReplyMessage.LOCATION_FORWARD, null );
                
		// Even if no interceptors are registered, 
		// invokeClientPIEndingPoint will return RemarshalException
		// because LOCATION_FORWARD was passed in.  We want to
		// preserve the state of the "exception" variable to be
		// null in this case so that the finally block works the
		// way it did before we introduced PI.  In the case of a 
		// SystemException, however, we do want the "exception"
		// variable set.
		if( !(newException instanceof RemarshalException) ) {
		    exception = newException;
		}

                // If PI did not raise exception, throw Remarshal, else
                // throw the exception PI wants thrown.
	        continueOrThrowSystemOrRemarshal( exception );

                throw new RemarshalException();
            } else if (response.isDifferentAddrDispositionRequested()) {
		if (orb.subcontractDebugFlag)
		    dprint( "invoke: response is NEEDS_ADDRESSING_MODE"  ) ;

                // set the desired target addressing disposition.
                addressingDisposition = response.getAddrDisposition();
                
                // Invoke Portable Interceptors with receive_other:
	        Exception newException = orb.invokeClientPIEndingPoint(
		    ReplyMessage.NEEDS_ADDRESSING_MODE, null );
                
		// Even if no interceptors are registered, 
		// invokeClientPIEndingPoint will return RemarshalException
		// because NEEDS_ADDRESSING_MODE was passed in.  We want to
		// preserve the state of the "exception" variable to be
		// null in this case so that the finally block works the
		// way it did before we introduced PI.  In the case of a 
		// SystemException, however, we do want the "exception"
		// variable set.
		if( !(newException instanceof RemarshalException) ) {
		    exception = newException;
		}

                // If PI did not raise exception, throw Remarshal, else
                // throw the exception PI wants thrown.
	        continueOrThrowSystemOrRemarshal( exception );

                throw new RemarshalException();                                
            } else /* normal response */ {
		if (orb.subcontractDebugFlag)
		    dprint( "invoke: response is normal"  ) ;
                // Invoke Portable Interceptors with receive_reply:
                exception = orb.invokeClientPIEndingPoint(
                    ReplyMessage.NO_EXCEPTION, null );
                
                // Remember: not thrown if exception is null.
                continueOrThrowSystemOrRemarshal( exception );
                
                return (InputStream) response;
            }
        } finally {
	    // _REVISIT_ PI Note: Any exceptions in the following try block 
	    // happen after PI endpoint has already run so they are not 
	    // reported to interceptors.
            try {
                //Call JTS sender's received_reply
                this.releaseReply( response, request.getOperationName(),
                                   exception);
            } catch ( WrongTransaction ex ) {
                // XXX return this for deferred sends
                throw new NO_IMPLEMENT(
                     com.sun.corba.se.internal.orbutil.MinorCodes.SEND_DEFERRED_NOTIMPLEMENTED,
                                       CompletionStatus.COMPLETED_MAYBE);
            } catch ( SystemException ex ) {
                throw ex;
            }
        }
    }
    
    // Filters the given exception into a SystemException or a 
    // RemarshalException and throws it.  Assumes the given exception is
    // of one of these two types.  This is a utility method for
    // the above invoke code which must do this numerous times.
    // If the exception is null, no exception is thrown.
    // 
    // Note that this code is duplicated in ClientDelegate.java
    private void continueOrThrowSystemOrRemarshal( Exception exception ) 
        throws SystemException, RemarshalException
    {
        if( exception == null ) {
            // do nothing.
        }
        else if( exception instanceof RemarshalException ) {
            throw (RemarshalException)exception;
        }
        else {
            throw (SystemException)exception;
        }
    }

    public ServiceContexts getServiceContexts( 
	Connection c, int requestId, String opName, boolean isOneWay, GIOPVersion gv )
    {
	ServiceContexts scs = super.getServiceContexts( 
	    c, requestId, opName, isOneWay, gv ) ;

	ServiceContext txsc = null ;
	if (locatedIOR.isTransactional() && !isSpecialMethod(opName) && 
	    !locatedIOR.isLocal())
	    txsc = ((POAORB)(orb)).getTxServiceContext( requestId ) ;

	if (txsc != null)
	    try {
		scs.put( txsc ) ;
	    } catch (DuplicateServiceContext dsc) {
		// This should never happen
		throw new INTERNAL() ;
	    }

        // call interceptor hooks if any are defined
        ((POAORB)orb).sendingRequestServiceContexts( scs ) ;

	return scs ;
    }

    // Invoked by DII layer (from RequestImpl.doInvocation())
    // after unmarshaling reply and exceptions.
    public void releaseReply(ClientResponse resp, String method,
			     Exception exception)
	throws WrongTransaction, SystemException
    {
	receivedReply(resp, method, exception);
    }

    private final boolean isTransient(int subcontractId)
    {
	// 2nd bit in subcontract id is 0 for transient case.
	return ((subcontractId & 2)==0);
    }

    protected IOR locate()
    {
	if ( isTransient(scid) ) // don't locate for transient objects
	    return ior;
	else { 
            ClientGIOP giop = orb.getClientGIOP();
	    return giop.locate(ior);
	}
    }

    public ServantObject servant_preinvoke(org.omg.CORBA.Object self,
                                           String operation,
                                           Class expectedType) 
    {
        ServantObject servantObject = serversc.preinvoke(ior,
            operation, expectedType); 
        // This is to make sure that local invocation does not result
        // in an infinite loop, in case servant_preinvoke cannot find
        // the servant Object.
        // To understand how this works, see the comments in 
        // ClientDelegate.servant_preinvoke
        if( servantObject == null ) {
            isNextIsLocalValid.set( Boolean.FALSE );
        }
	return servantObject;
    }

    public void servant_postinvoke(org.omg.CORBA.Object self,
                                   ServantObject servantobj) 
    {
	serversc.postinvoke(ior, servantobj);
    }



    /** Deliver the reply transaction context and any exception to JTS.
     */
    protected void receivedReply(ClientResponse resp, String method, 
			         Exception exception) 
	throws WrongTransaction, SystemException
    {

        if (resp != null) { // do processing for two way calls, since
                          // response is not null in that case
                          // The Interceptor writer should take care
                          // of invoking hooks in oneway case

	    // consume any others that may be defined
            POAORB myorb = (POAORB) orb;
            ServiceContexts svcCtxList = resp.getServiceContexts();

	    // If handleTxServiceContext throws an exception,
	    // old interceptor hooks are not called.  This is
	    // the way this used to work, but is probably not
	    // optimal.
	    if (locatedIOR.isTransactional() && !isSpecialMethod(method) && 
		!locatedIOR.isLocal())
		((POAORB)(orb)).handleTxServiceContext( svcCtxList,
		    exception, resp.getRequestId() ) ;

            // call any hooks that are defined
            try {
                myorb.receivedReplyServiceContexts(svcCtxList);
            } catch (Throwable ex) {
                ; //Do not let hook errors escape.
            }
        }
    }

    

    protected boolean isSpecialMethod(String method)
    {
	if (method.startsWith("_") && 
	    !method.startsWith("_get_") &&
	    !method.startsWith("_set_")) 
	    return true;
	else
	    return false;
    }

}
