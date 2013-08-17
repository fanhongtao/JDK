/*
 * @(#)NotActiveException.java	1.2 00/01/12
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
 * @version 1.9, 06/29/98
 * @since   JDK1.1
 */
public class NotActiveException extends ObjectStreamException {
    /**
     * Constructor to create a new NotActiveException with the reason given.
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
