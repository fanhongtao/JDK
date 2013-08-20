/*
 * @(#)ListenerThread.java	1.2 03/12/19
 * 
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.corba.se.pept.transport;

/**
 * @author Harold Carr
 */
public interface ListenerThread 
{
    public Acceptor getAcceptor();
    public void close();
}

// End of file.








