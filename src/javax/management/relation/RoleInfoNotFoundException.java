/*
 * @(#)RoleInfoNotFoundException.java	1.15 03/12/19
 * 
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.management.relation;

/**
 * This exception is raised when there is no role info with given name in a
 * given relation type.
 *
 * @since 1.5
 */
public class RoleInfoNotFoundException extends RelationException {

    /* Serial version */
    private static final long serialVersionUID = 4394092234999959939L;

    /**
     * Default constructor, no message put in exception.
     */
    public RoleInfoNotFoundException() {
	super();
    }

    /**
     * Constructor with given message put in exception.
     *
     * @param message the detail message.
     */
    public RoleInfoNotFoundException(String message) {
	super(message);
    }
}
