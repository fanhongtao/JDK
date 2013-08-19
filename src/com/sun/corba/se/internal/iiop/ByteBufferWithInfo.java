/*
 * @(#)ByteBufferWithInfo.java	1.8 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.corba.se.internal.iiop;

public class ByteBufferWithInfo
{
    public byte[]  buf;        // Marshal buffer.
    public int     buflen;     // Total length of buffer. // Unnecessary...
    public int     index;      // Current empty position in buffer.
    public int     needed;     // How many more bytes are needed on overflow.
    public boolean fragmented; // Did the overflow operation fragment?
	
    public ByteBufferWithInfo(byte[] buf, int index)
    {
	this.buf = buf;
        if (buf != null)
            this.buflen = buf.length;
	this.index = index;
	this.needed = 0;
        this.fragmented = false;
    }
	
    public ByteBufferWithInfo (byte[] buf)
    {
	this(buf, 0);
    }
	
    public ByteBufferWithInfo (int buflen)
    {
	this(new byte[buflen]);
    }

    // Shallow copy constructor
    public ByteBufferWithInfo (ByteBufferWithInfo bbwi)
    {
        this.buf = bbwi.buf;
        this.buflen = bbwi.buflen;
        this.index = bbwi.index;
        this.needed = bbwi.needed;
        this.fragmented = bbwi.fragmented;
    }

    // So IIOPOutputStream seems more intuitive
    public int getSize() 
    {
        return index;
    }

    public String toString()
    {
        StringBuffer str = new StringBuffer("ByteBufferWithInfo:");

        str.append(" buflen = " + buflen);
        str.append(" index = " + index);
        str.append(" needed = " + needed);
        str.append(" buf = " + (buf == null ? "null" : "not null"));
        str.append(" fragmented = " + fragmented);

        return str.toString();
    }
}
