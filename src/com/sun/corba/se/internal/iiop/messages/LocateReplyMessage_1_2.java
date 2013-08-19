/*
 * @(#)LocateReplyMessage_1_2.java	1.16 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.corba.se.internal.iiop.messages;

import org.omg.CORBA.INTERNAL;
import org.omg.CORBA.SystemException;
import org.omg.CORBA.CompletionStatus;

import com.sun.corba.se.internal.core.GIOPVersion;
import com.sun.corba.se.internal.orbutil.MinorCodes;
import com.sun.corba.se.internal.orbutil.ORBUtility;
import com.sun.corba.se.internal.core.IOR;
import com.sun.corba.se.internal.iiop.CDRInputStream;
import com.sun.corba.se.internal.iiop.CDROutputStream;
import com.sun.corba.se.internal.iiop.ORB;
import com.sun.corba.se.internal.orbutil.ORBConstants;
import com.sun.corba.se.internal.orbutil.ORBClassLoader;

/**
 * This implements the GIOP 1.2 LocateReply header.
 *
 * @author Ram Jeyaraman 05/14/2000
 * @version 1.0
 */

public final class LocateReplyMessage_1_2 extends Message_1_2
        implements LocateReplyMessage {

    // Instance variables

    private ORB orb = null;
    private int reply_status = (int) 0;
    private IOR ior = null;
    private String exClassName = null;
    private int minorCode = (int) 0;
    private CompletionStatus completionStatus = null;
    private short addrDisposition = KeyAddr.value; // default;

    // Constructors

    LocateReplyMessage_1_2(ORB orb) {
        this.orb = orb;
    }

    LocateReplyMessage_1_2(ORB orb, int _request_id,
            int _reply_status, IOR _ior) {
        super(Message.GIOPBigMagic, GIOPVersion.V1_2, FLAG_NO_FRAG_BIG_ENDIAN,
            Message.GIOPLocateReply, 0);
        this.orb = orb;
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
        return this.addrDisposition;
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

    // IO methods

    public void read(org.omg.CORBA.portable.InputStream istream) {
        super.read(istream);
        this.request_id = istream.read_ulong();
        this.reply_status = istream.read_long();
        isValidReplyStatus(this.reply_status); // raises exception on error

        // GIOP 1.2 LocateReply message bodies are not aligned on
        // 8 byte boundaries.

        // The code below reads the reply body in some cases
        // LOC_SYSTEM_EXCEPTION & OBJECT_FORWARD & OBJECT_FORWARD_PERM &
        // LOC_NEEDS_ADDRESSING_MODE
        if (this.reply_status == LOC_SYSTEM_EXCEPTION) {

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

        } else if ( (this.reply_status == OBJECT_FORWARD) ||
                (this.reply_status == OBJECT_FORWARD_PERM) ){
            CDRInputStream cdr = (CDRInputStream) istream;
	    this.ior = new IOR( cdr ) ;
        }  else if (this.reply_status == LOC_NEEDS_ADDRESSING_MODE) {
            // read GIOP::AddressingDisposition from body and resend the
            // original request using the requested addressing mode. The
            // resending is transparent to the caller.
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


        // GIOP 1.2 LocateReply message bodies are not aligned on
        // 8 byte boundaries.
    }

    // Static methods

    public static void isValidReplyStatus(int replyStatus) {
        switch (replyStatus) {
        case UNKNOWN_OBJECT :
        case OBJECT_HERE :
        case OBJECT_FORWARD :
        case OBJECT_FORWARD_PERM :
        case LOC_SYSTEM_EXCEPTION :
        case LOC_NEEDS_ADDRESSING_MODE :
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
} // class LocateReplyMessage_1_2
