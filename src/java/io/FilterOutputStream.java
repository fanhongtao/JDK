/*
 * @(#)FilterOutputStream.java	1.16 98/07/01
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
 * This class is the superclass of all classes that filter output 
 * streams. These streams sit on top of an already existing output 
 * stream (the <i>underlying</i> output stream), but provide 
 * additional functionality. 
 * <p>
 * The class <code>FilterOutputStream</code> itself simply overrides 
 * all methods of <code>OutputStream</code> with versions that pass 
 * all requests to the underlying output stream. Subclasses of 
 * <code>FilterOutputStream</code> may further override some of these 
 * methods as well as provide additional methods and fields. 
 *
 * @author  Jonathan Payne
 * @version 1.16, 07/01/98
 * @since   JDK1.0
 */
public
class FilterOutputStream extends OutputStream {
    /**
     * The underlying output stream. 
     *
     * @since   JDK1.0
     */
    protected OutputStream out;

    /**
     * Creates an output stream filter built on top of the specified 
     * underlying output stream. 
     *
     * @param   out   the underlying output stream.
     * @since   JDK1.0
     */
    public FilterOutputStream(OutputStream out) {
	this.out = out;
    }

    /**
     * Writes the specified <code>byte</code> to this output stream. 
     * <p>
     * The <code>write</code> method of <code>FilterOutputStream</code> 
     * calls the <code>write</code> method of its underlying output stream. 
     *
     * @param      b   the <code>byte</code>.
     * @exception  IOException  if an I/O error occurs.
     * @since      JDK1.0
     */
    public void write(int b) throws IOException {
	out.write(b);
    }

    /**
     * Writes <code>b.length</code> bytes to this output stream. 
     * <p>
     * The <code>write</code> method of <code>FilterOutputStream</code> 
     * calls its <code>write</code> method of three arguments with the 
     * arguments <code>b</code>, <code>0</code>, and 
     * <code>b.length</code>. 
     * <p>
     * Note that this method does not call the one-argument 
     * <code>write</code> method of its underlying stream with the single 
     * argument <code>b</code>. 
     *
     * @param      b   the data to be written.
     * @exception  IOException  if an I/O error occurs.
     * @see        java.io.FilterOutputStream#write(byte[], int, int)
     * @since      JDK1.0
     */
    public void write(byte b[]) throws IOException {
	write(b, 0, b.length);
    }

    /**
     * Writes <code>len</code> bytes from the specified 
     * <code>byte</code> array starting at offset <code>off</code> to 
     * this output stream. 
     * <p>
     * The <code>write</code> method of <code>FilterOutputStream</code> 
     * calls the <code>write</code> method of one argument on each 
     * <code>byte</code> to output. 
     * <p>
     * Note that this method does not call the <code>write</code> method 
     * of its underlying input stream with the same arguments. Subclasses 
     * of <code>FilterOutputStream</code> should provide a more efficient 
     * implementation of this method. 
     *
     * @param      b     the data.
     * @param      off   the start offset in the data.
     * @param      len   the number of bytes to write.
     * @exception  IOException  if an I/O error occurs.
     * @see        java.io.FilterOutputStream#write(int)
     * @since      JDK1.0
     */
    public void write(byte b[], int off, int len) throws IOException {
	for (int i = 0 ; i < len ; i++) {
	    out.write(b[off + i]);
	}
    }

    /**
     * Flushes this output stream and forces any buffered output bytes 
     * to be written out to the stream. 
     * <p>
     * The <code>flush</code> method of <code>FilterOutputStream</code> 
     * calls the <code>flush</code> method of its underlying output stream.
     *
     * @exception  IOException  if an I/O error occurs.
     * @see        java.io.FilterOutputStream#out
     * @since      JDK1.0
     */
    public void flush() throws IOException {
	out.flush();
    }

    /**
     * Closes this output stream and releases any system resources 
     * associated with the stream. 
     * <p>
     * The <code>close</code> method of <code>FilterOutputStream</code> 
     * calls its <code>flush</code> method, and then calls the 
     * <code>close</code> method of its underlying output stream. 
     *
     * @exception  IOException  if an I/O error occurs.
     * @see        java.io.FilterOutputStream#flush()
     * @see        java.io.FilterOutputStream#out
     * @since      JDK1.0
     */
    public void close() throws IOException {
	try {
	  flush();
	} catch (IOException ignored) {
	}
	out.close();
    }
}
