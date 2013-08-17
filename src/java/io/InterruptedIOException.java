/*
 * @(#)InterruptedIOException.java	1.9 01/12/10
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.io;

/**
 * Signals that an I/O operation has been interrupted. 
 *
 * @author  unascribed
 * @version 1.9, 12/10/01
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
