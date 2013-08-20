/*
 * @(#)BackingStoreException.java	1.7 03/12/19
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.util.prefs;

import java.io.NotSerializableException;

/**
 * Thrown to indicate that a preferences operation could not complete because
 * of a failure in the backing store, or a failure to contact the backing
 * store.
 *
 * @author  Josh Bloch
 * @version 1.7, 12/19/03
 * @since   1.4
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

    private static final long serialVersionUID = 859796500401108469L;
}
