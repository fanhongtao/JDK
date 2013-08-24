/*
 * @(#)JMException.java	4.17 05/11/17
 * 
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.management; 


/**
 * Exceptions thrown by JMX implementations.
 * It does not include the runtime exceptions.
 *
 * @since 1.5
 */
public class JMException extends java.lang.Exception   { 
    
    /* Serial version */
    private static final long serialVersionUID = 350520924977331825L;

    /**
     * Default constructor.
     */
    public JMException() {
	super();
    }
    
    /**
     * Constructor that allows a specific error message to be specified.
     *
     * @param msg the detail message.
     */
    public JMException(String msg) {
	super(msg);
    }
    
}
