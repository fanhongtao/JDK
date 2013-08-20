/*
 * @(#)NotActiveException.java	1.15 03/12/19
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.io;

/**
 * Thrown when serialization or deserialization is not active.
 *
 * @author  unascribed
 * @version 1.15, 12/19/03
 * @since   JDK1.1
 */
public class NotActiveException extends ObjectStreamException {
    /**
     * Constructor to create a new NotActiveException with the reason given.
     *
     * @param reason  a String describing the reason for the exception.
     */
    public NotActiveException(String reason) {
	super(reason);
    }

    /**
     * Constructor to create a new NotActiveException without a reason.
     */
    public NotActiveException() {
	super();
    }
}
