/*
 * @(#)NotSerializableException.java	1.5 98/07/01
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

/** Raised by a class or the serialization runtime when a class
 * may not be serialized. The argument should be the name of the class.
 *
 * @author  unascribed
 * @version 1.5, 07/01/98
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
