/*
 * @(#)RelationServiceNotRegisteredException.java	1.16 05/11/17
 * 
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.management.relation;

/**
 * This exception is raised when an access is done to the Relation Service and
 * that one is not registered.
 *
 * @since 1.5
 */
public class RelationServiceNotRegisteredException extends RelationException {

    /* Serial version */
    private static final long serialVersionUID = 8454744887157122910L;

    /**
     * Default constructor, no message put in exception.
     */
    public RelationServiceNotRegisteredException() {
	super();
    }

    /**
     * Constructor with given message put in exception.
     *
     * @param message the detail message.
     */
    public RelationServiceNotRegisteredException(String message) {
	super(message);
    }
}
