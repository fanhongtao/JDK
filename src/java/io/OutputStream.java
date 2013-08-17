/*
 * @(#)OutputStream.java	1.15 98/07/01
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
 * This abstract class is the superclass of all classes representing 
 * an output stream of bytes. 
 * <p>
 * Applications that need to define a subclass of 
 * <code>OutputStream</code> must always provide at least a method 
 * that writes one byte of output. 
 *
 * @author  Arthur van Hoff
 * @version 1.15, 07/01/98
 * @see     java.io.BufferedOutputStream
 * @see     java.io.ByteArrayOutputStream
 * @see     java.io.DataOutputStream
 * @see     java.io.FilterOutputStream
 * @see     java.io.InputStream
 * @see     java.io.OutputStream#write(int)
 * @since   JDK1.0
 */
public abstract class OutputStream {
    /**
     * Writes the specified byte to this output stream. 
     * <p>
     * Subclasses of <code>OutputStream</code> must provide an 
     * implementation for this method. 
     *
     * @param      b   the <code>byte</code>.
     * @exception  IOException  if an I/O error occurs.
     * @since      JDK1.0
     */
    public abstract void write(int b) throws IOException;

    /**
     * Writes <code>b.length</code> bytes from the specified byte array 
     * to this output stream. 
     * <p>
     * The <code>write</code> method of <code>OutputStream</code> calls 
     * the <code>write</code> method of three arguments with the three 
     * arguments <code>b</code>, <code>0</code>, and 
     * <code>b.length</code>. 
     *
     * @param      b   the data.
     * @exception  IOException  if an I/O error occurs.
     * @see        java.io.OutputStream#write(byte[], int, int)
     * @since      JDK1.0
     */
    public void write(byte b[]) throws IOException {
	write(b, 0, b.length);
    }

    /**
     * Writes <code>len</code> bytes from the specified byte array 
     * starting at offset <code>off</code> to this output stream. 
     * <p>
     * The <code>write</code> method of <code>OutputStream</code> calls 
     * the write method of one argument on each of the bytes to be 
     * written out. Subclasses are encouraged to override this method and 
     * provide a more efficient implementation. 
     *
     * @param      b     the data.
     * @param      off   the start offset in the data.
     * @param      len   the number of bytes to write.
     * @exception  IOException  if an I/O error occurs.
     * @since      JDK1.0
     */
    public void write(byte b[], int off, int len) throws IOException {
	for (int i = 0 ; i < len ; i++) {
	    write(b[off + i]);
	}
    }

    /**
     * Flushes this output stream and forces any buffered output bytes 
     * to be written out. 
     * <p>
     * The <code>flush</code> method of <code>OutputStream</code> does nothing.
     *
     * @exception  IOException  if an I/O error occurs.
     * @since      JDK1.0
     */
    public void flush() throws IOException {
    }

    /**
     * Closes this output stream and releases any system resources 
     * associated with this stream. 
     * <p>
     * The <code>close</code> method of <code>OutputStream</code> does nothing.
     *
     * @exception  IOException  if an I/O error occurs.
     * @since      JDK1.0
     */
    public void close() throws IOException {
    }
}
