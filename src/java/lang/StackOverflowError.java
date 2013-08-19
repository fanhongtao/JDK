/*
 * @(#)StackOverflowError.java	1.20 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.lang;

/**
 * Thrown when a stack overflow occurs because an application 
 * recurses too deeply. 
 *
 * @author unascribed
 * @version 1.20, 01/23/03
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
