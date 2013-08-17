/*
 * @(#)IndexOutOfBoundsException.java	1.4 98/07/01
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
 * Thrown to indicate that an index of some sort (such as to an array, to a
 * string, or to a vector) is out of range. 
 * <p>
 * Applications can subclass this class to indicate similar exceptions. 
 *
 * @author  Frank Yellin
 * @version 1.4, 07/01/98
 * @since   JDK1.0
 */
public
class IndexOutOfBoundsException extends RuntimeException {
    /**
     * Constructs an <code>IndexOutOfBoundsException</code> with no 
     * detail message. 
     *
     * @since   JDK1.0
     */
    public IndexOutOfBoundsException() {
	super();
    }

    /**
     * Constructs an <code>IndexOutOfBoundsException</code> with the 
     * specified detail message. 
     *
     * @param   s   the detail message.
     * @since   JDK1.0
     */
    public IndexOutOfBoundsException(String s) {
	super(s);
    }
}
