/*
 * @(#)RemarshalException.java	1.4 99/04/22
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
package org.omg.CORBA.portable;

/**
This class is used for reporting locate forward exceptions and object forward
GIOP messages back to the ORB. In this case the ORB must remarshal the request
before trying again.
Stubs which use the stream-based model shall catch the <code>RemarshalException</code>
which is potentially thrown from the <code>_invoke()</code> method of <code>ObjectImpl</code>.
Upon catching the exception, the stub shall immediately remarshal the request by calling
<code>_request()</code>, marshalling the arguments (if any), and then calling
<code>_invoke()</code>. The stub shall repeat this process until <code>_invoke()</code>
returns normally or raises some exception other than <code>RemarshalException</code>.
*/

public final class RemarshalException extends Exception {
    /**
    * Constructs a RemarshalException.
    */
    public RemarshalException() {
	super();
    }
}
