/*
 * @(#)UnsupportedClassVersionError.java	1.7 00/02/02
 *
 * Copyright 1994-2000 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
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
