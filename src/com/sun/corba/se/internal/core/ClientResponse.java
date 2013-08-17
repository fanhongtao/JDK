/*
 * @(#)ClientResponse.java	1.22 01/12/03
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.corba.se.internal.core;

/**
 * ClientResponse represents the result of a client request.
 */
public interface ClientResponse extends MarshalInputStream, Response {

    /** Peeks the user exception id from the response stream. */
    public String peekUserExceptionId();
    
    public boolean isDifferentAddrDispositionRequested();
    public short getAddrDisposition();
}
