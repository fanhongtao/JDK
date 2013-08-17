/*
 * @(#)IncompatibleClassChangeError.java	1.12 01/12/10
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.lang;

/**
 * Thrown when an incompatible class change has occurred to some class 
 * definition. The definition of some class, on which the currently 
 * executing method depends, has since changed. 
 *
 * @author  unascribed
 * @version 1.12, 12/10/01
 * @since   JDK1.0
 */
public
class IncompatibleClassChangeError extends LinkageError {
    /**
     * Constructs an <code>IncompatibleClassChangeError</code> with no 
     * detail message. 
     *
     * @since   JDK1.0
     */
    public IncompatibleClassChangeError () {
	super();
    }

    /**
     * Constructs an <code>IncompatibleClassChangeError</code> with the 
     * specified detail message. 
     *
     * @param   s   the detail message.
     * @since   JDK1.0
     */
    public IncompatibleClassChangeError(String s) {
	super(s);
    }
}
