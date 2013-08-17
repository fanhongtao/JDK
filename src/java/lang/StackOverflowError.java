/*
 * @(#)StackOverflowError.java	1.16 98/09/21
 *
 * Copyright 1994-1998 by Sun Microsystems, Inc.,
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
 * Thrown when a stack overflow occurs because an application 
 * recurses too deeply. 
 *
 * @author unascribed
 * @version 1.16, 09/21/98
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
