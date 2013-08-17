/*
 * @(#)ExceptionInInitializerError.java	1.4 01/12/10
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.lang;

/**
 * Signals that an unexpected exception has occurred in a static initializer.
 *
 * @author  Frank Yellin
 * @version 1.4, 12/10/01
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
