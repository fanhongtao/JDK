/*
 * @(#)POAId.java	1.4 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

// @(#)POAId.java	1.4 03/01/23

package com.sun.corba.se.internal.ior ;

import java.util.Iterator ;
import org.omg.CORBA_2_3.portable.OutputStream ;

public interface POAId {
    public int getNumLevels() ;

    public Iterator iterator() ;

    public void write( OutputStream os ) ;
}
