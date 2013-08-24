/*
 * @(#)RequestDispatcherRegistry.java	1.10 05/11/17
 *
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.corba.se.spi.protocol;

import java.util.Set;

import com.sun.corba.se.pept.protocol.ClientRequestDispatcher ;
import com.sun.corba.se.spi.protocol.CorbaServerRequestDispatcher ;
import com.sun.corba.se.spi.protocol.LocalClientRequestDispatcherFactory ;

import com.sun.corba.se.spi.oa.ObjectAdapterFactory ;

/**
 * This is a registry of all subcontract ID dependent objects.  This includes:
 * LocalClientRequestDispatcherFactory, ClientRequestDispatcher, ServerRequestDispatcher, and 
 * ObjectAdapterFactory. 
 * XXX Should the registerXXX methods take an scid or not?  I think we
 * want to do this so that the same instance can be shared across multiple
 * scids (and this is already true for ObjectAdapterFactory and LocalClientRequestDispatcherFactory), 
 * but this will require some changes for ClientRequestDispatcher and ServerRequestDispatcher.
 */
public interface RequestDispatcherRegistry {
    // XXX needs javadocs!

    void registerClientRequestDispatcher( ClientRequestDispatcher csc, int scid) ;

    ClientRequestDispatcher getClientRequestDispatcher( int scid ) ;

    void registerLocalClientRequestDispatcherFactory( LocalClientRequestDispatcherFactory csc, int scid) ;

    LocalClientRequestDispatcherFactory getLocalClientRequestDispatcherFactory( int scid ) ;

    void registerServerRequestDispatcher( CorbaServerRequestDispatcher ssc, int scid) ;

    CorbaServerRequestDispatcher getServerRequestDispatcher(int scid) ;

    void registerServerRequestDispatcher( CorbaServerRequestDispatcher ssc, String name ) ;

    CorbaServerRequestDispatcher getServerRequestDispatcher( String name ) ;

    void registerObjectAdapterFactory( ObjectAdapterFactory oaf, int scid) ;

    ObjectAdapterFactory getObjectAdapterFactory( int scid ) ;

    Set getObjectAdapterFactories() ;
}
