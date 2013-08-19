/*
 * @(#)ORBVersion.java	1.8 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.corba.se.internal.core ;

import org.omg.CORBA.portable.OutputStream ;

public interface ORBVersion extends Comparable {

    /**
     * ORB from another vendor.
     */
    byte FOREIGN = 0 ;

    /**
     * JDK 1.3.0 or earlier.
     */
    byte OLD = 1 ;

    /**
     * JDK 1.3.1 FCS.
     */
    byte NEW = 2 ;

    /**
     * JDK 1.3.1_01 patch.
     */
    byte JDK1_3_1_01 = 3;

    /**
     * JDK 1.4.0 or later.
     */
    byte NEWER = 10 ;

    byte getORBType() ;

    void write( OutputStream os ) ;
}
