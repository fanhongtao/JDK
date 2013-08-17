/*
 * @(#)FileDescriptor.java	1.11 98/07/01
 *
 * Copyright 1995-1998 by Sun Microsystems, Inc.,
 * 901 San Antonio Road, Palo Alto, California, 94303, U.S.A.
 * All rights reserved.
 * 
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
 */

package java.io;

/**
 * Instances of the file descriptor class serve as an opaque handle 
 * to the underlying machine-specific structure representing an open 
 * file or an open socket. 
 * <p>
 * Applications should not create their own file descriptors. 
 *
 * @author  Pavani Diwanji
 * @version 1.11, 07/01/98
 * @see	    java.io.FileInputStream
 * @see	    java.io.FileOutputStream
 * @see     java.net.SocketInputStream
 * @see     java.net.SocketOutputStream
 * @since   JDK1.0
 */
public final class FileDescriptor {

    private int fd; 

    /**
     * A handle to the standard input stream. 
     *
     * @since   JDK1.0
     */    
    public static final FileDescriptor in 
	= initSystemFD(new FileDescriptor(),0);

    /**
     * A handle to the standard output stream. 
     *
     * @since   JDK1.0
     */  
    public static final FileDescriptor out 
	= initSystemFD(new FileDescriptor(),1);

    /**
     * A handle to the standard error stream. 
     *
     * @since   JDK1.0
     */  
    public static final FileDescriptor err 
	= initSystemFD(new FileDescriptor(),2);

    /**
     * Tests if this file descriptor object is valid.
     *
     * @return  <code>true</code> if the file descriptor object represents a
     *          valid, open file or socket; <code>false</code> otherwise.
     * @since   JDK1.0
     */
    public native boolean valid();

    /**
     * Force all system buffers to synchronize with the underlying
     * device.  This method returns after all modified data and
     * attributes of this FileDescriptor have been written to the
     * relevant device(s).  In particular, if this FileDescriptor
     * refers to a physical storage medium, such as a file in a file
     * system, sync will not return until all in-memory modified copies
     * of buffers associated with this FileDesecriptor have been
     * written to the physical medium.
     *
     * sync is meant to be used by code that requires physical
     * storage (such as a file) to be in a known state  For
     * example, a class that provided a simple transaction facility
     * might use sync to ensure that all changes to a file caused
     * by a given transaction were recorded on a storage medium.
     *
     * sync only affects buffers downstream of this FileDescriptor.  If
     * any in-memory buffering is being done by the application (for
     * example, by a BufferedOutputStream object), those buffers must
     * be flushed into the FileDescriptor (for example, by invoking
     * OutputStream.flush) before that data will be affected by sync.
     *
     * @exception SyncFailedException
     *	      Thrown when the buffers cannot be flushed,
     *	      or because the system cannot guarantee that all the
     *	      buffers have been synchronized with physical media.
     * @since     JDK1.1
     */
    public native void sync() throws SyncFailedException;

    /**
     * This routine initializes in, out and err in a sytem dependent way.
     */
    private static native FileDescriptor initSystemFD(FileDescriptor fdObj, 
	int desc);
}
