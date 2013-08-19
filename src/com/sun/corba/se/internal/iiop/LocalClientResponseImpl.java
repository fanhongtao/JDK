/*
 * @(#)LocalClientResponseImpl.java	1.24 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.corba.se.internal.iiop;

import java.io.IOException;

import org.omg.CORBA.SystemException;
import org.omg.CORBA.CompletionStatus;

import com.sun.corba.se.internal.core.Response;
import com.sun.corba.se.internal.core.ClientResponse;
import com.sun.corba.se.internal.core.IOR;
import com.sun.corba.se.internal.core.ServiceContext;
import com.sun.corba.se.internal.core.ServiceContexts;
import com.sun.corba.se.internal.iiop.messages.Message;
import com.sun.corba.se.internal.iiop.messages.ReplyMessage;
import com.sun.corba.se.internal.orbutil.MinorCodes;

class LocalClientResponseImpl extends IIOPInputStream implements ClientResponse
{
    LocalClientResponseImpl(ORB orb, byte[] buf, ReplyMessage header)
    {
	super(orb, buf, header.getSize(), header.isLittleEndian(), header, null);

        this.reply = header;

        // NOTE (Ram J) (06/02/2000) if we set result.setIndex(bodyBegin)
        // in LocalServerResponse.getClientResponse(), then we do not need
        // to read the headers (done below) anymore.
        // This will be an optimisation which is can be done to speed up the
        // local invocation by avoiding reading the headers in the local cases.

        // BUGFIX(Ram Jeyaraman) This has been moved from
        // LocalServerResponse.getClientResponse()
        // Skip over all of the GIOP header information.  This positions
        // the offset in the buffer so that the skeleton can correctly read
        // the marshalled arguments.
        this.setIndex(Message.GIOPMessageHeaderLength);

        // BUGFIX(Ram Jeyaraman) For local invocations, the reply mesg fields
        // needs to be set, by reading the response buffer contents
        // to correctly set the exception type and other info.
        this.reply.read(this);
    }

    LocalClientResponseImpl(SystemException ex)
    {
	this.systemException = ex;
    }

    public boolean isSystemException() {
	if ( reply != null )
	    return reply.getReplyStatus() == ReplyMessage.SYSTEM_EXCEPTION;
	else
	    return (systemException != null);	
    }

    public boolean isUserException() {
	if ( reply != null )
	    return reply.getReplyStatus() == ReplyMessage.USER_EXCEPTION;
	else
	    return false;
    }

    public boolean isLocationForward() {
        if ( reply != null ) {
            return ( (reply.getReplyStatus() == ReplyMessage.LOCATION_FORWARD) ||
                     (reply.getReplyStatus() == ReplyMessage.LOCATION_FORWARD_PERM) );
            //return reply.getReplyStatus() == ReplyMessage.LOCATION_FORWARD;
        } else {
            return false;
        }
    }
    
    public boolean isDifferentAddrDispositionRequested() {
        if (reply != null) {
            return reply.getReplyStatus() == ReplyMessage.NEEDS_ADDRESSING_MODE;
        }
    
        return false;
    }
        
    public short getAddrDisposition() {
        if (reply != null) {
            return reply.getAddrDisposition();
        }
        
        throw new org.omg.CORBA.INTERNAL(
            "Null reply in getAddrDisposition",
            MinorCodes.NULL_REPLY_IN_GET_ADDR_DISPOSITION,        
            CompletionStatus.COMPLETED_MAYBE);
    }
        
    public IOR getForwardedIOR() {
	if ( reply != null )
	    return reply.getIOR();
	else
	    return null;
    }

    public int getRequestId() {
	if ( reply != null )
	    return reply.getRequestId();
	else
	    throw new org.omg.CORBA.INTERNAL("Error in getRequestId");
    }

    public ServiceContexts getServiceContexts() {
	if ( reply != null )
	    return reply.getServiceContexts();
	else
	    return null;
    }

    public SystemException getSystemException() {
	if ( reply != null )
	    return reply.getSystemException();
	else
	    return systemException;
    }

    public java.lang.String peekUserExceptionId() {
        mark(Integer.MAX_VALUE);
        String result = read_string();
        reset();
        return result;
    }

    /**
     * Check to see if the response is local.
     */
    public boolean isLocal(){
        return true;
    }

    private ReplyMessage reply;
    private SystemException systemException;
}
