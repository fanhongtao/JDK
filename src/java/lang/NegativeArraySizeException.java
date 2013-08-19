/*
 * @(#)NegativeArraySizeException.java	1.18 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.lang;

/**
 * Thrown if an application tries to create an array with negative size.
 *
 * @author  unascribed
 * @version 1.18, 01/23/03
 * @since   JDK1.0
 */
public
class NegativeArraySizeException extends RuntimeException {
    /**
     * Constructs a <code>NegativeArraySizeException</code> with no 
     * detail message. 
     */
    public NegativeArraySizeException() {
	super();
    }

    /**
     * Constructs a <code>NegativeArraySizeException</code> with the 
     * specified detail message. 
     *
     * @param   s   the detail message.
     */
    public NegativeArraySizeException(String s) {
	super(s);
    }
}
