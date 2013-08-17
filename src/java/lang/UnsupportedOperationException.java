/*
 * @(#)UnsupportedOperationException.java	1.2 00/01/12
 *
 * Copyright 1997-2000 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */

package java.lang;

/**
 * Thrown to indicate that the requested operation is not supported.
 *
 * @author  Josh Bloch
 * @version 1.7 09/21/98
 * @since   JDK1.2
 */
public class UnsupportedOperationException extends RuntimeException {
    /**
     * Constructs an UnsupportedOperationException with no detail message.
     */
    public UnsupportedOperationException() {
    }

    /**
     * Constructs an UnsupportedOperationException with the specified
     * detail message.
     */
    public UnsupportedOperationException(String message) {
	super(message);
    }
}
