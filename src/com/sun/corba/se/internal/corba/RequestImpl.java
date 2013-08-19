/*
 * @(#)RequestImpl.java	1.62 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
/*
 * Licensed Materials - Property of IBM
 * RMI-IIOP v1.0
 * Copyright IBM Corp. 1998 1999  All Rights Reserved
 *
 * US Government Users Restricted Rights - Use, duplication or
 * disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 */

package com.sun.corba.se.internal.corba;


import org.omg.CORBA.Any;
import org.omg.CORBA.ARG_IN;
import org.omg.CORBA.ARG_OUT;
import org.omg.CORBA.ARG_INOUT;
import org.omg.CORBA.Context;
import org.omg.CORBA.ContextList;
import org.omg.CORBA.Environment;
import org.omg.CORBA.ExceptionList;
import org.omg.CORBA.NVList;
import org.omg.CORBA.NamedValue;
import org.omg.CORBA.Request;
import org.omg.CORBA.SystemException;
import org.omg.CORBA.TCKind;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.TypeCodePackage.BadKind;
import org.omg.CORBA.UnknownUserException;
import org.omg.CORBA.Bounds;
import org.omg.CORBA.UNKNOWN;
import org.omg.CORBA.INTERNAL;
import org.omg.CORBA.NO_IMPLEMENT;
import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.WrongTransaction;

import org.omg.CORBA.portable.*;

import com.sun.corba.se.internal.corba.ClientDelegate;
import com.sun.corba.se.internal.core.*;
import com.sun.corba.se.internal.iiop.messages.ReplyMessage;
import com.sun.corba.se.internal.orbutil.MinorCodes;



public class RequestImpl extends Request {

    ///////////////////////////////////////////////////////////////////////////
    // data members

    protected ObjectImpl		 _target	= null;
    protected String       		 _opName	= null;
    protected NVList 		 _arguments 	= null;
    protected ExceptionList		 _exceptions	= null;
    private NamedValue		 _result  	= null;
    protected Environment		 _env		= null;
    private Context		 _ctx		= null;
    private ContextList		 _ctxList	= null;
    protected ORB			 _orb		= null;

    // invocation-specific stuff

    protected boolean 		 _isOneWay	= false;
    private int[]			 _paramCodes	= null;
    private long[]		 _paramLongs	= null;
    private java.lang.Object[] 	 _paramObjects	= null;

    // support for deferred invocations. 
    // protected instead of private since it needs to be set by the
    // thread object doing the asynchronous invocation.
    protected boolean 		 gotResponse 	= false;

    ///////////////////////////////////////////////////////////////////////////
    // constructor

    protected RequestImpl (ORB orb,
			   org.omg.CORBA.Object targetObject,
			   Context ctx,
			   String operationName,
			   NVList argumentList,
			   NamedValue resultContainer,
			   ExceptionList exceptionList,
			   ContextList ctxList)
    {

        // initialize the orb
        _orb 	= orb;

        // initialize target, context and operation name
        _target     = (ObjectImpl) targetObject;
        _ctx	= ctx;
        _opName	= operationName;

        // initialize argument list if not passed in
        if (argumentList == null)
            _arguments = new NVListImpl(_orb);
        else
            _arguments = argumentList;

        // set result container. 
        _result = resultContainer;

        // initialize exception list if not passed in
        if (exceptionList == null)
            _exceptions = new ExceptionListImpl();
        else
            _exceptions = exceptionList;

        // initialize context list if not passed in
        if (ctxList == null)
            _ctxList = new ContextListImpl(_orb);
        else
            _ctxList = ctxList;

        // initialize environment 
        _env	= new EnvironmentImpl();

    }

    public org.omg.CORBA.Object target()
    {
        return _target;
    }

    public String operation() 
    {
        return _opName;
    }

    public NVList arguments() 
    {
        return _arguments;
    }
    
    public NamedValue result() 
    {
        return _result;
    }
    
    public Environment env() 
    {
        return _env;
    }
    
    public ExceptionList exceptions() 
    {
        return _exceptions;
    }
    
    public ContextList contexts() 
    {
        return _ctxList;
    }
    
    public synchronized Context ctx() 
    {
        if (_ctx == null)
            _ctx = new ContextImpl(_orb);
        return _ctx;
    }
    
    public synchronized void ctx(Context newCtx) 
    {
        _ctx = newCtx;
    }

    public synchronized Any add_in_arg()
    {
        return _arguments.add(org.omg.CORBA.ARG_IN.value).value();
    }

    public synchronized Any add_named_in_arg(String name)
    {
        return _arguments.add_item(name, org.omg.CORBA.ARG_IN.value).value();
    }

    public synchronized Any add_inout_arg()
    {
        return _arguments.add(org.omg.CORBA.ARG_INOUT.value).value();
    }

    public synchronized Any add_named_inout_arg(String name)
    {
        return _arguments.add_item(name, org.omg.CORBA.ARG_INOUT.value).value();
    }

    public synchronized Any add_out_arg()
    {
        return _arguments.add(org.omg.CORBA.ARG_OUT.value).value();
    }

    public synchronized Any add_named_out_arg(String name)
    {
        return _arguments.add_item(name, org.omg.CORBA.ARG_OUT.value).value();
    }

    public synchronized void set_return_type(TypeCode tc)
    {
        if (_result == null)
            _result = new NamedValueImpl(_orb);
        _result.value().type(tc);
    }

    public synchronized Any return_value()
    {
        if (_result == null)
            _result = new NamedValueImpl(_orb);
        return _result.value();
    }

    public synchronized void add_exception(TypeCode exceptionType)
    {
        _exceptions.add(exceptionType);
    }
    
    public synchronized void invoke()
    {
        doInvocation();
    }

    public synchronized void send_oneway()
    {
        _isOneWay = true;
        doInvocation();
    }
    
    public synchronized void send_deferred()
    {
        AsynchInvoke invokeObject = new AsynchInvoke((com.sun.corba.se.internal.corba.ORB) _orb, this, false);
        new Thread(invokeObject).start();
    }
    
    public synchronized boolean poll_response()
    {
        // this method has to be synchronized even though it seems
        // "readonly" since the thread object doing the asynchronous
        // invocation can potentially update this variable in parallel.
        // updates are currently simply synchronized againt the request
        // object. 
        return gotResponse;
    }
    
    public synchronized void get_response()
        throws org.omg.CORBA.WrongTransaction
    {
        while (gotResponse == false) {
            // release the lock. wait to be notified by the thread that is
            // doing the asynchronous invocation.
            try {
	        wait();
            } 
	    catch (InterruptedException e) {}
        }
    }
    
    ///////////////////////////////////////////////////////////////////////////
    // private helper methods

    /*
     * The doInvocation operation is where the real mechanics of
     * performing the request invocation is done.
     */

    protected void doInvocation() {
        // Get delegate

        // CHANGE(RAM J) (05/01/2000) changed ClientSubContract to ClientDelegate
        //ClientSubcontract delegate = (ClientSubcontract)_target._get_delegate();
	// Initiate Client Portable Interceptors.  Inform the PIORB that
	// this is a DII request so that it knows to ignore the second
	// inevitable call to initiateClientPIRequest in createRequest.
	// Also, save the RequestImpl object for later use.
	_orb.initiateClientPIRequest( true );
	_orb.setClientPIInfo( this );

        ClientDelegate delegate = (ClientDelegate)_target._get_delegate();
        ClientRequest req;
        Exception exception=null;
        ClientResponse resp=null;

	// Note: This try/catch was added for PI.  It is analogous to the
	// try / catch / finally found below for the line 
	// "resp = delegate.invoke(req);".  The finally is moved into the
	// catch body of SystemException since it should only be executed
	// if the request creation failed.
	try {
	    req = delegate.createRequest(_opName, _isOneWay);
	}
	catch( SystemException ex ) {
	    _env.exception(ex);
	    exception = ex;

	    // Note, this try block is copied directly from the one that
	    // appears later in this method.  Please synchronize changes.
	    // _REVISIT_ This code needs to be reviewed by Ram.
            try {

		_orb.sendCancelRequestIfFinalFragmentNotSent();

                // REVISIT: Talk to sanjeevk and verify if this signature of
                // is releaseReply is important to DII. Can we just use one
                // releaseReply in ClientDelegate, instead of two?

		// _REVISIT_ The call to cleanupClientPIRequest should
		// really should go in releaseReply().
		// However, because GenericPOAClientSC:305 v1.54 is 
		// explicitly calling releaseReply, this would cause 
		// cleanup to occur twice.  Ask Ram why releaseReply is 
		// being called instead of receivedReply.

		// Invoke Portable Interceptors cleanup.  This is done to
		// handle exceptions during stream marshalling.
		_orb.cleanupClientPIRequest();

                delegate.releaseReply(resp, _opName, exception);
            } catch ( WrongTransaction ex2 ) {
                // XXX return this for deferred sends
                throw new NO_IMPLEMENT(MinorCodes.SEND_DEFERRED_NOTIMPLEMENTED,
                               CompletionStatus.COMPLETED_MAYBE);
            } catch ( SystemException ex2 ) {
                // substitute this exception for original exception
                _env.exception(ex2);
                throw ex2;
            }

	    throw ex;
	}

        OutputStream os = (OutputStream)req;

        // Marshal args
        try {
            for (int i=0; i<_arguments.count() ; i++) {
	        NamedValue nv = _arguments.item(i);
                switch (nv.flags()) {
		        case ARG_IN.value:
                    nv.value().write_value(os);
                    break;
		        case ARG_OUT.value:
		            break;
		        case ARG_INOUT.value:
                    nv.value().write_value(os);
                    break;
	            }
            }
        } catch ( org.omg.CORBA.Bounds ex ) {
	        // Cannot happen since we only iterate till _arguments.count()
        }

        try { // this outer try block ensures we do a req.completeRequest.

            // Invoke
            try {
                resp = delegate.invoke(req);
            } catch (SystemException ex) {
		// Pass SystemException through Portable Interceptors'
		// ending points, and then
                // set the SystemException in the env and rethrow it.
                exception = _orb.invokeClientPIEndingPoint(
		    ReplyMessage.SYSTEM_EXCEPTION, ex );
		continueOrThrowSystemOrRemarshal( exception );

		// Note: We should never need to execute this line,
		// but we should assert in case exception was set to null
		// somehow.
		throw new INTERNAL(
		    "Assertion failed: exception should not be null ." );
            }

            if (_isOneWay) {
		// Invoke Portable Interceptors with receive_other
		exception = _orb.invokeClientPIEndingPoint(
		    ReplyMessage.NO_EXCEPTION, exception );
                continueOrThrowSystemOrRemarshal( exception );
                return;
	    }

            // Process reply
            InputStream is = (InputStream) resp;

            if (resp.isSystemException()) {

                // Unmarshal the SystemException, set it in the env and throw it.
                SystemException se = resp.getSystemException();

                boolean doRemarshal = false;

                // FIX(Ram J) (05/01/2000) added locatedIOR = ior
                // and retry the request from root ior,
                // if system exception is COMM_FAILURE.
                // WARNING: There is a risk of infinite loopback
                // if the requests on location forwarded ior result in
                // system exception (COMM_FAILURE)
                if (se instanceof org.omg.CORBA.COMM_FAILURE
                     && se.completed == CompletionStatus.COMPLETED_NO) {
                    if (delegate.locatedIOR != delegate.ior) {
                        delegate.locatedIOR = delegate.ior; // retry from root ior
                        doRemarshal = true;
                    }
                }

                if (se.minor == MinorCodes.CONN_CLOSE_REBIND &&
                    (se instanceof org.omg.CORBA.COMM_FAILURE)) {
                    
                    doRemarshal = true;
                }
                
                if (doRemarshal) {
                    // Invoke Portable Interceptors with receive_exception:
                    exception = _orb.invokeClientPIEndingPoint(
                        ReplyMessage.SYSTEM_EXCEPTION, se );

                    // If PI did not change the exception, handle
                    // COMM_FAILURE by recursively calling doInvocation.
                    // Otherwise, throw the exception PI wants thrown.
                    if( se == exception ) {
                        doInvocation();
                    }
                    else {
                        continueOrThrowSystemOrRemarshal( exception );
                        throw new INTERNAL(
                            "Assertion failed in RequestImpl. " +
                            "exception should not be null." );
                    }

                    return;
                }

		// Invoke Portable Interceptors with receive_exception.
                exception = _orb.invokeClientPIEndingPoint(
		    ReplyMessage.SYSTEM_EXCEPTION, se );
		continueOrThrowSystemOrRemarshal( exception );

		// Note: We should never need to execute this line, but
		// we should assert in case exception was set to null somehow
		throw new INTERNAL(
		    "Assertion failed in RequestImpl:  " +
		    "exception should not be null." );
            } else if (resp.isUserException()) {
                // Peek exception's repository id
                String exid = resp.peekUserExceptionId();

                try {
                    // Find the typecode for the exception
                    for (int i=0; i<_exceptions.count() ; i++) {
                        TypeCode tc = _exceptions.item(i);
                        if ( tc.id().equals(exid) ) {
                            // Since we dont have the actual user exception
                            // class, the spec says we have to create an
                            // UnknownUserException and put it in the
                            // environment.
                            Any eany = _orb.create_any();
                            eany.read_value(is, (TypeCode)tc);
                            exception = new UnknownUserException(eany);

                            // _REVISIT_ Understand why this is 
                            // UnknownUserException.

			    // Invoke Portable Interceptors with 
			    // receive_exception (user exception)
			    Exception newException = 
				_orb.invokeClientPIEndingPoint(
				ReplyMessage.USER_EXCEPTION, exception );
			    // Note that continueOrThrowSystemOrRemarshal
			    // will not call _env.exception in the case of
			    // a UserException.  We must do so explicitly here.
                            _env.exception(newException);
                            if( exception != newException ) {
				exception = newException;
				continueOrThrowSystemOrRemarshal( exception );
			    }

                            return;
                        }
                    }
                } catch (Exception b) {
                    // Only exceptions to be caught are Bounds and BadKind.
                    // Both cannot happen here.
                }

                // must be a truly unknown exception
                SystemException u = new UNKNOWN(MinorCodes.UNKNOWN_CORBA_EXC,
                                CompletionStatus.COMPLETED_MAYBE);

		// Invoke Portable Interceptors with receive_exception:
                exception = _orb.invokeClientPIEndingPoint(
		    ReplyMessage.SYSTEM_EXCEPTION, u );
		continueOrThrowSystemOrRemarshal( exception );

		// Note: We should never need to execute this line, but
		// we should assert in case exception was set to null somehow.
		throw new INTERNAL(
		    "Assertion failed in RequestImpl : " +
		    "exception should not be null." );
            } else if (resp.isLocationForward()) {
                // FIXED(Ram J) (05/01/2000) added setting delegate.locatedIOR
                //  and reinvoking.
                delegate.locatedIOR = resp.getForwardedIOR();

		// Invoke Portable Interceptors with receive_other:
		exception = _orb.invokeClientPIEndingPoint(
		    ReplyMessage.LOCATION_FORWARD, null );

                // If PI did not raise exception, invoke again.  Otherwise
		// throw the exception PI wants thrown.
		if( exception == null ) {
                    doInvocation(); // invoke again.
		}
		else {
		    continueOrThrowSystemOrRemarshal( exception );
		}

                return;
                /*
                // Location forwards are handled internally in the subcontract.
                throw new INTERNAL(MinorCodes.LOCATIONFORWARD_ERROR,
                           CompletionStatus.COMPLETED_NO);
                */
            } else if (resp.isDifferentAddrDispositionRequested()) {
                // set the desired target addressing disposition.
                delegate.addressingDisposition = resp.getAddrDisposition();

		// Invoke Portable Interceptors with receive_other:
		exception = _orb.invokeClientPIEndingPoint(
		    ReplyMessage.NEEDS_ADDRESSING_MODE, null);

                // If PI did not raise exception, invoke again.  Otherwise
		// throw the exception PI wants thrown.
		if( exception == null ) {
                    doInvocation(); // invoke again.
		}
		else {
		    continueOrThrowSystemOrRemarshal( exception );
		}

                return;                
            } else { // normal return
                // Unmarshal return args
                unmarshalParams(is);

		// Invoke Portable Interceptors with receive_reply:
		exception = _orb.invokeClientPIEndingPoint(
		    ReplyMessage.NO_EXCEPTION, null );

		// Remember: not thrown if exception is null.
		continueOrThrowSystemOrRemarshal( exception );
            }
        } finally {
	    // _REVISIT_ PI Note: Any exceptions in the following try 
	    // block happen after PI endpoint has already run so they are not
	    // reported to interceptors.

	    // Note that changes to this try block should be synchronized with
	    // the identical try block above.
            try {

		_orb.sendCancelRequestIfFinalFragmentNotSent();

                // REVISIT: Talk to sanjeevk and verify if this signature of
                // is releaseReply is important to DII. Can we just use one
                // releaseReply in ClientDelegate, instead of two?

		// _REVISIT_ See above revisit for same 
		// cleanupClientPIRequest call.

		// Invoke Portable Interceptors cleanup.  This is done to
		// handle exceptions during stream marshalling.  
		_orb.cleanupClientPIRequest();

                delegate.releaseReply(resp, _opName, exception);
            } catch ( WrongTransaction ex ) {
                // XXX return this for deferred sends
                throw new NO_IMPLEMENT(MinorCodes.SEND_DEFERRED_NOTIMPLEMENTED,
                               CompletionStatus.COMPLETED_MAYBE);
            } catch ( SystemException ex ) {
                // substitute this exception for original exception
                _env.exception(ex);
                throw ex;
            }
        }
    }

    // Filters the given exception into a SystemException and throws it.
    // If this is a RemarshalException, handle it by recursively calling
    // doInvocation().  This method assumes the given exception is a 
    // SystemException or a RemarhsalException.  
    //
    // If this is a SystemException, call _env.exception( exception ) so that
    // it is set in the environment.
    // 
    // This is a utility method for the above doInvocation() code which must 
    // do this numerous times.  If the exception is null, no exception is 
    // thrown.
    //
    // Note that this code is essentially the same as in ClientDelegate.java
    // or GenericPOAClientSC.java
    //
    private void continueOrThrowSystemOrRemarshal( Exception exception )
        throws SystemException
    {
        if( exception == null ) {
            // do nothing.
        }
        else if( exception instanceof RemarshalException ) {
	    // invoke again:
	    doInvocation();
        }
        else {
	    _env.exception( exception );
            throw (SystemException)exception;
        }
    }

    protected void unmarshalParams(InputStream is)
    {
        // First unmarshal the return value if it is not void
        if ( _result != null ) {
            Any returnAny = _result.value();
            TypeCode returnType = returnAny.type();
            if ( returnType.kind().value() != TCKind._tk_void )
                returnAny.read_value(is, returnType);
        }
        
        // Now unmarshal the out/inout args
        try {
            for ( int i=0; i<_arguments.count() ; i++) {
                NamedValue nv = _arguments.item(i);
                switch( nv.flags() ) {
		case ARG_IN.value:
		    break;
		case ARG_OUT.value:
		case ARG_INOUT.value:
		    Any any = nv.value();	
		    any.read_value(is, any.type());
		    break;
                }
            }
        } 
	catch ( org.omg.CORBA.Bounds ex ) {
	    // Cannot happen since we only iterate till _arguments.count()
        }
    }
}
