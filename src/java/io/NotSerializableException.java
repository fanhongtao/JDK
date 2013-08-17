/*
 * @(#)NotSerializableException.java	1.6 01/12/10
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.io;

/** Raised by a class or the serialization runtime when a class
 * may not be serialized. The argument should be the name of the class.
 *
 * @author  unascribed
 * @version 1.6, 12/10/01
 * @since   JDK1.1
 */
public class NotSerializableException extends ObjectStreamException {
    /**
     * @since   JDK1.1
     */
    public NotSerializableException(String classname) {
	super(classname);
    }

    /**
     * @since   JDK1.1
     */
    public NotSerializableException() {
	super();
    }
}
