/*
 * @(#)IdEncapsulationFactory.java	1.11 01/12/04
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
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
