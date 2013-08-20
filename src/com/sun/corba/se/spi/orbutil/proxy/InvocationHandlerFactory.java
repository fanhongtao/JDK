/*
 * @(#)InvocationHandlerFactory.java	1.7 04/07/13
 * 
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.corba.se.spi.orbutil.proxy ;

import java.lang.reflect.InvocationHandler ;
                                                                                
public interface InvocationHandlerFactory
{
    /** Get an InvocationHandler.
     */
    InvocationHandler getInvocationHandler() ;
                                                                                
    /** Get the interfaces that InvocationHandler instances
     * produced by this InvocationHandlerFactory support.
     */
    Class[] getProxyInterfaces() ;
}


