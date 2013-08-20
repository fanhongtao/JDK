/*
 * @(#)RoleNotFoundException.java	1.15 03/12/19
 * 
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.management.relation;

/**
 * This exception is raised when a role in a relation does not exist, or is not
 * readable, or is not settable.
 *
 * @since 1.5
 */
public class RoleNotFoundException extends RelationException {

    /* Serial version */
    private static final long serialVersionUID = -2986406101364031481L;

    /**
     * Default constructor, no message put in exception.
     */
    public RoleNotFoundException() {
	super();
    }

    /**
     * Constructor with given message put in exception.
     *
     * @param message the detail message.
     */
    public RoleNotFoundException(String message) {
	super(message);
    }
}
