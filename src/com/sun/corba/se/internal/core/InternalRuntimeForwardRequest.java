/*
 * @(#)InternalRuntimeForwardRequest.java	1.4 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.sun.corba.se.internal.core;
             
/**
 * A Runtime version of the ForwardRequest exception.  For use in situations
 * where you do not wish to declare ForwardRequest as being thrown.  In most
 * cases, this should only be used as a temporary solution and the code 
 * that makes use of this should be redesigned.  Current uses:
 * <ul>
 *   <li>Due to the complex nature of the current call chain in our ORB, 
 *     we need to use this for send_* in Portable Interceptors.</li>
 * </ul>
 */
public class InternalRuntimeForwardRequest 
    extends RuntimeException 
{
    // The object to forward the request to:
    public org.omg.CORBA.Object forward;

    /**
     * Creates a new InternalRuntimeForwardRequest that will forward the
     * request to the given object.
     */
    public InternalRuntimeForwardRequest( org.omg.CORBA.Object forward ) {
	this.forward = forward;
    }
}
