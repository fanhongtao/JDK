/*
 * @(#)IllegalThreadStateException.java	1.14 98/07/01
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
 * Thrown to indicate that a thread is not in an appropriate state 
 * for the requested operation. See, for example, the 
 * <code>suspend</code> and <code>resume</code> methods in class 
 * <code>Thread</code>. 
 *
 * @author  unascribed
 * @version 1.14, 07/01/98
 * @see     java.lang.Thread#resume()
 * @see     java.lang.Thread#suspend()
 * @since   JDK1.0
 */
public class IllegalThreadStateException extends IllegalArgumentException {
    /**
     * Constructs an <code>IllegalThreadStateException</code> with no 
     * detail message. 
     *
     * @since   JDK1.0
     */
    public IllegalThreadStateException() {
	super();
    }

    /**
     * Constructs an <code>IllegalThreadStateException</code> with the 
     * specified detail message. 
     *
     * @param   s   the detail message.
     * @since   JDK1.0
     */
    public IllegalThreadStateException(String s) {
	super(s);
    }
}
