/*
 * @(#)IllegalArgumentException.java	1.15 98/09/21
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
 * Thrown to indicate that a method has been passed an illegal or 
 * inappropriate argument.
 *
 * @author  unascribed
 * @version 1.15, 09/21/98
 * @see	    java.lang.Thread#setPriority(int)
 * @since   JDK1.0
 */
public
class IllegalArgumentException extends RuntimeException {
    /**
     * Constructs an <code>IllegalArgumentException</code> with no 
     * detail message. 
     */
    public IllegalArgumentException() {
	super();
    }

    /**
     * Constructs an <code>IllegalArgumentException</code> with the 
     * specified detail message. 
     *
     * @param   s   the detail message.
     */
    public IllegalArgumentException(String s) {
	super(s);
    }
}
