/*
 * @(#)InvalidClassException.java	1.2 00/01/12
 *
 * Copyright 1996-2000 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
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
