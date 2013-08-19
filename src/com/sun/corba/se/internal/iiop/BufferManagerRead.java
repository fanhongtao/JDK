/*
 * @(#)BufferManagerRead.java	1.13 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.corba.se.internal.iiop;

import com.sun.corba.se.internal.iiop.ByteBufferWithInfo;
import com.sun.corba.se.internal.iiop.IIOPInputStream;
import com.sun.corba.se.internal.iiop.Connection;
import com.sun.corba.se.internal.iiop.messages.FragmentMessage;
import com.sun.corba.se.internal.iiop.messages.Message;

public interface BufferManagerRead
{
    /**
     * Case: Called from ReaderThread on complete message or fragments.
     *       The given buf may be entire message or a fragment.
     *
     *  The ReaderThread finds the ReadBufferManager instance either in
     *  in a fragment map (when collecting - GIOP 1.2 phase 1) or
     *  in an active server requests map (when streaming - GIOP 1.2 phase 2).
     *
     *  As a model for implementation see IIOPInputStream's 
     *  constructor of the same name. There are going to be some variations.
     *
     */

    public void processFragment (byte[] buf, FragmentMessage header);



    /**
     * Case: called from CDRInputStream constructor before unmarshaling.
     * 
     * Does:
     *
     *  this.bufQ.get()
     *
     *  If streaming then sync on bufQ and wait if empty.
     */


    /**
     * Case: called from CDRInputStream.grow.
     * 
     * Does:
     *
     *  this.bufQ.get()
     *
     *  If streaming then sync on bufQ and wait if empty.
     */

    public ByteBufferWithInfo underflow (ByteBufferWithInfo bbwi);

    /**
     * Called once after creating this buffer manager and before
     * it begins processing.
     */
    public void init(Message header);

    /**
     * Returns the mark/reset handler for this stream.
     */
    public MarkAndResetHandler getMarkAndResetHandler();

    /*
     * Signals that the processing be cancelled.
     */
    public void cancelProcessing(int requestId);
}

