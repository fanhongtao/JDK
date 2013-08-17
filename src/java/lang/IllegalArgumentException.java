/*
 * @(#)IllegalArgumentException.java	1.13 98/07/01
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

package java.lang;

/**
 * Thrown to indicate that a method has been passed an illegal or 
 * inappropriate argument.
 *
 * @author  unascribed
 * @version 1.13, 07/01/98
 * @see	    java.lang.Thread#setPriority(int)
 * @since   JDK1.0
 */
public
class IllegalArgumentException extends RuntimeException {
    /**
     * Constructs an <code>IllegalArgumentException</code> with no 
     * detail message. 
     *
     * @since   JDK1.0
     */
    public IllegalArgumentException() {
	super();
    }

    /**
     * Constructs an <code>IllegalArgumentException</code> with the 
     * specified detail message. 
     *
     * @param   s   the detail message.
     * @since   JDK1.0
     */
    public IllegalArgumentException(String s) {
	super(s);
    }
}
