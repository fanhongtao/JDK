/*
 * @(#)BufferManagerWriteGrow.java	1.10 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.corba.se.internal.iiop;

import java.util.Iterator;
import java.util.NoSuchElementException;
import com.sun.corba.se.internal.orbutil.ORBConstants;
import com.sun.corba.se.internal.iiop.messages.MessageBase;

public class BufferManagerWriteGrow extends BufferManagerWrite
{
    private ByteBufferWithInfo _bbwi; // For sending later.  REVISIT - iterator
    private int initialBufferSize;

    public BufferManagerWriteGrow(int initialBufferSize)
    {
        this.initialBufferSize = initialBufferSize;
    }

    public ByteBufferWithInfo getInitialBuffer (int size)
    {
	return new ByteBufferWithInfo(size);
    }

    public int getInitialBufferSize()
    {
        return initialBufferSize;
    }

    public void overflow (ByteBufferWithInfo bbwi)
    {
	// This code used to live directly in CDROutputStream.grow.

	byte[] old = bbwi.buf;
    	int newLength = old.length * 2;
    	while (bbwi.index + bbwi.needed >= newLength)
    	    newLength = newLength * 2;
    	bbwi.buf = new byte[newLength];
    	System.arraycopy(old, 0, bbwi.buf, 0, old.length);
	bbwi.buflen = bbwi.buf.length;
        
        // Must be false for the grow case
        bbwi.fragmented = false;
    }

    public void sendMessage ()
    {
        IIOPConnection conn = (IIOPConnection)stream.getConnection();

        conn.writeLock();

        //conn.createOutCallDescriptor(stream.getMessage().getRequestId());
        conn.createOutCallDescriptor(MessageBase.getRequestId(stream.getMessage()));

        try {

            conn.sendWithoutLock(stream);

        } finally {

            conn.writeUnlock();
        }
    }

    /*
    public Iterator iterator ()
    {
	return new BufferManagerWriteGrowIterator();
    }

    public class BufferManagerWriteGrowIterator
	implements
	    Iterator
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
		return _bbwi;
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
