/*
 * @(#)ProtocolException.java	1.10 01/12/10
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
 * @author  Chris Warth
 * @version 1.10, 12/10/01
 * @since   JDK1.0
 */
public 
class ProtocolException extends IOException { 
    /**
     * Constructs a new <code>ProtocolException</code> with the 
     * specified detail message. 
     *
     * @param   host   the detail message.
     * @since   JDK1.0
     */
    public ProtocolException(String host) {
	super(host);
    }
    
    /**
     * Constructs a new <code>ProtocolException</code> with no detail message.
     *
     * @since   JDK1.0
     */
    public ProtocolException() {
    }
}
