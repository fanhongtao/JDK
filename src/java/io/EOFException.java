/*
 * @(#)EOFException.java	1.4 98/07/01
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
 *
 * @author  Frank Yellin
 * @version 1.4, 07/01/98
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
