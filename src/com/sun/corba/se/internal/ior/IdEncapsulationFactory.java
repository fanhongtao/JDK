/*
 * @(#)IdEncapsulationFactory.java	1.12 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.corba.se.internal.ior ;

import org.omg.CORBA_2_3.portable.InputStream ;

import com.sun.corba.se.internal.ior.IdEncapsulation ;

public interface IdEncapsulationFactory {
    /** Construct the appropriate IdEncapsulation object with the 
    * given id from the InputStream is.
    */
    public IdEncapsulation create( int id, InputStream in ) ;
}
