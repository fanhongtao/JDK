/*
 * @(#)OutboundConnectionCache.java	1.2 03/12/19
 * 
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.corba.se.pept.transport;

/**
 * @author Harold Carr
 */
public interface OutboundConnectionCache 
    extends ConnectionCache
{
    public Connection get(ContactInfo contactInfo);

    public void put(ContactInfo contactInfo, Connection connection);

    public void remove(ContactInfo contactInfo);
}

// End of file.
