/*
 * @(#)UnsatisfiedLinkError.java	1.21 03/12/19
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.lang;

/**
 * Thrown if the Java Virtual Machine cannot find an appropriate 
 * native-language definition of a method declared <code>native</code>. 
 *
 * @author unascribed
 * @version 1.21, 12/19/03
 * @see     java.lang.Runtime
 * @since   JDK1.0
 */
public
class UnsatisfiedLinkError extends LinkageError {
    /**
     * Constructs an <code>UnsatisfiedLinkError</code> with no detail message.
     */
    public UnsatisfiedLinkError() {
	super();
    }

    /**
     * Constructs an <code>UnsatisfiedLinkError</code> with the 
     * specified detail message. 
     *
     * @param   s   the detail message.
     */
    public UnsatisfiedLinkError(String s) {
	super(s);
    }
}
