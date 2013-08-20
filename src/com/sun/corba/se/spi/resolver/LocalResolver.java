/*
 * @(#)LocalResolver.java	1.5 04/06/21
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.corba.se.spi.resolver ;

import com.sun.corba.se.spi.orbutil.closure.Closure ;

/** A LocalResolver is a Resolver that allows registration of (name, CORBA object)
 * bindings.
 */
public interface LocalResolver extends Resolver {
    /** Register the Closure with the given name.
     * The Closure must evaluate to an org.omg.CORBA.Object.
     */
    void register( String name, Closure closure ) ;
}
