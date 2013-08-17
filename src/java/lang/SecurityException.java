/*
 * @(#)SecurityException.java	1.7 98/07/01
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
 * Thrown by the security manager to indicate a security violation. 
 *
 * @author  unascribed
 * @version 1.7, 07/01/98
 * @see     java.lang.SecurityManager
 * @since   JDK1.0
 */
public class SecurityException extends RuntimeException {
    /**
     * Constructs a <code>SecurityException</code> with no detail  message.
     *
     * @since   JDK1.0
     */
    public SecurityException() {
	super();
    }

    /**
     * Constructs a <code>SecurityException</code> with the specified 
     * detail message. 
     *
     * @param   s   the detail message.
     * @since   JDK1.0
     */
    public SecurityException(String s) {
	super(s);
    }
}
