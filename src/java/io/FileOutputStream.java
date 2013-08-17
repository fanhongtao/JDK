/*
 * @(#)FileOutputStream.java	1.27 98/07/01
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
import java.io.File;

/**
 * A file output stream is an output stream for writing data to a 
 * <code>File</code> or to a <code>FileDescriptor</code>. 
 *
 * @author  Arthur van Hoff
 * @version 1.27, 07/01/98
 * @see     java.io.File
 * @see     java.io.FileDescriptor
 * @see     java.io.FileInputStream
 * @since   JDK1.0
 */
public
class FileOutputStream extends OutputStream
{
    /**
     * The system dependent file descriptor. The value is
     * 1 more than actual file descriptor. This means that
     * the default value 0 indicates that the file is not open.
     */
    private FileDescriptor fd;

    /**
     * Creates an output file stream to write to the file with the 
     * specified name. 
     *
     * @param      name   the system-dependent filename.
     * @exception  IOException  if the file could not be opened for writing.
     * @exception  SecurityException  if a security manager exists, its
     *               <code>checkWrite</code> method is called with the name
     *               argument to see if the application is allowed write access
     *               to the file.
     * @see        java.lang.SecurityManager#checkWrite(java.lang.String)
     * @since      JDK1.0
     */
    public FileOutputStream(String name) throws IOException {
	SecurityManager security = System.getSecurityManager();
	if (security != null) {
	    security.checkWrite(name);
	}
	try {
	    fd = new FileDescriptor();
	    open(name);
	} catch (IOException e) {
	    throw new FileNotFoundException(name);
	}
    }

    /**
     * Creates an output file with the specified system dependent
     * file name.
     * @param name the system dependent file name 
     * @exception IOException If the file is not found.
     * @since     JDK1.1
     */
    public FileOutputStream(String name, boolean append) throws IOException {
	SecurityManager security = System.getSecurityManager();
	if (security != null) {
	    security.checkWrite(name);
	}
	try {
	    fd = new FileDescriptor();
	    if(append)
		openAppend(name);
	    else
		open(name);
	} catch (IOException e) {
	    throw new FileNotFoundException(name);
	}
    }
    
    /**
     * Creates a file output stream to write to the specified 
     * <code>File</code> object. 
     *
     * @param      file   the file to be opened for writing.
     * @exception  IOException        if the file could not be opened for
     *               writing.
     * @exception  SecurityException  if a security manager exists, its
     *               <code>checkWrite</code> method is called with the pathname
     *               of the <code>File</code> argument to see if the
     *               application is allowed write access to the file. This may
     *               result in a security exception.
     * @see        java.io.File#getPath()
     * @see        java.lang.SecurityException
     * @see        java.lang.SecurityManager#checkWrite(java.lang.String)
     * @since      JDK1.0
     */
    public FileOutputStream(File file) throws IOException {
	this(file.getPath());
    }

    /**
     * Creates an output file stream to write to the specified file descriptor.
     *
     * @param      fdObj   the file descriptor to be opened for writing.
     * @exception  SecurityException  if a security manager exists, its
     *               <code>checkWrite</code> method is called with the file
     *               descriptor to see if the application is allowed to write
     *               to the specified file descriptor.
     * @see        java.lang.SecurityManager#checkWrite(java.io.FileDescriptor)
     * @since      JDK1.0
     */
    public FileOutputStream(FileDescriptor fdObj) {
	SecurityManager security = System.getSecurityManager();
	if (fdObj == null) {
	    throw new NullPointerException();
	}
	if (security != null) {
	    security.checkWrite(fdObj);
	}
	fd = fdObj;
    }

    /**
     * Opens a file, with the specified name, for writing.
     * @param name name of file to be opened
     */
    private native void open(String name) throws IOException;

    /**
     * Opens a file, with the specified name, for appending.
     * @param name name of file to be opened
     */
    private native void openAppend(String name) throws IOException;

    /**
     * Writes the specified byte to this file output stream. 
     *
     * @param      b   the byte to be written.
     * @exception  IOException  if an I/O error occurs.
     * @since      JDK1.0
     */
    public native void write(int b) throws IOException;

    /**
     * Writes a sub array as a sequence of bytes.
     * @param b the data to be written
     * @param off the start offset in the data
     * @param len the number of bytes that are written
     * @exception IOException If an I/O error has occurred.
     */
    private native void writeBytes(byte b[], int off, int len) throws IOException;

    /**
     * Writes <code>b.length</code> bytes from the specified byte array 
     * to this file output stream. 
     *
     * @param      b   the data.
     * @exception  IOException  if an I/O error occurs.
     * @since      JDK1.0
     */
    public void write(byte b[]) throws IOException {
	writeBytes(b, 0, b.length);
    }

    /**
     * Writes <code>len</code> bytes from the specified byte array 
     * starting at offset <code>off</code> to this file output stream. 
     *
     * @param      b     the data.
     * @param      off   the start offset in the data.
     * @param      len   the number of bytes to write.
     * @exception  IOException  if an I/O error occurs.
     * @since      JDK1.0
     */
    public void write(byte b[], int off, int len) throws IOException {
	writeBytes(b, off, len);
    }

    /**
     * Closes this file output stream and releases any system resources 
     * associated with this stream. 
     *
     * @exception  IOException  if an I/O error occurs.
     * @since      JDK1.0
     */
     public native void close() throws IOException;

     /**
      * Returns the file descriptor associated with this stream.
     *
     * @return  the file descriptor object associated with this stream.
     * @exception  IOException  if an I/O error occurs.
     * @see        java.io.FileDescriptor
     * @since      JDK1.0
      */
     public final FileDescriptor getFD()  throws IOException {
	if (fd != null) return fd;
	throw new IOException();
     }
    
    /**
     * Ensures that the <code>close</code> method of this file output stream is
     * called when there are no more references to this stream. 
     *
     * @exception  IOException  if an I/O error occurs.
     * @see        java.io.FileInputStream#close()
     * @since      JDK1.0
     */
    protected void finalize() throws IOException {
 	if (fd != null) {
 	    if (fd == fd.out || fd == fd.err) {
 		flush();
 	    } else {
 		close();
 	    }
 	}
    }
}
