/*
 * @(#)PortUnreachableException.java	1.2 01/12/03
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.net;

/**
 * Signals that an ICMP Port Unreachable message has been
 * received on a connected datagram.
 *
 * @since   1.4
 */

public class PortUnreachableException extends SocketException {

    /**
     * Constructs a new <code>PortUnreachableException</code> with a 
     * detail message.
     * @param msg the detail message
     */
    public PortUnreachableException(String msg) {
	super(msg);
    }

    /**
     * Construct a new <code>PortUnreachableException</code> with no 
     * detailed message.
     */
    public PortUnreachableException() {}
}
