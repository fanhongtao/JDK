/*
 * @(#)ObjectStreamException.java	1.2 00/01/12
 *
 * Copyright 1996-2000 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */

package java.io;

/**
 * Superclass of all exceptions specific to Object Stream classes.
 *
 * @author  unascribed
 * @version 1.7, 09/21/98
 * @since   JDK1.1
 */
public abstract class ObjectStreamException extends IOException {
    /**
     * Create an ObjectStreamException with the specified argument.
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
