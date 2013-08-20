/*
 * @(#)SocketException.java	1.17 03/12/19
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.net;

import java.io.IOException;

/**
 * Thrown to indicate that there is an error in the underlying 
 * protocol, such as a TCP error. 
 *
 * @author  Jonathan Payne
 * @version 1.17, 12/19/03
 * @since   JDK1.0
 */
public 
class SocketException extends IOException {
    /**
     * Constructs a new <code>SocketException</code> with the 
     * specified detail message. 
     *
     * @param msg the detail message.
     */
    public SocketException(String msg) {
	super(msg);
    }

    /**
     * Constructs a new <code>SocketException</code> with no detail message.
     */
    public SocketException() {
    }
}
