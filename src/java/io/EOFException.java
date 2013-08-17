/*
 * @(#)EOFException.java	1.7 98/06/29
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

package java.io;

/**
 * Signals that an end of file or end of stream has been reached
 * unexpectedly during input.
 * <p>
 * This exception is mainly used by data input streams, which
 * generally expect a binary file in a specific format, and for which
 * an end of stream is an unusual condition. Most other input streams
 * return a special value on end of stream.
 * <p>
 * Note that some input operations react to end-of-file by returning
 * a distinguished value (such as <code>-1</code>) rather than by
 * throwing an exception.
 *
 * @author  Frank Yellin
 * @version 1.7, 06/29/98
 * @see     java.io.DataInputStream
 * @see     java.io.IOException
 * @since   JDK1.0
 */
public
class EOFException extends IOException {
    /**
     * Constructs an <code>EOFException</code> with <code>null</code>
     * as its error detail message.
     */
    public EOFException() {
	super();
    }

    /**
     * Constructs an <code>EOFException</code> with the specified detail
     * message. The string <code>s</code> may later be retrieved by the
     * <code>{@link java.lang.Throwable#getMessage}</code> method of class
     * <code>java.lang.Throwable</code>.
     *
     * @param   s   the detail message.
     */
    public EOFException(String s) {
	super(s);
    }
}
