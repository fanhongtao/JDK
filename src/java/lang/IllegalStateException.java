/*
 * @(#)IllegalStateException.java	1.9 00/02/02
 *
 * Copyright 1996-2000 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */

package java.lang;

/**
 * Signals that a method has been invoked at an illegal or
 * inappropriate time.  In other words, the Java environment or
 * Java application is not in an appropriate state for the requested
 * operation.
 *
 * @author  Jonni Kanerva
 * @version 1.9, 02/02/00
 * @since   JDK1.1
 */
public
class IllegalStateException extends RuntimeException {
    /**
     * Constructs an IllegalStateException with no detail message.
     * A detail message is a String that describes this particular exception.
     */
    public IllegalStateException() {
	super();
    }

    /**
     * Constructs an IllegalStateException with the specified detail
     * message.  A detail message is a String that describes this particular
     * exception.
     *
     * @param s the String that contains a detailed message
     */
    public IllegalStateException(String s) {
	super(s);
    }
}
