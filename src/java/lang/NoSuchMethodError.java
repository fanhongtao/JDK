/*
 * @(#)NoSuchMethodError.java	1.18 00/02/02
 *
 * Copyright 1994-2000 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */

package java.lang;

/**
 * Thrown if an application tries to call a specified method of a 
 * class (either static or instance), and that class no longer has a 
 * definition of that method. 
 * <p>
 * Normally, this error is caught by the compiler; this error can 
 * only occur at run time if the definition of a class has 
 * incompatibly changed. 
 *
 * @author  unascribed
 * @version 1.18, 02/02/00
 * @since   JDK1.0
 */
public
class NoSuchMethodError extends IncompatibleClassChangeError {
    /**
     * Constructs a <code>NoSuchMethodError</code> with no detail message.
     */
    public NoSuchMethodError() {
	super();
    }

    /**
     * Constructs a <code>NoSuchMethodError</code> with the 
     * specified detail message. 
     *
     * @param   s   the detail message.
     */
    public NoSuchMethodError(String s) {
	super(s);
    }
}
