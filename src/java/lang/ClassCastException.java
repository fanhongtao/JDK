/*
 * @(#)ClassCastException.java	1.13 98/07/01
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
 * Thrown to indicate that the code has attempted to cast an object 
 * to a subclass of which it is not an instance. For example, the 
 * following code generates a <code>ClassCastException</code>: 
 * <p><blockquote><pre>
 *     Object x = new Integer(0);
 *     System.out.println((String)x);
 * </pre></blockquote>
 *
 * @author  unascribed
 * @version 1.13, 07/01/98
 * @since   JDK1.0
 */
public
class ClassCastException extends RuntimeException {
    /**
     * Constructs a <code>ClassCastException</code> with no detail message. 
     *
     * @since   JDK1.0
     */
    public ClassCastException() {
	super();
    }

    /**
     * Constructs a <code>ClassCastException</code> with the specified 
     * detail message. 
     *
     * @param   s   the detail message.
     * @since   JDK1.0
     */
    public ClassCastException(String s) {
	super(s);
    }
}
