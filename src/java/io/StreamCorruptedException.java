/*
 * @(#)StreamCorruptedException.java	1.6 01/12/10
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.io;

/**
 * Raised when control information read from an object stream
 * violates internal consistency checks.
 *
 * @author  unascribed
 * @version 1.6, 12/10/01
 * @since   JDK1.1
 */
public class StreamCorruptedException extends ObjectStreamException {
    /**
     * Create a StreamCorruptedException with a reason.
     * @since   JDK1.1
     */
    public StreamCorruptedException(String reason) {
	super(reason);
    }

    /**
     * Create a StreamCorruptedException with no reason.
     * @since   JDK1.1
     */
    public StreamCorruptedException() {
	super();
    }
}
