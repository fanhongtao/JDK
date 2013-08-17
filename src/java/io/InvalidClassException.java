/*
 * @(#)InvalidClassException.java	1.6 98/07/01
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

package java.io;

/**
 * Raised when the Serialization runtime detects a problem with a Class.
 * The class may: <UL>
 * <LI> not match the serial version of the class in the stream
 * <LI> the class contains unknown datatypes
 * <LI> the class implements only one of writeObject or readObject methods
 * <LI> the class is not public
 * <LI> the class does not have an accessible no-arg constructor
 * </UL>
 *
 * @author  unascribed
 * @version 1.6, 07/01/98
 * @since   JDK1.1
 */
public class InvalidClassException extends ObjectStreamException {
    /**
     * @since   JDK1.1
     */
    public String classname;

    /**
     * Report a InvalidClassException for the specified reason.
     * @since   JDK1.1
     */
    public InvalidClassException(String reason) {
	super(reason);
    }

    /**
     * @since   JDK1.1
     */
    public InvalidClassException(String cname, String reason) {
	super(reason);
	classname = cname;
    }

    /**
     * Produce the message, include the classname if present.
     * @since   JDK1.1
     */
    public String getMessage() {
	if (classname == null) 
	    return super.getMessage();
	else
	    return classname + "; " + super.getMessage();
    }
}
