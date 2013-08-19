/*
 * @(#)AbstractMethodError.java	1.17 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
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
 * @version 1.17, 01/23/03
 * @since   JDK1.0
 */
public
class AbstractMethodError extends IncompatibleClassChangeError {
    /**
     * Constructs an <code>AbstractMethodError</code> with no detail  message.
     */
    public AbstractMethodError() {
	super();
    }

    /**
     * Constructs an <code>AbstractMethodError</code> with the specified 
     * detail message. 
     *
     * @param   s   the detail message.
     */
    public AbstractMethodError(String s) {
	super(s);
    }
}
