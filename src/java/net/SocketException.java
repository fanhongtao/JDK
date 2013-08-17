/*
 * @(#)SocketException.java	1.9 98/07/01
 *
 * Copyright 1995-1998 by Sun Microsystems, Inc.,
 * 901 San Antonio Road, Palo Alto, California, 94303, U.S.A.
 * All rights reserved.
 * 
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
 */

package java.net;

import java.io.IOException;

/**
 * Thrown to indicate that there is an error in the underlying 
 * protocol, such as a TCP error. 
 *
 * @author  Jonathan Payne
 * @version 1.9, 07/01/98
 * @since   JDK1.0
 */
public 
class SocketException extends IOException {
    /**
     * Constructs a new <code>ProtocolException</code> with the 
     * specified detail message. 
     *
     * @param   host   the detail message.
     * @since   JDK1.0
     */
    public SocketException(String msg) {
	super(msg);
    }

    /**
     * Constructs a new <code>ProtocolException</code> with no detail message.
     *
     * @since   JDK1.0
     */
    public SocketException() {
    }
}
