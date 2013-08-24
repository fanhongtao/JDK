/*
 * @(#)NoSuchFieldException.java	1.15 05/11/17
 *
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.lang;

/**
 * Signals that the class doesn't have a field of a specified name.
 *
 * @author  unascribed
 * @version 1.15, 11/17/05
 * @since   JDK1.1
 */
public class NoSuchFieldException extends Exception {
    /**
     * Constructor.
     */
    public NoSuchFieldException() {
	super();
    }

    /**
     * Constructor with a detail message.
     *
     * @param s the detail message
     */
    public NoSuchFieldException(String s) {
	super(s);
    }
}
