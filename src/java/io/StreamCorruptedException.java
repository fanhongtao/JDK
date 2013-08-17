/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.io;

/**
 * Thrown when control information that was read from an object stream
 * violates internal consistency checks.
 *
 * @author  unascribed
 * @version 1.12, 02/06/02
 * @since   JDK1.1
 */
public class StreamCorruptedException extends ObjectStreamException {
    /**
     * Create a StreamCorruptedException and list a reason why thrown.
     *
     * @param reason  String describing the reason for the exception.
     */
    public StreamCorruptedException(String reason) {
	super(reason);
    }

    /**
     * Create a StreamCorruptedException and list no reason why thrown.
     */
    public StreamCorruptedException() {
	super();
    }
}
