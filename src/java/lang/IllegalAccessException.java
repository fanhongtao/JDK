/*
 * @(#)IllegalAccessException.java	1.7 01/11/29
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
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
 * @version 1.7, 11/29/01
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
     */
    public IllegalAccessException() {
	super();
    }

    /**
     * Constructs an <code>IllegalAccessException</code> with a detail message. 
     *
     * @param   s   the detail message.
     */
    public IllegalAccessException(String s) {
	super(s);
    }
}
