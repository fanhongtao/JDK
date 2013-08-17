/*
 * @(#)PrintWriter.java	1.13 98/07/01
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
 * Print formatted representations of objects to a text-output stream.  This
 * class implements all of the print methods found in PrintStream.  It does not
 * contain methods for writing raw bytes, for which a program should use
 * unencoded byte streams.
 *
 * <p> Unlike the PrintStream class, if automatic flushing is enabled it will
 * be done only when one of the println() methods is invoked, rather than
 * whenever a newline character happens to be output.  The println() methods
 * use the platform's own notion of line separator rather than the newline
 * character.
 *
 * <p> Methods in this class never throw I/O exceptions.  The client may
 * inquire as to whether any errors have occurred by invoking checkError().
 *
 * @version 	1.11, 97/01/27
 * @author	Frank Yellin
 * @author	Mark Reinhold
 * @since	JDK1.1
 */

public class PrintWriter extends Writer {

    private Writer out;
    private boolean autoFlush = false;
    private boolean trouble = false;

    /**
     * Line separator string.  This is the value of the line.separator
     * property at the moment that the stream was created.
     */
    private String lineSeparator;

    /**
     * Create a new PrintWriter, without automatic line flushing.
     *
     * @param  out        A character-output stream
     */
    public PrintWriter (Writer out) {
	this(out, false);
    }

    /**
     * Create a new PrintWriter.
     *
     * @param  out        A character-output stream
     * @param  autoFlush  A boolean; if true, the println() methods will flush
     *                    the output buffer
     */
    public PrintWriter(Writer out,
		       boolean autoFlush) {
	super(out);
	this.out = out;
	this.autoFlush = autoFlush;
	lineSeparator = System.getProperty("line.separator");
    }

    /**
     * Create a new PrintWriter, without automatic line flushing, from an
     * existing OutputStream.  This convenience constructor creates the
     * necessary intermediate OutputStreamWriter, which will convert characters
     * into bytes using the default character encoding.
     *
     * @param  out        An output stream
     *
     * @see java.io.OutputStreamWriter#OutputStreamWriter(java.io.OutputStream)
     */
    public PrintWriter(OutputStream out) {
	this(out, false);
    }

    /**
     * Create a new PrintWriter from an existing OutputStream.  This
     * convenience constructor creates the necessary intermediate
     * OutputStreamWriter, which will convert characters into bytes using the
     * default character encoding.
     *
     * @param  out        An output stream
     * @param  autoFlush  A boolean; if true, the println() methods will flush
     *                    the output buffer
     *
     * @see java.io.OutputStreamWriter#OutputStreamWriter(java.io.OutputStream)
     */
    public PrintWriter(OutputStream out, boolean autoFlush) {
	this(new BufferedWriter(new OutputStreamWriter(out)), autoFlush);
    }

    /** Check to make sure that the stream has not been closed */
    private void ensureOpen() throws IOException {
	if (out == null)
	    throw new IOException("Stream closed");
    }

    /** Flush the stream. */
    public void flush() {
	try {
	    synchronized (lock) {
		ensureOpen();
		out.flush();
	    }
	}
	catch (IOException x) {
	    trouble = true;
	}
    }

    /** Close the stream. */
    public void close() {
	try {
	    synchronized (lock) {
		if (out == null)
		    return;
		out.close();
		out = null;
	    }
	}
	catch (IOException x) {
	    trouble = true;
	}
    }

    /**
     * Flush the stream and check its error state.  Errors are cumulative;
     * once the stream encounters an error, this routine will return true on
     * all successive calls.
     *
     * @return True if the print stream has encountered an error, either on the
     * underlying output stream or during a format conversion.
     */
    public boolean checkError() {
	if (out != null)
	    flush();
	return trouble;
    }

    /** Indicate that an error has occurred. */
    protected void setError() {
	trouble = true;
    }


    /*
     * Exception-catching, synchronized output operations,
     * which also implement the write() methods of Writer
     */

    /** Write a single character. */
    public void write(int c) {
	try {
	    synchronized (lock) {
		ensureOpen();
		out.write(c);
	    }
	}
	catch (InterruptedIOException x) {
	    Thread.currentThread().interrupt();
	}
	catch (IOException x) {
	    trouble = true;
	}
    }

    /** Write a portion of an array of characters. */
    public void write(char buf[], int off, int len) {
	try {
	    synchronized (lock) {
		ensureOpen();
		out.write(buf, off, len);
	    }
	}
	catch (InterruptedIOException x) {
	    Thread.currentThread().interrupt();
	}
	catch (IOException x) {
	    trouble = true;
	}
    }

    /**
     * Write an array of characters.  This method cannot be inherited from the
     * Writer class because it must suppress I/O exceptions.
     */
    public void write(char buf[]) {
	write(buf, 0, buf.length);
    }

    /** Write a portion of a string. */
    public void write(String s, int off, int len) {
	try {
	    synchronized (lock) {
		ensureOpen();
		out.write(s, off, len);
	    }
	}
	catch (InterruptedIOException x) {
	    Thread.currentThread().interrupt();
	}
	catch (IOException x) {
	    trouble = true;
	}
    }

    /**
     * Write a string.  This method cannot be inherited from the Writer class
     * because it must suppress I/O exceptions.
     */
    public void write(String s) {
	write(s, 0, s.length());
    }

    private void newLine() {
	try {
	    synchronized (lock) {
		ensureOpen();
		out.write(lineSeparator);
		if (autoFlush)
		    out.flush();
	    }
	}
	catch (InterruptedIOException x) {
	    Thread.currentThread().interrupt();
	}
	catch (IOException x) {
	    trouble = true;
	}
    }


    /* Methods that do not terminate lines */

    /** Print a boolean. */
    public void print(boolean b) {
	write(b ? "true" : "false");
    }

    /** Print a character. */
    public void print(char c) {
	write(String.valueOf(c));
    }

    /** Print an integer. */
    public void print(int i) {
	write(String.valueOf(i));
    }

    /** Print a long. */
    public void print(long l) {
	write(String.valueOf(l));
    }

    /** Print a float. */
    public void print(float f) {
	write(String.valueOf(f));
    }

    /** Print a double. */
    public void print(double d) {
	write(String.valueOf(d));
    }

    /** Print an array of chracters. */
    public void print(char s[]) {
	write(s);
    }

    /** Print a String. */
    public void print(String s) {
	if (s == null) {
	    s = "null";
	}
	write(s);
    }

    /** Print an object. */
    public void print(Object obj) {
	write(String.valueOf(obj));
    }


    /* Methods that do terminate lines */

    /** Finish the line. */
    public void println() {
	synchronized (lock) {
	    newLine();
	}
    }

    /** Print a boolean, and then finish the line. */
    public void println(boolean x) {
	synchronized (lock) {
	    print(x);
	    newLine();
	}
    }

    /** Print a character, and then finish the line. */
    public void println(char x) {
	synchronized (lock) {
	    print(x);
	    newLine();
	}
    }

    /** Print an integer, and then finish the line. */
    public void println(int x) {
	synchronized (lock) {
	    print(x);
	    newLine();
	}
    }

    /** Print a long, and then finish the line. */
    public void println(long x) {
	synchronized (lock) {
	    print(x);
	    newLine();
	}
    }

    /** Print a float, and then finish the line. */
    public void println(float x) {
	synchronized (lock) {
	    print(x);
	    newLine();
	}
    }

    /** Print a double, and then finish the line. */
    public void println(double x) {
	synchronized (lock) {
	    print(x);
	    newLine();
	}
    }

    /** Print an array of characters, and then finish the line. */
    public void println(char x[]) {
	synchronized (lock) {
	    print(x);
	    newLine();
	}
    }

    /** Print a String, and then finish the line. */
    public void println(String x) {
	synchronized (lock) {
	    print(x);
	    newLine();
	}
    }

    /** Print an Object, and then finish the line. */
    public void println(Object x) {
	synchronized (lock) {
	    print(x);
	    newLine();
	}
    }

}
