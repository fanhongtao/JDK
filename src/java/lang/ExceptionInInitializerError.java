/*
 * @(#)ExceptionInInitializerError.java	1.3 98/07/01
 *
 * Copyright 1996-1998 by Sun Microsystems, Inc.,
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
 * Signals that an unexpected exception has occurred in a static initializer.
 *
 * @author  Frank Yellin
 * @version 1.3, 07/01/98
 *
 * @since   JDK1.1
 */
public
class ExceptionInInitializerError extends LinkageError {
    private Throwable exception;

    /**
     * Constructs an ExceptionInInitializerError with no detail message.
     * A detail message is a String that describes this particular exception.
     *
     * @since   JDK1.1
     */
    public ExceptionInInitializerError() {
	super();
    }

    /**
     * Constructs a new ExceptionInInitializerError class initialized to 
     * the specific throwable
     *
     * @param thrown The exception thrown
     * @since   JDK1.1
     */
    public ExceptionInInitializerError(Throwable thrown) {
	this.exception = thrown;
    }

    /**
     * Constructs a ExceptionInInitializerError with the specified detail message.
     * A detail message is a String that describes this particular exception.
     *
     * @param s the detail message
     * @since   JDK1.1
     */
    public ExceptionInInitializerError(String s) {
	super(s);
    }

    /**
     * Returns the exception that occurred during a static initialization that
     * caused this Error to be created.
     */
    public Throwable getException() { 
	return exception;
    }
}
