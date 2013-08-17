/*
 * @(#)StackOverflowError.java	1.15 01/12/10
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.lang;

/**
 * Thrown when a stack overflow occurs because an application 
 * recurses too deeply. 
 *
 * @author unascribed
 * @version 1.15, 12/10/01
 * @since   JDK1.0
 */
public
class StackOverflowError extends VirtualMachineError {
    /**
     * Constructs a <code>StackOverflowError</code> with no detail message.
     *
     * @since   JDK1.0
     */
    public StackOverflowError() {
	super();
    }

    /**
     * Constructs a <code>StackOverflowError</code> with the specified 
     * detail message. 
     *
     * @param   s   the detail message.
     * @since   JDK1.0
     */
    public StackOverflowError(String s) {
	super(s);
    }
}
