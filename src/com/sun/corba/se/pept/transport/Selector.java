/*
 * @(#)Selector.java	1.5 04/04/07
 * 
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.corba.se.pept.transport;

/**
 * @author Harold Carr
 */
public interface Selector
{
    public void setTimeout(long timeout);
    public long getTimeout();
    public void registerInterestOps(EventHandler eventHandler);
    public void registerForEvent(EventHandler eventHander);
    public void unregisterForEvent(EventHandler eventHandler);
    public void close();
}

// End of file.








