/*
 * @(#)FileInputStream.java	1.34 98/07/01
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
 * A file input stream is an input stream for reading data from a 
 * <code>File</code> or from a <code>FileDescriptor</code>. 
 *
 * @author  Arthur van Hoff
 * @version 1.34, 07/01/98
 * @see     java.io.File
 * @see     java.io.FileDescriptor
 * @see	    java.io.FileOutputStream
 * @since   JDK1.0
 */
public
class FileInputStream extends InputStream 
{
    /* File Descriptor - handle to the open file */
    private FileDescriptor fd;
    
    /**
     * Creates an input file stream to read from a file with the 
     * specified name. 
     *
     * @param      name   the system-dependent file name.
     * @exception  FileNotFoundException  if the file is not found.
     * @exception  SecurityException      if a security manager exists, its
     *               <code>checkRead</code> method is called with the name
     *               argument to see if the application is allowed read access
     *               to the file.
     * @see        java.lang.SecurityManager#checkRead(java.lang.String)
     * @since      JDK1.0
     */
    public FileInputStream(String name) throws FileNotFoundException {
	SecurityManager security = System.getSecurityManager();
	if (security != null) {
	    security.checkRead(name);
	}
	try {
	    fd = new FileDescriptor();
	    open(name);
	} catch (IOException e) {
	    throw new FileNotFoundException(name);
	}
    }
    
    /**
     * Creates an input file stream to read from the specified 
     * <code>File</code> object. 
     *
     * @param      file   the file to be opened for reading.
     * @exception  FileNotFoundException  if the file is not found.
     * @exception  SecurityException      if a security manager exists, its
     *               <code>checkRead</code> method is called with the pathname
     *               of this <code>File</code> argument to see if the
     *               application is allowed read access to the file.
     * @see        java.io.File#getPath()
     * @see        java.lang.SecurityManager#checkRead(java.lang.String)
     * @since      JDK1.0
     */
    public FileInputStream(File file) throws FileNotFoundException {
	this(file.getPath());
    }

    /**
     * Creates an input file stream to read from the specified file descriptor.
     *
     * @param      fdObj   the file descriptor to be opened for reading.
     * @exception  SecurityException  if a security manager exists, its
     *               <code>checkRead</code> method is called with the file
     *               descriptor to see if the application is allowed to read
     *               from the specified file descriptor.
     * @see        java.lang.SecurityManager#checkRead(java.io.FileDescriptor)
     * @since      JDK1.0
     */
    public FileInputStream(FileDescriptor fdObj) {
	SecurityManager security = System.getSecurityManager();
	if (fdObj == null) {
	    throw new NullPointerException();
	}
	if (security != null) {
	    security.checkRead(fdObj);
	}
	fd = fdObj;
    }

    /**
     * Opens the specified file for reading.
     * @param name the name of the file
     */
    private native void open(String name) throws IOException;

    /**
     * Reads a byte of data from this input stream. This method blocks 
     * if no input is yet available. 
     *
     * @return     the next byte of data, or <code>-1</code> if the end of the
     *             file is reached.
     * @exception  IOException  if an I/O error occurs.
     * @since      JDK1.0
     */
    public native int read() throws IOException;


    /** 
     * Reads a subarray as a sequence of bytes. 
     * @param b the data to be written
     * @param off the start offset in the data
     * @param len the number of bytes that are written
     * @exception IOException If an I/O error has occurred. 
     */ 
    private native int readBytes(byte b[], int off, int len) throws IOException;

    /**
     * Reads up to <code>b.length</code> bytes of data from this input 
     * stream into an array of bytes. This method blocks until some input 
     * is available. 
     *
     * @param      b   the buffer into which the data is read.
     * @return     the total number of bytes read into the buffer, or
     *             <code>-1</code> if there is no more data because the end of
     *             the file has been reached.
     * @exception  IOException  if an I/O error occurs.
     * @since      JDK1.0
     */
    public int read(byte b[]) throws IOException {
	return readBytes(b, 0, b.length);
    }

    /**
     * Reads up to <code>len</code> bytes of data from this input stream 
     * into an array of bytes. This method blocks until some input is 
     * available. 
     *
     * @param      b     the buffer into which the data is read.
     * @param      off   the start offset of the data.
     * @param      len   the maximum number of bytes read.
     * @return     the total number of bytes read into the buffer, or
     *             <code>-1</code> if there is no more data because the end of
     *             the file has been reached.
     * @exception  IOException  if an I/O error occurs.
     * @since      JDK1.0
     */
    public int read(byte b[], int off, int len) throws IOException {
	return readBytes(b, off, len);
    }

    /**
     * Skips over and discards <code>n</code> bytes of data from the 
     * input stream. The <code>skip</code> method may, for a variety of 
     * reasons, end up skipping over some smaller number of bytes, 
     * possibly <code>0</code>. The actual number of bytes skipped is returned.
     *
     * @param      n   the number of bytes to be skipped.
     * @return     the actual number of bytes skipped.
     * @exception  IOException  if an I/O error occurs.
     * @since      JDK1.0
     */
    public native long skip(long n) throws IOException;

    /**
     * Returns the number of bytes that can be read from this file input
     * stream without blocking.
     *
     * @return     the number of bytes that can be read from this file input
     *             stream without blocking.
     * @exception  IOException  if an I/O error occurs.
     * @since      JDK1.0
     */
    public native int available() throws IOException;

    /**
     * Closes this file input stream and releases any system resources 
     * associated with the stream. 
     *
     * @exception  IOException  if an I/O error occurs.
     * @since      JDK1.0
     */
    public native void close() throws IOException;

    /**
     * Returns the opaque file descriptor object associated with this stream.
     *
     * @return     the file descriptor object associated with this stream.
     * @exception  IOException  if an I/O error occurs.
     * @see        java.io.FileDescriptor
     * @since      JDK1.0
     */
    public final FileDescriptor getFD() throws IOException {
	if (fd != null) return fd;
	throw new IOException();
    }

    /**
     * Ensures that the <code>close</code> method of this file input stream is
     * called when there are no more references to it. 
     *
     * @exception  IOException  if an I/O error occurs.
     * @see        java.io.FileInputStream#close()
     * @since      JDK1.0
     */
    protected void finalize() throws IOException {
	if (fd != null) {
	    if (fd != fd.in) {
		close();
	    }
	}
    }
}
