/*
 * @(#)RelationNotFoundException.java	1.15 03/12/19
 * 
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.management.relation;

/**
 * This exception is raised when there is no relation for a given relation id
 * in a Relation Service.
 *
 * @since 1.5
 */
public class RelationNotFoundException extends RelationException {

    /* Serial version */
    private static final long serialVersionUID = -3793951411158559116L;

    /**
     * Default constructor, no message put in exception.
     */
    public RelationNotFoundException() {
	super();
    }

    /**
     * Constructor with given message put in exception.
     *
     * @param message the detail message.
     */
    public RelationNotFoundException(String message) {
	super(message);
    }
}
