/*
 * @(#)ConnectException.java	1.6 98/07/01
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

/**
 * Signals that an error occurred while attempting to connect a
 * socket to a remote address and port.  Typically, the connection
 * was refused remotely (e.g., no process is listening on the 
 * remote address/port).
 *
 * @since   JDK1.1
 */
public class ConnectException extends SocketException {
    /**
     * Constructs a new ConnectException with the specified detail 
     * message as to why the connect error occurred.
     * A detail message is a String that gives a specific 
     * description of this error.
     * @param msg the detail message
     * @since   JDK1.1
     */
    public ConnectException(String msg) {
	super(msg);
    }

    /**
     * Construct a new ConnectException with no detailed message.
     * @since   JDK1.1
     */
    public ConnectException() {}
}
