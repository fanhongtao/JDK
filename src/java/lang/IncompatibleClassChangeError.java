/*
 * @(#)IncompatibleClassChangeError.java	1.18 03/12/19
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.lang;

/**
 * Thrown when an incompatible class change has occurred to some class 
 * definition. The definition of some class, on which the currently 
 * executing method depends, has since changed. 
 *
 * @author  unascribed
 * @version 1.18, 12/19/03
 * @since   JDK1.0
 */
public
class IncompatibleClassChangeError extends LinkageError {
    /**
     * Constructs an <code>IncompatibleClassChangeError</code> with no 
     * detail message. 
     */
    public IncompatibleClassChangeError () {
	super();
    }

    /**
     * Constructs an <code>IncompatibleClassChangeError</code> with the 
     * specified detail message. 
     *
     * @param   s   the detail message.
     */
    public IncompatibleClassChangeError(String s) {
	super(s);
    }
}
