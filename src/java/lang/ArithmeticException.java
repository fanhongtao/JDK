/*
 * @(#)ArithmeticException.java	1.19 00/02/02
 *
 * Copyright 1994-2000 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */

package java.lang;

/**
 * Thrown when an exceptional arithmetic condition has occurred. For 
 * example, an integer "divide by zero" throws an 
 * instance of this class. 
 *
 * @author  unascribed
 * @version 1.19, 02/02/00
 * @since   JDK1.0
 */
public
class ArithmeticException extends RuntimeException {
    /**
     * Constructs an <code>ArithmeticException</code> with no detail 
     * message. 
     */
    public ArithmeticException() {
	super();
    }

    /**
     * Constructs an <code>ArithmeticException</code> with the specified 
     * detail message. 
     *
     * @param   s   the detail message.
     */
    public ArithmeticException(String s) {
	super(s);
    }
}
