/*
 * @(#)RandomAccessFile.java	1.34 98/07/01
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
 * Instances of this class support both reading and writing to a 
 * random access file. An application can modify the position in the 
 * file at which the next read or write occurs. 
 * This class provides a sense of security
 * by offering methods that allow specified mode accesses of 
 * read-only or read-write to files.
 *
 * @author  unascribed
 * @version 1.34, 07/01/98
 * @since   JDK1.0
 */
public
class RandomAccessFile implements DataOutput, DataInput {
    private FileDescriptor fd;

    /**
     * Creates a random access file stream to read from, and optionally 
     * to write to, a file with the specified name. 
     * <p>
     * The mode argument must either be equal to <code>"r"</code> or 
     * <code>"rw"</code>, indicating either to open the file for input or 
     * for both input and output. 
     *
     * @param      name   the system-dependent filename.
     * @param      mode   the access mode.
     * @exception  IllegalArgumentException  if the mode argument is not equal
     *               to <code>"r"</code> or to <code>"rw"</code>.
     * @exception  IOException               if an I/O error occurs.
     * @exception  SecurityException         if a security manager exists, its
     *               <code>checkRead</code> method is called with the name
     *               argument to see if the application is allowed read access
     *               to the file. If the mode argument is equal to
     *               <code>"rw"</code>, its <code>checkWrite</code> method also
     *               is called with the name argument to see if the application
     *               is allowed write access to the file. Either of these may
     *               result in a security exception.
     * @see        java.lang.SecurityException
     * @see        java.lang.SecurityManager#checkRead(java.lang.String)
     * @since      JDK1.0
     */
    public RandomAccessFile(String name, String mode) throws IOException {
	boolean rw = mode.equals("rw");
	if (!rw && !mode.equals("r"))
		throw new IllegalArgumentException("mode must be r or rw");
	SecurityManager security = System.getSecurityManager();
	if (security != null) {
	    security.checkRead(name);
	    if (rw) {
		security.checkWrite(name);
	    }
	}
	fd = new FileDescriptor();
	open(name, rw);
    }
    
    /**
     * Creates a random access file stream to read from, and optionally 
     * to write to, the file specified by the <code>File</code> argument. 
     * <p>
     * The mode argument must either be equal to <code>"r"</code> or to 
     * <code>"rw"</code>, indicating either to open the file for input, 
     * or for both input and output, respectively. 
     *
     * @param      file   the file object.
     * @param      mode   the access mode.
     * @exception  IllegalArgumentException  if the mode argument is not equal
     *               to <code>"r"</code> or to <code>"rw"</code>.
     * @exception  IOException               if an I/O error occurs.
     * @exception  SecurityException         if a security manager exists, its
     *               <code>checkRead</code> method is called with the pathname
     *               of the <code>File</code> argument to see if the
     *               application is allowed read access to the file. If the
     *               mode argument is equal to <code>"rw"</code>, its
     *               <code>checkWrite</code> method also is called with the
     *               pathname to see if the application is allowed write access
     *               to the file.
     * @see        java.io.File#getPath()
     * @see        java.lang.SecurityManager#checkRead(java.lang.String)
     * @since      JDK1.0
     */
    public RandomAccessFile(File file, String mode) throws IOException {
	this(file.getPath(), mode);
    }

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
     * Opens a file and returns the file descriptor.  The file is 
     * opened in read-write mode if writeable is true, else 
     * the file is opened as read-only.
     * @param name the name of the file
     * @param writeable the boolean indicating whether file is 
     * writeable or not.
     */
    private native void open(String name, boolean writeable) throws IOException;

    // 'Read' primitives
    
    /**
     * Reads a byte of data from this file. This method blocks if no 
     * input is yet available. 
     *
     * @return     the next byte of data, or <code>-1</code> if the end of the
     *             file is reached.
     * @exception  IOException  if an I/O error occurs.
     * @since      JDK1.0
     */
    public native int read() throws IOException;

    /**
     * Reads a sub array as a sequence of bytes. 
     * @param b the data to be written
     * @param off the start offset in the data
     * @param len the number of bytes that are written
     * @exception IOException If an I/O error has occurred.
     */
    private native int readBytes(byte b[], int off, int len) throws IOException;
 
    /**
     * Reads up to <code>len</code> bytes of data from this file into an 
     * array of bytes. This method blocks until at least one byte of input 
     * is available. 
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
     * Reads up to <code>b.length</code> bytes of data from this file 
     * into an array of bytes. This method blocks until at least one byte 
     * of input is available. 
     *
     * @param      b   the buffer into which the data is read.
     * @return     the total number of bytes read into the buffer, or
     *             <code>-1</code> if there is no more data because the end of
     *             this file has been reached.
     * @exception  IOException  if an I/O error occurs.
     * @since      JDK1.0 
     */
    public int read(byte b[]) throws IOException {
	return readBytes(b, 0, b.length);
    }
   
    /**
     * Reads <code>b.length</code> bytes from this file into the byte 
     * array. This method reads repeatedly from the file until all the 
     * bytes are read. This method blocks until all the bytes are read, 
     * the end of the stream is detected, or an exception is thrown. 
     *
     * @param      b   the buffer into which the data is read.
     * @exception  EOFException  if this file reaches the end before reading
     *               all the bytes.
     * @exception  IOException   if an I/O error occurs.
     * @since      JDK1.0
     */
    public final void readFully(byte b[]) throws IOException {
	readFully(b, 0, b.length);
    }

    /**
     * Reads exactly <code>len</code> bytes from this file into the byte 
     * array. This method reads repeatedly from the file until all the 
     * bytes are read. This method blocks until all the bytes are read, 
     * the end of the stream is detected, or an exception is thrown. 
     *
     * @param      b     the buffer into which the data is read.
     * @param      off   the start offset of the data.
     * @param      len   the number of bytes to read.
     * @exception  EOFException  if this file reaches the end before reading
     *               all the bytes.
     * @exception  IOException   if an I/O error occurs.
     * @since      JDK1.0
     */
    public final void readFully(byte b[], int off, int len) throws IOException {
	int n = 0;
	while (n < len) {
	    int count = this.read(b, off + n, len - n);
	    if (count < 0)
		throw new EOFException();
	    n += count;
	}
    }

    /**
     * Skips exactly <code>n</code> bytes of input. 
     * <p>
     * This method blocks until all the bytes are skipped, the end of 
     * the stream is detected, or an exception is thrown. 
     *
     * @param      n   the number of bytes to be skipped.
     * @return     the number of bytes skipped, which is always <code>n</code>.
     * @exception  EOFException  if this file reaches the end before skipping
     *               all the bytes.
     * @exception  IOException  if an I/O error occurs.
     * @since      JDK1.0
     */
    public int skipBytes(int n) throws IOException {
        seek(getFilePointer() + n);
        return n;
    }

    // 'Write' primitives

    /**
     * Writes the specified byte to this file. 
     *
     * @param      b   the <code>byte</code> to be written.
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
     * starting at offset <code>off</code> to this file. 
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
     * starting at offset <code>off</code> to this file. 
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

    // 'Random access' stuff

    /**
     * Returns the current offset in this file. 
     *
     * @return     the offset from the beginning of the file, in bytes,
     *             at which the next read or write occurs.
     * @exception  IOException  if an I/O error occurs.
     * @since      JDK1.0
     */
    public native long getFilePointer() throws IOException;

    /**
     * Sets the file-pointer offset, measured from the beginning of this 
     * file, at which the next read or write occurs.  The offset may be 
     * set beyond the end of the file. Setting the offset beyond the end 
     * of the file does not change the file length.  The file length will 
     * change only by writing after the offset has been set beyond the end 
     * of the file. 
     *
     * @param      pos   the offset position, measured in bytes from the 
     *                   beginning of the file, at which to set the file 
     *                   pointer.
     * @exception  IOException  if an I/O error occurs.
     * @since      JDK1.0
     */
    public native void seek(long pos) throws IOException;

    /**
     * Returns the length of this file.
     *
     * @return     the length of this file.
     * @exception  IOException  if an I/O error occurs.
     * @since      JDK1.0
     */
    public native long length() throws IOException;

    /**
     * Closes this random access file stream and releases any system 
     * resources associated with the stream. 
     *
     * @exception  IOException  if an I/O error occurs.
     * @since      JDK1.0
     */
    public native void close() throws IOException;

    //
    //  Some "reading/writing Java data types" methods stolen from
    //  DataInputStream and DataOutputStream.
    //

    /**
     * Reads a <code>boolean</code> from this file. This method reads a 
     * single byte from the file. A value of <code>0</code> represents 
     * <code>false</code>. Any other value represents <code>true</code>. 
     * This method blocks until the byte is read, the end of the stream 
     * is detected, or an exception is thrown. 
     *
     * @return     the <code>boolean</code> value read.
     * @exception  EOFException  if this file has reached the end.
     * @exception  IOException   if an I/O error occurs.
     * @since      JDK1.0
     */
    public final boolean readBoolean() throws IOException {
	int ch = this.read();
	if (ch < 0)
	    throw new EOFException();
	return (ch != 0);
    }

    /**
     * Reads a signed 8-bit value from this file. This method reads a 
     * byte from the file. If the byte read is <code>b</code>, where 
     * <code>0&nbsp;&lt;=&nbsp;b&nbsp;&lt;=&nbsp;255</code>, 
     * then the result is:
     * <ul><code>
     *     (byte)(b)
     *</code></ul>
     * <p>
     * This method blocks until the byte is read, the end of the stream 
     * is detected, or an exception is thrown. 
     *
     * @return     the next byte of this file as a signed 8-bit
     *             <code>byte</code>.
     * @exception  EOFException  if this file has reached the end.
     * @exception  IOException   if an I/O error occurs.
     * @since      JDK1.0
     */
    public final byte readByte() throws IOException {
	int ch = this.read();
	if (ch < 0)
	    throw new EOFException();
	return (byte)(ch);
    }

    /**
     * Reads an unsigned 8-bit number from this file. This method reads 
     * a byte from this file and returns that byte. 
     * <p>
     * This method blocks until the byte is read, the end of the stream 
     * is detected, or an exception is thrown. 
     *
     * @return     the next byte of this file, interpreted as an unsigned
     *             8-bit number.
     * @exception  EOFException  if this file has reached the end.
     * @exception  IOException   if an I/O error occurs.
     * @since      JDK1.0
     */
    public final int readUnsignedByte() throws IOException {
	int ch = this.read();
	if (ch < 0)
	    throw new EOFException();
	return ch;
    }

    /**
     * Reads a signed 16-bit number from this file. The method reads 2 
     * bytes from this file. If the two bytes read, in order, are 
     * <code>b1</code> and <code>b2</code>, where each of the two values is 
     * between <code>0</code> and <code>255</code>, inclusive, then the 
     * result is equal to:
     * <ul><code>
     *     (short)((b1 &lt;&lt; 8) | b2)
     * </code></ul>
     * <p>
     * This method blocks until the two bytes are read, the end of the 
     * stream is detected, or an exception is thrown. 
     *
     * @return     the next two bytes of this file, interpreted as a signed
     *             16-bit number.
     * @exception  EOFException  if this file reaches the end before reading
     *               two bytes.
     * @exception  IOException   if an I/O error occurs.
     * @since      JDK1.0
     */
    public final short readShort() throws IOException {
	int ch1 = this.read();
	int ch2 = this.read();
	if ((ch1 | ch2) < 0)
	     throw new EOFException();
	return (short)((ch1 << 8) + (ch2 << 0));
    }

    /**
     * Reads an unsigned 16-bit number from this file. This method reads 
     * two bytes from the file. If the bytes read, in order, are 
     * <code>b1</code> and <code>b2</code>, where 
     * <code>0&nbsp;&lt;=&nbsp;b1, b2&nbsp;&lt;=&nbsp;255</code>, 
     * then the result is equal to:
     * <ul><code>
     *     (b1 &lt;&lt; 8) | b2
     * </code></ul>
     * <p>
     * This method blocks until the two bytes are read, the end of the 
     * stream is detected, or an exception is thrown. 
     *
     * @return     the next two bytes of this file, interpreted as an unsigned
     *             16-bit integer.
     * @exception  EOFException  if this file reaches the end before reading
     *               two bytes.
     * @exception  IOException   if an I/O error occurs.
     * @since      JDK1.0
     */
    public final int readUnsignedShort() throws IOException {
	int ch1 = this.read();
	int ch2 = this.read();
	if ((ch1 | ch2) < 0)
	     throw new EOFException();
	return (ch1 << 8) + (ch2 << 0);
    }

    /**
     * Reads a Unicode character from this file. This method reads two
     * bytes from the file. If the bytes read, in order, are 
     * <code>b1</code> and <code>b2</code>, where 
     * <code>0&nbsp;&lt;=&nbsp;b1,&nbsp;b2&nbsp;&lt;=&nbsp;255</code>, 
     * then the result is equal to:
     * <ul><code>
     *     (char)((b1 &lt;&lt; 8) | b2)
     * </code></ul>
     * <p>
     * This method blocks until the two bytes are read, the end of the 
     * stream is detected, or an exception is thrown. 
     *
     * @return     the next two bytes of this file as a Unicode character.
     * @exception  EOFException  if this file reaches the end before reading
     *               two bytes.
     * @exception  IOException   if an I/O error occurs.
     * @since      JDK1.0
     */
    public final char readChar() throws IOException {
	int ch1 = this.read();
	int ch2 = this.read();
	if ((ch1 | ch2) < 0)
	     throw new EOFException();
	return (char)((ch1 << 8) + (ch2 << 0));
    }

    /**
     * Reads a signed 32-bit integer from this file. This method reads 4 
     * bytes from the file. If the bytes read, in order, are <code>b1</code>,
     * <code>b2</code>, <code>b3</code>, and <code>b4</code>, where 
     * <code>0&nbsp;&lt;=&nbsp;b1, b2, b3, b4&nbsp;&lt;=&nbsp;255</code>, 
     * then the result is equal to:
     * <ul><code>
     *     (b1 &lt;&lt; 24) | (b2 &lt;&lt; 16) + (b3 &lt;&lt; 8) + b4
     * </code></ul>
     * <p>
     * This method blocks until the four bytes are read, the end of the 
     * stream is detected, or an exception is thrown. 
     *
     * @return     the next four bytes of this file, interpreted as an
     *             <code>int</code>.
     * @exception  EOFException  if this file reaches the end before reading
     *               four bytes.
     * @exception  IOException   if an I/O error occurs.
     * @since      JDK1.0
     */
    public final int readInt() throws IOException {
	int ch1 = this.read();
	int ch2 = this.read();
	int ch3 = this.read();
	int ch4 = this.read();
	if ((ch1 | ch2 | ch3 | ch4) < 0)
	     throw new EOFException();
	return ((ch1 << 24) + (ch2 << 16) + (ch3 << 8) + (ch4 << 0));
    }

    /**
     * Reads a signed 64-bit integer from this file. This method reads eight
     * bytes from the file. If the bytes read, in order, are 
     * <code>b1</code>, <code>b2</code>, <code>b3</code>, 
     * <code>b4</code>, <code>b5</code>, <code>b6</code>, 
     * <code>b7</code>, and <code>b8,</code> where:
     * <ul><code>
     *     0 &lt;= b1, b2, b3, b4, b5, b6, b7, b8 &lt;=255,
     * </code></ul>
     * <p>
     * then the result is equal to:
     * <p><blockquote><pre>
     *     ((long)b1 &lt;&lt; 56) + ((long)b2 &lt;&lt; 48)
     *     + ((long)b3 &lt;&lt; 40) + ((long)b4 &lt;&lt; 32)
     *     + ((long)b5 &lt;&lt; 24) + ((long)b6 &lt;&lt; 16)
     *     + ((long)b7 &lt;&lt; 8) + b8
     * </pre></blockquote>
     * <p>
     * This method blocks until the eight bytes are read, the end of the 
     * stream is detected, or an exception is thrown. 
     *
     * @return     the next eight bytes of this file, interpreted as a
     *             <code>long</code>.
     * @exception  EOFException  if this file reaches the end before reading
     *               eight bytes.
     * @exception  IOException   if an I/O error occurs.
     * @since      JDK1.0
     */
    public final long readLong() throws IOException {
	return ((long)(readInt()) << 32) + (readInt() & 0xFFFFFFFFL);
    }

    /**
     * Reads a <code>float</code> from this file. This method reads an 
     * <code>int</code> value as if by the <code>readInt</code> method 
     * and then converts that <code>int</code> to a <code>float</code> 
     * using the <code>intBitsToFloat</code> method in class 
     * <code>Float</code>. 
     * <p>
     * This method blocks until the four bytes are read, the end of the 
     * stream is detected, or an exception is thrown. 
     *
     * @return     the next four bytes of this file, interpreted as a
     *             <code>float</code>.
     * @exception  EOFException  if this file reaches the end before reading
     *             four bytes.
     * @exception  IOException   if an I/O error occurs.
     * @see        java.io.RandomAccessFile#readInt()
     * @see        java.lang.Float#intBitsToFloat(int)
     * @since      JDK1.0
     */
    public final float readFloat() throws IOException {
	return Float.intBitsToFloat(readInt());
    }

    /**
     * Reads a <code>double</code> from this file. This method reads a 
     * <code>long</code> value as if by the <code>readLong</code> method 
     * and then converts that <code>long</code> to a <code>double</code> 
     * using the <code>longBitsToDouble</code> method in 
     * class <code>Double</code>.
     * <p>
     * This method blocks until the eight bytes are read, the end of the 
     * stream is detected, or an exception is thrown. 
     *
     * @return     the next eight bytes of this file, interpreted as a
     *             <code>double</code>.
     * @exception  EOFException  if this file reaches the end before reading
     *             eight bytes.
     * @exception  IOException   if an I/O error occurs.
     * @see        java.io.RandomAccessFile#readLong()
     * @see        java.lang.Double#longBitsToDouble(long)
     * @since      JDK1.0
     */
    public final double readDouble() throws IOException {
	return Double.longBitsToDouble(readLong());
    }

    /**
     * Reads the next line of text from this file. This method 
     * successively reads bytes from the file until it reaches the end of 
     * a line of text. 
     * <p>
     * A line of text is terminated by a carriage-return character 
     * (<code>'&#92;r'</code>), a newline character (<code>'&#92;n'</code>), a 
     * carriage-return character immediately followed by a newline 
     * character, or the end of the input stream. The line-terminating 
     * character(s), if any, are included as part of the string returned. 
     * <p>
     * This method blocks until a newline character is read, a carriage 
     * return and the byte following it are read (to see if it is a 
     * newline), the end of the stream is detected, or an exception is thrown.
     *
     * @return     the next line of text from this file.
     * @exception  IOException  if an I/O error occurs.
     * @since      JDK1.0
     */
    public final String readLine() throws IOException {
	StringBuffer input = new StringBuffer();
	int c;

	while (((c = read()) != -1) && (c != '\n')) {
	    input.append((char)c);
	}
	if ((c == -1) && (input.length() == 0)) {
	    return null;
	}
	return input.toString();
    }

    /**
     * Reads in a string from this file. The string has been encoded 
     * using a modified UTF-8 format. 
     * <p>
     * The first two bytes are read as if by 
     * <code>readUnsignedShort</code>. This value gives the number of 
     * following bytes that are in the encoded string, not
     * the length of the resulting string. The following bytes are then 
     * interpreted as bytes encoding characters in the UTF-8 format 
     * and are converted into characters. 
     * <p>
     * This method blocks until all the bytes are read, the end of the 
     * stream is detected, or an exception is thrown. 
     *
     * @return     a Unicode string.
     * @exception  EOFException            if this file reaches the end before
     *               reading all the bytes.
     * @exception  IOException             if an I/O error occurs.
     * @exception  UTFDataFormatException  if the bytes do not represent 
     *               valid UTF-8 encoding of a Unicode string.
     * @see        java.io.RandomAccessFile#readUnsignedShort()
     * @since      JDK1.0
     */
    public final String readUTF() throws IOException {
	return DataInputStream.readUTF(this);
    }

    /**
     * Writes a <code>boolean</code> to the file as a 1-byte value. The 
     * value <code>true</code> is written out as the value 
     * <code>(byte)1</code>; the value <code>false</code> is written out 
     * as the value <code>(byte)0</code>.
     *
     * @param      v   a <code>boolean</code> value to be written.
     * @exception  IOException  if an I/O error occurs.
     * @since      JDK1.0
     */
    public final void writeBoolean(boolean v) throws IOException {
	write(v ? 1 : 0);
	//written++;
    }

    /**
     * Writes a <code>byte</code> to the file as a 1-byte value. 
     *
     * @param      v   a <code>byte</code> value to be written.
     * @exception  IOException  if an I/O error occurs.
     * @since      JDK1.0
     */
    public final void writeByte(int v) throws IOException {
	write(v);
	//written++;
    }

    /**
     * Writes a <code>short</code> to the file as two bytes, high byte first.
     *
     * @param      v   a <code>short</code> to be written.
     * @exception  IOException  if an I/O error occurs.
     * @since      JDK1.0
     */
    public final void writeShort(int v) throws IOException {
	write((v >>> 8) & 0xFF);
	write((v >>> 0) & 0xFF);
	//written += 2;
    }

    /**
     * Writes a <code>char</code> to the file as a 2-byte value, high
     * byte first.
     *
     * @param      v   a <code>char</code> value to be written.
     * @exception  IOException  if an I/O error occurs.
     * @since      JDK1.0
     */
    public final void writeChar(int v) throws IOException {
	write((v >>> 8) & 0xFF);
	write((v >>> 0) & 0xFF);
	//written += 2;
    }

    /**
     * Writes an <code>int</code> to the file as four bytes, high byte first.
     *
     * @param      v   an <code>int</code> to be written.
     * @exception  IOException  if an I/O error occurs.
     * @since      JDK1.0
     */
    public final void writeInt(int v) throws IOException {
	write((v >>> 24) & 0xFF);
	write((v >>> 16) & 0xFF);
	write((v >>>  8) & 0xFF);
	write((v >>>  0) & 0xFF);
	//written += 4;
    }

    /**
     * Writes a <code>long</code> to the file as eight bytes, high byte first.
     *
     * @param      v   a <code>long</code> to be written.
     * @exception  IOException  if an I/O error occurs.
     * @since      JDK1.0
     */
    public final void writeLong(long v) throws IOException {
	write((int)(v >>> 56) & 0xFF);
	write((int)(v >>> 48) & 0xFF);
	write((int)(v >>> 40) & 0xFF);
	write((int)(v >>> 32) & 0xFF);
	write((int)(v >>> 24) & 0xFF);
	write((int)(v >>> 16) & 0xFF);
	write((int)(v >>>  8) & 0xFF);
	write((int)(v >>>  0) & 0xFF);
	//written += 8;
    }

    /**
     * Converts the float argument to an <code>int</code> using the 
     * <code>floatToIntBits</code> method in class <code>Float</code>, 
     * and then writes that <code>int</code> value to the file as a 
     * 4-byte quantity, high byte first. 
     *
     * @param      v   a <code>float</code> value to be written.
     * @exception  IOException  if an I/O error occurs.
     * @see        java.lang.Float#floatToIntBits(float)
     * @since      JDK1.0
     */
    public final void writeFloat(float v) throws IOException {
	writeInt(Float.floatToIntBits(v));
    }

    /**
     * Converts the double argument to a <code>long</code> using the 
     * <code>doubleToLongBits</code> method in class <code>Double</code>, 
     * and then writes that <code>long</code> value to the file as an 
     * 8-byte quantity, high byte first. 
     *
     * @param      v   a <code>double</code> value to be written.
     * @exception  IOException  if an I/O error occurs.
     * @see        java.lang.Double#doubleToLongBits(double)
     * @since      JDK1.0
     */
    public final void writeDouble(double v) throws IOException {
	writeLong(Double.doubleToLongBits(v));
    }

    /**
     * Writes the string to the file as a sequence of bytes. Each 
     * character in the string is written out, in sequence, by discarding 
     * its high eight bits. 
     *
     * @param      s   a string of bytes to be written.
     * @exception  IOException  if an I/O error occurs.
     * @since      JDK1.0
     */
    public final void writeBytes(String s) throws IOException {
	int len = s.length();
	byte[] b = new byte[len];
	s.getBytes(0, len, b, 0);
	writeBytes(b, 0, len);
    }

    /**
     * Writes a string to the file as a sequence of characters. Each 
     * character is written to the data output stream as if by the 
     * <code>writeChar</code> method. 
     *
     * @param      s   a <code>String</code> value to be written.
     * @exception  IOException  if an I/O error occurs.
     * @see        java.io.RandomAccessFile#writeChar(int)
     * @since      JDK1.0
     */
    public final void writeChars(String s) throws IOException {
	int clen = s.length();
	int blen = 2*clen;
	byte[] b = new byte[blen];
	char[] c = new char[clen];
	s.getChars(0, clen, c, 0);
	for (int i = 0, j = 0; i < clen; i++) {
	    b[j++] = (byte)(c[i] >>> 8);
	    b[j++] = (byte)(c[i] >>> 0);
	}
	writeBytes(b, 0, blen);
    }

    /**
     * Writes a string to the file using UTF-8 encoding in a 
     * machine-independent manner. 
     * <p>
     * First, two bytes are written to the file as if by the 
     * <code>writeShort</code> method giving the number of bytes to 
     * follow. This value is the number of bytes actually written out, 
     * not the length of the string. Following the length, each character 
     * of the string is output, in sequence, using the UTF-8 encoding 
     * for each character. 
     *
     * @param      str   a string to be written.
     * @exception  IOException  if an I/O error occurs.
     * @since      JDK1.0
     */
    public final void writeUTF(String str) throws IOException {
	int strlen = str.length();
	int utflen = 0;

	for (int i = 0 ; i < strlen ; i++) {
	    int c = str.charAt(i);
	    if ((c >= 0x0001) && (c <= 0x007F)) {
		utflen++;
	    } else if (c > 0x07FF) {
		utflen += 3;
	    } else {
		utflen += 2;
	    }
	}

	if (utflen > 65535)
	    throw new UTFDataFormatException();		  

	write((utflen >>> 8) & 0xFF);
	write((utflen >>> 0) & 0xFF);
	for (int i = 0 ; i < strlen ; i++) {
	    int c = str.charAt(i);
	    if ((c >= 0x0001) && (c <= 0x007F)) {
		write(c);
	    } else if (c > 0x07FF) {
		write(0xE0 | ((c >> 12) & 0x0F));
		write(0x80 | ((c >>  6) & 0x3F));
		write(0x80 | ((c >>  0) & 0x3F));
		//written += 2;
	    } else {
		write(0xC0 | ((c >>  6) & 0x1F));
		write(0x80 | ((c >>  0) & 0x3F));
		//written += 1;
	    }
	}
	//written += strlen + 2;
    }
}
