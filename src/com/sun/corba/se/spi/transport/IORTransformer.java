/*
 * @(#)IORTransformer.java	1.7 03/12/19
 * 
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.corba.se.spi.transport ;

import com.sun.corba.se.spi.ior.IOR ;
import com.sun.corba.se.spi.encoding.CorbaInputObject ;
import com.sun.corba.se.spi.encoding.CorbaOutputObject ;

/** Interface that provides operations to transorm an IOR
 * between its programmatic representation and a representation
 * in an Input or Output object.
 */
public interface IORTransformer {
    IOR unmarshal( CorbaInputObject io ) ;

    void marshal( CorbaOutputObject oo, IOR ior ) ;
}
