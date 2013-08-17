/*
 * @(#)NegativeArraySizeException.java	1.2 00/01/12
 *
 * Copyright 1994-2000 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */

package java.lang;

/**
 * Thrown if an application tries to create an array with negative size.
 *
 * @author  unascribed
 * @version 1.14, 09/21/98
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
