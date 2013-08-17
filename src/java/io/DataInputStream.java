/*
 * @(#)DataInputStream.java	1.39 98/07/01
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
 * A data input stream lets an application read primitive Java data 
 * types from an underlying input stream in a machine-independent 
 * way. An application uses a data output stream to write data that 
 * can later be read by a data input stream. 
 * <p>
 * Data input streams and data output streams represent Unicode 
 * strings in a format that is a slight modification of UTF-8. (For 
 * more information, see X/Open Company Ltd., "File System Safe 
 * UCS Transformation Format (FSS_UTF)", X/Open Preliminary 
 * Specification, Document Number: P316. This information also 
 * appears in ISO/IEC 10646, Annex P.) 
 * <p>
 * All characters in the range <code>'&#92;u0001'</code> to 
 * <code>'&#92;u007F'</code> are represented by a single byte:
 * <center><table border="3">
 *   <tr><td><i>0</i></td>  <td>bits 0-7</td></tr>
 * </table></center>
 * <p>
 * The null character <code>'&#92;u0000'</code> and characters in the 
 * range <code>'&#92;u0080'</code> to <code>'&#92;u07FF'</code> are 
 * represented by a pair of bytes:
 * <center><table border="3">
 *   <tr><td>1</td>  <td>1</td>  <td>0</td>  <td>bits 6-10</td></tr>
 *   <tr><td>1</td>  <td>0</td>  <td colspan=2>bits 0-5</td></tr>
 * </table></center><br>
 * Characters in the range <code>'&#92;u0800'</code> to 
 * <code>'&#92;uFFFF'</code> are represented by three bytes:
 * <center><table border="3">
 *   <tr><td>1</td>  <td>1</td>  <td>1</td>  <td>0</td>  <td>bits 12-15</td</tr>
 *   <tr><td>1</td>  <td>0</td>  <td colspan=3>bits 6-11</td></tr>
 *   <tr><td>1</td>  <td>0</td>  <td colspan=3>bits 0-5</td></tr>
 * </table></center>
 * <p>
 * The two differences between this format and the 
 * "standard" UTF-8 format are the following: 
 * <ul>
 * <li>The null byte <code>'&#92;u0000'</code> is encoded in 2-byte format 
 *     rather than 1-byte, so that the encoded strings never have 
 *     embedded nulls. 
 * <li>Only the 1-byte, 2-byte, and 3-byte formats are used. 
 * </ul>
 *
 * @author  Arthur van Hoff
 * @version 1.39, 07/01/98
 * @see     java.io.DataOutputStream
 * @since   JDK1.0
 */
public
class DataInputStream extends FilterInputStream implements DataInput {
    /**
     * Creates a new data input stream to read data from the specified 
     * input stream. 
     *
     * @param  in   the input stream.
     */
    public DataInputStream(InputStream in) {
	super(in);
    }

    /**
     * Reads up to <code>byte.length</code> bytes of data from this data 
     * input stream into an array of bytes. This method blocks until some 
     * input is available. 
     * <p>
     * The <code>read</code> method of <code>DataInputStream</code> 
     * calls the <code>read</code> method of its underlying input stream 
     * with the three arguments <code>b</code>, <code>0</code>, and 
     * <code>b.length</code> and returns whatever value that method returns.
     *
     * @param      b   the buffer into which the data is read.
     * @return     the total number of bytes read into the buffer, or
     *             <code>-1</code> if there is no more data because the end
     *             of the stream has been reached.
     * @exception  IOException  if an I/O error occurs.
     * @see        java.io.FilterInputStream#in
     * @see        java.io.InputStream#read(byte[], int, int)
     */
    public final int read(byte b[]) throws IOException {
	return in.read(b, 0, b.length);
    }

    /**
     * Reads up to <code>len</code> bytes of data from this data input 
     * stream into an array of bytes. This method blocks until some input 
     * is available. 
     * <p>
     * The <code>read</code> method of <code>DataInputStream</code> 
     * calls the <code>read</code> method of its underlying input stream 
     * with the same arguments and returns whatever value that method returns.
     *
     * @param      b     the buffer into which the data is read.
     * @param      off   the start offset of the data.
     * @param      len   the maximum number of bytes read.
     * @return     the total number of bytes read into the buffer, or
     *             <code>-1</code> if there is no more data because the end
     *             of the stream has been reached.
     * @exception  IOException  if an I/O error occurs.
     * @see        java.io.FilterInputStream#in
     * @see        java.io.InputStream#read(byte[], int, int)
     */
    public final int read(byte b[], int off, int len) throws IOException {
	return in.read(b, off, len);
    }

    /**
     * Reads <code>b.length</code> bytes from this data input stream 
     * into the byte array. This method reads repeatedly from the 
     * underlying stream until all the bytes are read. This method blocks 
     * until all the bytes are read, the end of the stream is detected, 
     * or an exception is thrown. 
     *
     * @param      b   the buffer into which the data is read.
     * @exception  EOFException  if this input stream reaches the end before
     *               reading all the bytes.
     * @exception  IOException   if an I/O error occurs.
     * @see        java.io.FilterInputStream#in
     */
    public final void readFully(byte b[]) throws IOException {
	readFully(b, 0, b.length);
    }

    /**
     * Reads exactly <code>len</code> bytes from this data input stream 
     * into the byte array. This method reads repeatedly from the 
     * underlying stream until all the bytes are read. This method blocks 
     * until all the bytes are read, the end of the stream is detected, 
     * or an exception is thrown. 
     *
     * @param      b     the buffer into which the data is read.
     * @param      off   the start offset of the data.
     * @param      len   the number of bytes to read.
     * @exception  EOFException  if this input stream reaches the end before
     *               reading all the bytes.
     * @exception  IOException   if an I/O error occurs.
     * @see        java.io.FilterInputStream#in
     */
    public final void readFully(byte b[], int off, int len) throws IOException {
	InputStream in = this.in;
	int n = 0;
	while (n < len) {
	    int count = in.read(b, off + n, len - n);
	    if (count < 0)
		throw new EOFException();
	    n += count;
	}
    }

    /**
     * Skips exactly <code>n</code> bytes of input in the underlying 
     * input stream. This method blocks until all the bytes are skipped, 
     * the end of the stream is detected, or an exception is thrown. 
     *
     * @param      n   the number of bytes to be skipped.
     * @return     the number of bytes skipped, which is always <code>n</code>.
     * @exception  EOFException  if this input stream reaches the end before
     *               skipping all the bytes.
     * @exception  IOException   if an I/O error occurs.
     */
    public final int skipBytes(int n) throws IOException {
	InputStream in = this.in;
	for (int i = 0 ; i < n ; i += (int)in.skip(n - i));
	return n;
    }

    /**
     * Reads a <code>boolean</code> from this data input stream. This 
     * method reads a single byte from the underlying input stream. A 
     * value of <code>0</code> represents <code>false</code>. Any other 
     * value represents <code>true</code>. This method blocks until 
     * either the byte is read, the end of the stream is detected, or an 
     * exception is thrown. 
     *
     * @return     the <code>boolean</code> value read.
     * @exception  EOFException  if this input stream has reached the end.
     * @exception  IOException   if an I/O error occurs.
     * @see        java.io.FilterInputStream#in
     */
    public final boolean readBoolean() throws IOException {
	int ch = in.read();
	if (ch < 0)
	    throw new EOFException();
	return (ch != 0);
    }

    /**
     * Reads a signed 8-bit value from this data input stream. This 
     * method reads a byte from the underlying input stream. If the byte 
     * read is <code>b</code>, where 
     * 0&nbsp;&lt;=&nbsp;<code>b</code>&nbsp;&lt;=&nbsp;255, then the 
     * result is:
     * <ul><code>
     *     (byte)(b)
     * </code></ul>
     * <p>
     * This method blocks until either the byte is read, the end of the 
     * stream is detected, or an exception is thrown. 
     *
     * @return     the next byte of this input stream as a signed 8-bit
     *             <code>byte</code>.
     * @exception  EOFException  if this input stream has reached the end.
     * @exception  IOException   if an I/O error occurs.
     * @see        java.io.FilterInputStream#in
     */
    public final byte readByte() throws IOException {
	int ch = in.read();
	if (ch < 0)
	    throw new EOFException();
	return (byte)(ch);
    }

    /**
     * Reads an unsigned 8-bit number from this data input stream. This 
     * method reads a byte from this data input stream's underlying input 
     * stream and returns that byte. This method blocks until the byte is 
     * read, the end of the stream is detected, or an exception is thrown.
     *
     * @return     the next byte of this input stream, interpreted as an
     *             unsigned 8-bit number.
     * @exception  EOFException  if this input stream has reached the end.
     * @exception  IOException   if an I/O error occurs.
     * @see         java.io.FilterInputStream#in
     */
    public final int readUnsignedByte() throws IOException {
	int ch = in.read();
	if (ch < 0)
	    throw new EOFException();
	return ch;
    }

    /**
     * Reads a signed 16-bit number from this data input stream. The 
     * method reads two bytes from the underlying input stream. If the two
     * bytes read, in order, are <code>b1</code> and <code>b2</code>, 
     * where each of the two values is between <code>0</code> and 
     * <code>255</code>, inclusive, then the result is equal to:
     * <ul><code>
     *     (short)((b1 &lt;&lt; 8) | b2)
     * </code></ul>
     * <p>
     * This method blocks until the two bytes are read, the end of the 
     * stream is detected, or an exception is thrown. 
     *
     * @return     the next two bytes of this input stream, interpreted as a
     *             signed 16-bit number.
     * @exception  EOFException  if this input stream reaches the end before
     *               reading two bytes.
     * @exception  IOException   if an I/O error occurs.
     * @see        java.io.FilterInputStream#in
     */
    public final short readShort() throws IOException {
	InputStream in = this.in;
	int ch1 = in.read();
	int ch2 = in.read();
	if ((ch1 | ch2) < 0)
	     throw new EOFException();
	return (short)((ch1 << 8) + (ch2 << 0));
    }

    /**
     * Reads an unsigned 16-bit number from this data input stream. This 
     * method reads two bytes from the underlying input stream. If the 
     * bytes read, in order, are <code>b1</code> and <code>b2</code>, 
     * where <code>0&nbsp;&lt;=&nbsp;b1</code>, 
     * <code>b2&nbsp;&lt;=&nbsp;255</code>, then the result is equal to:
     * <ul><code>
     *     (b1 &lt;&lt; 8) | b2
     * </code></ul>
     * <p>
     * This method blocks until the two bytes are read, the end of the 
     * stream is detected, or an exception is thrown. 
     *
     * @return     the next two bytes of this input stream, interpreted as an
     *             unsigned 16-bit integer.
     * @exception  EOFException  if this input stream reaches the end before
     *               reading two bytes.
     * @exception  IOException   if an I/O error occurs.
     * @see        java.io.FilterInputStream#in
     */
    public final int readUnsignedShort() throws IOException {
	InputStream in = this.in;
	int ch1 = in.read();
	int ch2 = in.read();
	if ((ch1 | ch2) < 0)
	     throw new EOFException();
	return (ch1 << 8) + (ch2 << 0);
    }

    /**
     * Reads a Unicode character from this data input stream. This 
     * method reads two bytes from the underlying input stream. If the 
     * bytes read, in order, are <code>b1</code> and <code>b2</code>, 
     * where 0&nbsp;&lt;=&nbsp;<code>b1</code>, 
     * <code>b1</code>&nbsp;&lt;=&nbsp;255, then the result is equal to:
     * <ul><code>
     *     (char)((b1 &lt;&lt; 8) | b2)
     * </code></ul>
     * <p>
     * This method blocks until either the two bytes are read, the end of 
     * the stream is detected, or an exception is thrown. 
     *
     * @return     the next two bytes of this input stream as a Unicode
     *             character.
     * @exception  EOFException  if this input stream reaches the end before
     *               reading two bytes.
     * @exception  IOException   if an I/O error occurs.
     * @see        java.io.FilterInputStream#in
     */
    public final char readChar() throws IOException {
	InputStream in = this.in;
	int ch1 = in.read();
	int ch2 = in.read();
	if ((ch1 | ch2) < 0)
	     throw new EOFException();
	return (char)((ch1 << 8) + (ch2 << 0));
    }

    /**
     * Reads a signed 32-bit integer from this data input stream. This 
     * method reads four bytes from the underlying input stream. If the 
     * bytes read, in order, are <code>b1</code>, <code>b2</code>, 
     * <code>b3</code>, and <code>b4</code>, where 
     * 0&nbsp;&lt;=&nbsp;<code>b1</code>, <code>b2</code>, 
     * <code>b3</code>, <code>b4</code>&nbsp;&lt;=&nbsp;255, then the 
     * result is equal to:
     * <ul><code>
     *     (b1 &lt;&lt; 24) | (b2 &lt;&lt; 16) + (b3 &lt;&lt; 8) +b4
     * </code></ul>
     * <p>
     * This method blocks until the four bytes are read, the end of the 
     * stream is detected, or an exception is thrown. 
     *
     * @return     the next four bytes of this input stream, interpreted as an
     *             <code>int</code>.
     * @exception  EOFException  if this input stream reaches the end before
     *               reading four bytes.
     * @exception  IOException   if an I/O error occurs.
     * @see        java.io.FilterInputStream#in
     */
    public final int readInt() throws IOException {
	InputStream in = this.in;
	int ch1 = in.read();
	int ch2 = in.read();
	int ch3 = in.read();
	int ch4 = in.read();
	if ((ch1 | ch2 | ch3 | ch4) < 0)
	     throw new EOFException();
	return ((ch1 << 24) + (ch2 << 16) + (ch3 << 8) + (ch4 << 0));
    }

    /**
     * Reads a signed 64-bit integer from this data input stream. This 
     * method reads eight bytes from the underlying input stream. If the 
     * bytes read, in order, are <code>b1</code>, <code>b2</code>, 
     * <code>b3</code>, <code>b4</code>, <code>b5</code>, 
     * <code>b6</code>, <code>b7</code>, and <code>b8</code>, where 
     * <ul><code>
     *     0 &lt;= b1, b2, b3, b4, b5, b6, b7, b8 &lt;= 255,
     * </code></ul>
     * <p>
     * then the result is equal to:
     * <p><blockquote><pre>
     *     ((long)b1 &lt;&lt; 56) + ((long)b2 &lt;&lt; 48) +
     *        ((long)b3 &lt;&lt; 40) + ((long)b4 &lt;&lt; 32) +
     *        ((long)b5 &lt;&lt; 24) + (b6 &lt;&lt; 16) +
     *        (b7 &lt;&lt; 8) + b8
     * </pre></blockquote>
     * <p>
     * This method blocks until the eight bytes are read, the end of the 
     * stream is detected, or an exception is thrown. 
     *
     * @return     the next eight bytes of this input stream, interpreted as a
     *             <code>long</code>.
     * @exception  EOFException  if this input stream reaches the end before
     *               reading eight bytes.
     * @exception  IOException   if an I/O error occurs.
     * @see        java.io.FilterInputStream#in
     */
    public final long readLong() throws IOException {
	InputStream in = this.in;
	return ((long)(readInt()) << 32) + (readInt() & 0xFFFFFFFFL);
    }

    /**
     * Reads a <code>float</code> from this data input stream. This 
     * method reads an <code>int</code> value as if by the 
     * <code>readInt</code> method and then converts that 
     * <code>int</code> to a <code>float</code> using the 
     * <code>intBitsToFloat</code> method in class <code>Float</code>. 
     * This method blocks until the four bytes are read, the end of the 
     * stream is detected, or an exception is thrown. 
     *
     * @return     the next four bytes of this input stream, interpreted as a
     *             <code>float</code>.
     * @exception  EOFException  if this input stream reaches the end before
     *               reading four bytes.
     * @exception  IOException   if an I/O error occurs.
     * @see        java.io.DataInputStream#readInt()
     * @see        java.lang.Float#intBitsToFloat(int)
     */
    public final float readFloat() throws IOException {
	return Float.intBitsToFloat(readInt());
    }

    /**
     * Reads a <code>double</code> from this data input stream. This 
     * method reads a <code>long</code> value as if by the 
     * <code>readLong</code> method and then converts that 
     * <code>long</code> to a <code>double</code> using the 
     * <code>longBitsToDouble</code> method in class <code>Double</code>.
     * <p>
     * This method blocks until the eight bytes are read, the end of the 
     * stream is detected, or an exception is thrown. 
     *
     * @return     the next eight bytes of this input stream, interpreted as a
     *             <code>double</code>.
     * @exception  EOFException  if this input stream reaches the end before
     *               reading eight bytes.
     * @exception  IOException   if an I/O error occurs.
     * @see        java.io.DataInputStream#readLong()
     * @see        java.lang.Double#longBitsToDouble(long)
     */
    public final double readDouble() throws IOException {
	return Double.longBitsToDouble(readLong());
    }

    private char lineBuffer[];

    /**
     * Reads the next line of text from this data input stream. This 
     * method successively reads bytes from the underlying input stream 
     * until it reaches the end of a line of text. 
     * <p>
     * A line of text is terminated by a carriage return character 
     * (<code>'&#92;r'</code>), a newline character (<code>'&#92;n'</code>), a 
     * carriage return character immediately followed by a newline 
     * character, or the end of the input stream. The line-terminating 
     * character(s), if any, are not returned as part of the string that 
     * is returned. 
     * <p>
     * This method blocks until a newline character is read, a carriage 
     * return and the byte following it are read (to see if it is a 
     * newline), the end of the stream is detected, or an exception is 
     * thrown.
     *
     * @deprecated This method does not properly convert bytes to characters.
     * As of JDK&nbsp;1.1, the preferred way to read lines of text is via the
     * <code>BufferedReader.readLine()</code> method.  Programs that use the
     * <code>DataInputStream</code> class to read lines can be converted to use
     * the <code>BufferedReader</code> class by replacing code of the form
     * <ul>
     *     <code>DataInputStream d =&nbsp;new&nbsp;DataInputStream(in);</code>
     * </ul>
     * with
     * <ul>
     *     <code>BufferedReader d
     *          =&nbsp;new&nbsp;BufferedReader(new&nbsp;InputStreamReader(in));
     *      </code>
     * </ul>
     *
     * @return     the next line of text from this input stream, or 
     *             <tt>null</tt> if no bytes are read before end-of-file 
     *             is reached.
     * @exception  IOException  if an I/O error occurs.
     * @see        java.io.BufferedReader#readLine()
     * @see        java.io.FilterInputStream#in
     */
    public final String readLine() throws IOException {
	InputStream in = this.in;
	char buf[] = lineBuffer;

	if (buf == null) {
	    buf = lineBuffer = new char[128];
	}

	int room = buf.length;
	int offset = 0;
	int c;

loop:	while (true) {
	    switch (c = in.read()) {
	      case -1: 
	      case '\n':
		break loop;

	      case '\r':
		int c2 = in.read();
		if (c2 != '\n') {
		    if (!(in instanceof PushbackInputStream)) {
			in = this.in = new PushbackInputStream(in);
		    }
		    ((PushbackInputStream)in).unread(c2);
		}
		break loop;

	      default:
		if (--room < 0) {
		    buf = new char[offset + 128];
		    room = buf.length - offset - 1;
		    System.arraycopy(lineBuffer, 0, buf, 0, offset);
		    lineBuffer = buf;
		}
		buf[offset++] = (char) c;
		break;
	    }
	}
	if ((c == -1) && (offset == 0)) {
	    return null;
	}
	return String.copyValueOf(buf, 0, offset);
    }

    /**
     * Reads in a string that has been encoded using a modified UTF-8 
     * format from this data input stream. This method calls 
     * <code>readUTF(this)</code>.
     * See <code>readUTF(java.io.DataInput)</code> for a more 
     * complete description of the format. 
     * <p>
     * This method blocks until all the bytes are read, the end of the 
     * stream is detected, or an exception is thrown. 
     *
     * @return     a Unicode string.
     * @exception  EOFException  if this input stream reaches the end before
     *               reading all the bytes.
     * @exception  IOException   if an I/O error occurs.
     * @see        java.io.DataInputStream#readUTF(java.io.DataInput)
     */
    public final String readUTF() throws IOException {
        return readUTF(this);
    }

    /**
     * Reads in a string from the specified data input stream. The 
     * string has been encoded using a modified UTF-8 format. 
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
     * @param      in   a data input stream.
     * @return     a Unicode string.
     * @exception  EOFException            if the input stream reaches the end
     *               before all the bytes.
     * @exception  IOException             if an I/O error occurs.
     * @exception  UTFDataFormatException  if the bytes do not represent a
     *               valid UTF-8 encoding of a Unicode string.
     * @see        java.io.DataInputStream#readUnsignedShort()
     */
    public final static String readUTF(DataInput in) throws IOException {
        int utflen = in.readUnsignedShort();
        char str[] = new char[utflen];
	int count = 0;
	int strlen = 0;
	while (count < utflen) {
	    int c = in.readUnsignedByte();
	    int char2, char3;
	    switch (c >> 4) { 
	        case 0: case 1: case 2: case 3: case 4: case 5: case 6: case 7:
		    // 0xxxxxxx
		    count++;
		    str[strlen++] = (char)c;
		    break;
	        case 12: case 13:
		    // 110x xxxx   10xx xxxx
		    count += 2;
		    if (count > utflen) 
			throw new UTFDataFormatException();		  
		    char2 = in.readUnsignedByte();
		    if ((char2 & 0xC0) != 0x80)
			throw new UTFDataFormatException();		  
		    str[strlen++] = (char)(((c & 0x1F) << 6) | (char2 & 0x3F));
		    break;
	        case 14:
		    // 1110 xxxx  10xx xxxx  10xx xxxx
		    count += 3;
		    if (count > utflen) 
			throw new UTFDataFormatException();		  
		    char2 = in.readUnsignedByte();
		    char3 = in.readUnsignedByte();
		    if (((char2 & 0xC0) != 0x80) || ((char3 & 0xC0) != 0x80))
			throw new UTFDataFormatException();		  
		    str[strlen++] = (char)(((c & 0x0F) << 12) |
					   ((char2 & 0x3F) << 6) |
					   ((char3 & 0x3F) << 0));
		    break;
	        default:
		    // 10xx xxxx,  1111 xxxx
		    throw new UTFDataFormatException();		  
		}
	}
        return new String(str, 0, strlen);
    }
}
