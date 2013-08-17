/*
 * @(#)ArrayIndexOutOfBoundsException.java	1.14 98/07/01
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
 * Thrown to indicate that an array has been accessed with an 
 * illegal index. The index is either negative or greater than or 
 * equal to the size of the array. 
 *
 * @author  unascribed
 * @version 1.14, 07/01/98
 * @since   JDK1.0
 */
public
class ArrayIndexOutOfBoundsException extends IndexOutOfBoundsException {
    /**
     * Constructs an <code>ArrayIndexOutOfBoundsException</code> with no 
     * detail message. 
     *
     * @since   JDK1.0
     */
    public ArrayIndexOutOfBoundsException() {
	super();
    }

    /**
     * Constructs a new <code>ArrayIndexOutOfBoundsException</code> 
     * class with an argument indicating the illegal index. 
     *
     * @param   index   the illegal index.
     * @since   JDK1.0
     */
    public ArrayIndexOutOfBoundsException(int index) {
	super("Array index out of range: " + index);
    }

    /**
     * Constructs an <code>ArrayIndexOutOfBoundsException</code> class 
     * with the specified detail message. 
     *
     * @param   s   the detail message.
     * @since   JDK1.0
     */
    public ArrayIndexOutOfBoundsException(String s) {
	super(s);
    }
}
