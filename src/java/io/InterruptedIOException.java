/*
 * @(#)InterruptedIOException.java	1.8 98/07/01
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
 * Signals that an I/O operation has been interrupted. 
 *
 * @author  unascribed
 * @version 1.8, 07/01/98
 * @see     java.io.InputStream
 * @see     java.io.OutputStream
 * @see     java.lang.Thread#interrupt()
 * @since   JDK1.0
 */
public
class InterruptedIOException extends IOException {
    /**
     * Constructs an <code>InterruptedIOException</code> with no detail 
     * message. 
     *
     * @since   JDK1.0
     */
    public InterruptedIOException() {
	super();
    }

    /**
     * Constructs an <code>InterruptedIOException</code> with the 
     * specified detail message. 
     *
     * @param   s   the detail message.
     * @since   JDK1.0
     */
    public InterruptedIOException(String s) {
	super(s);
    }

    /**
     * Reports how many bytes had been transferred as part of the I/O 
     * operation before it was interrupted. 
     *
     * @since   JDK1.0
     */ 
    public int bytesTransferred = 0;
}
