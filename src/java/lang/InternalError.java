/*
 * @(#)InternalError.java	1.18 00/02/02
 *
 * Copyright 1994-2000 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */

package java.lang;

/**
 * Thrown to indicate some unexpected internal error has occurred in 
 * the Java Virtual Machine. 
 *
 * @author  unascribed
 * @version 1.18, 02/02/00
 * @since   JDK1.0
 */
public
class InternalError extends VirtualMachineError {
    /**
     * Constructs an <code>InternalError</code> with no detail message. 
     */
    public InternalError() {
	super();
    }

    /**
     * Constructs an <code>InternalError</code> with the specified 
     * detail message. 
     *
     * @param   s   the detail message.
     */
    public InternalError(String s) {
	super(s);
    }
}
