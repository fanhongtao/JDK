/*
 * @(#)DataOutputStream.java	1.24 98/07/01
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
 * A data input stream lets an application write primitive Java data 
 * types to an output stream in a portable way. An application can 
 * then use a data input stream to read the data back in. 
 *
 * @author  unascribed
 * @version 1.24, 07/01/98
 * @see     java.io.DataInputStream
 * @since   JDK1.0
 */
public
class DataOutputStream extends FilterOutputStream implements DataOutput {
    /**
     * The number of bytes written to the data output stream. 
     *
     * @since   JDK1.0
     */
    protected int written;

    /**
     * Creates a new data output stream to write data to the specified 
     * underlying output stream. 
     *
     * @param   out   the underlying output stream.
     * @see     java.io.FilterOutputStream#out
     * @since   JDK1.0
     */
    public DataOutputStream(OutputStream out) {
	super(out);
    }

    /**
     * Writes the specified byte to the underlying output stream. 
     *
     * @param      b   the <code>byte</code> to be written.
     * @exception  IOException  if an I/O error occurs.
     * @see        java.io.FilterOutputStream#out
     * @since      JDK1.0
     */
    public synchronized void write(int b) throws IOException {
	out.write(b);
	written++;
    }

    /**
     * Writes <code>len</code> bytes from the specified byte array 
     * starting at offset <code>off</code> to the underlying output stream.
     *
     * @param      b     the data.
     * @param      off   the start offset in the data.
     * @param      len   the number of bytes to write.
     * @exception  IOException  if an I/O error occurs.
     * @see        java.io.FilterOutputStream#out
     * @since      JDK1.0
     */
    public synchronized void write(byte b[], int off, int len)
	throws IOException
    {
	out.write(b, off, len);
	written += len;
    }

    /**
     * Flushes this data output stream. This forces any buffered output 
     * bytes to be written out to the stream. 
     * <p>
     * The <code>flush</code> method of <code>DataOuputStream</code> 
     * calls the <code>flush</code> method of its underlying output stream.
     *
     * @exception  IOException  if an I/O error occurs.
     * @see        java.io.FilterOutputStream#out
     * @see        java.io.OutputStream#flush()
     * @since      JDK1.0
     */
    public void flush() throws IOException {
	out.flush();
    }

    /**
     * Writes a <code>boolean</code> to the underlying output stream as 
     * a 1-byte value. The value <code>true</code> is written out as the 
     * value <code>(byte)1</code>; the value <code>false</code> is 
     * written out as the value <code>(byte)0</code>.
     *
     * @param      v   a <code>boolean</code> value to be written.
     * @exception  IOException  if an I/O error occurs.
     * @see        java.io.FilterOutputStream#out
     * @since      JDK1.0
     */
    public final void writeBoolean(boolean v) throws IOException {
	out.write(v ? 1 : 0);
	written++;
    }

    /**
     * Writes out a <code>byte</code> to the underlying output stream as 
     * a 1-byte value. 
     *
     * @param      v   a <code>byte</code> value to be written.
     * @exception  IOException  if an I/O error occurs.
     * @see        java.io.FilterOutputStream#out
     * @since      JDK1.0
     */
    public final void writeByte(int v) throws IOException {
	out.write(v);
	written++;
    }

    /**
     * Writes a <code>short</code> to the underlying output stream as two
     * bytes, high byte first. 
     *
     * @param      v   a <code>short</code> to be written.
     * @exception  IOException  if an I/O error occurs.
     * @see        java.io.FilterOutputStream#out
     * @since      JDK1.0
     */
    public final void writeShort(int v) throws IOException {
	OutputStream out = this.out;
	out.write((v >>> 8) & 0xFF);
	out.write((v >>> 0) & 0xFF);
	written += 2;
    }

    /**
     * Writes a <code>char</code> to the underlying output stream as a 
     * 2-byte value, high byte first. 
     *
     * @param      v   a <code>char</code> value to be written.
     * @exception  IOException  if an I/O error occurs.
     * @see        java.io.FilterOutputStream#out
     * @since      JDK1.0
     */
    public final void writeChar(int v) throws IOException {
	OutputStream out = this.out;
	out.write((v >>> 8) & 0xFF);
	out.write((v >>> 0) & 0xFF);
	written += 2;
    }

    /**
     * Writes an <code>int</code> to the underlying output stream as four
     * bytes, high byte first. 
     *
     * @param      v   an <code>int</code> to be written.
     * @exception  IOException  if an I/O error occurs.
     * @see        java.io.FilterOutputStream#out
     * @since      JDK1.0
     */
    public final void writeInt(int v) throws IOException {
	OutputStream out = this.out;
	out.write((v >>> 24) & 0xFF);
	out.write((v >>> 16) & 0xFF);
	out.write((v >>>  8) & 0xFF);
	out.write((v >>>  0) & 0xFF);
	written += 4;
    }

    /**
     * Writes a <code>long</code> to the underlying output stream as eight
     * bytes, high byte first. 
     *
     * @param      v   a <code>long</code> to be written.
     * @exception  IOException  if an I/O error occurs.
     * @see        java.io.FilterOutputStream#out
     * @since      JDK1.0
     */
    public final void writeLong(long v) throws IOException {
	OutputStream out = this.out;
	out.write((int)(v >>> 56) & 0xFF);
	out.write((int)(v >>> 48) & 0xFF);
	out.write((int)(v >>> 40) & 0xFF);
	out.write((int)(v >>> 32) & 0xFF);
	out.write((int)(v >>> 24) & 0xFF);
	out.write((int)(v >>> 16) & 0xFF);
	out.write((int)(v >>>  8) & 0xFF);
	out.write((int)(v >>>  0) & 0xFF);
	written += 8;
    }

    /**
     * Converts the float argument to an <code>int</code> using the 
     * <code>floatToIntBits</code> method in class <code>Float</code>, 
     * and then writes that <code>int</code> value to the underlying 
     * output stream as a 4-byte quantity, high byte first. 
     *
     * @param      v   a <code>float</code> value to be written.
     * @exception  IOException  if an I/O error occurs.
     * @see        java.io.FilterOutputStream#out
     * @see        java.lang.Float#floatToIntBits(float)
     * @since      JDK1.0
     */
    public final void writeFloat(float v) throws IOException {
	writeInt(Float.floatToIntBits(v));
    }

    /**
     * Converts the double argument to a <code>long</code> using the 
     * <code>doubleToLongBits</code> method in class <code>Double</code>, 
     * and then writes that <code>long</code> value to the underlying 
     * output stream as an 8-byte quantity, high byte first. 
     *
     * @param      v   a <code>double</code> value to be written.
     * @exception  IOException  if an I/O error occurs.
     * @see        java.io.FilterOutputStream#out
     * @see        java.lang.Double#doubleToLongBits(double)
     * @since      JDK1.0
     */
    public final void writeDouble(double v) throws IOException {
	writeLong(Double.doubleToLongBits(v));
    }

    /**
     * Writes out the string to the underlying output stream as a 
     * sequence of bytes. Each character in the string is written out, in 
     * sequence, by discarding its high eight bits. 
     *
     * @param      s   a string of bytes to be written.
     * @exception  IOException  if an I/O error occurs.
     * @see        java.io.FilterOutputStream#out
     * @since      JDK1.0
     */
    public final void writeBytes(String s) throws IOException {
	OutputStream out = this.out;
	int len = s.length();
	for (int i = 0 ; i < len ; i++) {
	    out.write((byte)s.charAt(i));
	}
	written += len;
    }

    /**
     * Writes a string to the underlying output stream as a sequence of 
     * characters. Each character is written to the data output stream as 
     * if by the <code>writeChar</code> method. 
     *
     * @param      s   a <code>String</code> value to be written.
     * @exception  IOException  if an I/O error occurs.
     * @see        java.io.DataOutputStream#writeChar(int)
     * @see        java.io.FilterOutputStream#out
     * @since      JDK1.0
     */
    public final void writeChars(String s) throws IOException {
	OutputStream out = this.out;
	int len = s.length();
	for (int i = 0 ; i < len ; i++) {
	    int v = s.charAt(i);
	    out.write((v >>> 8) & 0xFF);
	    out.write((v >>> 0) & 0xFF);
	}
	written += len * 2;
    }

    /**
     * Writes a string to the underlying output stream using UTF-8 
     * encoding in a machine-independent manner. 
     * <p>
     * First, two bytes are written to the output stream as if by the 
     * <code>writeShort</code> method giving the number of bytes to 
     * follow. This value is the number of bytes actually written out, 
     * not the length of the string. Following the length, each character 
     * of the string is output, in sequence, using the UTF-8 encoding 
     * for the character. 
     *
     * @param      str   a string to be written.
     * @exception  IOException  if an I/O error occurs.
     * @since      JDK1.0
     */
    public final void writeUTF(String str) throws IOException {
	OutputStream out = this.out;
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

	out.write((utflen >>> 8) & 0xFF);
	out.write((utflen >>> 0) & 0xFF);
	for (int i = 0 ; i < strlen ; i++) {
	    int c = str.charAt(i);
	    if ((c >= 0x0001) && (c <= 0x007F)) {
		out.write(c);
	    } else if (c > 0x07FF) {
		out.write(0xE0 | ((c >> 12) & 0x0F));
		out.write(0x80 | ((c >>  6) & 0x3F));
		out.write(0x80 | ((c >>  0) & 0x3F));
		written += 2;
	    } else {
		out.write(0xC0 | ((c >>  6) & 0x1F));
		out.write(0x80 | ((c >>  0) & 0x3F));
		written += 1;
	    }
	}
	written += strlen + 2;
    }

    /**
     * Returns the number of bytes written to this data output stream.
     *
     * @return  the value of the <code>written</code> field.
     * @see     java.io.DataOutputStream#written
     * @since   JDK1.0
     */
    public final int size() {
	return written;
    }
}
