/*
 * @(#)InvalidRelationTypeException.java	1.15 03/12/19
 * 
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.management.relation;

/**
 * Invalid relation type.
 * This exception is raised when, in a relation type, there is already a
 * relation type with that name, or the same name has been used for two
 * different role infos, or no role info provided, or one null role info
 * provided.
 *
 * @since 1.5
 */
public class InvalidRelationTypeException extends RelationException {

    /* Serial version */
    private static final long serialVersionUID = 3007446608299169961L;

    /**
     * Default constructor, no message put in exception.
     */
    public InvalidRelationTypeException() {
	super();
    }

    /**
     * Constructor with given message put in exception.
     *
     * @param message the detail message.
     */
    public InvalidRelationTypeException(String message) {
	super(message);
    }
}
