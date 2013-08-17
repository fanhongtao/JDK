/*
 * @(#)InvalidClassException.java	1.7 01/12/10
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
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
 * @version 1.7, 12/10/01
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
