/*
 * @(#)InvalidRoleValueException.java	1.17 03/12/19
 * 
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.management.relation;

/**
 * Role value is invalid.
 * This exception is raised when, in a role, the number of referenced MBeans
 * in given value is less than expected minimum degree, or the number of
 * referenced MBeans in provided value exceeds expected maximum degree, or
 * one referenced MBean in the value is not an Object of the MBean
 * class expected for that role, or an MBean provided for that role does not
 * exist.
 *
 * @since 1.5
 */
public class InvalidRoleValueException extends RelationException {

    /* Serial version */
    private static final long serialVersionUID = -2066091747301983721L;

    /**
     * Default constructor, no message put in exception.
     */
    public InvalidRoleValueException() {
	super();
    }

    /**
     * Constructor with given message put in exception.
     *
     * @param message the detail message.
     */
    public InvalidRoleValueException(String message) {
	super(message);
    }
}
