/*
 * @(#)ReferenceObjectCopierImpl.java	1.7 03/12/19
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
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

