/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.omg.CORBA;

import org.omg.CORBA.portable.ObjectImpl;

/**
 * The base class for all object implementations using the DSI.
 * It defines a single abstract method,
 * <code>invoke</code>, that a dynamic servant needs to implement.
 * DynamicImplementation has been deprecated by the OMG in favor of
 * the Portable Object Adapter.
 *
 * @version 1.6, 09/09/97
 * @see org.omg.CORBA.ServerRequest
 * @since JDK1.2
 */

public abstract
    class DynamicImplementation extends org.omg.CORBA.portable.ObjectImpl {

	/**
	 * Accepts a <code>ServerRequest</code> object and uses its methods to
	 * determine the request target, operation, and parameters, and to
	 * set the result or exception.
	 * Deprecated by the Portable Object Adapter.
	 *
	 * @param request             a <code>ServerRequest</code> object representing
	 *                            the request to be invoked
	 *
	 */

	public abstract void invoke(ServerRequest request);
    }
