/*
 * @(#)POAId.java	1.3 01/12/04
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

// @(#)POAId.java	1.3 01/12/04

package com.sun.corba.se.internal.ior ;

import java.util.Iterator ;
import org.omg.CORBA_2_3.portable.OutputStream ;

public interface POAId {
    public int getNumLevels() ;

    public Iterator iterator() ;

    public void write( OutputStream os ) ;
}
