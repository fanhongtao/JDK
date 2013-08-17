/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.io;

/**
 * Thrown when an instance is required to have a Serializable interface.
 * The serialization runtime or the class of the instance can throw
 * this exception. The argument should be the name of the class.
 *
 * @author  unascribed
 * @version 1.12, 02/06/02
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
