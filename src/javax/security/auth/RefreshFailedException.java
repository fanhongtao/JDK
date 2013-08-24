/*
 * @(#)RefreshFailedException.java	1.9 05/11/17
 *
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.security.auth;

/**
 * Signals that a <code>refresh</code> operation failed.
 * 
 * <p> This exception is thrown by credentials implementing
 * the <code>Refreshable</code> interface when the <code>refresh</code>
 * method fails.
 *
 * @version 1.9, 11/17/05
 */
public class RefreshFailedException extends Exception {

    private static final long serialVersionUID = 5058444488565265840L;

    /**
     * Constructs a RefreshFailedException with no detail message. A detail
     * message is a String that describes this particular exception.
     */
    public RefreshFailedException() {
	super();
    }

    /**
     * Constructs a RefreshFailedException with the specified detail
     * message.  A detail message is a String that describes this particular
     * exception.
     *
     * <p>
     *
     * @param msg the detail message.  
     */
    public RefreshFailedException(String msg) {
	super(msg);
    }
}
