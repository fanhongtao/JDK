/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.security;

/**
 * This exception is thrown when an invalid parameter is passed 
 * to a method.
 *
 * @author Benjamin Renaud
 * @version 1.16, 02/02/06
 */

public class InvalidParameterException extends IllegalArgumentException {

    /**
     * Constructs an InvalidParameterException with no detail message.
     * A detail message is a String that describes this particular
     * exception.
     */
    public InvalidParameterException() {
	super();
    }

    /**
     * Constructs an InvalidParameterException with the specified
     * detail message.  A detail message is a String that describes
     * this particular exception.
     *
     * @param msg the detail message.  
     */
    public InvalidParameterException(String msg) {
	super(msg);
    }
}
