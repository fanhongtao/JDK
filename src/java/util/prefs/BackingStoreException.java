/*
 * @(#)BackingStoreException.java	1.5 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.util.prefs;

import java.io.NotSerializableException;

/**
 * Thrown to indicate that a preferences operation could not complete because
 * of a failure in the backing store, or a failure to contact the backing
 * store. <p>
 *
 * Note, that although BackingStoreException inherits Serializable interface 
 * from Exception, it is not intended to be Serializable. Appropriate
 * serialization methods are implemented to throw NotSerializableException.
 *
 * @author  Josh Bloch
 * @version 1.5, 01/23/03
 * @since   1.4
 * @serial exclude
 */

public class BackingStoreException extends Exception {
    /**
     * Constructs a BackingStoreException with the specified detail message.
     *
     * @param s the detail message.
     */
    public BackingStoreException(String s) {
        super(s);
    }

    /**
     * Constructs a BackingStoreException with the specified cause.
     *
     * @param cause the cause
     */
    public BackingStoreException(Throwable cause) {
        super(cause);
    }

    /**
     * Throws NotSerializableException, since BackingStoreException objects
     * are not intended to be serializable.
     */
     private void writeObject(java.io.ObjectOutputStream out)
                                               throws NotSerializableException {
         throw new NotSerializableException("Not serializable.");
     }

    /**
     * Throws NotSerializableException, since BackingStoreException objects
     * are not intended to be serializable.
     */
     private void readObject(java.io.ObjectInputStream in)
                                               throws NotSerializableException {
         throw new NotSerializableException("Not serializable.");
     }
}
