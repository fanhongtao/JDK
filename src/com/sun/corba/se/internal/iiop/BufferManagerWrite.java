/*
 * @(#)BufferManagerWrite.java	1.8 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.corba.se.internal.iiop;

import com.sun.corba.se.internal.iiop.ByteBufferWithInfo;
import java.util.Iterator;
import com.sun.corba.se.internal.iiop.CDROutputStream;
import com.sun.corba.se.internal.iiop.IIOPOutputStream;
import com.sun.corba.se.internal.iiop.IIOPInputStream;

public abstract class BufferManagerWrite
{
    /**
     * Case: Called from CDROutputStream constructor
     *       before starting to marshal.
     *
     * Does:
     *
     *  bbwi = new ByteBufferWithInfo(size);
     *
     *  Fill in initial message headers and adjust bbwi.* appropriately
     *
     *  Returns bbwi
     */

    // This has taken on a different meaning -- simply allocating the
    // first bbwi.  It doesn't marshal in the header.
    public abstract ByteBufferWithInfo getInitialBuffer (int size);

    public abstract int getInitialBufferSize();

    /*
     * Case: Called from CDROutputStream.grow (instead of current default).
     *
     * bbwi.buf contains a byte array which needs to grow by bbwi.needed bytes.
     * 
     * This can be handled in several ways:
     *
     * 1. Resize the bbwi.buf like the current implementation of
     *    CDROutputStream.grow.
     *
     * 2. Collect the buffer for a later send:
     *    this.bufQ.put(bbwi);
     *    return new ByteBufferWithInfo(bbwi.length);
     *
     * 3. Send buffer as fragment:
     *    Backpatch fragment size field in bbwi.buf.
     *    Set more fragments bit in bbwi.buf.
     *    this.connection.send(bbwi);
     *    return reinitialized bbwi.buf with fragment header
     *
     * All cases should adjust the returned bbwi.* appropriately.
     *
     * Should set the bbwi.fragmented flag to true only in cases 2 and 3.
     */

    public abstract void overflow (ByteBufferWithInfo bbwi);



    /**
     * Called after Stub._invoke (i.e., before complete message has been sent).
     *
     * IIOPOutputStream.writeTo called from IIOPOutputStream.invoke 
     *
     * Case: overflow was never called (bbwi.buf contains complete message).
     *       Backpatch size field.
     *       If growing or collecting:
     *          this.bufQ.put(bbwi).
     *          this.bufQ.iterate // However, see comment in getBufferQ
     *             this.connection.send(fragment)
     *       If streaming:
     *          this.connection.send(bbwi).
     *
     * Case: overflow was called N times (bbwi.buf contains last buffer).
     *       If growing or collecting:
     *          this.bufQ.put(bbwi).
     *          backpatch size field in first buffer.
     *          this.bufQ.iterate // However, see comment in getBufferQ
     *             this.connection.send(fragment)
     *       If streaming:
     *          backpatch fragment size field in bbwi.buf.
     *          Set no more fragments bit.
     *          this.connection.send(bbwi).
     */


    public abstract void sendMessage ();
    


    /**
     * Case: Access bufferQ to no fragments, grow, or collect.
     *
     *
     * IIOPOutputStream.writeTo needs buffer Q to write its
     * contents on the connection.
     *
     * Does:
     *
     *  Abstracts bufferQ.
     */

    // public Iterator iterator ();

    // A reference to the IIOPOutputStream will be required when
    // sending fragments.
    public void setIIOPOutputStream(IIOPOutputStream stream) {
        this.stream = stream;
    }

    protected IIOPOutputStream stream;
}

