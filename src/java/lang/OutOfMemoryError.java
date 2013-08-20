/*
 * @(#)OutOfMemoryError.java	1.21 03/12/19
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.lang;

/**
 * Thrown when the Java Virtual Machine cannot allocate an object 
 * because it is out of memory, and no more memory could be made 
 * available by the garbage collector. 
 *
 * @author  unascribed
 * @version 1.21, 12/19/03
 * @since   JDK1.0
 */
public
class OutOfMemoryError extends VirtualMachineError {
    /**
     * Constructs an <code>OutOfMemoryError</code> with no detail message.
     */
    public OutOfMemoryError() {
	super();
    }

    /**
     * Constructs an <code>OutOfMemoryError</code> with the specified 
     * detail message. 
     *
     * @param   s   the detail message.
     */
    public OutOfMemoryError(String s) {
	super(s);
    }
}
