/*
 * @(#)NotSerializableException.java	1.11 00/02/02
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
 * @version 1.11, 02/02/00
 * @since   JDK1.1
 */
public class NotSerializableException extends ObjectStreamException {
    /** 
     * Constructs a NotSerializableException object with message string. 
     *
     * @param classname Class of the instance being serialized/deserialized.
     */
    public NotSerializableException(String classname) {
	super(classname);
    }

    /**
     *  Constructs a NotSerializableException object.
     */
    public NotSerializableException() {
	super();
    }
}
