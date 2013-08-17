/*
 * @(#)SyncFailedException.java	1.7 01/12/10
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.io;

/*
 * Signals that a sync operation has failed.
 *
 * @author  Ken Arnold
 * @version 1.7, 12/10/01
 * @see     java.io.FileDescriptor#sync
 * @see	    java.io.IOException
 * @since   JDK1.1
 */
public class SyncFailedException extends IOException {
    /**
     * Constructs an SyncFailedException with a detail message.
     * A detail message is a String that describes this particular exception.
     *
     * @since   JDK1.1
     */
    public SyncFailedException(String desc) {
	super(desc);
    }
}
