/*
 * @(#)NumberFormatException.java	1.12 98/07/01
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
 * Thrown to indicate that the application has attempted to convert 
 * a string to one of the numeric types, but that the string does not 
 * have the appropriate format. 
 *
 * @author  unascribed
 * @version 1.12, 07/01/98
 * @see     java.lang.Integer#toString()
 * @since   JDK1.0
 */
public
class NumberFormatException extends IllegalArgumentException {
    /**
     * Constructs a <code>NumberFormatException</code> with no detail message.
     *
     * @since   JDK1.0
     */
    public NumberFormatException () {
	super();
    }

    /**
     * Constructs a <code>NumberFormatException</code> with the 
     * specified detail message. 
     *
     * @param   s   the detail message.
     * @since   JDK1.0
     */
    public NumberFormatException (String s) {
	super (s);
    }
}
