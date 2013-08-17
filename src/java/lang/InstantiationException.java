/*
 * @(#)InstantiationException.java	1.10 98/07/01
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
 * Thrown when an application tries to create an instance of a class 
 * using the <code>newInstance</code> method in class 
 * <code>Class</code>, but the specified class object cannot be 
 * instantiated because it is an interface or is an abstract class. 
 *
 * @author  unascribed
 * @version 1.10, 07/01/98
 * @see     java.lang.Class#newInstance()
 * @since   JDK1.0
 */
public
class InstantiationException extends Exception {
    /**
     * Constructs an <code>InstantiationException</code> with no detail message.
     *
     * @since   JDK1.0
     */
    public InstantiationException() {
	super();
    }

    /**
     * Constructs an <code>InstantiationException</code> with the 
     * specified detail message. 
     *
     * @param   s   the detail message.
     * @since   JDK1.0
     */
    public InstantiationException(String s) {
	super(s);
    }
}
