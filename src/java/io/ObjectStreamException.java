/*
 * @(#)ObjectStreamException.java	1.13 03/12/19
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.io;

/**
 * Superclass of all exceptions specific to Object Stream classes.
 *
 * @author  unascribed
 * @version 1.13, 12/19/03
 * @since   JDK1.1
 */
public abstract class ObjectStreamException extends IOException {
    /**
     * Create an ObjectStreamException with the specified argument.
     *
     * @param classname the detailed message for the exception
     */
    protected ObjectStreamException(String classname) {
	super(classname);
    }

    /**
     * Create an ObjectStreamException.
     */
    protected ObjectStreamException() {
	super();
    }
}
