/*
 * @(#)Message.java	1.8 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.corba.se.internal.iiop.messages;

import com.sun.corba.se.internal.core.GIOPVersion;

/**
 * This is the base interface for different message type interfaces.
 *
 * @author Ram Jeyaraman 05/14/2000
 * @version 1.0
 */

public interface Message {

    // Generic constants

    int defaultBufferSize = 1024;
    int GIOPBigEndian = 0;
    int GIOPLittleEndian = 1;
    int GIOPBigMagic =    0x47494F50;
    int GIOPLittleMagic = 0x504F4947;
    int GIOPMessageHeaderLength = 12;

    // Other useful constants

    byte LITTLE_ENDIAN_BIT = 0x01;
    byte MORE_FRAGMENTS_BIT = 0x02;
    byte FLAG_NO_FRAG_BIG_ENDIAN = 0x00;

    // Message types

    byte GIOPRequest = 0;
    byte GIOPReply = 1;
    byte GIOPCancelRequest = 2;
    byte GIOPLocateRequest = 3;
    byte GIOPLocateReply = 4;
    byte GIOPCloseConnection = 5;
    byte GIOPMessageError = 6;
    byte GIOPFragment = 7; // 1.1 & 1.2:

    // Accessor methods

    GIOPVersion getGIOPVersion();
    boolean isLittleEndian();
    boolean moreFragmentsToFollow();
    int getType();
    int getSize();

    // Mutator methods

    void read(org.omg.CORBA.portable.InputStream istream);
    void write(org.omg.CORBA.portable.OutputStream ostream);
    void setSize(byte[] buf, int size);
    FragmentMessage createFragmentMessage();

}
