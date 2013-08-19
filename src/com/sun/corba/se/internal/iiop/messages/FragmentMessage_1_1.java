/*
 * @(#)FragmentMessage_1_1.java	1.7 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.corba.se.internal.iiop.messages;

/**
 * This implements the GIOP 1.1 Fragment header.
 *
 * @author Ram Jeyaraman 05/14/2000
 * @version 1.0
 */

public final class FragmentMessage_1_1 extends Message_1_1
        implements FragmentMessage {

    // Constructors

    FragmentMessage_1_1() {}

    FragmentMessage_1_1(Message_1_1 msg11) {
        this.magic = msg11.magic;
        this.GIOP_version = msg11.GIOP_version;
        this.flags = FLAG_NO_FRAG_BIG_ENDIAN;
        this.message_type = GIOPFragment;
        this.message_size = 0;
    }

    // Accessor methods

    public int getRequestId() {
        return -1; // 1.1 has no fragment header and so no request_id
    }

    public int getHeaderLength() {
        return GIOPMessageHeaderLength;
    }

    // IO methods

    /* This will never be called, since we do not currently read the
     * request_id from an CDRInputStream. Instead we use the
     * readGIOP_1_1_requestId to read the requestId from a byte buffer.
     */
    public void read(org.omg.CORBA.portable.InputStream istream) {
        super.read(istream);
    }

    /* 1.1 has no request_id; so nothing to write */
    public void write(org.omg.CORBA.portable.OutputStream ostream) {
        super.write(ostream);
    }

    public final void callback(com.sun.corba.se.internal.iiop.MessageMediator m)
        throws java.io.IOException
    {
        m.handleInput(this);
    }
} // class FragmentMessage_1_1
