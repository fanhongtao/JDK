/*
 * @(#)ArrayStoreException.java	1.2 00/01/12
 *
 * Copyright 1995-2000 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */

package java.lang;

/**
 * Thrown to indicate that an attempt has been made to store the 
 * wrong type of object into an array of objects. For example, the 
 * following code generates an <code>ArrayStoreException</code>: 
 * <p><blockquote><pre>
 *     Object x[] = new String[3];
 *     x[0] = new Integer(0);
 * </pre></blockquote>
 *
 * @author  unascribed
 * @version 1.6, 09/21/98
 * @since   JDK1.0
 */
public
class ArrayStoreException extends RuntimeException {
    /**
     * Constructs an <code>ArrayStoreException</code> with no detail message. 
     */
    public ArrayStoreException() {
	super();
    }

    /**
     * Constructs an <code>ArrayStoreException</code> with the specified 
     * detail message. 
     *
     * @param   s   the detail message.
     */
    public ArrayStoreException(String s) {
	super(s);
    }
}

