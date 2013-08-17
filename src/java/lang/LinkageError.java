/*
 * @(#)LinkageError.java	1.10 00/02/02
 *
 * Copyright 1995-2000 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */

package java.lang;

/**
 * Subclasses of <code>LinkageError</code> indicate that a class has 
 * some dependency on another class; however, the latter class has 
 * incompatibly changed after the compilation of the former class. 
 *
 *
 * @author  Frank Yellin
 * @version 1.10, 02/02/00
 * @since   JDK1.0
 */
public
class LinkageError extends Error {
    /**
     * Constructs a <code>LinkageError</code> with no detail message. 
     */
    public LinkageError() {
	super();
    }

    /**
     * Constructs a <code>LinkageError</code> with the specified detail 
     * message. 
     *
     * @param   s   the detail message.
     */
    public LinkageError(String s) {
	super(s);
    }
}
