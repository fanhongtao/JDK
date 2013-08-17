/*
 * @(#)ServerResponse.java	1.18 01/12/03
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.corba.se.internal.core;

/**
 * ServerResponse represents the response the server subcontract
 * builds after the dispatch to the server object is done.
 */
public interface ServerResponse extends MarshalOutputStream, Response {
}
