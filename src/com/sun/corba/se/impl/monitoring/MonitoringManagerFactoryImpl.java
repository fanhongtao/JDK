/*
 * @(#)MonitoringManagerFactoryImpl.java	1.4 05/11/17
 * 
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.corba.se.impl.monitoring;

import java.util.HashMap;
import com.sun.corba.se.spi.monitoring.MonitoringManagerFactory;
import com.sun.corba.se.spi.monitoring.MonitoringManager;

public class MonitoringManagerFactoryImpl implements MonitoringManagerFactory {

    private HashMap monitoringManagerTable = new HashMap();

    public synchronized MonitoringManager createMonitoringManager( 
        String nameOfTheRoot, String description ) 
    {
	MonitoringManagerImpl m = null;
	m = (MonitoringManagerImpl)monitoringManagerTable.get(nameOfTheRoot);
	if (m == null) {
	    m = new MonitoringManagerImpl( nameOfTheRoot, description );
	    monitoringManagerTable.put(nameOfTheRoot, m);
	}
        return m;
    }
}

