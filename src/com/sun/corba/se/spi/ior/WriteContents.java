/*
 * @(#)WriteContents.java	1.3 03/12/19
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.corba.se.spi.ior;

import org.omg.CORBA_2_3.portable.OutputStream ;

public interface WriteContents {
    void writeContents( OutputStream os ) ;
}
