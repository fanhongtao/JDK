/*
 * @(#)BufferManagerReadGrow.java	1.18 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.corba.se.internal.iiop;

import com.sun.corba.se.internal.orbutil.MinorCodes;
import org.omg.CORBA.MARSHAL;
import org.omg.CORBA.CompletionStatus;
import com.sun.corba.se.internal.iiop.messages.FragmentMessage;
import com.sun.corba.se.internal.iiop.messages.Message;

public class BufferManagerReadGrow
    implements BufferManagerRead, MarkAndResetHandler
{
    public void processFragment (byte[] buf, FragmentMessage header) {}

    public void init(Message msg) {}

    public ByteBufferWithInfo underflow (ByteBufferWithInfo bbwi)
    {
	throw new MARSHAL("underflow called with grow strategy",
                          MinorCodes.UNEXPECTED_EOF,
			  CompletionStatus.COMPLETED_NO);
    }

    public void cancelProcessing(int requestId) {}
    
    // Mark and reset handler -------------------------

    private Object streamMemento;
    private RestorableInputStream inputStream;
    private boolean markEngaged = false;

    public MarkAndResetHandler getMarkAndResetHandler() {
        return this;
    }

    public void mark(RestorableInputStream is) {
        markEngaged = true;
        inputStream = is;
        streamMemento = inputStream.createStreamMemento();
    }

    // This will never happen
    public void fragmentationOccured(ByteBufferWithInfo newFragment) {}

    public void reset() {

        if (!markEngaged)
            return;

        markEngaged = false;
        inputStream.restoreInternalState(streamMemento);
        streamMemento = null;
    }
}
