/*
 * @(#)NotActiveException.java	1.12 00/02/02
 *
 * Copyright 1996-2000 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */

package java.io;

/**
 * Thrown when serialization or deserialization is not active.
 *
 * @author  unascribed
 * @version 1.12, 02/02/00
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
