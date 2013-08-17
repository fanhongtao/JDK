/*
 * @(#)FailedLoginException.java	1.12 01/12/03
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
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
 * @version 1.12, 12/03/01
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
