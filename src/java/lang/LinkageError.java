/*
 * @(#)LinkageError.java	1.6 98/07/01
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
 * Subclasses of <code>LinkageError</code> indicate that a class has 
 * some dependency on another class; however, the latter class has 
 * incompatibly changed after the compilation of the former class. 
 *
 *
 * @author  Frank Yellin
 * @version 1.6, 07/01/98
 * @since   JDK1.0
 */
public
class LinkageError extends Error {
    /**
     * Constructs a <code>LinkageError</code> with no detail message. 
     *
     * @since   JDK1.0
     */
    public LinkageError() {
	super();
    }

    /**
     * Constructs a <code>LinkageError</code> with the specified detail 
     * message. 
     *
     * @param   s   the detail message.
     * @since   JDK1.0
     */
    public LinkageError(String s) {
	super(s);
    }
}
