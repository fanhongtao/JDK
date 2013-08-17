/*
 * @(#)InvokeHandler.java	1.3 98/09/08
 *
 * Copyright 1998 by Sun Microsystems, Inc.,
 * 901 San Antonio Road, Palo Alto, California, 94303, U.S.A.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
 */
package org.omg.CORBA.portable;

public interface InvokeHandler {
    /**
     * Invoked by the ORB to dispatch a request to the servant.
     *
     * ORB passes the method name, an InputStream containing the
     * marshalled arguments, and a ResponseHandler which the servant
     * uses to construct a proper reply.
     *
     * Only CORBA SystemException may be thrown by this method.
     *
     * The method must return an OutputStream created by the
     * ResponseHandler which contains the marshalled reply.
     *
     * A servant must not retain a reference to the ResponseHandler
     * beyond the lifetime of a method invocation.
     *
     * Servant behaviour is defined as follows:
     * 1. Determine correct method, and unmarshal parameters from
     *    InputStream.
     * 2. Invoke method implementation.
     * 3. If no user exception, create a normal reply using
     *    ResponseHandler.
     * 4. If user exception occurred, create exception reply using
     *    ResponseHandler.
     * 5. Marshal reply into OutputStream returned by
     *    ResponseHandler.
     * 6. Return OutputStream to ORB.
	 * 
	 * @see <a href="package-summary.html#unimpl><code>portable</code>
	 * package comments for unimplemented features</a>
     */

    OutputStream _invoke(String method, InputStream input,
			 ResponseHandler handler)
	throws org.omg.CORBA.SystemException;
}

