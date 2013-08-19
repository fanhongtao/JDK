/*
 * @(#)AccountExpiredException.java	1.15 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.security.auth.login;

/**
 * Signals that a user account has expired.
 * 
 * <p> This exception is thrown by LoginModules when they determine
 * that an account has expired.  For example, a <code>LoginModule</code>,
 * after successfully authenticating a user, may determine that the
 * user's account has expired.  In this case the <code>LoginModule</code>
 * throws this exception to notify the application.  The application can
 * then take the appropriate steps to notify the user.
 *
 * @version 1.15, 01/23/03
 */
public class AccountExpiredException extends LoginException {

    /**
     * Constructs a AccountExpiredException with no detail message. A detail
     * message is a String that describes this particular exception.
     */
    public AccountExpiredException() {
	super();
    }

    /**
     * Constructs a AccountExpiredException with the specified detail
     * message.  A detail message is a String that describes this particular
     * exception.
     *
     * <p>
     *
     * @param msg the detail message.  
     */
    public AccountExpiredException(String msg) {
	super(msg);
    }
}
