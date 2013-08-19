/*
 * @(#)CancelRequestMessage_1_0.java	1.9 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.corba.se.internal.iiop.messages;

import com.sun.corba.se.internal.core.GIOPVersion;

/**
 * This implements the GIOP 1.0 CancelRequest header.
 *
 * @author Ram Jeyaraman 05/14/2000
 * @version 1.0
 */

public final class CancelRequestMessage_1_0 extends Message_1_0
        implements CancelRequestMessage {

    // Instance variables

    private int request_id = (int) 0;

    // Constructors

    CancelRequestMessage_1_0() {}

    CancelRequestMessage_1_0(int _request_id) {
        super(Message.GIOPBigMagic, false, Message.GIOPCancelRequest,
              CANCEL_REQ_MSG_SIZE);
        request_id = _request_id;
    }

    // Accessor methods

    public int getRequestId() {
        return this.request_id;
    }

    // IO methods

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
} // class CancelRequestMessage_1_0
