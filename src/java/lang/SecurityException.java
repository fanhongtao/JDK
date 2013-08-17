/*
 * @(#)SecurityException.java	1.11 00/02/02
 *
 * Copyright 1995-2000 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package java.lang;

/**
 * Thrown by the security manager to indicate a security violation. 
 *
 * @author  unascribed
 * @version 1.11, 02/02/00
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
