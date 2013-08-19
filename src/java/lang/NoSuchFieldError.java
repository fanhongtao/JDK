/*
 * @(#)NoSuchFieldError.java	1.11 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
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
 * @version 1.11, 01/23/03
 * @since   JDK1.0
 */
public
class NoSuchFieldError extends IncompatibleClassChangeError {
    /**
     * Constructs a <code>NoSuchFieldException</code> with no detail  message.
     */
    public NoSuchFieldError() {
	super();
    }

    /**
     * Constructs a <code>NoSuchFieldException</code> with the specified 
     * detail message. 
     *
     * @param   s   the detail message.
     */
    public NoSuchFieldError(String s) {
	super(s);
    }
}
