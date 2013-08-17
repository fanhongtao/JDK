/*
 * @(#)NoSuchElementException.java	1.2 00/01/12
 *
 * Copyright 1994-2000 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */

package java.util;

/**
 * Thrown by the <code>nextElement</code> method of an 
 * <code>Enumeration</code> to indicate that there are no more 
 * elements in the enumeration. 
 *
 * @author  unascribed
 * @version 1.16, 03/18/98
 * @see     java.util.Enumeration
 * @see     java.util.Enumeration#nextElement()
 * @since   JDK1.0
 */
public
class NoSuchElementException extends RuntimeException {
    /**
     * Constructs a <code>NoSuchElementException</code> with <tt>null</tt> 
     * as its error message string.
     */
    public NoSuchElementException() {
	super();
    }

    /**
     * Constructs a <code>NoSuchElementException</code>, saving a reference 
     * to the error message string <tt>s</tt> for later retrieval by the 
     * <tt>getMessage</tt> method.
     *
     * @param   s   the detail message.
     */
    public NoSuchElementException(String s) {
	super(s);
    }
}
