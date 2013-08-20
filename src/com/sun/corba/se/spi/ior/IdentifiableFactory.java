/*
 * @(#)IdentifiableFactory.java	1.14 03/12/19
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.corba.se.spi.ior ;

import org.omg.CORBA_2_3.portable.InputStream ;

import com.sun.corba.se.spi.ior.Identifiable ;

/** Factory interface for creating Identifiables.
 */
public interface IdentifiableFactory {
    /** Return the id of this factory, which is the id of the result 
     * of any create call.
     */
    public int getId() ;

    /** Construct the appropriate Identifiable object with the 
     * given id from the InputStream is.  
     */
    public Identifiable create( InputStream in ) ;
}
