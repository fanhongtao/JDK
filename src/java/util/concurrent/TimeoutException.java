/*
 * @(#)TimeoutException.java	1.4 05/11/17
 *
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.util.concurrent;

/**
 * Exception thrown when a blocking operation times out.  Blocking
 * operations for which a timeout is specified need a means to
 * indicate that the timeout has occurred. For many such operations it
 * is possible to return a value that indicates timeout; when that is
 * not possible or desirable then <tt>TimeoutException</tt> should be
 * declared and thrown.
 *
 * @since 1.5
 * @author Doug Lea
 */
public class TimeoutException extends Exception {
    private static final long serialVersionUID = 1900926677490660714L;

    /**
     * Constructs a <tt>TimeoutException</tt> with no specified detail
     * message.
     */
    public TimeoutException() {}

    /**
     * Constructs a <tt>TimeoutException</tt> with the specified detail
     * message.
     *
     * @param message the detail message
     */
    public TimeoutException(String message) {
        super(message);
    }
}
