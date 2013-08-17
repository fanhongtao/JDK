/*
 * @(#)InvalidClassException.java	1.13 98/06/29
 *
 * Copyright 1996-1998 by Sun Microsystems, Inc.,
 * 901 San Antonio Road, Palo Alto, California, 94303, U.S.A.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
 */

package java.io;

/**
 * Thrown when the Serialization runtime detects one of the following
 * problems with a Class.
 * <UL>
 * <LI> The serial version of the class does not match that of the class
 *      descriptor read from the stream
 * <LI> The class contains unknown datatypes
 * <LI> The class does not have an accessible no-arg constructor
 * </UL>
 *
 * @author  unascribed
 * @version 1.13, 06/29/98
 * @since   JDK1.1
 */
public class InvalidClassException extends ObjectStreamException {
    /**
     * @serial Name of the invalid class.
     */
    public String classname;

    /**
     * Report a InvalidClassException for the reason specified.
     */
    public InvalidClassException(String reason) {
	super(reason);
    }

    /**
     */
    public InvalidClassException(String cname, String reason) {
	super(reason);
	classname = cname;
    }

    /**
     * Produce the message and include the classname, if present.
     */
    public String getMessage() {
	if (classname == null)
	    return super.getMessage();
	else
	    return classname + "; " + super.getMessage();
    }
}
