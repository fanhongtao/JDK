/*
 * @(#)JarException.java	1.13 05/11/17
 *
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.util.jar;

/**
 * Signals that an error of some sort has occurred while reading from
 * or writing to a JAR file.
 *
 * @author  David Connelly
 * @version 1.13, 11/17/05
 * @since   1.2
 */
public
class JarException extends java.util.zip.ZipException {
    /**
     * Constructs a JarException with no detail message.
     */
    public JarException() {
    }

    /**
     * Constructs a JarException with the specified detail message.
     * @param s the detail message
     */
    public JarException(String s) {
	super(s);
    }
}
