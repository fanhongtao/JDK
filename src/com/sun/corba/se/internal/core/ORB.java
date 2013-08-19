/*
 * @(#)ORB.java	1.37 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.corba.se.internal.core;

/**
 *
 */
public abstract class ORB extends com.sun.corba.se.org.omg.CORBA.ORB {
    /**
     * Get an instance of the GIOP client implementation.
     */
    public abstract ClientGIOP getClientGIOP();

    /**
     * Get an instance of the GIOP server implementation.
     */
    public abstract ServerGIOP getServerGIOP();

    /**
     * Return the subcontract registry
     */
    public abstract SubcontractRegistry getSubcontractRegistry();

    /** Obtain the service context registry.
     */
    public abstract ServiceContextRegistry getServiceContextRegistry() ;

    /**
     * Get a new instance of a GIOP input stream.
     */
    public abstract MarshalInputStream newInputStream();

    /**
     * Get a new instance of a GIOP input stream.
     */
    public abstract MarshalInputStream newInputStream(byte[] buffer, int size);

    public abstract MarshalInputStream newInputStream(byte[] buffer, int size, 
						      boolean littleEndian);
    
    /**
     * Get a new instance of a GIOP output stream.
     */
    public abstract MarshalOutputStream newOutputStream();

    /**
     * Get the transient server ID
     */
    public abstract int getTransientServerId();

    /**
     * Return the bootstrap naming port specified in the ORBInitialPort param.
     */
    public abstract int getORBInitialPort();

    /**
     * Return the bootstrap naming host specified in the ORBInitialHost param.
     */
    public abstract String getORBInitialHost();

    /**
     * Get the server host name. Note that this could be set the
     * application programmer using a system property.
     */
    public abstract String getORBServerHost();

    /**
     * Get the server port no. Note that this could be set the
     * application programmer using a system property.
     */
    public abstract int getORBServerPort();

    /**
     * Get the Character Code Set encoding info.
     */
    public abstract CodeSetComponentInfo getCodeSetComponentInfo();

    /** 
     * Return true iff the hostName is the same as the host name
     * on which this ORB is running.
     */
    public abstract boolean isLocalHost( String hostName ) ;

    /**
     * Return true iff the server id in the object key is the same as
     * the corresponding server id in this ORB.
     */
    public abstract boolean isLocalServerId( int subcontractId, int serverId ) ;

    /**
     * Return true if the port number is same as the ORB Server port.
     */
    public abstract boolean isLocalServerPort( int port );

    /**
     * Get the default GIOP version the ORB will support.
     */
    public abstract GIOPVersion getGIOPVersion();
}
