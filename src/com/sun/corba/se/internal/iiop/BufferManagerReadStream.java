/*
 * @(#)BufferManagerReadStream.java	1.11 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.sun.corba.se.internal.iiop;

import org.omg.CORBA.MARSHAL;
import org.omg.CORBA.CompletionStatus;
import com.sun.corba.se.internal.orbutil.MinorCodes;
import com.sun.corba.se.internal.iiop.messages.FragmentMessage;
import com.sun.corba.se.internal.iiop.messages.Message;
import java.util.*;

public class BufferManagerReadStream
    implements BufferManagerRead, MarkAndResetHandler
{
    private boolean receivedCancel = false;
    private int cancelReqId = 0;

    // We should convert endOfStream to a final static dummy end node
    private boolean endOfStream = true;
    private BufferQueue fragmentQueue = new BufferQueue();

    public void cancelProcessing(int requestId) {
        synchronized(fragmentQueue) {
            receivedCancel = true;
            cancelReqId = requestId;
            fragmentQueue.notify();
        }
    }

    public void processFragment (byte[] buf, FragmentMessage msg)
    {
        ByteBufferWithInfo bbwi = new ByteBufferWithInfo(buf, msg.getHeaderLength());

        synchronized (fragmentQueue) {
            fragmentQueue.enqueue(bbwi);
            endOfStream = !msg.moreFragmentsToFollow();
            fragmentQueue.notify();
        }
    }

    public ByteBufferWithInfo underflow (ByteBufferWithInfo bbwi)
    {
        synchronized (fragmentQueue) {

            if (receivedCancel) {
                throw new RequestCanceledException(cancelReqId);
            }

            while (fragmentQueue.size() == 0) {

                if (endOfStream) {
                    throw new MARSHAL("Unmarshaller requested more data after end of stream",
                                      MinorCodes.END_OF_STREAM,
                                      CompletionStatus.COMPLETED_NO);
                }

                try {
                    fragmentQueue.wait();
                } catch (InterruptedException e) {}

                if (receivedCancel) {
                    throw new RequestCanceledException(cancelReqId);
                }
            }

            ByteBufferWithInfo result = fragmentQueue.dequeue();
            result.fragmented = true;

            return result;
        }
    }

    public void init(Message msg) {
        if (msg != null)
            endOfStream = !msg.moreFragmentsToFollow();
    }

    // Mark and reset handler ----------------------------------------

    private boolean markEngaged = false;

    // List of fragment ByteBufferWithInfos received since
    // the mark was engaged.
    private LinkedList fragmentStack = null;
    private RestorableInputStream inputStream = null;

    // Original state of the stream
    private Object streamMemento = null;

    public void mark(RestorableInputStream inputStream)
    {
        this.inputStream = inputStream;
        markEngaged = true;

        // Get the magic Object that the stream will use to
        // reconstruct it's state when reset is called
        streamMemento = inputStream.createStreamMemento();

        if (fragmentStack != null) {
            fragmentStack.clear();
        }
    }

    // Collects fragments received since the mark was engaged.
    public void fragmentationOccured(ByteBufferWithInfo newFragment)
    {
        if (!markEngaged)
            return;

        if (fragmentStack == null)
            fragmentStack = new LinkedList();

        fragmentStack.addFirst(new ByteBufferWithInfo(newFragment));
    }

    public void reset()
    {
        if (!markEngaged) {
            // REVISIT - call to reset without call to mark
            return;
        }

        markEngaged = false;

        // If we actually did peek across fragments, we need
        // to push those fragments onto the front of the
        // buffer queue.
        if (fragmentStack != null && fragmentStack.size() != 0) {
            ListIterator iter = fragmentStack.listIterator();

            synchronized(fragmentQueue) {
                while (iter.hasNext()) {
                    fragmentQueue.push((ByteBufferWithInfo)iter.next());
                }
            }

            fragmentStack.clear();
        }

        // Give the stream the magic Object to restore
        // it's state.
        inputStream.restoreInternalState(streamMemento);
    }

    public MarkAndResetHandler getMarkAndResetHandler() {
        return this;
    }
}
