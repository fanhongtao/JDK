/*
 * @(#)AbstractMethodError.java	1.12 01/12/10
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.lang;

/**
  * Thrown when an application tries to call an abstract method. 
 * Normally, this error is caught by the compiler; this error can 
 * only occur at run time if the definition of some class has 
 * incompatibly changed since the currently executing method was last
 * compiled. 
 *
 * @author  unascribed
 * @version 1.12, 12/10/01
 * @since   JDK1.0
 */
public
class AbstractMethodError extends IncompatibleClassChangeError {
    /**
     * Constructs an <code>AbstractMethodError</code> with no detail  message.
     *
     * @since   JDK1.0
     */
    public AbstractMethodError() {
	super();
    }

    /**
     * Constructs an <code>AbstractMethodError</code> with the specified 
     * detail message. 
     *
     * @param   s   the detail message.
     * @since   JDK1.0
     */
    public AbstractMethodError(String s) {
	super(s);
    }
}
