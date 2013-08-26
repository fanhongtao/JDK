/*
 * @(#)NoSuchFieldException.java	1.16 10/03/23
 *
 * Copyright (c) 2006, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.lang;

/**
 * Signals that the class doesn't have a field of a specified name.
 *
 * @author  unascribed
 * @version 1.16, 03/23/10
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
