/*
 * @(#)ObjectAdapterFactory.java	1.13 03/12/19
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.corba.se.spi.oa ;

import com.sun.corba.se.spi.oa.ObjectAdapter ;

import com.sun.corba.se.spi.orb.ORB ;

import com.sun.corba.se.spi.ior.ObjectAdapterId ;

public interface ObjectAdapterFactory {
    /** Initialize this object adapter factory instance.
    */
    void init( ORB orb ) ;

    /** Shutdown all object adapters and other state associated
     * with this factory.
     */
    void shutdown( boolean waitForCompletion ) ;

    /** Find the ObjectAdapter instance that corresponds to the
    * given ObjectAdapterId.
    */
    ObjectAdapter find( ObjectAdapterId oaid ) ;

    ORB getORB() ;
}
