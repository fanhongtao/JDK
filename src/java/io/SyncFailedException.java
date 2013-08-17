/*
 * @(#)SyncFailedException.java	1.9 01/11/29
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.io;

/*
 * Signals that a sync operation has failed.
 *
 * @author  Ken Arnold
 * @version 1.9, 11/29/01
 * @see     java.io.FileDescriptor#sync
 * @see	    java.io.IOException
 * @since   JDK1.1
 */
public class SyncFailedException extends IOException {
    /**
     * Constructs an SyncFailedException with a detail message.
     * A detail message is a String that describes this particular exception.
     */
    public SyncFailedException(String desc) {
	super(desc);
    }
}
