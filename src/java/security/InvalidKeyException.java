/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */


package java.security;

/**
 * This is the exception for invalid Keys (invalid encoding, wrong
 * length, uninitialized, etc).
 *
 * @version 1.12, 02/06/02
 * @author Benjamin Renaud 
 */

public class InvalidKeyException extends KeyException {

    /**
     * Constructs an InvalidKeyException with no detail message. A
     * detail message is a String that describes this particular
     * exception.
     */
    public InvalidKeyException() {
	super();
    }

    /**
     * Constructs an InvalidKeyException with the specified detail
     * message. A detail message is a String that describes this
     * particular exception.  
     *
     * @param msg the detail message.  
     */
    public InvalidKeyException(String msg) {
	super(msg);
    }
}
