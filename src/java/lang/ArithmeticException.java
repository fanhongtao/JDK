/*
 * @(#)ArithmeticException.java	1.17 98/09/21
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

package java.lang;

/**
 * Thrown when an exceptional arithmetic condition has occurred. For 
 * example, an integer "divide by zero" throws an 
 * instance of this class. 
 *
 * @author  unascribed
 * @version 1.17, 09/21/98
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
