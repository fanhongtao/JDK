/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.net;

import java.io.IOException;

/**
 * Thrown to indicate that the IP address of a host could not be determined.
 *
 * @author  Jonathan Payne 
 * @version 1.13, 02/06/02
 * @since   JDK1.0
 */
public 
class UnknownHostException extends IOException {
    /**
     * Constructs a new <code>UnknownHostException</code> with the 
     * specified detail message. 
     *
     * @param   host   the detail message.
     */
    public UnknownHostException(String host) {
	super(host);
    }

    /**
     * Constructs a new <code>UnknownHostException</code> with no detail 
     * message. 
     */
    public UnknownHostException() {
    }
}
