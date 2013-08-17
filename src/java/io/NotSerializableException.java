/*
 * @(#)NotSerializableException.java	1.2 00/01/12
 *
 * Copyright 1996-2000 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */

package java.io;

/**
 * Thrown when an instance is required to have a Serializable interface.
 * The serialization runtime or the class of the instance can throw
 * this exception. The argument should be the name of the class.
 *
 * @author  unascribed
 * @version 1.8, 06/29/98
 * @since   JDK1.1
 */
public class NotSerializableException extends ObjectStreamException {
    /**
     * @param classname Class of the instance being serialized/deserialized.
     */
    public NotSerializableException(String classname) {
	super(classname);
    }

    /**
     */
    public NotSerializableException() {
	super();
    }
}
