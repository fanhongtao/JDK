/*
 * @(#)UnknownError.java	1.6 98/07/01
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
 * Thrown when an unknown but serious exception has occurred in the 
 * Java Virtual Machine. 
 *
 * @author unascribed
 * @version 1.6, 07/01/98
 * @since   JDK1.0
 */
public
class UnknownError extends VirtualMachineError {
    /**
     * Constructs an <code>UnknownError</code> with no detail message. 
     *
     * @since   JDK1.0
     */
    public UnknownError() {
	super();
    }

    /**
     * Constructs an <code>UnknownError</code> with the specified detail 
     * message. 
     *
     * @param   s   the detail message.
     * @since   JDK1.0
     */
    public UnknownError(String s) {
	super(s);
    }
}
