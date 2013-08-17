/*
 * @(#)ResponseHandler.java	1.7 99/04/22
 *
 * Copyright 1998, 1999 by Sun Microsystems, Inc.,
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

/**
This interface is supplied by an ORB to a servant at invocation time and allows
the servant to later retrieve an OutputStream for returning the invocation results.
*/

public interface ResponseHandler {
    /**
     * Called by the servant during a method invocation. The servant
     * should call this method to create a reply marshal buffer if no
     * exception occurred.
     *
     * @return an OutputStream suitable for marshalling the reply.
	 *
	 * @see <a href="package-summary.html#unimpl"><code>portable</code>
	 * package comments for unimplemented features</a>
     */
    OutputStream createReply();

    /**
     * Called by the servant during a method invocation. The servant
     * should call this method to create a reply marshal buffer if a
     * user exception occurred.
     *
     * @return an OutputStream suitable for marshalling the exception
     * ID and the user exception body.
     */
    OutputStream createExceptionReply();
}
