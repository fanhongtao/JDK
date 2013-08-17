/*
 * @(#)UnknownError.java	1.9 01/11/29
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.lang;

/**
 * Thrown when an unknown but serious exception has occurred in the 
 * Java Virtual Machine. 
 *
 * @author unascribed
 * @version 1.9, 11/29/01
 * @since   JDK1.0
 */
public
class UnknownError extends VirtualMachineError {
    /**
     * Constructs an <code>UnknownError</code> with no detail message. 
     */
    public UnknownError() {
	super();
    }

    /**
     * Constructs an <code>UnknownError</code> with the specified detail 
     * message. 
     *
     * @param   s   the detail message.
     */
    public UnknownError(String s) {
	super(s);
    }
}
