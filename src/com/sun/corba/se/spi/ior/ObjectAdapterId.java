/*
 * @(#)ObjectAdapterId.java	1.5 03/12/19 
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.corba.se.spi.ior ;

import java.util.Iterator ;

/** This is the object adapter ID for an object adapter.
* Typically this is the path of strings starting from the
* Root POA to get to a POA, but other implementations are possible.
*/
public interface ObjectAdapterId extends Writeable {
    /** Return the number of elements in the adapter ID.
    */
    int getNumLevels() ;

    /** Return an iterator that iterates over the components 
    * of this adapter ID.  Each element is returned as a String.
    */
    Iterator iterator() ;

    /** Get the adapter name simply as an array of strings.
    */
    String[] getAdapterName() ;
}
