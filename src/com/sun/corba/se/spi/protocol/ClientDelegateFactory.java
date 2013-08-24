/*
 * @(#)ClientDelegateFactory.java	1.9 05/11/17
 * 
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.corba.se.spi.protocol ;

import com.sun.corba.se.spi.transport.CorbaContactInfoList ;

import com.sun.corba.se.spi.protocol.CorbaClientDelegate ;

/** Interface used to create a ClientDelegate from a ContactInfoList.
 */
public interface ClientDelegateFactory {
    CorbaClientDelegate create( CorbaContactInfoList list ) ;
}
