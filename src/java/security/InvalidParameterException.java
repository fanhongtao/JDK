/*
 * @(#)InvalidParameterException.java	1.20 03/12/19
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.security;

/**
 * This exception, designed for use by the JCA/JCE engine classes, 
 * is thrown when an invalid parameter is passed 
 * to a method.
 *
 * @author Benjamin Renaud
 * @version 1.20, 03/12/19
 */

public class InvalidParameterException extends IllegalArgumentException {

    private static final long serialVersionUID = -857968536935667808L;

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
