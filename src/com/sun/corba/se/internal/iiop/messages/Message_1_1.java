/*
 * @(#)Message_1_1.java	1.9 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.corba.se.internal.iiop.messages;

import org.omg.CORBA.INTERNAL;
import org.omg.CORBA.CompletionStatus;

import com.sun.corba.se.internal.core.GIOPVersion;
import com.sun.corba.se.internal.orbutil.MinorCodes;

/*
 * This implements the GIOP 1.1 & 1.2 Message header.
 *
 * @author Ram Jeyaraman 05/14/2000
 * @version 1.0
 */

public class Message_1_1
        extends com.sun.corba.se.internal.iiop.messages.MessageBase {

    // Instance variables

    int magic = (int) 0;
    GIOPVersion GIOP_version = null;
    byte flags = (byte) 0;
    byte message_type = (byte) 0;
    int message_size = (int) 0;

    // Constructor

    Message_1_1() {}
    
    Message_1_1(int _magic, GIOPVersion _GIOP_version, byte _flags,
            byte _message_type, int _message_size) {
        magic = _magic;
        GIOP_version = _GIOP_version;
        flags = _flags;
        message_type = _message_type;
        message_size = _message_size;
    }

    // Accessor methods

    public GIOPVersion getGIOPVersion() {
        return this.GIOP_version;
    }

    public int getType() {
    	return this.message_type;
    }

    public int getSize() {
	    return this.message_size;
    }

    public boolean isLittleEndian() {
    	return ((this.flags & LITTLE_ENDIAN_BIT) == LITTLE_ENDIAN_BIT);
    }

    public boolean moreFragmentsToFollow() {
        return ( (this.flags & MORE_FRAGMENTS_BIT) == MORE_FRAGMENTS_BIT );
    }

    // Mutator methods

    public void	setSize(byte[] buf, int size) {
	    this.message_size = size;

	    //
    	// Patch the size field in the header.
    	//
	    int patch = size - GIOPMessageHeaderLength;
        if (!isLittleEndian()) {
            buf[8]  = (byte)((patch >>> 24) & 0xFF);
            buf[9]  = (byte)((patch >>> 16) & 0xFF);
            buf[10] = (byte)((patch >>> 8)  & 0xFF);
            buf[11] = (byte)((patch >>> 0)  & 0xFF);
        } else {
            buf[8]  = (byte)((patch >>> 0)  & 0xFF);
            buf[9]  = (byte)((patch >>> 8)  & 0xFF);
            buf[10] = (byte)((patch >>> 16) & 0xFF);
            buf[11] = (byte)((patch >>> 24) & 0xFF);
        }
    }

    /**
     * Allows us to create a fragment message from any message type.
     */
    public FragmentMessage createFragmentMessage() {

        // check for message type validity

        switch (this.message_type) {
        case GIOPCancelRequest :
        case GIOPCloseConnection :
        case GIOPMessageError :
            throw new INTERNAL(MinorCodes.FRAGMENTATION_DISALLOWED,
                                CompletionStatus.COMPLETED_MAYBE);
        case GIOPLocateRequest :
        case GIOPLocateReply :
            if (this.GIOP_version.equals(GIOPVersion.V1_1)) {
                throw new INTERNAL(MinorCodes.FRAGMENTATION_DISALLOWED,
                                    CompletionStatus.COMPLETED_MAYBE);
            }
            break;
        }

        /*
        // A fragmented mesg can be created only if the current mesg' fragment
        // bit is set. Otherwise, raise error
        // too stringent check
        if ( (this.flags & MORE_FRAGMENTS_BIT) != MORE_FRAGMENTS_BIT ) {
                throw new INTERNAL(MinorCodes.FRAGMENTATION_DISALLOWED,
                                    CompletionStatus.COMPLETED_MAYBE);
        }
        */
        if (this.GIOP_version.equals(GIOPVersion.V1_1)) {
            return new FragmentMessage_1_1(this);
        } else if (this.GIOP_version.equals(GIOPVersion.V1_2)) {
            return new FragmentMessage_1_2(this);
        }

        throw new INTERNAL(MinorCodes.GIOP_VERSION_ERROR,
                            CompletionStatus.COMPLETED_MAYBE);
    }

    // IO methods

    // This should do nothing even if it is called. The Message Header is read
    // off a java.io.InputStream (not a CDRInputStream) by IIOPConnection
    // in order to choose the correct CDR Version , msg_type, and msg_size.
    // So, we would never need to read the Message Header off a CDRInputStream.
    public void read(org.omg.CORBA.portable.InputStream istream) {
        /*
        this.magic = istream.read_long();
        this.GIOP_version = (new GIOPVersion()).read(istream);
        this.flags = istream.read_octet();
        this.message_type = istream.read_octet();
        this.message_size = istream.read_ulong();
        */
    }

    public void write(org.omg.CORBA.portable.OutputStream ostream) {
        ostream.write_long(this.magic);
        nullCheck(this.GIOP_version);
        this.GIOP_version.write(ostream);
        ostream.write_octet(this.flags);
        ostream.write_octet(this.message_type);
        ostream.write_ulong(this.message_size);
    }
} // class Message_1_1
