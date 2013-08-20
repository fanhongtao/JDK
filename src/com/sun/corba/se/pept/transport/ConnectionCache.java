/*
 * @(#)ConnectionCache.java	1.12 03/12/19
 * 
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.corba.se.pept.transport;

/**
 * @author Harold Carr
 */
public interface ConnectionCache 
{
    public String getCacheType();

    public void stampTime(Connection connection);

    public long numberOfConnections();

    public long numberOfIdleConnections();

    public long numberOfBusyConnections();

    public boolean reclaim();
}

// End of file.
