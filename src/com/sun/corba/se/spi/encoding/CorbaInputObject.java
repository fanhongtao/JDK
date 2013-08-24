/*
 * @(#)CorbaInputObject.java	1.7 05/11/17
 * 
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.corba.se.spi.encoding ;

import com.sun.corba.se.impl.encoding.CDRInputStream ;
import com.sun.corba.se.pept.encoding.InputObject ;

public abstract class CorbaInputObject 
    extends CDRInputStream
    implements InputObject
{
}
