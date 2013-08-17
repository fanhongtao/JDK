/*
 * @(#)ClassNotFoundException.java	1.4 98/07/01
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
 * Thrown when an application tries to load in a class through its 
 * string name using:
 * <ul>
 * <li>The <code>forName</code> method in class <code>Class</code>.
 * <li>The <code>findSystemClass</code> method in class
 *     <code>ClassLoader</code> .
 * <li>The <code>loadClass</code> method in class <code>ClassLoader</code>.
 * </ul>
 * <p>
 * but no definition for the class with the specifed name could be found. 
 *
 * @author  unascribed
 * @version 1.4, 07/01/98
 * @see     java.lang.Class#forName(java.lang.String)
 * @see     java.lang.ClassLoader#findSystemClass(java.lang.String)
 * @see     java.lang.ClassLoader#loadClass(java.lang.String, boolean)
 * @since   JDK1.0
 */
public
class ClassNotFoundException extends Exception {
    /**
     * Constructs a <code>ClassNotFoundException</code> with no detail message.
     *
     * @since   JDK1.0
     */
    public ClassNotFoundException() {
	super();
    }

    /**
     * Constructs a <code>ClassNotFoundException</code> with the 
     * specified detail message. 
     *
     * @param   s   the detail message.
     * @since   JDK1.0
     */
    public ClassNotFoundException(String s) {
	super(s);
    }
}
