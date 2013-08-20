/*
 * @(#)Resolver.java	1.4 03/12/19
 * 
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.corba.se.spi.resolver ;

/** Resolver defines the operations needed to support ORB operations for 
 * resolve_initial_references and list_initial_services.
 */
public interface Resolver {
    /** Look up the name using this resolver and return the CORBA object
     * reference bound to this name, if any.  Returns null if no object
     * is bound to the name.
     */
    org.omg.CORBA.Object resolve( String name ) ;

    /** Return the entire collection of names that are currently bound 
     * by this resolver.  Resulting collection contains only strings for
     * which resolve does not return null.  Some resolvers may not support
     * this method, in which case they return an empty set.
     */
    java.util.Set list() ;
}
