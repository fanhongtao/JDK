/*
 * @(#)BufferManagerWriteStream.java	1.13 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.sun.corba.se.internal.iiop;

import java.util.Iterator;
import java.util.NoSuchElementException;
import com.sun.corba.se.internal.orbutil.ORBConstants;
import com.sun.corba.se.internal.iiop.messages.Message;
import com.sun.corba.se.internal.iiop.messages.MessageBase;
import com.sun.corba.se.internal.iiop.messages.FragmentMessage;

/**
 * Initial implementation of the streaming buffer manager.
 */
public class BufferManagerWriteStream extends BufferManagerWrite
{
    private int initialFragmentSize = 0, fragmentCount = 0;

    private ByteBufferWithInfo finalFragment = null;

    public BufferManagerWriteStream(int initialFragmentSize) {
        this.stream = null;
        this.initialFragmentSize = initialFragmentSize;
    }

    public ByteBufferWithInfo getInitialBuffer(int size)
    {
	return new ByteBufferWithInfo(size);
    }

    public int getInitialBufferSize() {
        return initialFragmentSize;
    }

    public void overflow (ByteBufferWithInfo bbwi)
    {
        // Set the fragment's moreFragments field to true
        MessageBase.setFlag(bbwi.buf, Message.MORE_FRAGMENTS_BIT);

        sendFragment(false);

        // Reuse the old buffer

        // REVISIT - need to account for case when needed > available
        // even after fragmenting.  This is the large array case, so
        // the caller should retry when it runs out of space.
        bbwi.index = 0;
        bbwi.buflen = bbwi.buf.length;

        bbwi.fragmented = true;

        // Now we must marshal in the fragment header/GIOP header

        // REVISIT - we can optimize this by not creating the fragment message
        // each time.  

        FragmentMessage header = stream.getMessage().createFragmentMessage();

        // Is this necessary?
        // stream.setMessage(header);

        header.write(stream);
    }

    private void sendFragment(boolean isLastFragment)
    {
        IIOPConnection conn = (IIOPConnection) stream.getConnection();

        conn.writeLock();

        if (this.fragmentCount == 0) {
	    int requestID = MessageBase.getRequestId(stream.getMessage());

            // create OutCallDesc once before sending the first fragment
            conn.createOutCallDescriptor(requestID);

	    // Also, remember fragments in progress in case of errors.
	    conn.createIdToFragmentedOutputStreamEntry(requestID, stream);
        }

        try {
            // Send the fragment
            conn.sendWithoutLock(stream);
        } finally {

	    if (isLastFragment) {
		int requestID = MessageBase.getRequestId(stream.getMessage());
		conn.removeIdToFragmentedOutputStreamEntry(requestID);
	    }

            conn.writeUnlock();

	    // REVISIT: this should go after sendWithoutLock
            this.fragmentCount++; // keeps count of # of fragments sent

        }

    }

    // Sends the last fragment
    public void sendMessage ()
    {
        sendFragment(true);
    }

    /*
    private Iterator iterator ()
    {
	return new BufferManagerWriteStreamIterator();
    }

    // Only handles the very last fragment
    private class BufferManagerWriteStreamIterator implements Iterator
    {
	private boolean _hasNext = true;

	public boolean hasNext ()
	{
	    return _hasNext;
	}

	public Object next ()
	{
	    if (_hasNext) {
		_hasNext = false;
		return finalFragment;
	    }
	    throw new NoSuchElementException();
	}

	public void remove ()
	{
	    throw new UnsupportedOperationException();
	}
    }
    */
}
