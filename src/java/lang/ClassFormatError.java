/*
 * @(#)ClassFormatError.java	1.2 00/01/12
 *
 * Copyright 1994-2000 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */

package java.lang;

/**
 * Thrown when the Java Virtual Machine attempts to read a class 
 * file and determines that the file is malformed or otherwise cannot 
 * be interpreted as a class file. 
 *
 * @author  unascribed
 * @version 1.15, 09/21/98
 * @since   JDK1.0
 */
public
class ClassFormatError extends LinkageError {
    /**
     * Constructs a <code>ClassFormatError</code> with no detail message. 
     */
    public ClassFormatError() {
	super();
    }

    /**
     * Constructs a <code>ClassFormatError</code> with the specified 
     * detail message. 
     *
     * @param   s   the detail message.
     */
    public ClassFormatError(String s) {
	super(s);
    }
}
