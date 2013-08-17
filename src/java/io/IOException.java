/*
 * @(#)IOException.java	1.15 01/12/10
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.io;

/**
 * Signals that an I/O exception of some sort has occurred. 
 *
 * @author  unascribed
 * @version 1.15, 12/10/01
 * @see     java.io.InputStream
 * @see     java.io.OutputStream
 * @since   JDK1.0
 */
public
class IOException extends Exception {
    /**
     * Constructs an <code>IOException</code> with no detail message. 
     *
     * @since   JDK1.0
     */
    public IOException() {
	super();
    }

    /**
     * Constructs an <code>IOException</code> with the specified detail 
     * message. 
     *
     * @param   s   the detail message.
     * @since   JDK1.0
     */
    public IOException(String s) {
	super(s);
    }
}
