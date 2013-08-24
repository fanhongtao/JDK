/*
 * @(#)Writeable.java	1.16 05/11/17
 *
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.corba.se.spi.ior;

import org.omg.CORBA_2_3.portable.OutputStream ;

/** This interface represents an entity that can be written to an OutputStream.
 * @author Ken Cavanaugh
 */
public interface Writeable 
{
    /** Write this object directly to the output stream.
     */
    void write(OutputStream arg0);
}
