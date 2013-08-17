/*
 * @(#)IOException.java	1.17 98/06/29
 *
 * Copyright 1994-1998 by Sun Microsystems, Inc.,
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
 * Signals that an I/O exception of some sort has occurred. This
 * class is the general class of exceptions produced by failed or
 * interrupted I/O operations.
 *
 * @author  unascribed
 * @version 1.17, 06/29/98
 * @see     java.io.InputStream
 * @see     java.io.OutputStream
 * @since   JDK1.0
 */
public
class IOException extends Exception {
    /**
     * Constructs an <code>IOException</code> with <code>null</code>
     * as its error detail message.
     */
    public IOException() {
	super();
    }

    /**
     * Constructs an <code>IOException</code> with the specified detail
     * message. The error message string <code>s</code> can later be
     * retrieved by the <code>{@link java.lang.Throwable#getMessage}</code>
     * method of class <code>java.lang.Throwable</code>.
     *
     * @param   s   the detail message.
     */
    public IOException(String s) {
	super(s);
    }
}
