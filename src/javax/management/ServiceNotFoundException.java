/*
 * @(#)ServiceNotFoundException.java	4.16 03/12/19
 * 
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.management; 

//RI import
import javax.management.OperationsException;


/**
 * Represents exceptions raised when a requested service is not supported.
 *
 * @since 1.5
 */
public class ServiceNotFoundException extends OperationsException   { 
    
    /* Serial version */
    private static final long serialVersionUID = -3990675661956646827L;

    /**
     * Default constructor.
     */
    public ServiceNotFoundException() {
	super();
    }
    
    /**
     * Constructor that allows a specific error message to be specified.
     *
     * @param message the detail message.
     */
    public ServiceNotFoundException(String message) {
	super(message);
    }

 }
