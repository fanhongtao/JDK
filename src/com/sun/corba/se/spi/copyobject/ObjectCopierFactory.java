/*
 * @(#)ObjectCopierFactory.java	1.5 05/11/17
 *
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.corba.se.spi.copyobject ;

import com.sun.corba.se.spi.orb.ORB ;

/** ObjectCopier factory interface used for registration.
 */
public interface ObjectCopierFactory {
    /** Create a new instance of an ObjectCopier.
    */
    ObjectCopier make() ;
}
