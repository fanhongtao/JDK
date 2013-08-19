/*
 * @(#)LocateReplyMessage_1_0.java	1.11 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.corba.se.internal.iiop.messages;

import org.omg.CORBA.INTERNAL;
import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.SystemException;

import com.sun.corba.se.internal.core.GIOPVersion;
import com.sun.corba.se.internal.orbutil.MinorCodes;
import com.sun.corba.se.internal.core.IOR;
import com.sun.corba.se.internal.iiop.CDRInputStream;
import com.sun.corba.se.internal.iiop.ORB;

/**
 * This implements the GIOP 1.0 LocateReply header.
 *
 * @author Ram Jeyaraman 05/14/2000
 * @version 1.0
 */

public final class LocateReplyMessage_1_0 extends Message_1_0
        implements LocateReplyMessage {

    // Instance variables

    private ORB orb = null;
    private int request_id = (int) 0;
    private int locate_status = (int) 0;
    private IOR ior = null;

    // Constructors

    LocateReplyMessage_1_0(ORB orb) {
        this.orb = orb;
    }

    LocateReplyMessage_1_0(ORB orb, int _request_id,
            int _locate_status, IOR _ior) {
        super(Message.GIOPBigMagic, false, Message.GIOPLocateReply, 0);
        this.orb = orb;
        request_id = _request_id;
        locate_status = _locate_status;
        ior = _ior;
    }

    // Accessor methods

    public int getRequestId() {
        return this.request_id;
    }

    public int getReplyStatus() {
        return this.locate_status;
    }

    public short getAddrDisposition() {
        return KeyAddr.value;
    }
        
    public SystemException getSystemException() {
        return null;  // 1.0 LocateReply body does not contain SystemException
    }

    public IOR getIOR() {
        return this.ior;
    }

    // IO methods

    public void read(org.omg.CORBA.portable.InputStream istream) {
        super.read(istream);
        this.request_id = istream.read_ulong();
        this.locate_status = istream.read_long();
        isValidReplyStatus(this.locate_status); // raises exception on error

        // The code below reads the reply body if status is OBJECT_FORWARD
        if (this.locate_status == OBJECT_FORWARD) {
            CDRInputStream cdr = (CDRInputStream) istream;
	    this.ior = new IOR( cdr ) ;
        }
    }

    // Note, this writes only the header information.
    // IOR may be written afterwards into the reply mesg body.
    public void write(org.omg.CORBA.portable.OutputStream ostream) {
        super.write(ostream);
        ostream.write_ulong(this.request_id);
        ostream.write_long(this.locate_status);
    }

    // Static methods

    public static void isValidReplyStatus(int replyStatus) {
        switch (replyStatus) {
        case UNKNOWN_OBJECT :
        case OBJECT_HERE :
        case OBJECT_FORWARD :
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
} // class LocateReplyMessage_1_0
