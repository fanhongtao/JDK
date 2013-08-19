/*
 * @(#)ClassFormatError.java	1.19 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.lang;

/**
 * Thrown when the Java Virtual Machine attempts to read a class 
 * file and determines that the file is malformed or otherwise cannot 
 * be interpreted as a class file. 
 *
 * @author  unascribed
 * @version 1.19, 01/23/03
 * @since   JDK1.0
 */
public
class ClassFormatError extends LinkageError {
    /**
     * Constructs a <code>ClassFormatError</code> with no detail message. 
     */
    public ClassFormatError() {
	super();
    }

    /**
     * Constructs a <code>ClassFormatError</code> with the specified 
     * detail message. 
     *
     * @param   s   the detail message.
     */
    public ClassFormatError(String s) {
	super(s);
    }
}
