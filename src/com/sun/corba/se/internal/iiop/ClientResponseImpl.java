/*
 * @(#)ClientResponseImpl.java	1.35 03/01/23
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

public class ClientResponseImpl extends IIOPInputStream implements ClientResponse
{
    protected ClientResponseImpl(Connection conn, byte[] buf, ReplyMessage header)
	throws IOException
    {
	super(conn, buf, header);

        this.reply = header;
    }

    protected ClientResponseImpl(SystemException ex)
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
     * Check to see if response is local.
     */
    public boolean isLocal(){
        return false;
    }

    protected ReplyMessage reply;
    private SystemException systemException;
}
