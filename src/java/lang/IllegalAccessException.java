/*
 * @(#)IllegalAccessException.java	1.4 98/07/01
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
 *     <code>ClassLoader</code>.
 * <li>The <code>loadClass</code> method in class <code>ClassLoader</code>.
 * </ul>
 * <p>
 * but the currently executing method does not have access to the 
 * definition of the specified class, because the class is not public 
 * and in another package. 
 * <p>
 * An instance of this class can also be thrown when an application 
 * tries to create an instance of a class using the 
 * <code>newInstance</code> method in class <code>Class</code>, but 
 * the current method does not have access to the appropriate 
 * zero-argument constructor. 
 *
 * @author  unascribed
 * @version 1.4, 07/01/98
 * @see     java.lang.Class#forName(java.lang.String)
 * @see     java.lang.Class#newInstance()
 * @see     java.lang.ClassLoader#findSystemClass(java.lang.String)
 * @see     java.lang.ClassLoader#loadClass(java.lang.String, boolean)
 * @since   JDK1.0
 */
public
class IllegalAccessException extends Exception {
    /**
     * Constructs an <code>IllegalAccessException</code> without a 
     * detail message. 
     *
     * @since   JDK1.0
     */
    public IllegalAccessException() {
	super();
    }

    /**
     * Constructs an <code>IllegalAccessException</code> with a detail message. 
     *
     * @param   s   the detail message.
     * @since   JDK1.0
     */
    public IllegalAccessException(String s) {
	super(s);
    }
}
