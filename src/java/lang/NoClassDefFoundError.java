/*
 * @(#)NoClassDefFoundError.java	1.13 98/07/01
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
 * Thrown if the Java Virtual Machine or a classloader tries to load 
 * in the definition of a class (as part of a normal method call or 
 * as part of creating a new instance using the <code>new</code> 
 * expression) and no definition of the class could be found. 
 * <p>
 * The searched-for class definition existed when the currently 
 * executing class was compiled, but the definition can no longer be 
 * found. 
 *
 * @author  unascribed
 * @version 1.13, 07/01/98
 * @since   JDK1.0
 */
public
class NoClassDefFoundError extends LinkageError {
    /**
     * Constructs a <code>NoClassDefFoundError</code> with no detail message.
     *
     * @since   JDK1.0
     */
    public NoClassDefFoundError() {
	super();
    }

    /**
     * Constructs a <code>NoClassDefFoundError</code> with the specified 
     * detail message. 
     *
     * @param   s   the detail message.
     * @since   JDK1.0
     */
    public NoClassDefFoundError(String s) {
	super(s);
    }
}
