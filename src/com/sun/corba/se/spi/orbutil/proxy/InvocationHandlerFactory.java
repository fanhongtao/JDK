/*
 * @(#)InvocationHandlerFactory.java	1.8 05/11/17
 * 
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
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


