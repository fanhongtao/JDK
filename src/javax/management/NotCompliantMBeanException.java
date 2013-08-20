/*
 * @(#)NotCompliantMBeanException.java	4.16 03/12/19
 * 
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.management;


/**
 * Exception which occurs when trying to register an  object in the MBean server that is not a JMX compliant MBean. 
 *
 * @since 1.5
 */
public class NotCompliantMBeanException  extends OperationsException {


    /* Serial version */
    private static final long serialVersionUID = 5175579583207963577L;

    /**
     * Default constructor.
     */
    public NotCompliantMBeanException()  {      
	super();
    } 

    /**
     * Constructor that allows a specific error message to be specified.
     *
     * @param message the detail message.
     */
    public NotCompliantMBeanException(String message)  {      
	super(message);
    } 
    
 }
