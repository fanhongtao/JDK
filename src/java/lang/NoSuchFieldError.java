/*
 * @(#)NoSuchFieldError.java	1.6 01/12/10
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
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
 * @version 1.6, 12/10/01
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
