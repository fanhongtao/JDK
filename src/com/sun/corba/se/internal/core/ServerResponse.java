/*
 * @(#)ServerResponse.java	1.19 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.corba.se.internal.core;

/**
 * ServerResponse represents the response the server subcontract
 * builds after the dispatch to the server object is done.
 */
public interface ServerResponse extends MarshalOutputStream, Response {
}
