/*
 * @(#)LocateRequestMessage_1_1.java	1.9 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.corba.se.internal.iiop.messages;

import com.sun.corba.se.internal.core.GIOPVersion;
import com.sun.corba.se.internal.iiop.ORB;
import com.sun.corba.se.internal.ior.ObjectKey;

/**
 * This implements the GIOP 1.1 LocateRequest header.
 *
 * @author Ram Jeyaraman 05/14/2000
 * @version 1.0
 */

public final class LocateRequestMessage_1_1 extends Message_1_1
        implements LocateRequestMessage {

    // Instance variables

    private ORB orb = null;
    private int request_id = (int) 0;
    private byte[] object_key = null;
    private ObjectKey objectKey = null;

    // Constructors

    LocateRequestMessage_1_1(ORB orb) {
        this.orb = orb;
    }

    LocateRequestMessage_1_1(ORB orb, int _request_id, byte[] _object_key) {
        super(Message.GIOPBigMagic, GIOPVersion.V1_1, FLAG_NO_FRAG_BIG_ENDIAN,
            Message.GIOPLocateRequest, 0);
        this.orb = orb;
        request_id = _request_id;
        object_key = _object_key;
    }

    // Accessor methods (LocateRequestMessage interface)

    public int getRequestId() {
        return this.request_id;
    }

    public ObjectKey getObjectKey() {
        if (this.objectKey == null) {
	    // this will raise a MARSHAL exception upon errors.
	    this.objectKey = MessageBase.extractObjectKey(object_key, orb);
        }

	return this.objectKey;
    }

    // IO methods

    public void read(org.omg.CORBA.portable.InputStream istream) {
        super.read(istream);
        this.request_id = istream.read_ulong();
        int _len1 = istream.read_long();
        this.object_key = new byte[_len1];
        istream.read_octet_array(this.object_key, 0, _len1);
    }

    public void write(org.omg.CORBA.portable.OutputStream ostream) {
        super.write(ostream);
        ostream.write_ulong(this.request_id);
        nullCheck(this.object_key);
        ostream.write_long(this.object_key.length);
        ostream.write_octet_array(this.object_key, 0, this.object_key.length);
    }

    public final void callback(com.sun.corba.se.internal.iiop.MessageMediator m)
        throws java.io.IOException
    {
        m.handleInput(this);
    }
} // class LocateRequestMessage_1_1
