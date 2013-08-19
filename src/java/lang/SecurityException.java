/*
 * @(#)SecurityException.java	1.13 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package java.lang;

/**
 * Thrown by the security manager to indicate a security violation. 
 *
 * @author  unascribed
 * @version 1.13, 01/23/03
 * @see     java.lang.SecurityManager
 * @since   JDK1.0
 */
public class SecurityException extends RuntimeException {
    /**
     * Constructs a <code>SecurityException</code> with no detail  message.
     */
    public SecurityException() {
	super();
    }

    /**
     * Constructs a <code>SecurityException</code> with the specified 
     * detail message. 
     *
     * @param   s   the detail message.
     */
    public SecurityException(String s) {
	super(s);
    }
}
