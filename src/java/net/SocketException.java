/*
 * @(#)SocketException.java	1.12 01/11/29
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.net;

import java.io.IOException;

/**
 * Thrown to indicate that there is an error in the underlying 
 * protocol, such as a TCP error. 
 *
 * @author  Jonathan Payne
 * @version 1.12, 11/29/01
 * @since   JDK1.0
 */
public 
class SocketException extends IOException {
    /**
     * Constructs a new <code>ProtocolException</code> with the 
     * specified detail message. 
     *
     * @param   host   the detail message.
     */
    public SocketException(String msg) {
	super(msg);
    }

    /**
     * Constructs a new <code>ProtocolException</code> with no detail message.
     */
    public SocketException() {
    }
}
