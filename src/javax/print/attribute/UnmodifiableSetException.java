/*
 * @(#)UnmodifiableSetException.java	1.6 06/04/06
 *
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.print.attribute;

/**
 * Thrown to indicate that the requested operation cannot be performed
 * because the set is unmodifiable.
 *
 * @author  Phil Race
 * @since   1.4
 */
public class UnmodifiableSetException extends RuntimeException {
    /**
     * Constructs an UnsupportedOperationException with no detail message.
     */
    public UnmodifiableSetException() {
    }

    /**
     * Constructs an UnmodifiableSetException with the specified
     * detail message.
     *
     * @param message the detail message
     */
    public UnmodifiableSetException(String message) {
	super(message);
    }
}
