/*
 * @(#)InvalidAttributeValueException.java	4.17 05/11/17
 * 
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.management; 


/**
 * The value specified is not valid for the attribute.
 *
 * @since 1.5
 */
public class InvalidAttributeValueException extends OperationsException   { 
    
    /* Serial version */
    private static final long serialVersionUID = 2164571879317142449L;

    /**
     * Default constructor.
     */
    public InvalidAttributeValueException() {
	super();
    }
    
    /**
     * Constructor that allows a specific error message to be specified.
     *
     * @param message the detail message.
     */
    public InvalidAttributeValueException(String message) {
	super(message);
    }
}
