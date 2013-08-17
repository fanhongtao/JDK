/*
 * @(#)InvalidPreferencesFormatException.java	1.4 01/12/03
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.util.prefs;

import java.io.NotSerializableException;

/**
 * Thrown to indicate that an operation could not complete because
 * the input did not conform to the appropriate XML document type
 * for a collection of preferences, as per the {@link Preferences} 
 * specification.<p>
 * 
 * Note, that although InvalidPreferencesFormatException inherits Serializable
 * interface from Exception, it is not intended to be Serializable. Appropriate
 * serialization methods are implemented to throw NotSerializableException.
 *
 * @author  Josh Bloch
 * @version 1.4, 12/03/01
 * @see     Preferences
 * @since   1.4
 * @serial exclude
 */

public class InvalidPreferencesFormatException extends Exception {
    /**
     * Constructs an InvalidPreferencesFormatException with the specified
     * cause.
     *
     * @param  cause the cause (which is saved for later retrieval by the
     *         {@link Throwable#getCause()} method).
     */
    public InvalidPreferencesFormatException(Throwable cause) {
        super(cause);
    }

   /**
    * Constructs an InvalidPreferencesFormatException with the specified
    * detail message.
    *
    * @param   message   the detail message. The detail message is saved for 
    *          later retrieval by the {@link Throwable#getMessage()} method.
    */
    public InvalidPreferencesFormatException(String message) {
        super(message);
    }

    /**
     * Constructs an InvalidPreferencesFormatException with the specified
     * detail message and cause.
     *
     * @param  message   the detail message. The detail message is saved for
     *         later retrieval by the {@link Throwable#getMessage()} method.
     * @param  cause the cause (which is saved for later retrieval by the
     *         {@link Throwable#getCause()} method).
     */
    public InvalidPreferencesFormatException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Throws NotSerializableException, since InvalidPreferencesFormatException
     * objects are not intended to be serializable.
     */
     private void writeObject(java.io.ObjectOutputStream out)
                                               throws NotSerializableException {
         throw new NotSerializableException("Not serializable.");
     }

    /**
     * Throws NotSerializableException, since InvalidPreferencesFormatException
     * objects are not intended to be serializable.
     */
     private void readObject(java.io.ObjectInputStream in)
                                               throws NotSerializableException {
         throw new NotSerializableException("Not serializable.");
     }

}
