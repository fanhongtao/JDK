/*
 * @(#)OperationsException.java	4.17 05/11/17
 * 
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.management; 



/**
 * Represents exceptions thrown in the MBean server when performing operations
 * on MBeans.
 *
 * @since 1.5
 */
public class OperationsException extends JMException   { 

    /* Serial version */
    private static final long serialVersionUID = -4967597595580536216L;

    /**
     * Default constructor.
     */
    public OperationsException() {
	super();
    }
    
    /**
     * Constructor that allows a specific error message to be specified.
     *
     * @param message the detail message.
     */
    public OperationsException(String message) {
	super(message);
    }
    
}
