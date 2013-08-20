/*
 * @(#)InstanceAlreadyExistsException.java	4.15 03/12/19
 * 
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.management; 


/**
 * The MBean is already registered in the repository.
 *
 * @since 1.5
 */
public class InstanceAlreadyExistsException extends OperationsException   { 

    /* Serial version */
    private static final long serialVersionUID = 8893743928912733931L;

    /**
     * Default constructor.
     */
    public InstanceAlreadyExistsException() {
	super();
    }
    
    /**
     * Constructor that allows a specific error message to be specified.
     *
     * @param message the detail message.
     */
    public InstanceAlreadyExistsException(String message) {
	super(message);
    }
    
 }
