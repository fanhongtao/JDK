/*
 * @(#)ServerRequestImpl.java	1.59 03/01/23
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
import org.omg.CORBA.Context;
import org.omg.CORBA.NamedValue;
import org.omg.CORBA.NVList;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.TCKind;
import org.omg.CORBA.ServerRequest;
import org.omg.CORBA.SystemException;
import org.omg.CORBA.NO_IMPLEMENT;
import org.omg.CORBA.BAD_INV_ORDER;
import org.omg.CORBA.BAD_PARAM;
import org.omg.CORBA.MARSHAL;
import org.omg.CORBA.Bounds;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.CompletionStatus;

import com.sun.corba.se.internal.core.*;
import com.sun.corba.se.internal.orbutil.MinorCodes;


public class ServerRequestImpl extends ServerRequest {

    ///////////////////////////////////////////////////////////////////////////
    // data members

    private ORB			 _orb		= null;
    private String       	 _opName	= null;
    private NVList 		 _arguments 	= null;
    private Context		 _ctx		= null;
    private InputStream	 	 _ins		= null;
        
    // booleans to check for various operation invocation restrictions
    private boolean		_paramsCalled	= false;
    private boolean		_resultSet	= false;
    private boolean             _exceptionSet   = false;
    private Any			_resultAny      = null;
    private Any			_exception      = null;


    public ServerRequestImpl (com.sun.corba.se.internal.core.ServerRequest req, ORB orb) {
        _opName	= req.getOperationName(); 
        _ins	= (InputStream)req;
        _ctx 	= null; 	// if we support contexts, this would
				// presumably also  be available on
				// the server invocation
        _orb = orb;
    }

    public String operation() {
        return _opName;
    }

    public void arguments(NVList args) 
    {
        if ( _paramsCalled || _exceptionSet ) 
            throw new BAD_INV_ORDER();

	if ( args == null )
	    throw new BAD_PARAM();

        _paramsCalled = true;

        NamedValue arg = null;
        for (int i=0; i < args.count() ; i++) {
            try {
	        arg = args.item(i);
            } 
            catch (Bounds e) {        
            }

	    try {
                if ((arg.flags() == org.omg.CORBA.ARG_IN.value) || 
	            (arg.flags() == org.omg.CORBA.ARG_INOUT.value)) {
	            // unmarshal the value into the Any
	            arg.value().read_value(_ins, arg.value().type());
                }
	    } catch ( Exception ex ) {
		throw new MARSHAL("Bad arguments NVList. Error while unmarshaling parameters");
	    }
        }

        // hang on to the NVList for marshaling the result
        _arguments = args;

	_orb.setServerPIInfo( _arguments );
	_orb.invokeServerPIIntermediatePoint();
    }
    
    public void set_result(Any res) {

        // check for invocation restrictions
        if ( !_paramsCalled || _resultSet || _exceptionSet )
            throw new BAD_INV_ORDER();

	if ( res == null )
	    throw new BAD_PARAM();

        _resultAny = res;
        _resultSet = true;

	// Notify portable interceptors of the result so that 
	// ServerRequestInfo.result() functions as desired.
	_orb.setServerPIInfo( _resultAny );

        // actual marshaling of the reply msg header and params takes place
        // after the DSI returns control to the ORB.
    }

    public void set_exception(Any exc) 
    {
	// except can be called by the DIR at any time (CORBA 2.2 section 6.3).

	if ( exc == null )
	    throw new BAD_PARAM();

	// Ensure that the Any contains a SystemException or a 
	// UserException. If the UserException is not a declared exception,
	// the client will get an UNKNOWN exception.
	TCKind kind = exc.type().kind();
	if ( kind != TCKind.tk_except )
	    throw new BAD_PARAM();

        _exception = exc;

	// Inform Portable interceptors of the exception that was set
	// so sending_exception can return the right value.
	_orb.setServerPIExceptionInfo( _exception );

	// The user can only call arguments once and not at all after
	// set_exception.  (internal flags ensure this).  However, the user
	// can call set_exception multiple times.  Therefore, we only 
	// invoke receive_request the first time set_exception is
	// called (if they haven't already called arguments).
	if( !_exceptionSet && !_paramsCalled ) {
	    // We need to invoke intermediate points here.
	    _orb.invokeServerPIIntermediatePoint();
	}

        _exceptionSet = true;

        // actual marshaling of the reply msg header and exception takes place
        // after the DSI returns control to the ORB.
    }
    

    /** This is called from the ORB after the DynamicImplementation.invoke
     *  returns. Here we set the result if result() has not already been called.
     *  @return the exception if there is one (then ORB will not call 
     *  marshalReplyParams()) otherwise return null.
     */
    public Any checkResultCalled()
    {
	// Two things to be checked (CORBA 2.2 spec, section 6.3):
	// 1. Unless it calls set_exception(), the DIR must call arguments()
	//    exactly once, even if the operation signature contains 
	//    no parameters.
	// 2. Unless set_exception() is called, if the invoked operation has a 
	//    non-void result type, set_result() must be called exactly once 
	//    before the DIR returns.

	if ( _paramsCalled && _resultSet ) // normal invocation return
	    return null;
        else if ( _paramsCalled && !_resultSet && !_exceptionSet ) {
            try {
		// Neither a result nor an exception has been set.
		// Assume that the return type is void. If this is not so,
		// the client will throw a MARSHAL exception while
		// unmarshaling the return value.
                TypeCode result_tc = _orb.get_primitive_tc(org.omg.CORBA.TCKind.tk_void);
        	_resultAny = _orb.create_any();
                _resultAny.type(result_tc);
        	_resultSet = true;

	        return null;
            } catch ( Exception ex ) {
                throw new org.omg.CORBA.MARSHAL(MinorCodes.DSI_RESULT_EXCEPTION,
						CompletionStatus.COMPLETED_MAYBE);
            }
        }
        else if ( _exceptionSet )
            return _exception;
        else { 
	    // neither params() nor except() has been called.
            throw new BAD_INV_ORDER(MinorCodes.DSIMETHOD_NOTCALLED,
                                    CompletionStatus.COMPLETED_MAYBE);
        }
    }

    /** This is called from the ORB after the DynamicImplementation.invoke
     *  returns. Here we marshal the return value and inout/out params.
     */
    public void marshalReplyParams(OutputStream os)
    {
        // marshal the operation return value
        _resultAny.write_value(os);

        // marshal the inouts/outs
        NamedValue arg = null;

        for (int i=0; i < _arguments.count() ; i++) {
            try {
                arg = _arguments.item(i);
            } catch (Bounds e) {}

            if ((arg.flags() == org.omg.CORBA.ARG_OUT.value) ||
                (arg.flags() == org.omg.CORBA.ARG_INOUT.value)) {
                arg.value().write_value(os);
            }
        }
    }

    
    public Context ctx() 
    {
        if ( !_paramsCalled || _resultSet || _exceptionSet ) 
            throw new BAD_INV_ORDER();

        throw new NO_IMPLEMENT(MinorCodes.CONTEXT_NOT_IMPLEMENTED,
                               CompletionStatus.COMPLETED_NO);
    }  
}
