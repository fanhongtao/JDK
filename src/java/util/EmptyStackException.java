/*
 * @(#)EmptyStackException.java	1.21 05/11/17
 *
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.util;

/**
 * Thrown by methods in the <code>Stack</code> class to indicate 
 * that the stack is empty. 
 *
 * @author  Jonathan Payne
 * @version 1.21, 11/17/05
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
