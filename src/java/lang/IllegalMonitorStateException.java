/*
 * @(#)IllegalMonitorStateException.java	1.5 98/07/01
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
 * Thrown to indicate that a thread has attempted to wait on an 
 * object's monitor or to notify other threads waiting on an object's
 * monitor without owning the specified monitor. 
 *
 * @author  unascribed
 * @version 1.5, 07/01/98
 * @see     java.lang.Object#notify()
 * @see     java.lang.Object#notifyAll()
 * @see     java.lang.Object#wait() 
 * @see     java.lang.Object#wait(long) 
 * @see     java.lang.Object#wait(long, int) 
 * @since   JDK1.0
 */
public
class IllegalMonitorStateException extends RuntimeException {
    /**
     * Constructs an <code>IllegalMonitorStateException</code> with no 
     * detail message. 
     *
     * @since   JDK1.0
     */
    public IllegalMonitorStateException() {
	super();
    }

    /**
     * Constructs an <code>IllegalMonitorStateException</code> with the 
     * specified detail message. 
     *
     * @param   s   the detail message.
     * @since   JDK1.0
     */
    public IllegalMonitorStateException(String s) {
	super(s);
    }
}
