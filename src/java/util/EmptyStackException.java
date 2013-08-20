/*
 * @(#)EmptyStackException.java	1.20 03/12/19
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.util;

/**
 * Thrown by methods in the <code>Stack</code> class to indicate 
 * that the stack is empty. 
 *
 * @author  Jonathan Payne
 * @version 1.20, 12/19/03
 * @see     java.util.Stack
 * @since   JDK1.0
 */
public
class EmptyStackException extends RuntimeException {
    /**
     * Constructs a new <code>EmptyStackException</code> with <tt>null</tt> 
     * as its error message string.
     */
    public EmptyStackException() {
    }
}
