/*
 * @(#)ObjectStreamException.java	1.5 98/07/01
 *
 * Copyright 1995-1998 by Sun Microsystems, Inc.,
 * 901 San Antonio Road, Palo Alto, California, 94303, U.S.A.
 * All rights reserved.
 * 
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
 */

package java.io;

/**
 * Superclass of all exceptions specific to Object Stream classes.
 *
 * @author  unascribed
 * @version 1.5, 07/01/98
 * @since   JDK1.1
 */
public abstract class ObjectStreamException extends IOException {
    /**
     * Create an ObjectStreamException with the specified argument.
     * @since   JDK1.1
     */
    protected ObjectStreamException(String classname) {
	super(classname);
    }

    /**
     * Create an ObjectStreamException.
     * @since   JDK1.1
     */
    protected ObjectStreamException() {
	super();
    }
}
