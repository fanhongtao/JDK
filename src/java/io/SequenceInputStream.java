/*
 * @(#)SequenceInputStream.java	1.15 98/07/01
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

import java.io.InputStream;
import java.util.Enumeration;
import java.util.Vector;

/**
 * The sequence input stream class allows an application to combine 
 * several input streams serially and make them appear as if they 
 * were a single input stream. Each input stream is read from, in 
 * turn, until it reaches the end of the stream. The sequence input 
 * stream class then closes that stream and automatically switches to 
 * the next input stream. 
 *
 * @author  Author van Hoff
 * @version 1.15, 07/01/98
 * @since   JDK1.0
 */
public
class SequenceInputStream extends InputStream {
    Enumeration e;
    InputStream in;
    
    /**
     * Constructs a new sequence input stream initialized to the 
     * specified enumeration of input streams. Each object in the 
     * enumeration must be an <code>InputStream</code>. 
     *
     * @param   e   an enumeration of input streams.
     * @see     java.util.Enumeration
     * @since   JDK1.0
     */
    public SequenceInputStream(Enumeration e) {
	this.e = e;
	try {
	    nextStream();
	} catch (IOException ex) {
	    // This should never happen
	    throw new Error("panic");
	}
    }
  
    /**
     * Constructs a new sequence input stream initialized to read first 
     * from the input stream <code>s1</code>, and then from the input 
     * stream <code>s2</code>. 
     *
     * @param   s1   the first input stream to read.
     * @param   s2   the second input stream to read.
     * @since   JDK1.0
     */
    public SequenceInputStream(InputStream s1, InputStream s2) {
	Vector	v = new Vector(2);

	v.addElement(s1);
	v.addElement(s2);
	e = v.elements();
	try {
	    nextStream();
	} catch (IOException ex) {
	    // This should never happen
	    throw new Error("panic");
	}
    }
   
    /**
     *  Continues reading in the next stream if an EOF is reached.
     */
    final void nextStream() throws IOException {
	if (in != null) {
	    in.close();
	}
	in = e.hasMoreElements() ? (InputStream) e.nextElement() : null;
    }

    /**
     * Returns the number of bytes available on the current stream.
     *
     * @since   JDK1.1
     */
    public int available() throws IOException {
	if(in == null) {
	    return 0; // no way to signal EOF from available()
	} 
	return in.available();
    }

    /**
     * Reads the next byte of data from this input stream. The byte is 
     * returned as an <code>int</code> in the range <code>0</code> to 
     * <code>255</code>. If no byte is available because the end of the 
     * stream has been reached, the value <code>-1</code> is returned. 
     * This method blocks until input data is available, the end of the 
     * stream is detected, or an exception is thrown. 
     * <p>
     * The <code>read</code> method of <code>SequenceInputStream</code> 
     * tries to read one character from the current substream. If it 
     * reaches the end of the stream, it calls the <code>close</code> 
     * method of the current substream and begins reading from the next 
     * substream. 
     *
     * @return     the next byte of data, or <code>-1</code> if the end of the
     *             stream is reached.
     * @exception  IOException  if an I/O error occurs.
     * @since      JDK1.0
     */
    public int read() throws IOException {
	if (in == null) {
	    return -1;
	}
	int c = in.read();
	if (c == -1) {
	    nextStream();
	    return read();
	}
	return c;
    }

    /**
     * Reads up to <code>len</code> bytes of data from this input stream 
     * into an array of bytes. This method blocks until at least 1 byte 
     * of input is available. If the first argument is <code>null</code>, 
     * up to <code>len</code> bytes are read and discarded. 
     * <p>
     * The <code>read</code> method of <code>SequenceInputStream</code> 
     * tries to read the data from the current substream. If it fails to 
     * read any characters because the substream has reached the end of 
     * the stream, it calls the <code>close</code> method of the current 
     * substream and begins reading from the next substream. 
     *
     * @param      b     the buffer into which the data is read.
     * @param      off   the start offset of the data.
     * @param      len   the maximum number of bytes read.
     * @exception  IOException  if an I/O error occurs.
     * @since      JDK1.0
     */
    public int read(byte buf[], int pos, int len) throws IOException {
	if (in == null) {
	    return -1;
	} else if (len == 0) { 
	    return 0;
	}
	int n = in.read(buf, pos, len);
	if (n <= 0) {
	    nextStream();
	    return read(buf, pos, len);
	}
	return n;
    }

    /**
     * Closes this input stream and releases any system resources 
     * associated with the stream. 
     * <p>
     * The <code>close</code> method of <code>SequenceInputStream</code> 
     * calls the <code>close</code> method of both the substream from 
     * which it is currently reading and the <code>close</code> method of 
     * all the substreams that it has not yet begun to read from. 
     *
     * @exception  IOException  if an I/O error occurs.
     * @since      JDK1.0
     */
    public void close() throws IOException {
	do {
	    nextStream();
	} while (in != null);
    }
}
