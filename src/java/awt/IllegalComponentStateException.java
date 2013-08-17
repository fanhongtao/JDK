/*
 * @(#)IllegalComponentStateException.java	1.2 00/01/12
 *
 * Copyright 1996-2000 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */

package java.awt;

/**
 * Signals that an AWT component is not in an appropriate state for
 * the requested operation.
 *
 * @version 	1.7, 09/21/98
 * @author	Jonni Kanerva
 */
public class IllegalComponentStateException extends IllegalStateException {
    /*
     * JDK 1.1 serialVersionUID 
     */
     private static final long serialVersionUID = -1889339587208144238L;

    /**
     * Constructs an IllegalComponentStateException with no detail message.
     * A detail message is a String that describes this particular exception.
     */
    public IllegalComponentStateException() {
	super();
    }

    /**
     * Constructs an IllegalComponentStateException with the specified detail
     * message.  A detail message is a String that describes this particular
     * exception.
     * @param s the String that contains a detailed message
     */
    public IllegalComponentStateException(String s) {
	super(s);
    }
}
