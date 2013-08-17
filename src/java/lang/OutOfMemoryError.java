/*
 * @(#)OutOfMemoryError.java	1.14 98/07/01
 *
 * Copyright 1995-1998 by Sun Microsystems, Inc.,
 * 901 San Antonio Road, Palo Alto, California, 94303, U.S.A.
 * All rights reserved.
 * 
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
 */

package java.lang;

/**
 * Thrown when the Java Virtual Machine cannot allocate an object 
 * because it is out of memory, and no more memory could be made 
 * available by the garbage collector. 
 *
 * @author  unascribed
 * @version 1.14, 07/01/98
 * @since   JDK1.0
 */
public
class OutOfMemoryError extends VirtualMachineError {
    /**
     * Constructs an <code>OutOfMemoryError</code> with no detail message.
     *
     * @since   JDK1.0
     */
    public OutOfMemoryError() {
	super();
    }

    /**
     * Constructs an <code>OutOfMemoryError</code> with the specified 
     * detail message. 
     *
     * @param   s   the detail message.
     * @since   JDK1.0
     */
    public OutOfMemoryError(String s) {
	super(s);
    }
}
