/*
 * @(#)DigestInputStream.java	1.29 99/02/09
 *
 * Copyright 1995-1999 by Sun Microsystems, Inc.,
 * 901 San Antonio Road, Palo Alto, California, 94303, U.S.A.
 * All rights reserved.
 * 
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
 */

package java.security;

import java.io.IOException;
import java.io.EOFException;
import java.io.InputStream;
import java.io.FilterInputStream;
import java.io.PrintStream;
import java.io.ByteArrayInputStream;

/** 
 * A transparent stream that updates the associated message digest using 
 * the bits going through the stream. 
 *
 * <p>To complete the message digest computation, call one of the 
 * <code>digest</code> methods on the associated message 
 * digest after your calls to one of this digest input stream's <a href = 
 * "#read()">read</a> methods.
 *      
 * <p>It is possible to turn this stream on or off (see <a href =
 * "#on">on</a>). When it is on, a call to <code>read</code>
 * results in an update on the message digest.  But when it is off,
 * the message digest is not updated. The default is for the stream
 * to be on.
 *
 * <p>Note that digest objects can compute only one digest (see
 * MessageDigest),
 * so that in order to compute intermediate digests, a caller should
 * retain a handle onto the digest object, and clone it for each
 * digest to be computed, leaving the orginal digest untouched.
 *
 * @see MessageDigest
 * 
 * @see DigestOutputStream
 *
 * @version 1.29 99/02/09
 * @author Benjamin Renaud 
 */

public class DigestInputStream extends FilterInputStream {

    /* NOTE: This should be made a generic UpdaterInputStream */

    /* Are we on or off? */
    private boolean on = true;

    /**
     * The message digest associated with this stream.
     */
    protected MessageDigest digest;

    /**
     * Creates a digest input stream, using the specified input stream
     * and message digest.
     *
     * @param stream the input stream.
     *
     * @param digest the message digest to associate with this stream.
     */
    public DigestInputStream(InputStream stream, MessageDigest digest) {
	super(stream);
	setMessageDigest(digest);
    }

    /**
     * Returns the message digest associated with this stream.
     *
     * @return the message digest associated with this stream.
     */
    public MessageDigest getMessageDigest() {
	return digest;
    }    

    /**
     * Associates the specified message digest with this stream.
     *
     * @param digest the message digest to be associated with this stream.  
     */
    public void setMessageDigest(MessageDigest digest) {
	this.digest = digest;
    }

    /**
     * Reads a byte, and updates the message digest (if the digest
     * function is on).  That is, this method reads a byte from the
     * input stream, blocking until the byte is actually read. If the 
     * digest function is on (see <a href = "#on">on</a>), this method 
     * will then call <code>update</code> on the message digest associated
     * with this stream, passing it the byte read. 
     *
     * @return the byte read.
     *
     * @exception IOException if an I/O error occurs.
     * 
     * @see MessageDigest#update(byte) 
     */
    public int read() throws IOException {
	int ch = in.read();
	if (on && ch != -1) {
	    digest.update((byte)ch);
	}
	return ch;
    }

    /**
     * Reads into a byte array, and updates the message digest (if the
     * digest function is on).  That is, this method reads up to
     * <code>len</code> bytes from the input stream into the array 
     * <code>b</code>, starting at offset <code>off</code>. This method 
     * blocks until the data is actually
     * read. If the digest function is on (see <a href =
     * "#on">on</a>), this method will then call <code>update</code>
     * on the message digest associated with this stream, passing it
     * the data.
     *
     * @param b	the array into which the data is read.
     *
     * @param off the starting offset into <code>b</code> of where the 
     * data should be placed.
     *
     * @param len the maximum number of bytes to be read from the input
     * stream into b, starting at offset <code>off</code>.
     *
     * @return  the actual number of bytes read. This is less than 
     * <code>len</code> if the end of the stream is reached prior to 
     * reading <code>len</code> bytes. -1 is returned if no bytes were 
     * read because the end of the stream had already been reached when 
     * the call was made.
     *
     * @exception IOException if an I/O error occurs.
     * 
     * @see MessageDigest#update(byte[], int, int) 
     */
    public int read(byte[] b, int off, int len) throws IOException {
	int result = in.read(b, off, len);
	if (on && result != -1) {
	    digest.update(b, off, result);
	}
	return result;
    }

    /**
     * Turns the digest function on or off. The default is on.  When
     * it is on, a call to <a href = "#read">read</a> results in an
     * update on the message digest.  But when it is off, the message
     * digest is not updated.
     *   
     * @param on true to turn the digest function on, false to turn
     * it off.
     */
    public void on(boolean on) {
	this.on = on;
    }
	
    /**
     * Prints a string representation of this digest input stream and
     * its associated message digest object.  
     */
     public String toString() {
	 return "[Digest Input Stream] " + digest.toString();
     }
}	


  

