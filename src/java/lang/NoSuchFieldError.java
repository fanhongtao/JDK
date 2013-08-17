/*
 * @(#)NoSuchFieldError.java	1.5 98/07/01
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

package java.lang;

/**
 * Thrown if an application tries to access or modify a specified 
 * field of an object, and that object no longer has that field. 
 * <p>
 * Normally, this error is caught by the compiler; this error can 
 * only occur at run time if the definition of a class has 
 * incompatibly changed. 
 *
 * @author  unascribed
 * @version 1.5, 07/01/98
 * @since   JDK1.0
 */
public
class NoSuchFieldError extends IncompatibleClassChangeError {
    /**
     * Constructs a <code>NoSuchFieldException</code> with no detail  message.
     *
     * @since   JDK1.0
     */
    public NoSuchFieldError() {
	super();
    }

    /**
     * Constructs a <code>NoSuchFieldException</code> with the specified 
     * detail message. 
     *
     * @param   s   the detail message.
     * @since   JDK1.0
     */
    public NoSuchFieldError(String s) {
	super(s);
    }
}
