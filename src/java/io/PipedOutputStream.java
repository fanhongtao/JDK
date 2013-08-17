/*
 * @(#)PipedOutputStream.java	1.15 98/07/01
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

import java.io.*;

/**
 * A piped output stream is the sending end of a communications 
 * pipe. Two threads can communicate by having one thread send data 
 * through a piped output stream and having the other thread read the 
 * data through a piped input stream. 
 *
 * @author  James Gosling
 * @version 1.15, 07/01/98
 * @see     java.io.PipedInputStream
 * @since   JDK1.0
 */
public
class PipedOutputStream extends OutputStream {

	/* REMIND: identification of the read and write sides needs to be
	   more sophisticated.  Either using thread groups (but what about
	   pipes within a thread?) or using finalization (but it may be a
	   long time until the next GC). */
    private PipedInputStream sink;
    boolean connected = false;

    /**
     * Creates a piped output stream connected to the specified piped 
     * input stream. 
     *
     * @param      snk   The piped input stream to connect to.
     * @exception  IOException  if an I/O error occurs.
     * @since      JDK1.0
     */
    public PipedOutputStream(PipedInputStream snk)  throws IOException {
	connect(snk);
    }
    
    /**
     * Creates a piped output stream that is not yet connected to a 
     * piped input stream. It must be connected to a piped input stream, 
     * either by the receiver or the sender, before being used. 
     *
     * @see     java.io.PipedInputStream#connect(java.io.PipedOutputStream)
     * @see     java.io.PipedOutputStream#connect(java.io.PipedInputStream)
     * @since   JDK1.0
     */
    public PipedOutputStream() {
    }
    
    /**
     * Connects this piped output stream to a receiver. 
     *
     * @param      snk   the piped output stream to connect to.
     * @exception  IOException  if an I/O error occurs.
     * @since      JDK1.0
     */
    public void connect(PipedInputStream snk) throws IOException {
	if (connected || snk.connected) {
	    throw new IOException("Already connected");
	}
	sink = snk;
	snk.closed = false;
	snk.in = -1;
	snk.out = 0;
	connected = true;
    }

    /**
     * Writes the specified <code>byte</code> to the piped output stream.
     *
     * @param      b   the <code>byte</code> to be written.
     * @exception  IOException  if an I/O error occurs.
     * @since      JDK1.0
     */
    public void write(int b)  throws IOException {
	sink.receive(b);
    }

    /**
     * Writes <code>len</code> bytes from the specified byte array 
     * starting at offset <code>off</code> to this piped output stream. 
     *
     * @param      b     the data.
     * @param      off   the start offset in the data.
     * @param      len   the number of bytes to write.
     * @exception  IOException  if an I/O error occurs.
     * @since      JDK1.0
     */
    public void write(byte b[], int off, int len) throws IOException {
	sink.receive(b, off, len);
    }

    /**
     * Flushes this output stream and forces any buffered output bytes 
     * to be written out. 
     * This will notify any readers that bytes are waiting in the pipe.
     *
     * @exception IOException if an I/O error occurs.
     * @since     JDK1.0
     */
    public synchronized void flush() throws IOException {
	if (sink != null) {
            synchronized (sink) {
                sink.notifyAll();
            }
	}
    }

    /**
     * Closes this piped output stream and releases any system resources 
     * associated with this stream. 
     *
     * @exception  IOException  if an I/O error occurs.
     * @since      JDK1.0
     */
    public void close()  throws IOException {
	if (sink != null) {
	    sink.receivedLast();
	}
    }
}
