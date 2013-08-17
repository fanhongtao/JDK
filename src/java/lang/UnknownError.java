/*
 * @(#)UnknownError.java	1.2 00/01/12
 *
 * Copyright 1995-2000 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */

package java.lang;

/**
 * Thrown when an unknown but serious exception has occurred in the 
 * Java Virtual Machine. 
 *
 * @author unascribed
 * @version 1.8, 09/21/98
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
