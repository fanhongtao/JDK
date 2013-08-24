/*
 * @(#)CorbaTransportManager.java	1.8 05/11/17
 *
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.corba.se.spi.transport;

import java.util.Collection;

import com.sun.corba.se.pept.transport.TransportManager;
import com.sun.corba.se.spi.ior.IORTemplate;
import com.sun.corba.se.spi.ior.ObjectAdapterId;

// REVISIT - impl/poa specific:
import com.sun.corba.se.impl.oa.poa.Policies;

/**
 * @author Harold Carr
 */
public interface CorbaTransportManager
    extends
	TransportManager
{
    public static final String SOCKET_OR_CHANNEL_CONNECTION_CACHE =
	"SocketOrChannelConnectionCache";

    public Collection getAcceptors(String objectAdapterManagerId,
				   ObjectAdapterId objectAdapterId);

    // REVISIT - POA specific policies
    public void addToIORTemplate(IORTemplate iorTemplate, 
				 Policies policies,
				 String codebase,
				 String objectAdapterManagerId,
				 ObjectAdapterId objectAdapterId);
}
    
// End of file.
