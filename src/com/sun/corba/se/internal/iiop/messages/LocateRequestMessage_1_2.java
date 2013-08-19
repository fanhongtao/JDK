/*
 * @(#)LocateRequestMessage_1_2.java	1.11 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.corba.se.internal.iiop.messages;

import com.sun.corba.se.internal.core.GIOPVersion;
import com.sun.corba.se.internal.ior.ObjectKey;
import com.sun.corba.se.internal.iiop.ORB;

/**
 * This implements the GIOP 1.2 LocateRequest header.
 *
 * @author Ram Jeyaraman 05/14/2000
 * @version 1.0
 */

public final class LocateRequestMessage_1_2 extends Message_1_2
        implements LocateRequestMessage {

    // Instance variables

    private ORB orb = null;
    private ObjectKey objectKey = null;
    private TargetAddress target = null;

    // Constructors

    LocateRequestMessage_1_2(ORB orb) {
        this.orb = orb;
    }

    LocateRequestMessage_1_2(ORB orb, int _request_id, TargetAddress _target) {
        super(Message.GIOPBigMagic, GIOPVersion.V1_2, FLAG_NO_FRAG_BIG_ENDIAN,
            Message.GIOPLocateRequest, 0);
        this.orb = orb;
        request_id = _request_id;
        target = _target;
    }

    // Accessor methods (LocateRequestMessage interface)

    public int getRequestId() {
        return this.request_id;
    }

    public ObjectKey getObjectKey() {
        if (this.objectKey == null) {
	    // this will raise a MARSHAL exception upon errors.
	    this.objectKey = MessageBase.extractObjectKey(target, orb);
        }

	return this.objectKey;
    }

    // IO methods

    public void read(org.omg.CORBA.portable.InputStream istream) {
        super.read(istream);
        this.request_id = istream.read_ulong();
        this.target = TargetAddressHelper.read(istream);
        getObjectKey(); // this does AddressingDisposition check        
    }

    public void write(org.omg.CORBA.portable.OutputStream ostream) {
        super.write(ostream);
        ostream.write_ulong (this.request_id);
        nullCheck(this.target);
        TargetAddressHelper.write(ostream, this.target);
    }

    public final void callback(com.sun.corba.se.internal.iiop.MessageMediator m)
        throws java.io.IOException
    {
        m.handleInput(this);
    }
} // class LocateRequestMessage_1_2
