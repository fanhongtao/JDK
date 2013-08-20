/*
 * @(#)StackOverflowError.java	1.21 03/12/19
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.lang;

/**
 * Thrown when a stack overflow occurs because an application 
 * recurses too deeply. 
 *
 * @author unascribed
 * @version 1.21, 12/19/03
 * @since   JDK1.0
 */
public
class StackOverflowError extends VirtualMachineError {
    /**
     * Constructs a <code>StackOverflowError</code> with no detail message.
     */
    public StackOverflowError() {
	super();
    }

    /**
     * Constructs a <code>StackOverflowError</code> with the specified 
     * detail message. 
     *
     * @param   s   the detail message.
     */
    public StackOverflowError(String s) {
	super(s);
    }
}
