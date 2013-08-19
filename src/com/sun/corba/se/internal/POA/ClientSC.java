/*
 * @(#)ClientSC.java	1.9 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.corba.se.internal.POA;

import com.sun.corba.se.internal.ior.POAId ;

public interface ClientSC 
{
    /** Return the objectid of the target POA objref associated with this
     *	Delegate. 
     */
    public byte[] getObjectId();
 
    /** Return the objectid of the target POA objref associated with this
     *	Delegate. 
     */
    public POAId getPOAId();
}
