/*
 * @(#)IllegalComponentStateException.java	1.4 98/07/01
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

package java.awt;

/**
 * Signals that an AWT component is not in an appropriate state for
 * the requested operation.
 *
 * @version 	1.4, 07/01/98
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
