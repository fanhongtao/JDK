/*
 * @(#)LogWrapperFactory.java	1.6 10/03/23
 * 
 * Copyright (c) 2006, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.corba.se.spi.logging ;

import java.util.logging.Logger ;

import com.sun.corba.se.spi.logging.LogWrapperBase ;

public interface LogWrapperFactory {
    LogWrapperBase create( Logger logger ) ;
}
