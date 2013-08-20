/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2000-2002 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Xerces" and "Apache Software Foundation" must
 *    not be used to endorse or promote products derived from this
 *    software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    nor may "Apache" appear in their name, without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation and was
 * originally based on software copyright (c) 1999, International
 * Business Machines, Inc., http://www.apache.org.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */

package com.sun.org.apache.xerces.internal.impl.io;

import java.io.InputStream;
import java.io.IOException;
import java.io.Reader;

/** 
 * Reader for UCS-2 and UCS-4 encodings.
 * (i.e., encodings from ISO-10646-UCS-(2|4)).
 *
 * @author Neil Graham, IBM
 *
 * @version $Id: UCSReader.java,v 1.3 2002/07/08 16:24:03 neilg Exp $
 */
public class UCSReader extends Reader {

    //
    // Constants
    //

    /** Default byte buffer size (8192, larger than that of ASCIIReader
     * since it's reasonable to surmise that the average UCS-4-encoded
     * file should be 4 times as large as the average ASCII-encoded file). 
     */
    public static final int DEFAULT_BUFFER_SIZE = 8192;

    public static final short UCS2LE = 1;
    public static final short UCS2BE = 2;
    public static final short UCS4LE = 4;
    public static final short UCS4BE = 8;

    //
    // Data
    //

    /** Input stream. */
    protected InputStream fInputStream;

    /** Byte buffer. */
    protected byte[] fBuffer;

    // what kind of data we're dealing with
    protected short fEncoding;

    //
    // Constructors
    //

    /** 
     * Constructs an ASCII reader from the specified input stream 
     * using the default buffer size.  The Endian-ness and whether this is
     * UCS-2 or UCS-4 needs also to be known in advance.
     *
     * @param inputStream The input stream.
     * @param encoding One of UCS2LE, UCS2BE, UCS4LE or UCS4BE.
     */
    public UCSReader(InputStream inputStream, short encoding) {
        this(inputStream, DEFAULT_BUFFER_SIZE, encoding);
    } // <init>(InputStream, short)

    /** 
     * Constructs an ASCII reader from the specified input stream 
     * and buffer size.  The Endian-ness and whether this is
     * UCS-2 or UCS-4 needs also to be known in advance.
     *
     * @param inputStream The input stream.
     * @param size        The initial buffer size.
     * @param encoding One of UCS2LE, UCS2BE, UCS4LE or UCS4BE.
     */
    public UCSReader(InputStream inputStream, int size, short encoding) {
        fInputStream = inputStream;
        fBuffer = new byte[size];
        fEncoding = encoding;
    } // <init>(InputStream,int,short)

    //
    // Reader methods
    //

    /**
     * Read a single character.  This method will block until a character is
     * available, an I/O error occurs, or the end of the stream is reached.
     *
     * <p> Subclasses that intend to support efficient single-character input
     * should override this method.
     *
     * @return     The character read, as an integer in the range 0 to 127
     *             (<tt>0x00-0x7f</tt>), or -1 if the end of the stream has
     *             been reached
     *
     * @exception  IOException  If an I/O error occurs
     */
    public int read() throws IOException { 
        int b0 = fInputStream.read() & 0xff;
        if (b0 == 0xff)
            return -1;
        int b1 = fInputStream.read() & 0xff;
        if (b1 == 0xff)
            return -1;
        if(fEncoding >=4) {
            int b2 = fInputStream.read() & 0xff;
            if (b2 == 0xff)
                return -1;
            int b3 = fInputStream.read() & 0xff;
            if (b3 == 0xff)
                return -1;
            System.err.println("b0 is " + (b0 & 0xff) + " b1 " + (b1 & 0xff) + " b2 " + (b2 & 0xff) + " b3 " + (b3 & 0xff));
            if (fEncoding == UCS4BE)
                return (b0<<24)+(b1<<16)+(b2<<8)+b3;
            else
                return (b3<<24)+(b2<<16)+(b1<<8)+b0;
        } else { // UCS-2
            if (fEncoding == UCS2BE)
                return (b0<<8)+b1;
            else
                return (b1<<8)+b0;
        }
    } // read():int

    /**
     * Read characters into a portion of an array.  This method will block
     * until some input is available, an I/O error occurs, or the end of the
     * stream is reached.
     *
     * @param      ch     Destination buffer
     * @param      offset Offset at which to start storing characters
     * @param      length Maximum number of characters to read
     *
     * @return     The number of characters read, or -1 if the end of the
     *             stream has been reached
     *
     * @exception  IOException  If an I/O error occurs
     */
    public int read(char ch[], int offset, int length) throws IOException {
        int byteLength = length << ((fEncoding >= 4)?2:1);
        if (byteLength > fBuffer.length) {
            byteLength = fBuffer.length;
        }
        int count = fInputStream.read(fBuffer, 0, byteLength);
        if(count == -1) return -1;
        // try and make count be a multiple of the number of bytes we're looking for
        if(fEncoding >= 4) { // BigEndian
            // this looks ugly, but it avoids an if at any rate...
            int numToRead = (4 - (count & 3) & 3);
            for(int i=0; i<numToRead; i++) {
                int charRead = fInputStream.read();
                if(charRead == -1) { // end of input; something likely went wrong!A  Pad buffer with nulls.
                    for (int j = i;j<numToRead; j++)
                        fBuffer[count+j] = 0;
                    break;
                } else {
                    fBuffer[count+i] = (byte)charRead; 
                }
            }
            count += numToRead;
        } else {
            int numToRead = count & 1;
            if(numToRead != 0) {
                count++;
                int charRead = fInputStream.read();
                if(charRead == -1) { // end of input; something likely went wrong!A  Pad buffer with nulls.
                    fBuffer[count] = 0;
                } else {
                    fBuffer[count] = (byte)charRead;
                }
            }
        }

        // now count is a multiple of the right number of bytes
        int numChars = count >> ((fEncoding >= 4)?2:1);
        int curPos = 0;
        for (int i = 0; i < numChars; i++) {
            int b0 = fBuffer[curPos++] & 0xff;
            int b1 = fBuffer[curPos++] & 0xff;
            if(fEncoding >=4) {
                int b2 = fBuffer[curPos++] & 0xff;
                int b3 = fBuffer[curPos++] & 0xff;
                if (fEncoding == UCS4BE)
                    ch[offset+i] = (char)((b0<<24)+(b1<<16)+(b2<<8)+b3);
                else
                    ch[offset+i] = (char)((b3<<24)+(b2<<16)+(b1<<8)+b0);
            } else { // UCS-2
                if (fEncoding == UCS2BE)
                    ch[offset+i] = (char)((b0<<8)+b1);
                else
                    ch[offset+i] = (char)((b1<<8)+b0);
            }
        }
        return numChars;
    } // read(char[],int,int)

    /**
     * Skip characters.  This method will block until some characters are
     * available, an I/O error occurs, or the end of the stream is reached.
     *
     * @param  n  The number of characters to skip
     *
     * @return    The number of characters actually skipped
     *
     * @exception  IOException  If an I/O error occurs
     */
    public long skip(long n) throws IOException {
        // charWidth will represent the number of bits to move
        // n leftward to get num of bytes to skip, and then move the result rightward
        // to get num of chars effectively skipped.
        // The trick with &'ing, as with elsewhere in this dcode, is
        // intended to avoid an expensive use of / that might not be optimized
        // away.
        int charWidth = (fEncoding >=4)?2:1;
        long bytesSkipped = fInputStream.skip(n<<charWidth);
        if((bytesSkipped & (charWidth | 1)) == 0) return bytesSkipped >> charWidth;
        return (bytesSkipped >> charWidth) + 1;
    } // skip(long):long

    /**
     * Tell whether this stream is ready to be read.
     *
     * @return True if the next read() is guaranteed not to block for input,
     * false otherwise.  Note that returning false does not guarantee that the
     * next read will block.
     *
     * @exception  IOException  If an I/O error occurs
     */
    public boolean ready() throws IOException {
	    return false;
    } // ready()

    /**
     * Tell whether this stream supports the mark() operation.
     */
    public boolean markSupported() {
	    return fInputStream.markSupported();
    } // markSupported()

    /**
     * Mark the present position in the stream.  Subsequent calls to reset()
     * will attempt to reposition the stream to this point.  Not all
     * character-input streams support the mark() operation.
     *
     * @param  readAheadLimit  Limit on the number of characters that may be
     *                         read while still preserving the mark.  After
     *                         reading this many characters, attempting to
     *                         reset the stream may fail.
     *
     * @exception  IOException  If the stream does not support mark(),
     *                          or if some other I/O error occurs
     */
    public void mark(int readAheadLimit) throws IOException {
	    fInputStream.mark(readAheadLimit);
    } // mark(int)

    /**
     * Reset the stream.  If the stream has been marked, then attempt to
     * reposition it at the mark.  If the stream has not been marked, then
     * attempt to reset it in some way appropriate to the particular stream,
     * for example by repositioning it to its starting point.  Not all
     * character-input streams support the reset() operation, and some support
     * reset() without supporting mark().
     *
     * @exception  IOException  If the stream has not been marked,
     *                          or if the mark has been invalidated,
     *                          or if the stream does not support reset(),
     *                          or if some other I/O error occurs
     */
    public void reset() throws IOException {
        fInputStream.reset();
    } // reset()

    /**
     * Close the stream.  Once a stream has been closed, further read(),
     * ready(), mark(), or reset() invocations will throw an IOException.
     * Closing a previously-closed stream, however, has no effect.
     *
     * @exception  IOException  If an I/O error occurs
     */
     public void close() throws IOException {
         fInputStream.close();
     } // close()

} // class UCSReader
