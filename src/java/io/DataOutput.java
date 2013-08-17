/*
 * @(#)DataOutput.java	1.7 98/07/01
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
 * The data output interface is implemented by streams that can 
 * write primitive Java data types to an output stream in a 
 * machine-independent manner. 
 *
 * @author  Frank Yellin
 * @version 1.7, 07/01/98
 * @see     java.io.DataInput  
 * @see     java.io.DataOutputStream
 * @since   JDK1.0
 */
public
interface DataOutput {
    /**
     * Writes the specified byte to this data output stream. 
     *
     * @param      b   the byte to be written.
     * @exception  IOException  if an I/O error occurs.
     * @since      JDK1.0
     */
    void write(int b) throws IOException;

    /**
     * Writes <code>b.length</code> bytes from the specified byte array 
     * to this output stream. 
     *
     * @param      b   the data.
     * @exception  IOException  if an I/O error occurs.
     * @since      JDK1.0
     */
    void write(byte b[]) throws IOException;

    /**
     * Writes <code>len</code> bytes from the specified byte array 
     * starting at offset <code>off</code> to this output stream. 
     *
     * @param      b     the data.
     * @param      off   the start offset in the data.
     * @param      len   the number of bytes to write.
     * @exception  IOException  if an I/O error occurs.
     * @since      JDK1.0
     */
    void write(byte b[], int off, int len) throws IOException;

    /**
     * Writes a <code>boolean</code> value to this output stream. 
     *
     * @param      v   the boolean to be written.
     * @exception  IOException  if an I/O error occurs.
     * @since      JDK1.0
     */
    void writeBoolean(boolean v) throws IOException;

    /**
     * Writes an 8-bit value to this output stream. 
     *
     * @param      v   the byte value to be written.
     * @exception  IOException  if an I/O error occurs.
     * @since      JDK1.0
     */
    void writeByte(int v) throws IOException;

    /**
     * Writes a 16-bit value to this output stream. 
     *
     * @param      v   the <code>short</code> value to be written.
     * @exception  IOException  if an I/O error occurs.
     * @since      JDK1.0
     */
    void writeShort(int v) throws IOException;

    /**
     * Writes a <code>char</code> value to this output stream. 
     *
     * @param      v   the <code>char</code> value to be written.
     * @exception  IOException  if an I/O error occurs.
     * @since      JDK1.0
     */
    void writeChar(int v) throws IOException;

    /**
     * Writes an <code>int</code> value to this output stream. 
     *
     * @param      v   the <code>int</code> value to be written.
     * @exception  IOException  if an I/O error occurs.
     * @since      JDK1.0
     */
    void writeInt(int v) throws IOException;

    /**
     * Writes a <code>long</code> value to this output stream. 
     *
     * @param      v   the <code>long</code> value to be written.
     * @exception  IOException  if an I/O error occurs.
     * @since      JDK1.0
     */
    void writeLong(long v) throws IOException;

    /**
     * Writes a <code>float</code> value to this output stream. 
     *
     * @param      v   the <code>float</code> value to be written.
     * @exception  IOException  if an I/O error occurs.
     * @since      JDK1.0
     */
    void writeFloat(float v) throws IOException;

    /**
     * Writes a <code>double</code> value to this output stream. 
     *
     * @param      v   the <code>double</code> value to be written.
     * @exception  IOException  if an I/O error occurs.
     * @since      JDK1.0
     */
    void writeDouble(double v) throws IOException;

    /**
     * Writes a string to this output stream. 
     *
     * @param      s   the string of bytes to be written.
     * @exception  IOException  if an I/O error occurs.
     * @since      JDK1.0
     */
    void writeBytes(String s) throws IOException;

    /**
     * Writes a string to this output stream. 
     *
     * @param      s   the string value to be written.
     * @exception  IOException  if an I/O error occurs.
     * @since      JDK1.0
     */
    void writeChars(String s) throws IOException;

    /**
     * Writes a Unicode string by encoding it using modified UTF-8 format.
     *
     * @param      str   the string value to be written.
     * @exception  IOException  if an I/O error occurs.
     * @since   JDK1.0
     */
    void writeUTF(String str) throws IOException;
}
