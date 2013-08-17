/*
 * @(#)Exception.java	1.2 00/01/12
 *
 * Copyright 1994-2000 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */

package java.lang;

/**
 * The class <code>Exception</code> and its subclasses are a form of 
 * <code>Throwable</code> that indicates conditions that a reasonable 
 * application might want to catch.
 *
 * @author  Frank Yellin
 * @version 1.25, 09/21/98
 * @see     java.lang.Error
 * @since   JDK1.0
 */
public
class Exception extends Throwable {
    /**
     * Constructs an <code>Exception</code> with no specified detail message. 
     */
    public Exception() {
	super();
    }

    /**
     * Constructs an <code>Exception</code> with the specified detail message. 
     *
     * @param   s   the detail message.
     */
    public Exception(String s) {
	super(s);
    }
}
