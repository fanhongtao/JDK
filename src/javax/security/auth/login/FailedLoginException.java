/*
 * @(#)FailedLoginException.java	1.13 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.security.auth.login;

/**
 * Signals that user authentication failed.
 * 
 * <p> This exception is thrown by LoginModules if authentication failed.
 * For example, a <code>LoginModule</code> throws this exception if
 * the user entered an incorrect password.
 *
 * @version 1.13, 01/23/03
 */
public class FailedLoginException extends LoginException {

    /**
     * Constructs a FailedLoginException with no detail message. A detail
     * message is a String that describes this particular exception.
     */
    public FailedLoginException() {
	super();
    }

    /**
     * Constructs a FailedLoginException with the specified detail
     * message.  A detail message is a String that describes this particular
     * exception.
     *
     * <p>
     *
     * @param msg the detail message.  
     */
    public FailedLoginException(String msg) {
	super(msg);
    }
}
