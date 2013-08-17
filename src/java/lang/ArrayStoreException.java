/*
 * @(#)ArrayStoreException.java	1.4 98/07/01
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
 * Thrown to indicate that an attempt has been made to store the 
 * wrong type of object into an array of objects. For example, the 
 * following code generates an <code>ArrayStoreException</code>: 
 * <p><blockquote><pre>
 *     Object x[] = new String[3];
 *     x[0] = new Integer(0);
 * </pre></blockquote>
 *
 * @author  unascribed
 * @version 1.4, 07/01/98
 * @since   JDK1.0
 */
public
class ArrayStoreException extends RuntimeException {
    /**
     * Constructs an <code>ArrayStoreException</code> with no detail message. 
     *
     * @since   JDK1.0
     */
    public ArrayStoreException() {
	super();
    }

    /**
     * Constructs an <code>ArrayStoreException</code> with the specified 
     * detail message. 
     *
     * @param   s   the detail message.
     * @since   JDK1.0
     */
    public ArrayStoreException(String s) {
	super(s);
    }
}

