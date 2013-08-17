/*
 * @(#)EOFException.java	1.5 01/12/10
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
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
 *
 * @author  Frank Yellin
 * @version 1.5, 12/10/01
 * @see     java.io.DataInputStream
 * @see     java.io.IOException
 * @since   JDK1.0
 */
public
class EOFException extends IOException {
    /**
     * Constructs an <code>EOFException</code> with no detail message. 
     *
     * @since   JDK1.0
     */
    public EOFException() {
	super();
    }

    /**
     * Constructs an <code>EOFException</code> with the specified detail
     * message. 
     *
     * @param   s   the detail message.
     * @since   JDK1.0
     */
    public EOFException(String s) {
	super(s);
    }
}
