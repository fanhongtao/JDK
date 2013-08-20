/*
 * @(#)FileOutputStream.java	1.57 03/12/19
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.io;

import java.nio.channels.FileChannel;
import sun.nio.ch.FileChannelImpl;


/**
 * A file output stream is an output stream for writing data to a 
 * <code>File</code> or to a <code>FileDescriptor</code>. Whether or not
 * a file is available or may be created depends upon the underlying
 * platform.  Some platforms, in particular, allow a file to be opened
 * for writing by only one <tt>FileOutputStream</tt> (or other
 * file-writing object) at a time.  In such situations the constructors in
 * this class will fail if the file involved is already open.
 *
 * <p><code>FileOutputStream</code> is meant for writing streams of raw bytes
 * such as image data. For writing streams of characters, consider using
 * <code>FileWriter</code>.
 *
 * @author  Arthur van Hoff
 * @version 1.57, 12/19/03
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

    private FileChannel channel= null;

    private boolean append = false;

    /**
     * Creates an output file stream to write to the file with the 
     * specified name. A new <code>FileDescriptor</code> object is 
     * created to represent this file connection.
     * <p>
     * First, if there is a security manager, its <code>checkWrite</code> 
     * method is called with <code>name</code> as its argument.
     * <p>
     * If the file exists but is a directory rather than a regular file, does
     * not exist but cannot be created, or cannot be opened for any other
     * reason then a <code>FileNotFoundException</code> is thrown.
     *
     * @param      name   the system-dependent filename
     * @exception  FileNotFoundException  if the file exists but is a directory
     *                   rather than a regular file, does not exist but cannot
     *                   be created, or cannot be opened for any other reason
     * @exception  SecurityException  if a security manager exists and its
     *               <code>checkWrite</code> method denies write access
     *               to the file.
     * @see        java.lang.SecurityManager#checkWrite(java.lang.String)
     */
    public FileOutputStream(String name) throws FileNotFoundException {
	this(name != null ? new File(name) : null, false);
    }

    /**
     * Creates an output file stream to write to the file with the specified
     * <code>name</code>.  If the second argument is <code>true</code>, then
     * bytes will be written to the end of the file rather than the beginning.
     * A new <code>FileDescriptor</code> object is created to represent this
     * file connection.
     * <p>
     * First, if there is a security manager, its <code>checkWrite</code> 
     * method is called with <code>name</code> as its argument.
     * <p>
     * If the file exists but is a directory rather than a regular file, does
     * not exist but cannot be created, or cannot be opened for any other
     * reason then a <code>FileNotFoundException</code> is thrown.
     * 
     * @param     name        the system-dependent file name
     * @param     append      if <code>true</code>, then bytes will be written
     *                   to the end of the file rather than the beginning
     * @exception  FileNotFoundException  if the file exists but is a directory
     *                   rather than a regular file, does not exist but cannot
     *                   be created, or cannot be opened for any other reason.
     * @exception  SecurityException  if a security manager exists and its
     *               <code>checkWrite</code> method denies write access
     *               to the file.
     * @see        java.lang.SecurityManager#checkWrite(java.lang.String)
     * @since     JDK1.1
     */
    public FileOutputStream(String name, boolean append)
        throws FileNotFoundException
    {
        this(name != null ? new File(name) : null, append);
    }

    /**
     * Creates a file output stream to write to the file represented by 
     * the specified <code>File</code> object. A new 
     * <code>FileDescriptor</code> object is created to represent this 
     * file connection.
     * <p>
     * First, if there is a security manager, its <code>checkWrite</code> 
     * method is called with the path represented by the <code>file</code> 
     * argument as its argument.
     * <p>
     * If the file exists but is a directory rather than a regular file, does
     * not exist but cannot be created, or cannot be opened for any other
     * reason then a <code>FileNotFoundException</code> is thrown.
     *
     * @param      file               the file to be opened for writing.
     * @exception  FileNotFoundException  if the file exists but is a directory
     *                   rather than a regular file, does not exist but cannot
     *                   be created, or cannot be opened for any other reason
     * @exception  SecurityException  if a security manager exists and its
     *               <code>checkWrite</code> method denies write access
     *               to the file.
     * @see        java.io.File#getPath()
     * @see        java.lang.SecurityException
     * @see        java.lang.SecurityManager#checkWrite(java.lang.String)
     */
    public FileOutputStream(File file) throws FileNotFoundException {
	this(file, false);
    }

    /**
     * Creates a file output stream to write to the file represented by 
     * the specified <code>File</code> object. If the second argument is
     * <code>true</code>, then bytes will be written to the end of the file
     * rather than the beginning. A new <code>FileDescriptor</code> object is
     * created to represent this file connection.
     * <p>
     * First, if there is a security manager, its <code>checkWrite</code> 
     * method is called with the path represented by the <code>file</code> 
     * argument as its argument.
     * <p>
     * If the file exists but is a directory rather than a regular file, does
     * not exist but cannot be created, or cannot be opened for any other
     * reason then a <code>FileNotFoundException</code> is thrown.
     *
     * @param      file               the file to be opened for writing.
     * @param     append      if <code>true</code>, then bytes will be written
     *                   to the end of the file rather than the beginning
     * @exception  FileNotFoundException  if the file exists but is a directory
     *                   rather than a regular file, does not exist but cannot
     *                   be created, or cannot be opened for any other reason
     * @exception  SecurityException  if a security manager exists and its
     *               <code>checkWrite</code> method denies write access
     *               to the file.
     * @see        java.io.File#getPath()
     * @see        java.lang.SecurityException
     * @see        java.lang.SecurityManager#checkWrite(java.lang.String)
     * @since 1.4
     */
    public FileOutputStream(File file, boolean append)
        throws FileNotFoundException
    {
        String name = (file != null ? file.getPath() : null);
	SecurityManager security = System.getSecurityManager();
	if (security != null) {
	    security.checkWrite(name);
	}
        if (name == null) {
            throw new NullPointerException();
        }
	fd = new FileDescriptor();
        this.append = append;
	if (append) {
	    openAppend(name);
	} else {
	    open(name);
	}
    }

    /**
     * Creates an output file stream to write to the specified file 
     * descriptor, which represents an existing connection to an actual 
     * file in the file system.
     * <p>
     * First, if there is a security manager, its <code>checkWrite</code> 
     * method is called with the file descriptor <code>fdObj</code> 
     * argument as its argument.
     *
     * @param      fdObj   the file descriptor to be opened for writing
     * @exception  SecurityException  if a security manager exists and its
     *               <code>checkWrite</code> method denies
     *               write access to the file descriptor
     * @see        java.lang.SecurityManager#checkWrite(java.io.FileDescriptor)
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
    private native void open(String name) throws FileNotFoundException;

    /**
     * Opens a file, with the specified name, for appending.
     * @param name name of file to be opened
     */
    private native void openAppend(String name) throws FileNotFoundException;

    /**
     * Writes the specified byte to this file output stream. Implements 
     * the <code>write</code> method of <code>OutputStream</code>.
     *
     * @param      b   the byte to be written.
     * @exception  IOException  if an I/O error occurs.
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
     */
    public void write(byte b[], int off, int len) throws IOException {
	writeBytes(b, off, len);
    }

    /**
     * Closes this file output stream and releases any system resources 
     * associated with this stream. This file output stream may no longer 
     * be used for writing bytes. 
     *
     * <p> If this stream has an associated channel then the channel is closed
     * as well.
     *
     * @exception  IOException  if an I/O error occurs.
     *
     * @revised 1.4
     * @spec JSR-51
     */
    public void close() throws IOException {
        if (channel != null)
            channel.close();
        close0();
    }

    /**
     * Returns the file descriptor associated with this stream.
     *
     * @return  the <code>FileDescriptor</code> object that represents 
     *          the connection to the file in the file system being used 
     *          by this <code>FileOutputStream</code> object. 
     * 
     * @exception  IOException  if an I/O error occurs.
     * @see        java.io.FileDescriptor
     */
     public final FileDescriptor getFD()  throws IOException {
	if (fd != null) return fd;
	throw new IOException();
     }
    
    /**
     * Returns the unique {@link java.nio.channels.FileChannel FileChannel}
     * object associated with this file output stream. </p>
     *
     * <p> The initial {@link java.nio.channels.FileChannel#position()
     * </code>position<code>} of the returned channel will be equal to the
     * number of bytes written to the file so far unless this stream is in
     * append mode, in which case it will be equal to the size of the file.
     * Writing bytes to this stream will increment the channel's position
     * accordingly.  Changing the channel's position, either explicitly or by
     * writing, will change this stream's file position.
     *
     * @return  the file channel associated with this file output stream
     *
     * @since 1.4
     * @spec JSR-51
     */
    public FileChannel getChannel() {
	synchronized (this) {
	    if (channel == null)
		channel = FileChannelImpl.open(fd, false, true, this, append);
	    return channel;
	}
    }

    /**
     * Cleans up the connection to the file, and ensures that the 
     * <code>close</code> method of this file output stream is
     * called when there are no more references to this stream. 
     *
     * @exception  IOException  if an I/O error occurs.
     * @see        java.io.FileInputStream#close()
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

    private native void close0() throws IOException;

    private static native void initIDs();
    
    static {
	initIDs();
    }

}
