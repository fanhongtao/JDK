/*
 * @(#)NoSuchFieldException.java	1.2 00/01/12
 *
 * Copyright 1996-2000 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */

package java.lang;

/**
 * Signals that the class doesn't have a field of a specified name.
 *
 * @author  unascribed
 * @version 1.8, 09/21/98
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
     */
    public NoSuchFieldException(String s) {
	super(s);
    }
}
