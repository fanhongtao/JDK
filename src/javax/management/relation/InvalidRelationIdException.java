/*
 * @(#)InvalidRelationIdException.java	1.15 03/12/19
 * 
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.management.relation;

/**
 * This exception is raised when relation id provided for a relation is already
 * used.
 *
 * @since 1.5
 */
public class InvalidRelationIdException extends RelationException {

    /* Serial version */
    private static final long serialVersionUID = -7115040321202754171L;

    /**
     * Default constructor, no message put in exception.
     */
    public InvalidRelationIdException() {
	super();
    }

    /**
     * Constructor with given message put in exception.
     *
     * @param message the detail message.
     */
    public InvalidRelationIdException(String message) {
	super(message);
    }
}
