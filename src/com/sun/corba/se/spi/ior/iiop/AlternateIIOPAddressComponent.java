/*
 * @(#)AlternateIIOPAddressComponent.java	1.2 04/05/24
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.corba.se.spi.ior.iiop;

import com.sun.corba.se.spi.ior.iiop.IIOPAddress ;
import com.sun.corba.se.spi.ior.TaggedComponent ;

/**
 * @author Ken Cavanaugh
 */
public interface AlternateIIOPAddressComponent extends TaggedComponent
{
    public IIOPAddress getAddress() ;
}
