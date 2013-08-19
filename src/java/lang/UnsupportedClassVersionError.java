/*
 * @(#)UnsupportedClassVersionError.java	1.9 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.lang;

/**
 * Thrown when the Java Virtual Machine attempts to read a class 
 * file and determines that the major and minor version numbers
 * in the file are not supported.
 *
 * @since   1.2
 */
public
class UnsupportedClassVersionError extends ClassFormatError {
    /**
     * Constructs a <code>UnsupportedClassVersionError</code> 
     * with no detail message. 
     */
    public UnsupportedClassVersionError() {
	super();
    }

    /**
     * Constructs a <code>UnsupportedClassVersionError</code> with
     * the specified detail message. 
     *
     * @param   s   the detail message.
     */
    public UnsupportedClassVersionError(String s) {
	super(s);
    }
}
