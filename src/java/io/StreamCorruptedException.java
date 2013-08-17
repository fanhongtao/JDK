/*
 * @(#)StreamCorruptedException.java	1.9 01/11/29
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.io;

/**
 * Thrown when control information that was read from an object stream
 * violates internal consistency checks.
 *
 * @author  unascribed
 * @version 1.9, 11/29/01
 * @since   JDK1.1
 */
public class StreamCorruptedException extends ObjectStreamException {
    /**
     * Create a StreamCorruptedException and list a reason why thrown.
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
