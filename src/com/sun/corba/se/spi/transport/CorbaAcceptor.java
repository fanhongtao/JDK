/*
 * @(#)CorbaAcceptor.java	1.12 03/12/19
 * 
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.corba.se.spi.transport;

import com.sun.corba.se.pept.transport.Acceptor;

import com.sun.corba.se.spi.ior.IORTemplate;

// REVISIT - impl/poa specific:
import com.sun.corba.se.impl.oa.poa.Policies;

/**
 * @author Harold Carr
 */
public interface CorbaAcceptor
    extends
	Acceptor
{
    public String getObjectAdapterId();
    public String getObjectAdapterManagerId();
    public void addToIORTemplate(IORTemplate iorTemplate, Policies policies,
				 String codebase);
    public String getMonitoringName();
}

// End of file.
