/*
 * @(#)ReplyMessage_1_2.java	1.19 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.corba.se.internal.iiop.messages;

import org.omg.CORBA.INTERNAL;
import org.omg.CORBA.SystemException;
import org.omg.CORBA.CompletionStatus;

import com.sun.corba.se.internal.core.ServiceContexts;
import com.sun.corba.se.internal.core.GIOPVersion;
import com.sun.corba.se.internal.orbutil.MinorCodes;
import com.sun.corba.se.internal.orbutil.ORBUtility;
import com.sun.corba.se.internal.orbutil.ORBClassLoader;
import com.sun.corba.se.internal.core.IOR;
import com.sun.corba.se.internal.iiop.CDRInputStream;
import com.sun.corba.se.internal.iiop.CDROutputStream;
import com.sun.corba.se.internal.iiop.ORB;
import com.sun.corba.se.internal.orbutil.ORBConstants;

/**
 * This implements the GIOP 1.2 Reply header.
 *
 * @author Ram Jeyaraman 05/14/2000
 * @version 1.0
 */

public final class ReplyMessage_1_2 extends Message_1_2
        implements ReplyMessage {

    // Instance variables

    private ORB orb = null;
    private int reply_status = (int) 0;
    private ServiceContexts service_contexts = null;
    private IOR ior = null;
    private String exClassName = null;
    private int minorCode = (int) 0;
    private CompletionStatus completionStatus = null;
    private short addrDisposition = KeyAddr.value; // default;
    
    // Constructors

    ReplyMessage_1_2(ORB orb) {
        this.orb = orb;
    }

    ReplyMessage_1_2(ORB orb, int _request_id, int _reply_status,
            ServiceContexts _service_contexts, IOR _ior) {
        super(Message.GIOPBigMagic, GIOPVersion.V1_2, FLAG_NO_FRAG_BIG_ENDIAN,
            Message.GIOPReply, 0);
        this.orb = orb;
        request_id = _request_id;
        reply_status = _reply_status;
        service_contexts = _service_contexts;
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
        return this.addrDisposition;
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
        this.request_id = istream.read_ulong();
        this.reply_status = istream.read_long();
        isValidReplyStatus(this.reply_status); // raises exception on error
        this.service_contexts 
            = new ServiceContexts((org.omg.CORBA_2_3.portable.InputStream) istream);

        // CORBA formal 00-11-0 15.4.2.2 GIOP 1.2 body must be
        // aligned on an 8 octet boundary.
        ((CDRInputStream)istream).alignOnBoundary(ORBConstants.GIOP_12_MSG_BODY_ALIGNMENT);

        // The code below reads the reply body in some cases
        // SYSTEM_EXCEPTION & LOCATION_FORWARD & LOCATION_FORWARD_PERM &
        // NEEDS_ADDRESSING_MODE
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
        } else if ( (this.reply_status == LOCATION_FORWARD) ||
                (this.reply_status == LOCATION_FORWARD_PERM) ){
            CDRInputStream cdr = (CDRInputStream) istream;
	    this.ior = new IOR( cdr ) ;
        }  else if (this.reply_status == NEEDS_ADDRESSING_MODE) {
            // read GIOP::AddressingDisposition from body and resend the
            // original request using the requested addressing mode. The
            // resending is transparent to the client program.
            this.addrDisposition = AddressingDispositionHelper.read(istream);            
        }
    }

    // Note, this writes only the header information. SystemException or
    // IOR or GIOP::AddressingDisposition may be written afterwards into the
    // reply mesg body.
    public void write(org.omg.CORBA.portable.OutputStream ostream) {
        super.write(ostream);
        ostream.write_ulong(this.request_id);
        ostream.write_long(this.reply_status);
    	if (this.service_contexts != null) {
	        service_contexts.write(
                (org.omg.CORBA_2_3.portable.OutputStream) ostream,
                GIOPVersion.V1_2);
	    } else {
	        ServiceContexts.writeNullServiceContext(
                (org.omg.CORBA_2_3.portable.OutputStream) ostream);
        }

        // CORBA formal 00-11-0 15.4.2.2 GIOP 1.2 body must be
        // aligned on an 8 octet boundary.
        ((CDROutputStream)ostream).alignOnBoundary(ORBConstants.GIOP_12_MSG_BODY_ALIGNMENT);
    }

    // Static methods

    public static void isValidReplyStatus(int replyStatus) {
        switch (replyStatus) {
        case NO_EXCEPTION :
        case USER_EXCEPTION :
        case SYSTEM_EXCEPTION :
        case LOCATION_FORWARD :
        case LOCATION_FORWARD_PERM :
        case NEEDS_ADDRESSING_MODE :
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
} // class ReplyMessage_1_2
