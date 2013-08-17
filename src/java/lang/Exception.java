/*
 * @(#)Exception.java	1.23 98/07/01
 *
 * Copyright 1995-1998 by Sun Microsystems, Inc.,
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
 * The class <code>Exception</code> and its subclasses are a form of 
 * <code>Throwable</code> that indicates conditions that a reasonable 
 * application might want to catch.
 *
 * @author  Frank Yellin
 * @version 1.23, 07/01/98
 * @see     java.lang.Error
 * @since   JDK1.0
 */
public
class Exception extends Throwable {
    /**
     * Constructs an <code>Exception</code> with no specified detail message. 
     *
     * @since   JDK1.0
     */
    public Exception() {
	super();
    }

    /**
     * Constructs an <code>Exception</code> with the specified detail message. 
     *
     * @param   s   the detail message.
     * @since   JDK1.0
     */
    public Exception(String s) {
	super(s);
    }
}
