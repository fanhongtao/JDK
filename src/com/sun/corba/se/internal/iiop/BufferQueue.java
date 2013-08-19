/*
 * @(#)BufferQueue.java	1.5 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.sun.corba.se.internal.iiop;

import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.LinkedList;

/**
 * Simple unsynchronized queue implementation for ByteBufferWithInfos.
 */
class BufferQueue
{
    private LinkedList list = new LinkedList();
    
    public void enqueue(ByteBufferWithInfo item)
    {
        list.addLast(item);
    }
    
    public ByteBufferWithInfo dequeue() throws NoSuchElementException
    {
        return (ByteBufferWithInfo)list.removeFirst();
    }
    
    public int size()
    {
        return list.size();
    }

    // Adds the given ByteBufferWithInfo to the front
    // of the queue.
    public void push(ByteBufferWithInfo item)
    {
        list.addFirst(item);
    }
}
