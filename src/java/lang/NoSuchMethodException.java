/*
 * @(#)NoSuchMethodException.java	1.2 00/01/12
 *
 * Copyright 1995-2000 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */

package java.lang;

/**
 * Thrown when a particular method cannot be found.
 *
 * @author     unascribed
 * @version    1.8, 09/21/98
 * @since      JDK1.0
 */
public
class NoSuchMethodException extends Exception {
    /**
     * Constructs a <code>NoSuchMethodException</code> without a detail message.
     */
    public NoSuchMethodException() {
	super();
    }

    /**
     * Constructs a <code>NoSuchMethodException</code> with a detail message. 
     *
     * @param      s   the detail message.
     */
    public NoSuchMethodException(String s) {
	super(s);
    }
}
