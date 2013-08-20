/*
 * @(#)StreamCorruptedException.java	1.14 03/12/19
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.io;

/**
 * Thrown when control information that was read from an object stream
 * violates internal consistency checks.
 *
 * @author  unascribed
 * @version 1.14, 12/19/03
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
