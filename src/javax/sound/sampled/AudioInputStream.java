/*
 * @(#)AudioInputStream.java	1.27 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.sound.sampled;

import java.io.InputStream;
import java.io.PushbackInputStream;
import java.io.IOException;


/**
 * An audio input stream is an input stream with a specified audio format and
 * length.  The length is expressed in sample frames, not bytes.
 * Several methods are provided for reading a certain number of bytes from
 * the stream, or an unspecified number of bytes.
 * The audio input stream keeps track  of the last byte that was read.
 * You can skip over an arbitrary number of bytes to get to a later position
 * for reading. An audio input stream may support marks.  When you set a mark,
 * the current position is remembered so that you can return to it later.
 * <p>
 * The <code>AudioSystem</code> class includes many methods that manipulate
 * <code>AudioInputStream</code> objects.
 * For example, the methods let you:
 * <ul>
 * <li> obtain an
 * audio input stream from an external audio file, stream, or URL
 * <li> write an external file from an audio input stream
 * <li> convert an audio input stream to a different audio format
 * </ul>
 *
 * @author David Rivas
 * @author Kara Kytle
 * @version 1.27, 03/01/23
 *
 * @see AudioSystem
 * @see Clip#open(AudioInputStream) Clip.open(AudioInputStream)
 * @since 1.3
 */
public class AudioInputStream extends InputStream {
    
    /**
     * The <code>InputStream</code> from which this <code>AudioInputStream</code>
     * object was constructed.
     */
    private PushbackInputStream stream;
    
    /**
     * PushbackInputStream overrides markSupported() to always return false.
     * However, it does not override the superclass mark() and reset() methods.
     * So, if these methods are supported in the stream on which the PushbackInputStream
     * is based, they actually do work.
     */
    private boolean markSupported;
    
    /**
     * The format of the audio data contained in the stream.
     */
    protected AudioFormat format;
    
    /**
     * This stream's length, in sample frames.
     */
    protected long frameLength;
    
    /**
     * The size of each frame, in bytes.
     */
    protected int frameSize;
    
    /**
     * The current position in this stream, in sample frames (zero-based).
     */
    protected long framePos;
    
    /**
     * The position where a mark was set.
     */
    private long markpos;
    
    
    /**
     * Constructs an audio input stream that has the requested format and length in sample frames,
     * using audio data from the specified input stream.
     * @param stream the stream on which this <code>AudioInputStream</code>
     * object is based
     * @param format the format of this stream's audio data
     * @param length the length in sample frames of the data in this stream
     */
    /*
     * Constructs an audio input stream that has the requested format and length in sample frames,
     * using audio data from the specified input stream.
     * @param stream the stream on which this <code>AudioInputStream</code>
     * object is based
     * @param format the format of this stream's audio data
     * @param length the length in sample frames of the data in this stream
     * @see AudioSystem#NOT_SPECIFIED
     */
    public AudioInputStream(InputStream stream, AudioFormat format, long length) {
	
	super();
	
	this.format = format;
	this.frameLength = length;
	this.frameSize = format.getFrameSize();
	
	if( this.frameSize == AudioSystem.NOT_SPECIFIED ) {
	    this.frameSize = 1;
	}
	
	if( this.frameSize > 1 ) {
	    this.stream = new PushbackInputStream(stream, frameSize-1);
	} else {
	    this.stream = new PushbackInputStream(stream, 1);
	}
	framePos = 0;
	markpos = 0;
	markSupported = stream.markSupported();
    }
    
    
    /**
     * Constructs an audio input stream that reads its data from the target
     * data line indicated.  The format of the stream is the same as that of
     * the target data line, and the length is AudioSystem#NOT_SPECIFIED.
     * @param line the target data line from which this stream obtains its data.
     * @see AudioSystem#NOT_SPECIFIED
     */
    public AudioInputStream(TargetDataLine line) {
	
	TargetDataLineInputStream tstream = new TargetDataLineInputStream(line);
	format = line.getFormat();
	frameLength = AudioSystem.NOT_SPECIFIED;
	frameSize = format.getFrameSize();
	
	if( frameSize == AudioSystem.NOT_SPECIFIED ) {
	    frameSize = 1;
	}
	if( this.frameSize > 1 ) {
	    this.stream = new PushbackInputStream(tstream, frameSize-1);
	} else {
	    this.stream = new PushbackInputStream(tstream, 1);
	}
	
	framePos = 0;
	markpos = 0;
	markSupported = stream.markSupported();
    }
    
    
    /**
     * Obtains the audio format of the sound data in this audio input stream.
     * @return an audio format object describing this stream's format
     */
    public AudioFormat getFormat() {
	return format;
    }
    
    
    /**
     * Obtains the length of the stream, expressed in sample frames rather than bytes.
     * @return the length in sample frames
     */
    public long getFrameLength() {
	return frameLength;
    }
    
    
    // $$jb: 06.02.99: Deleting this method...
    /*
     * Obtains the current read position in the stream, in sample frames.  This
     * indicates the zero-based number of the next sample frame that will be read.
     * @return the current read position
     */
    /*
      public long getFramePosition() {
      return framePos;
      }
    */
    
    
    /**
     * Reads the next byte of data from the audio input stream.  The audio input
     * stream's frame size must be one byte, or an <code>IOException</code>
     * will be thrown.
     *
     * @return the next byte of data, or -1 if the end of the stream is reached
     * @throws IOException if an input or output error occurs
     * @see #read(byte[], int, int)
     * @see #read(byte[])
     * @see #available
     * <p>
     */
    public int read() throws IOException {
	
	int temp;
	
	if( frameSize != 1 ) {
	    
	    throw new IOException("cannot read a single byte if frame size != 1");
	}
	
	if( (frameLength != AudioSystem.NOT_SPECIFIED) && (framePos >= frameLength) ) {
	    return -1;
	} else {
	    temp = stream.read();
	    
	    // update position only if an error is not returned from
	    // the underlying stream
	    if( temp >= 0 ) {
		framePos++;
	    }
	    return temp;
	}
    }
    
    
    /**
     * Reads some number of bytes from the audio input stream and stores them into
     * the buffer array <code>b</code>. The number of bytes actually read is
     * returned as an integer. This method blocks until input data is
     * available, the end of the stream is detected, or an exception is thrown.
     * @param b the buffer into which the data is read
     * @return the total number of bytes read into the buffer, or -1 if there
     * is no more data because the end of the stream has been reached
     * @throws IOException if an input or output error occurs
     * @see #read(byte[], int, int)
     * @see #read()
     * @see #available
     */
    /*
     * <p>
     * ISSUE: what happens if (b.length % frame size  != 0)??  do we throw
     * an exception, read the requested number of bytes, or read an integral
     * number of frames and return the number of bytes read?
     */
    public int read(byte[] b) throws IOException {
	return read(b,0,b.length);
    }
    
    
    /**
     * Reads up to a specified maximum number of bytes of data from the audio
     * stream, putting them into the given byte array.
     * @param b the buffer into which the data is read
     * @param off the offset, from the beginning of array <code>b</code>, at which
     * the data will be written
     * @param len the maximum number of bytes to read
     * @return the total number of bytes read into the buffer, or -1 if there
     * is no more data because the end of the stream has been reached
     * @throws IOException if an input or output error occurs
     * @see #read(byte[])
     * @see #read()
     * @see #skip
     * @see #available
     */
    /*
     * <p>
     * ISSUE: what happens if (len % frame size  != 0)??  do we throw
     * an exception, read the requested number of bytes, or read an integral
     * number of frames and return the number of bytes read?
     */
    public int read(byte[] b, int off, int len) throws IOException {
	
	int bytesRead;
	int extraBytes;
	
	// make sure we don't read fractions of a frame.
	if( (len%frameSize) != 0 ) {
	    len -= (len%frameSize);
	}
	
	if( frameLength != AudioSystem.NOT_SPECIFIED ) {
	    if( framePos >= frameLength ) {
		return -1;
	    } else {
		
		// don't try to read beyond our own set length in frames
		if( (len/frameSize) > (frameLength-framePos) ) {
		    len = (int) (frameLength-framePos) * frameSize;
		}
	    }
	}
	
	bytesRead = stream.read(b, off, len);
	
	if (bytesRead > 0) {
	    extraBytes = bytesRead % frameSize;
	    if (extraBytes > 0) {
		stream.unread(b, (off + bytesRead - extraBytes), extraBytes);
		bytesRead -= extraBytes;
	    }
	}
	
	// make sure to update our framePos
	framePos += bytesRead/frameSize;
	
	return bytesRead;
    }
    
    
    /**
     * Skips over and discards a specified number of bytes from this
     * audio input stream.
     * @param n the requested number of bytes to be skipped
     * @return the actual number of bytes skipped
     * @throws IOException if an input or output error occurs
     * @see #read
     * @see #available
     */
    
    public long skip(long n) throws IOException {
	
	long temp;
	
	// make sure not to skip fractional frames
	if( (n%frameSize) != 0 ) {
	    n -= (n%frameSize);
	}
	
	if( frameLength != AudioSystem.NOT_SPECIFIED ) {
	    // don't skip more than our set length in frames.
	    if( (n/frameSize) > (frameLength-framePos) ) {
		n = (frameLength-framePos) * frameSize;
	    }
	}
	temp = stream.skip(n);
	
	// if no error, update our position.
	if( temp%frameSize != 0 ) {
	    
	    // Throw an IOException if we've skipped a fractional number of frames
	    throw new IOException("Could not skip an integer number of frames.");
	}
	if( temp >= 0 ) {
	    framePos += temp/frameSize;
	}
	return temp;
	
    }
    
    
    /**
     * Returns the maximum number of bytes that can be read (or skipped over) from this
     * audio input stream without blocking.  This limit applies only to the next invocation of
     * a <code>read</code> or <code>skip</code> method for this audio input stream; the limit
     * can vary each time these methods are invoked.
     * @return the number of bytes that can be read from this audio input stream without blocking
     * @throws IOException if an input or output error occurs
     * @see #read(byte[], int, int)
     * @see #read(byte[])
     * @see #read()
     * @see #skip
     */
    public int available() throws IOException {
	
	int temp;
	
	temp = stream.available();
	
	// don't return greater than our set length in frames
	if( (frameLength != AudioSystem.NOT_SPECIFIED) && ( (temp/frameSize) > (frameLength-framePos)) ) {
	    return (int) (frameLength-framePos) * frameSize;
	} else {
	    return temp;
	}
    }
    
    
    /**
     * Closes this audio input stream and releases any system resources associated
     * with the stream.
     * @throws IOException if an input or output error occurs
     */
    public void close() throws IOException {
	stream.close();
    }
    
    
    /**
     * Marks the current position in this audio input stream.
     * @param readlimit the maximum number of bytes that can be read before
     * the mark position becomes invalid.
     * @see #reset
     * @see #markSupported
     */
    
    public void mark(int readlimit) {
	
	stream.mark(readlimit);
	
	if( markSupported() ) {
	    markpos = framePos;
	}
    }
    
    
    /**
     * Repositions this audio input stream to the position it had at the time its
     * <code>mark</code> method was last invoked.
     * @throws IOException if an input or output error occurs.
     * @see #mark
     * @see #markSupported
     */
    public void reset() throws IOException {
	
	stream.reset();
	framePos = markpos;
    }
    
    
    /**
     * Tests whether this audio input stream supports the <code>mark</code> and
     * <code>reset</code> methods.
     * @return <code>true</code> if this stream supports the <code>mark</code>
     * and <code>reset</code> methods; <code>false</code> otherwise
     * @see #mark
     * @see #reset
     */
    public boolean markSupported() {
	
	return markSupported;
    }
    
    
    /**
     * Private inner class that makes a TargetDataLine look like an InputStream.
     */
    private class TargetDataLineInputStream extends InputStream {
	
	/**
	 * The TargetDataLine on which this TargetDataLineInputStream is based.
	 */
	TargetDataLine line;
	
	
	TargetDataLineInputStream(TargetDataLine line) {
	    super();
	    this.line = line;
	}
	
	
	public int available() throws IOException {
	    return line.available();
	}
	
	//$$fb 2001-07-16: added this method to correctly close the underlying TargetDataLine.
	// fixes bug 4479984
	public void close() throws IOException {
	    // the line needs to be flushed and stopped to avoid a dead lock...
	    // Probably related to bugs 4417527, 4334868, 4383457
	    if (line.isActive()) {
		line.flush();
		line.stop();
	    }
	    line.close();
	}
	
	public int read() throws IOException {
	    
	    byte[] b = new byte[1];
	    
	    int value = read(b, 0, 1);
	    
	    if (value == -1) {
		return -1;
	    }
	    
	    value = (int)b[0];
	    
	    if (line.getFormat().getEncoding() == AudioFormat.Encoding.PCM_SIGNED) {
		value += 128;
	    }
	    
	    return value;
	}
	
	
	public int read(byte[] b, int off, int len) throws IOException {
	    try {
		return line.read(b, off, len);
	    } catch (IllegalArgumentException e) {
		throw new IOException(e.getMessage());
	    }
	}
    }
}
