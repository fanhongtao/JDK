/*
 * @(#)LogWrapperFactory.java	1.4 03/12/19
 * 
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.corba.se.spi.logging ;

import java.util.logging.Logger ;

import com.sun.corba.se.spi.logging.LogWrapperBase ;

public interface LogWrapperFactory {
    LogWrapperBase create( Logger logger ) ;
}
