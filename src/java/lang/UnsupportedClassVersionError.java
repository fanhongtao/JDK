/*
 * @(#)UnsupportedClassVersionError.java	1.3 98/09/21
 *
 * Copyright 1994-1998 by Sun Microsystems, Inc.,
 * 901 San Antonio Road, Palo Alto, California, 94303, U.S.A.
 * All rights reserved.
 * 
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
 */

package java.lang;

/**
 * Thrown when the Java Virtual Machine attempts to read a class 
 * file and determines that the major and minor version numbers
 * in the file are not supported.
 *
 * @since   JDK1.2
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
