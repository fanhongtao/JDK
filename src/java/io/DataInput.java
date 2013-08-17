/*
 * @(#)DataInput.java	1.9 98/07/01
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
 * The data input interface is implemented by streams that can read 
 * primitive Java data types from a stream in a machine-independent 
 * manner. 
 *
 * @author  Frank Yellin
 * @version 1.9, 07/01/98
 * @see     java.io.DataInputStream 
 * @see     java.io.DataOutput  
 * @since   JDK1.0
 */
public
interface DataInput {
    /**
     * Reads <code>b.length</code> bytes into the byte array. This 
     * method blocks until all the bytes are read. 
     *
     * @param     b   the buffer into which the data is read.
     * @exception  EOFException  if this stream reaches the end before reading
     *               all the bytes.
     * @exception  IOException   if an I/O error occurs.
     * @since      JDK1.0
     */
    void readFully(byte b[]) throws IOException;

    /**
     * Reads <code>b.length</code> bytes into the byte array. This 
     * method blocks until all the bytes are read. 
     *
     * @param     b   the buffer into which the data is read.
     * @exception  EOFException  if this stream reaches the end before reading
     *               all the bytes.
     * @exception  IOException   if an I/O error occurs.
     * @since   JDK1.0
     */
    void readFully(byte b[], int off, int len) throws IOException;

    /**
     * Skips exactly <code>n</code> bytes of input. 
     *
     * @param      n   the number of bytes to be skipped.
     * @return     the number of bytes skipped, which is always <code>n</code>.
     * @exception  EOFException  if this stream reaches the end before skipping
     *               all the bytes.
     * @exception  IOException   if an I/O error occurs.
     * @since      JDK1.0
     */
    int skipBytes(int n) throws IOException;

    /**
     * Reads a <code>boolean</code> value from the input stream. 
     *
     * @return     the <code>boolean</code> value read.
     * @exception  EOFException  if this stream reaches the end before reading
     *               all the bytes.
     * @exception  IOException   if an I/O error occurs.
     * @since      JDK1.0
     */
    boolean readBoolean() throws IOException;

    /**
     * Reads a signed 8-bit value from the input stream. 
     *
     * @return     the 8-bit value read.
     * @exception  EOFException  if this stream reaches the end before reading
     *               all the bytes.
     * @exception  IOException   if an I/O error occurs.
     * @since      JDK1.0
     */
    byte readByte() throws IOException;

    /**
     * Reads an unsigned 8-bit value from the input stream. 
     *
     * @return     the unsigned 8-bit value read.
     * @exception  EOFException  if this stream reaches the end before reading
     *               all the bytes.
     * @exception  IOException   if an I/O error occurs.
     * @since      JDK1.0
     */
    int readUnsignedByte() throws IOException;

    /**
     * Reads a 16-bit value from the input stream. 
     *
     * @return     the 16-bit value read.
     * @exception  EOFException  if this stream reaches the end before reading
     *               all the bytes.
     * @exception  IOException   if an I/O error occurs.
     * @since      JDK1.0
     */
    short readShort() throws IOException;

    /**
     * Reads an unsigned 16-bit value from the input stream. 
     *
     * @return     the unsigned 16-bit value read.
     * @exception  EOFException  if this stream reaches the end before reading
     *               all the bytes.
     * @exception  IOException   if an I/O error occurs.
     * @since      JDK1.0
     */
    int readUnsignedShort() throws IOException;

    /**
     * Reads a Unicode <code>char</code> value from the input stream. 
     *
     * @return     the Unicode <code>char</code> read.
     * @exception  EOFException  if this stream reaches the end before reading
     *               all the bytes.
     * @exception  IOException   if an I/O error occurs.
     * @since      JDK1.0
     */
    char readChar() throws IOException;

    /**
     * Reads an <code>int</code> value from the input stream. 
     *
     * @return     the <code>int</code> value read.
     * @exception  EOFException  if this stream reaches the end before reading
     *               all the bytes.
     * @exception  IOException   if an I/O error occurs.
     * @since      JDK1.0
     */
    int readInt() throws IOException;

    /**
     * Reads a <code>long</code> value from the input stream. 
     *
     * @return     the <code>long</code> value read.
     * @exception  EOFException  if this stream reaches the end before reading
     *               all the bytes.
     * @exception  IOException   if an I/O error occurs.
     * @since      JDK1.0
     */
    long readLong() throws IOException;

    /**
     * Reads a <code>float</code> value from the input stream. 
     *
     * @return     the <code>float</code> value read.
     * @exception  EOFException  if this stream reaches the end before reading
     *               all the bytes.
     * @exception  IOException   if an I/O error occurs.
     * @since      JDK1.0
     */
    float readFloat() throws IOException;

    /**
     * Reads a <code>double</code> value from the input stream. 
     *
     * @return     the <code>double</code> value read.
     * @exception  EOFException  if this stream reaches the end before reading
     *               all the bytes.
     * @exception  IOException   if an I/O error occurs.
     * @since      JDK1.0
     */
    double readDouble() throws IOException;

    /**
     * Reads the next line of text from the input stream. 
     *
     * @return     if this stream reaches the end before reading all the bytes.
     * @exception  IOException  if an I/O error occurs.
     * @since      JDK1.0
     */
    String readLine() throws IOException;

    /**
     * Reads in a string that has been encoded using a modified UTF-8 format.
     * <p>
     * For an exact description of this method, see the discussion in 
     * Gosling, Joy, and Steele, <i>The Java Language Specification</i>. 
     *
     * @return     a Unicode string.
     * @exception  EOFException            if this stream reaches the end
     *               before reading all the bytes.
     * @exception  IOException             if an I/O error occurs.
     * @exception  UTFDataFormatException  if the bytes do not represent a
     *               valid UTF-8 encoding of a string.
     * @since      JDK1.0
     */
    String readUTF() throws IOException;
}
