/*
 * @(#)IllegalClassFormatException.java	1.2 03/08/16
 *
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms. 
 */

package java.lang.instrument;

/*
 * Copyright 2003 Wily Technology, Inc.
 */

/**
 * Thrown by an implementation of
 * {@link java.lang.instrument.ClassFileTransformer#transform ClassFileTransformer.transform}
 * when its input parameters are invalid. 
 * This may occur either because the initial class file bytes were 
 * invalid or a previously applied transform corrupted the bytes.
 *
 * @see     java.lang.instrument.ClassFileTransformer#transform
 * @since   JDK1.5
 */
public class IllegalClassFormatException extends Exception {
    /**
     * Constructs an <code>IllegalClassFormatException</code> with no 
     * detail message. 
     */
    public
    IllegalClassFormatException() {
        super();
    }

    /**
     * Constructs an <code>IllegalClassFormatException</code> with the 
     * specified detail message. 
     *
     * @param   s   the detail message.
     */
    public
    IllegalClassFormatException(String s) {
        super(s);
    }
}
