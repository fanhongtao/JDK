/*
 * @(#)InstanceNotFoundException.java	4.17 05/11/17
 * 
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.management; 


/**
 * The specified MBean does not exist in the repository.
 *
 * @since 1.5
 */
public class InstanceNotFoundException extends OperationsException   { 

    /* Serial version */
    private static final long serialVersionUID = -882579438394773049L;

    /**
     * Default constructor.
     */
    public InstanceNotFoundException() {
	super();
    }
    
    /**
     * Constructor that allows a specific error message to be specified.
     *
     * @param message the detail message.
     */
    public InstanceNotFoundException(String message) {
	super(message);
    }
}
