/*
 * @(#)Writeable.java	1.13 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

//Source file: J:/ws/serveractivation/src/share/classes/com.sun.corba.se.internal.ior/Writeable.java

package com.sun.corba.se.internal.ior;

import org.omg.CORBA_2_3.portable.OutputStream ;

/**
 * @author 
 */
interface Writeable 
{
    
    /**
     * @param arg0
     * @return void
     * @exception 
     * @author 
     * @roseuid 3910985202DB
     */
    public abstract void write(OutputStream arg0);
}
