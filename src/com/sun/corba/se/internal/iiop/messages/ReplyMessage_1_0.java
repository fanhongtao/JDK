/*
 * @(#)ReplyMessage_1_0.java	1.17 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.corba.se.internal.iiop.messages;

import org.omg.CORBA.INTERNAL;
import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.SystemException;

import com.sun.corba.se.internal.core.ServiceContexts;
import com.sun.corba.se.internal.core.GIOPVersion;
import com.sun.corba.se.internal.orbutil.MinorCodes;
import com.sun.corba.se.internal.orbutil.ORBUtility;
import com.sun.corba.se.internal.orbutil.ORBClassLoader;
import com.sun.corba.se.internal.core.IOR;
import com.sun.corba.se.internal.iiop.CDRInputStream;
import com.sun.corba.se.internal.iiop.ORB;

/**
 * This implements the GIOP 1.0 Reply header.
 *
 * @author Ram Jeyaraman 05/14/2000
 * @version 1.0
 */

public final class ReplyMessage_1_0 extends Message_1_0
        implements ReplyMessage {

    // Instance variables

    private ORB orb = null;
    private ServiceContexts service_contexts = null;
    private int request_id = (int) 0;
    private int reply_status = (int) 0;
    private IOR ior = null;
    private String exClassName = null;
    private int minorCode = (int) 0;
    private CompletionStatus completionStatus = null;

    // Constructors

    ReplyMessage_1_0(ORB orb) {
        this.orb = orb;
    }

    ReplyMessage_1_0(ORB orb, ServiceContexts _service_contexts,
            int _request_id, int _reply_status, IOR _ior) {
        super(Message.GIOPBigMagic, false, Message.GIOPReply, 0);
        this.orb = orb;
        service_contexts = _service_contexts;
        request_id = _request_id;
        reply_status = _reply_status;
        ior = _ior;
    }

    // Accessor methods

    public int getRequestId() {
        return this.request_id;
    }

    public int getReplyStatus() {
        return this.reply_status;
    }
    
    public short getAddrDisposition() {
        return KeyAddr.value;
    }

    public ServiceContexts getServiceContexts() {
        return this.service_contexts;
    }

    public void setServiceContexts( ServiceContexts sc ) {
	this.service_contexts = sc;
    }

    public SystemException getSystemException() {

        SystemException sysEx = null;

        try {
            sysEx = (SystemException) ORBClassLoader.loadClass(exClassName).newInstance();
        } catch (Exception someEx) {
            throw new INTERNAL("BAD SystemException: " + exClassName,
                                0, CompletionStatus.COMPLETED_MAYBE);
        }

        sysEx.minor = minorCode;
        sysEx.completed = completionStatus;

        return sysEx;
    }

    public IOR getIOR() {
        return this.ior;
    }

    public void setIOR( IOR ior ) {
	this.ior = ior;
    }

    // IO methods

    public void read(org.omg.CORBA.portable.InputStream istream) {
        super.read(istream);
        this.service_contexts 
            = new ServiceContexts((org.omg.CORBA_2_3.portable.InputStream) istream);
        this.request_id = istream.read_ulong();
        this.reply_status = istream.read_long();
        isValidReplyStatus(this.reply_status); // raises exception on error

        // The code below reads the reply body in some cases
        // SYSTEM_EXCEPTION & LOCATION_FORWARD
        if (this.reply_status == SYSTEM_EXCEPTION) {

            String reposId = istream.read_string();
            this.exClassName = ORBUtility.classNameOf(reposId);
            this.minorCode = istream.read_long();
            int status = istream.read_long();

            switch (status) {
            case CompletionStatus._COMPLETED_YES:
                this.completionStatus = CompletionStatus.COMPLETED_YES;
                break;
            case CompletionStatus._COMPLETED_NO:
                this.completionStatus = CompletionStatus.COMPLETED_NO;
                break;
            case CompletionStatus._COMPLETED_MAYBE:
                this.completionStatus = CompletionStatus.COMPLETED_MAYBE;
                break;
            default:
                throw new INTERNAL("BAD completion status: " + status,
                                    0, CompletionStatus.COMPLETED_MAYBE);
            }

        } else if (this.reply_status == USER_EXCEPTION) {
            // do nothing. The client stub will read the exception from body.
        } else if (this.reply_status == LOCATION_FORWARD) {
            CDRInputStream cdr = (CDRInputStream) istream;
	    this.ior = new IOR( cdr ) ;
        }
    }

    // Note, this writes only the header information. SystemException or
    // IOR may be written afterwards into the reply mesg body.
    public void write(org.omg.CORBA.portable.OutputStream ostream) {
        super.write(ostream);
    	if (this.service_contexts != null) {
	        service_contexts.write(
                (org.omg.CORBA_2_3.portable.OutputStream) ostream,
                GIOPVersion.V1_0);
	    } else {
	        ServiceContexts.writeNullServiceContext(
                (org.omg.CORBA_2_3.portable.OutputStream) ostream);
        }
        ostream.write_ulong(this.request_id);
        ostream.write_long(this.reply_status);
    }

    // Static methods

    public static void isValidReplyStatus(int replyStatus) {
        switch (replyStatus) {
        case NO_EXCEPTION :
        case USER_EXCEPTION :
        case SYSTEM_EXCEPTION :
        case LOCATION_FORWARD :
            break;
        default :
            throw new INTERNAL(MinorCodes.ILLEGAL_REPLY_STATUS,
                                CompletionStatus.COMPLETED_MAYBE);
        }
    }

    public final void callback(com.sun.corba.se.internal.iiop.MessageMediator m)
        throws java.io.IOException
    {
        m.handleInput(this);
    }    
} // class ReplyMessage_1_0
