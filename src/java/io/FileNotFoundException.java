/*
 * @(#)FileNotFoundException.java	1.13 01/12/10
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.io;

/**
 * Signals that a file could not be found. 
 *
 * @author  unascribed
 * @version 1.13, 12/10/01
 * @since   JDK1.0
 */
public class FileNotFoundException extends IOException {
    /**
     * Constructs a <code>FileNotFoundException</code> with no detail message.
     *
     * @since   JDK1.0
     */
    public FileNotFoundException() {
	super();
    }

    /**
     * Constructs a <code>FileNotFoundException</code> with the 
     * specified detail message. 
     *
     * @param   s   the detail message.
     * @since   JDK1.0
     */
    public FileNotFoundException(String s) {
	super(s);
    }
}
