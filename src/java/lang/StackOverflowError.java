/*
 * @(#)StackOverflowError.java	1.18 00/02/02
 *
 * Copyright 1994-2000 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */

package java.lang;

/**
 * Thrown when a stack overflow occurs because an application 
 * recurses too deeply. 
 *
 * @author unascribed
 * @version 1.18, 02/02/00
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
