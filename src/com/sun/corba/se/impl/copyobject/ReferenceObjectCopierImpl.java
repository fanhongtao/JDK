/*
 * @(#)ReferenceObjectCopierImpl.java	1.8 05/11/17
 *
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.corba.se.impl.copyobject ;

import com.sun.corba.se.spi.copyobject.ObjectCopier ;

public class ReferenceObjectCopierImpl implements ObjectCopier
{
    public Object copy( Object obj )
    {
	return obj ;
    }
}

