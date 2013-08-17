/*
 * @(#)UnsatisfiedLinkError.java	1.16 98/09/21
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
 * Thrown if the Java Virtual Machine cannot find an appropriate 
 * native-language definition of a method declared <code>native</code>. 
 *
 * @author unascribed
 * @version 1.16, 09/21/98
 * @see     java.lang.Runtime
 * @since   JDK1.0
 */
public
class UnsatisfiedLinkError extends LinkageError {
    /**
     * Constructs an <code>UnsatisfiedLinkError</code> with no detail message.
     */
    public UnsatisfiedLinkError() {
	super();
    }

    /**
     * Constructs an <code>UnsatisfiedLinkError</code> with the 
     * specified detail message. 
     *
     * @param   s   the detail message.
     */
    public UnsatisfiedLinkError(String s) {
	super(s);
    }
}
