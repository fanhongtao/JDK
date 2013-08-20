/*
 * @(#)MonitoredObjectFactoryImpl.java	1.2 03/12/19
 * 
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
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

