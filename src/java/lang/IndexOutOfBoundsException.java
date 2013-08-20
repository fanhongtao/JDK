/*
 * @(#)IndexOutOfBoundsException.java	1.11 03/12/19
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.lang;

/**
 * Thrown to indicate that an index of some sort (such as to an array, to a
 * string, or to a vector) is out of range. 
 * <p>
 * Applications can subclass this class to indicate similar exceptions. 
 *
 * @author  Frank Yellin
 * @version 1.11, 12/19/03
 * @since   JDK1.0
 */
public
class IndexOutOfBoundsException extends RuntimeException {
    /**
     * Constructs an <code>IndexOutOfBoundsException</code> with no 
     * detail message. 
     */
    public IndexOutOfBoundsException() {
	super();
    }

    /**
     * Constructs an <code>IndexOutOfBoundsException</code> with the 
     * specified detail message. 
     *
     * @param   s   the detail message.
     */
    public IndexOutOfBoundsException(String s) {
	super(s);
    }
}
