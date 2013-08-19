/*
 * @(#)FragmentMessage_1_2.java	1.9 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.corba.se.internal.iiop.messages;

import com.sun.corba.se.internal.core.GIOPVersion;

/**
 * This implements the GIOP 1.2 Fragment header.
 *
 * @author Ram Jeyaraman 05/14/2000
 * @version 1.0
 */

public final class FragmentMessage_1_2 extends Message_1_2
        implements FragmentMessage {

    // Constructors

    FragmentMessage_1_2() {}

    // This is currently never called.
    FragmentMessage_1_2(int _request_id) {
        super(Message.GIOPBigMagic, GIOPVersion.V1_2, FLAG_NO_FRAG_BIG_ENDIAN,
            Message.GIOPFragment, 0);
        this.message_type = GIOPFragment;
        request_id = _request_id;
    }

    FragmentMessage_1_2(Message_1_1 msg12) {
        this.magic = msg12.magic;
        this.GIOP_version = msg12.GIOP_version;
        this.flags = FLAG_NO_FRAG_BIG_ENDIAN;
        this.message_type = GIOPFragment;
        this.message_size = 0;

        switch (msg12.message_type) {
        case GIOPRequest :
            this.request_id = ((RequestMessage) msg12).getRequestId();
            break;
        case GIOPReply :
            this.request_id = ((ReplyMessage) msg12).getRequestId();
            break;
        case GIOPLocateRequest :
            this.request_id = ((LocateRequestMessage) msg12).getRequestId();
            break;
        case GIOPLocateReply :
            this.request_id = ((LocateReplyMessage) msg12).getRequestId();
            break;
        case GIOPFragment :
            this.request_id = ((FragmentMessage) msg12).getRequestId();
            break;
        }
    }

    // Accessor methods

    public int getRequestId() {
        return this.request_id;
    }

    public int getHeaderLength() {
        return GIOPMessageHeaderLength + 4;
    }
    
    // IO methods

    /* This will never be called, since we do not currently read the
     * request_id from an CDRInputStream. Instead we use the
     * readGIOP_1_2_requestId to read the requestId from a byte buffer.
     */
    public void read(org.omg.CORBA.portable.InputStream istream) {
        super.read(istream);
        this.request_id = istream.read_ulong();
    }

    public void write(org.omg.CORBA.portable.OutputStream ostream) {
        super.write(ostream);
        ostream.write_ulong(this.request_id);
    }

    public final void callback(com.sun.corba.se.internal.iiop.MessageMediator m)
        throws java.io.IOException
    {
        m.handleInput(this);
    }
} // class FragmentMessage_1_2
