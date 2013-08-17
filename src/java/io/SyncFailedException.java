/*
 * @(#)SyncFailedException.java	1.2 00/01/12
 *
 * Copyright 1996-2000 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */

package java.io;

/*
 * Signals that a sync operation has failed.
 *
 * @author  Ken Arnold
 * @version 1.8, 09/21/98
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
