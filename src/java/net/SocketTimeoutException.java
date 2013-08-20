/*
 * @(#)SocketTimeoutException.java	1.5 03/12/19
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.net;

/**
 * Signals that a timeout has occurred on a socket read or accept.
 *
 * @since   1.4
 */

public class SocketTimeoutException extends java.io.InterruptedIOException {

    /**
     * Constructs a new SocketTimeoutException with a detail 
     * message.
     * @param msg the detail message
     */
    public SocketTimeoutException(String msg) {
	super(msg);
    }

    /**
     * Construct a new SocketTimeoutException with no detailed message.
     */
    public SocketTimeoutException() {}
}
