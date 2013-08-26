/*
 * @(#)OpenDataException.java	3.20 10/03/23
 * 
 * Copyright (c) 2006, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.management.openmbean;

// jmx import
//
import javax.management.JMException;

/**
 * This checked exception is thrown when an <i>open type</i>, an <i>open data</i>  or an <i>open MBean metadata info</i> instance 
 * could not be constructed because one or more validity constraints were not met.
 *
 * @version     3.20  10/03/23
 * @author      Sun Microsystems, Inc.
 *
 * @since 1.5
 * @since.unbundled JMX 1.1
 */
public class OpenDataException extends JMException {

    private static final long serialVersionUID = 8346311255433349870L;

    /**
     * An OpenDataException with no detail message.
     */
    public OpenDataException() {
	super();
    }

    /**
     * An OpenDataException with a detail message.
     *
     * @param msg the detail message.
     */
    public OpenDataException(String msg) {
	super(msg);
    }

}
