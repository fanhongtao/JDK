/*
 * @(#)SyncFailedException.java	1.14 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.io;

/**
 * Signals that a sync operation has failed.
 *
 * @author  Ken Arnold
 * @version 1.14, 01/23/03
 * @see     java.io.FileDescriptor#sync
 * @see	    java.io.IOException
 * @since   JDK1.1
 */
public class SyncFailedException extends IOException {
    /**
     * Constructs an SyncFailedException with a detail message.
     * A detail message is a String that describes this particular exception.
     *
     * @param desc  a String describing the exception.
     */
    public SyncFailedException(String desc) {
	super(desc);
    }
}
