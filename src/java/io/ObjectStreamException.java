/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.io;

/**
 * Superclass of all exceptions specific to Object Stream classes.
 *
 * @author  unascribed
 * @version 1.11, 02/06/02
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
