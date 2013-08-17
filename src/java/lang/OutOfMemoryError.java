/*
 * @(#)OutOfMemoryError.java	1.2 00/01/12
 *
 * Copyright 1994-2000 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */

package java.lang;

/**
 * Thrown when the Java Virtual Machine cannot allocate an object 
 * because it is out of memory, and no more memory could be made 
 * available by the garbage collector. 
 *
 * @author  unascribed
 * @version 1.16, 09/21/98
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
