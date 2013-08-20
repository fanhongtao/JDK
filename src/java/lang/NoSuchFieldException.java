/*
 * @(#)NoSuchFieldException.java	1.14 03/12/19
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.lang;

/**
 * Signals that the class doesn't have a field of a specified name.
 *
 * @author  unascribed
 * @version 1.14, 12/19/03
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
