/*
 * @(#)BufferedReader.java	1.12 98/07/01
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
 * Read text from a character-input stream, buffering characters so as to
 * provide for the efficient reading of characters, arrays, and lines.
 *
 * <p> The buffer size may be specified, or the default size may be used.  The
 * default is large enough for most purposes.
 *
 * <p> In general, each read request made of a Reader causes a corresponding
 * read request to be made of the underlying character or byte stream.  It is
 * therefore advisable to wrap a BufferedReader around any Reader whose read()
 * operations may be costly, such as FileReaders and InputStreamReaders.  For
 * example,
 *
 * <pre>
 * BufferedReader in
 *   = new BufferedReader(new FileReader("foo.in"));
 * </pre>
 *
 * will buffer the input from the specified file.  Without buffering, each
 * invocation of read() or readLine() could cause bytes to be read from the
 * file, converted into characters, and then returned, which can be very
 * inefficient. 
 *
 * <p> Programs that use DataInputStreams for textual input can be localized by
 * replacing each DataInputStream with an appropriate BufferedReader.
 *
 * @see FileReader
 * @see InputStreamReader
 *
 * @version 	1.12, 98/07/01
 * @author	Mark Reinhold
 * @since	JDK1.1
 */

public class BufferedReader extends Reader {

    private Reader in;

    private char cb[];
    private int nChars, nextChar;

    private static final int INVALIDATED = -2;
    private static final int UNMARKED = -1;
    private int markedChar = UNMARKED;
    private int readAheadLimit = 0; /* Valid only when markedChar > 0 */

    private static int defaultCharBufferSize = 8192;
    private static int defaultExpectedLineLength = 80;

    /**
     * Create a buffering character-input stream that uses an input buffer of
     * the specified size.
     *
     * @param  in   A Reader
     * @param  sz   Input-buffer size
     *
     * @exception  IllegalArgumentException  If sz is <= 0
     */
    public BufferedReader(Reader in, int sz) {
	super(in);
	if (sz <= 0)
	    throw new IllegalArgumentException("Buffer size <= 0");
	this.in = in;
	cb = new char[sz];
	nextChar = nChars = 0;
    }

    /**
     * Create a buffering character-input stream that uses a default-sized
     * input buffer.
     *
     * @param  in   A Reader
     */
    public BufferedReader(Reader in) {
	this(in, defaultCharBufferSize);
    }

    /** Check to make sure that the stream has not been closed */
    private void ensureOpen() throws IOException {
	if (in == null)
	    throw new IOException("Stream closed");
    }

    /**
     * Fill the input buffer, taking the mark into account if it is valid.
     */
    private void fill() throws IOException {
	int dst;
	if (markedChar <= UNMARKED) {
	    /* No mark */
	    dst = 0;
	} else {
	    /* Marked */
	    int delta = nextChar - markedChar;
	    if (delta >= readAheadLimit) {
		/* Gone past read-ahead limit: Invalidate mark */
		markedChar = INVALIDATED;
		readAheadLimit = 0;
		dst = 0;
	    } else {
		if (readAheadLimit <= cb.length) {
		    /* Shuffle in the current buffer */
		    System.arraycopy(cb, markedChar, cb, 0, delta);
		    markedChar = 0;
		    dst = delta;
		} else {
		    /* Reallocate buffer to accomodate read-ahead limit */
		    char ncb[] = new char[readAheadLimit];
		    System.arraycopy(cb, markedChar, ncb, 0, delta);
		    cb = ncb;
		    markedChar = 0;
		    dst = delta;
		}
	    }
	}

	int n;
	do {
	    n = in.read(cb, dst, cb.length - dst);
	} while (n == 0);
	if (n > 0) {
	    nChars = dst + n;
	    nextChar = dst;
	}
    }

    /**
     * Read a single character.
     *
     * @exception  IOException  If an I/O error occurs
     */
    public int read() throws IOException {
	synchronized (lock) {
	    ensureOpen();
	    if (nextChar >= nChars) {
		fill();
		if (nextChar >= nChars)
		    return -1;
	    }
	    return cb[nextChar++];
	}
    }

    /**
     * Read characters into a portion of an array.
     *
     * <p> Ordinarily this method takes characters from this stream's character
     * buffer, filling it from the underlying stream as necessary.  If,
     * however, the buffer is empty, the mark is not valid, and the requested
     * length is at least as large as the buffer, then this method will read
     * characters directly from the underlying stream into the given array.
     * Thus redundant <code>BufferedReader</code>s will not copy data
     * unnecessarily.
     *
     * @param      cbuf  Destination buffer
     * @param      off   Offset at which to start storing characters
     * @param      len   Maximum number of characters to read
     *
     * @return     The number of bytes read, or -1 if the end of the stream has
     *             been reached
     *
     * @exception  IOException  If an I/O error occurs
     */
    public int read(char cbuf[], int off, int len) throws IOException {
	synchronized (lock) {
	    ensureOpen();
	    if (nextChar >= nChars) {
		/* If the requested length is larger than the buffer, and if
		   there is no mark/reset activity, do not bother to copy the
		   bytes into the local buffer.  In this way buffered streams
		   will cascade harmlessly. */
		if (len >= cb.length && markedChar <= UNMARKED) {
		    return in.read(cbuf, off, len);
		}
		fill();
	    }
	    if (nextChar >= nChars)
		return -1;
	    int n = Math.min(len, nChars - nextChar);
	    System.arraycopy(cb, nextChar,
			     cbuf, off, n);
	    nextChar += n;
	    return n;
	}
    }

    /**
     * Read a line of text.  A line is considered to be terminated by any one
     * of a line feed ('\n'), a carriage return ('\r'), or a carriage return
     * followed immediately by a linefeed.
     *
     * @return     A String containing the contents of the line, not including
     *             any line-termination characters, or null if the end of the
     *             stream has been reached
     *
     * @exception  IOException  If an I/O error occurs
     */
    public String readLine() throws IOException {
	StringBuffer s = new StringBuffer(defaultExpectedLineLength);
	synchronized (lock) {
	    ensureOpen();

	bufferLoop:
	    for (;;) {

		if (nextChar >= nChars)
		    fill();
		if (nextChar >= nChars) { /* EOF */
		    if (s.length() > 0)
			return s.toString();
		    else
			return null;
		}
		boolean eol = false;
		char c = 0;
		int i;

	    charLoop:
		for (i = nextChar; i < nChars; i++) {
		    c = cb[i];
		    if ((c == '\n') || (c == '\r')) {
			eol = true;
			break charLoop;
		    }
		}
		s.append(cb, nextChar, i - nextChar);
		nextChar = i;

		if (eol) {
		    nextChar++;
		    if (c == '\r') {
			if (nextChar >= nChars)
			    fill();
			if ((nextChar < nChars) && (cb[nextChar] == '\n'))
			    nextChar++;
		    }
		    break bufferLoop;
		}
	    }
	}

	return s.toString();
    }

    /**
     * Skip characters.
     *
     * @param  n  The number of characters to skip
     *
     * @return    The number of characters actually skipped
     *
     * @exception  IOException  If an I/O error occurs
     */
    public long skip(long n) throws IOException {
	synchronized (lock) {
	    ensureOpen();
	    long r = n;
	    while (r > 0) {
		if (nextChar >= nChars)
		    fill();
		if (nextChar >= nChars)	/* EOF */
		    break;
		long d = nChars - nextChar;
		if (r <= d) {
		    nextChar += r;
		    r = 0;
		    break;
		}
		else {
		    r -= d;
		    nextChar = nChars;
		}
	    }
	    return n - r;
	}
    }

    /**
     * Tell whether this stream is ready to be read.  A buffered character
     * stream is ready if the buffer is not empty, or if the underlying
     * character stream is ready.
     *
     * @exception  IOException  If an I/O error occurs
     */
    public boolean ready() throws IOException {
	synchronized (lock) {
	    ensureOpen();
	    return (nextChar < nChars) || in.ready();
	}
    }

    /**
     * Tell whether this stream supports the mark() operation, which it does.
     */
    public boolean markSupported() {
	return true;
    }

    /**
     * Mark the present position in the stream.  Subsequent calls to reset()
     * will attempt to reposition the stream to this point.
     *
     * @param readAheadLimit   Limit on the number of characters that may be
     *                         read while still preserving the mark.  After
     *                         reading this many characters, attempting to
     *                         reset the stream may fail.  A limit value larger
     *                         than the size of the input buffer will cause a
     *                         new buffer to be allocated whose size is no
     *                         smaller than limit.  Therefore large values
     *                         should be used with care.
     *
     * @exception  IllegalArgumentException  If readAheadLimit is < 0
     * @exception  IOException  If an I/O error occurs
     */
    public void mark(int readAheadLimit) throws IOException {
	if (readAheadLimit < 0) {
	    throw new IllegalArgumentException("Read-ahead limit < 0");
	}
	synchronized (lock) {
	    ensureOpen();
	    this.readAheadLimit = readAheadLimit;
	    markedChar = nextChar;
	}
    }

    /**
     * Reset the stream to the most recent mark.
     *
     * @exception  IOException  If the stream has never been marked,
     *                          or if the mark has been invalidated
     */
    public void reset() throws IOException {
	synchronized (lock) {
	    ensureOpen();
	    if (markedChar < 0)
		throw new IOException((markedChar == INVALIDATED)
				      ? "Mark invalid"
				      : "Stream not marked");
	    nextChar = markedChar;
	}
    }

    /**
     * Close the stream.
     *
     * @exception  IOException  If an I/O error occurs
     */
    public void close() throws IOException {
	synchronized (lock) {
	    if (in == null)
		return;
	    in.close();
	    in = null;
	    cb = null;
	}
    }

}
