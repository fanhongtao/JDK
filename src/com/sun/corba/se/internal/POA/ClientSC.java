/*
 * @(#)ClientSC.java	1.8 01/12/03
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
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
