/*
 * @(#)WrongTransaction.java	1.14 99/04/22
 *
 * Copyright 1997-1999 by Sun Microsystems, Inc.,
 * 901 San Antonio Road, Palo Alto, California, 94303, U.S.A.
 * All rights reserved.
 * 
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
 */

package org.omg.CORBA;

/**
 * The CORBA <code>WrongTransaction</code> user-defined exception.
 * This exception is thrown only by the methods
 * <code>Request.get_response</code>
 * and <code>ORB.get_next_response</code> when they are invoked
 * from a transaction scope that is different from the one in
 * which the client originally sent the request.
 * See the OMG Transaction Service Specification for details.
 *
 * @see <A href="../../../../guide/idl/jidlExceptions.html">documentation on
 * Java&nbsp;IDL exceptions</A>
 */

public class WrongTransaction extends UserException {
    /**
    * Constructs a WrongTransaction object with an empty detail message.
    */
    public WrongTransaction() {
	super();
    }

    /**
    * Constructs a WrongTransaction object with the given detail message.
    * @param reason The detail message explaining what caused this exception to be thrown.
    */
    public WrongTransaction(String reason) {
	super(reason);
    }
}
