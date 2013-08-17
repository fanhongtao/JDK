/*
 * @(#)ClassCircularityError.java	1.12 00/02/02
 *
 * Copyright 1995-2000 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */

package java.lang;

/**
 * Thrown when a circularity has been detected while initializing a class.
 *
 * @author     unascribed
 * @version    1.12, 02/02/00
 * @since      JDK1.0
 */
public class ClassCircularityError extends LinkageError {
    /**
     * Constructs a <code>ClassCircularityError</code> with no detail  message.
     */
    public ClassCircularityError() {
	super();
    }

    /**
     * Constructs a <code>ClassCircularityError</code> with the 
     * specified detail message. 
     *
     * @param      s   the detail message.
     */
    public ClassCircularityError(String s) {
	super(s);
    }
}
