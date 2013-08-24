/*
 * @(#)MonitoredObjectFactoryImpl.java	1.3 05/11/17
 * 
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.corba.se.impl.monitoring;

import com.sun.corba.se.spi.monitoring.MonitoredObjectFactory;
import com.sun.corba.se.spi.monitoring.MonitoredObject;

public class MonitoredObjectFactoryImpl implements MonitoredObjectFactory {

    public MonitoredObject createMonitoredObject( String name, 
        String description ) 
    {
        return new MonitoredObjectImpl( name, description );
    }
}

