/*
 * @(#)NumberFormatException.java	1.17 01/12/03
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.lang;

/**
 * Thrown to indicate that the application has attempted to convert 
 * a string to one of the numeric types, but that the string does not 
 * have the appropriate format. 
 *
 * @author  unascribed
 * @version 1.17, 12/03/01
 * @see     java.lang.Integer#toString()
 * @since   JDK1.0
 */
public
class NumberFormatException extends IllegalArgumentException {
    /**
     * Constructs a <code>NumberFormatException</code> with no detail message.
     */
    public NumberFormatException () {
	super();
    }

    /**
     * Constructs a <code>NumberFormatException</code> with the 
     * specified detail message. 
     *
     * @param   s   the detail message.
     */
    public NumberFormatException (String s) {
	super (s);
    }
}
