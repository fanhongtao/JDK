/*
 * @(#)Message_1_2.java	1.4 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.sun.corba.se.internal.iiop.messages;

import com.sun.corba.se.internal.core.GIOPVersion;

public class Message_1_2 extends Message_1_1
{
    protected int request_id = (int) 0;

    Message_1_2() {}
    
    Message_1_2(int _magic, GIOPVersion _GIOP_version, byte _flags,
            byte _message_type, int _message_size) {

        super(_magic,
              _GIOP_version,
              _flags,
              _message_type,
              _message_size);
    }    

    /**
     * The byte buffer is presumed to have contents of the message already
     * read in.  It must have 12 bytes of space at the beginning for the GIOP header,
     * but the header doesn't have to be copied in.
     */
    public void unmarshalRequestID(byte buf[]) {
        int b1, b2, b3, b4;

        if (!isLittleEndian()) {
            b1 = (buf[GIOPMessageHeaderLength + 0] << 24) & 0xFF000000;
            b2 = (buf[GIOPMessageHeaderLength + 1] << 16) & 0x00FF0000;
            b3 = (buf[GIOPMessageHeaderLength + 2] << 8)  & 0x0000FF00;
            b4 = (buf[GIOPMessageHeaderLength + 3] << 0)  & 0x000000FF;
        } else {
            b1 = (buf[GIOPMessageHeaderLength + 3] << 24) & 0xFF000000;
            b2 = (buf[GIOPMessageHeaderLength + 2] << 16) & 0x00FF0000;
            b3 = (buf[GIOPMessageHeaderLength + 1] << 8)  & 0x0000FF00;
            b4 = (buf[GIOPMessageHeaderLength + 0] << 0)  & 0x000000FF;
        }

        this.request_id = (b1 | b2 | b3 | b4);
    }
}

