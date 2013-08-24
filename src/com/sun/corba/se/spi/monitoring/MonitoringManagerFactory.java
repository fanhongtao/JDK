/*
 * @(#)MonitoringManagerFactory.java	1.3 08/10/03
 * 
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.sun.corba.se.spi.monitoring;

/**
 * <p>
 *
 * @author Hemanth Puttaswamy
 * </p>
 * <p>
 * MonitoringObjectFactory is used internally by the ORB, It is not for
 * general public use.
 * </p>
 */
public interface MonitoringManagerFactory {
    /** 
     *  A Simple Factory Method to create the Monitored Attribute Info.
     */
    MonitoringManager createMonitoringManager( String nameOfTheRoot,
        String description );

    void remove( String nameOfTheRoot ) ;
}
