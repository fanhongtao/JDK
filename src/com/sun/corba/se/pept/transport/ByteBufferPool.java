/*
 * @(#)ByteBufferPool.java	1.5 05/11/17
 * 
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.corba.se.pept.transport;

import java.nio.ByteBuffer;

/**
 * @author Charlie Hunt
 */
public interface ByteBufferPool
{
    public ByteBuffer getByteBuffer(int theSize);
    public void releaseByteBuffer(ByteBuffer thebb);
    public int activeCount();
}

// End of file.
