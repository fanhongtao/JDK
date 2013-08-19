/*
 * @(#)BufferManagerWriteCollect.java	1.12 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.sun.corba.se.internal.iiop;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.LinkedList;

import com.sun.corba.se.internal.orbutil.ORBConstants;
import com.sun.corba.se.internal.iiop.messages.Message;
import com.sun.corba.se.internal.iiop.messages.MessageBase;
import com.sun.corba.se.internal.iiop.messages.FragmentMessage;

/**
 * Initial implementation of the collect buffer manager.
 */
public class BufferManagerWriteCollect extends BufferManagerWrite
{
    private BufferQueue queue = new BufferQueue();

    private int initialFragmentSize;

    public BufferManagerWriteCollect(int initialFragmentSize) {
        this.stream = null;
        this.initialFragmentSize = initialFragmentSize;
    }

    public ByteBufferWithInfo getInitialBuffer (int size)
    {
	return new ByteBufferWithInfo(size);
    }

    public int getInitialBufferSize () {
        return initialFragmentSize;
    }

    // Set the fragment's "more fragments" bit to true, put it in the
    // queue, and allocate a new bbwi.
    public void overflow (ByteBufferWithInfo bbwi)
    {
        // Set the fragment's moreFragments field to true
        MessageBase.setFlag(bbwi.buf, Message.MORE_FRAGMENTS_BIT);

        // Enqueue the previous fragment
        queue.enqueue(bbwi);

        // Create a new bbwi
        ByteBufferWithInfo newBbwi = new ByteBufferWithInfo(getInitialBufferSize());
        newBbwi.fragmented = true;

        stream.setByteBufferWithInfo(newBbwi);

        // Now we must marshal in the fragment header/GIOP header

        // REVISIT - we can optimize this by not creating the fragment message
        // each time.  

        FragmentMessage header = stream.getMessage().createFragmentMessage();

        header.write(stream);
    }

    // Send all fragments
    public void sendMessage ()
    {
        // Enqueue the last fragment
        queue.enqueue(stream.getByteBufferWithInfo());

        Iterator bufs = iterator();

        IIOPConnection conn = (IIOPConnection)stream.getConnection();

        // With the collect strategy, we must lock the connection
        // while fragments are being sent.  This is so that there are
        // no interleved fragments in GIOP 1.1.
        //
        // Note that this thread must not call writeLock again in any
        // of its send methods!
        conn.writeLock();

        //conn.createOutCallDescriptor(stream.getMessage().getRequestId());
        conn.createOutCallDescriptor(MessageBase.getRequestId(stream.getMessage()));

        try {

            while (bufs.hasNext()) {
                
                stream.setByteBufferWithInfo((ByteBufferWithInfo)bufs.next());
                
                conn.sendWithoutLock(stream);
            }
            
        } finally {

            conn.writeUnlock();
        }
    }

    private Iterator iterator ()
    {
	return new BufferManagerWriteCollectIterator();
    }

    private class BufferManagerWriteCollectIterator implements Iterator
    {
	public boolean hasNext ()
	{
            return queue.size() != 0;
	}

	public Object next ()
	{
            return queue.dequeue();
        }

	public void remove ()
	{
	    throw new UnsupportedOperationException();
	}
    }
}
