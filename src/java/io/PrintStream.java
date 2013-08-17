/*
 * @(#)PrintStream.java	1.11 98/07/01
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
 * Print values and objects to an output stream, using the platform's default
 * character encoding to convert characters into bytes.
 *
 * <p> If automatic flushing is enabled at creation time, then the stream will
 * be flushed each time a line is terminated or a newline character is written.
 *
 * <p> Methods in this class never throw I/O exceptions.  Client code may
 * inquire as to whether any errors have occurred by invoking the
 * <code>checkError</code> method.
 *
 * <p><b>Note:</b> <i>This class is provided primarily for use in debugging,
 * and for compatibility with existing code; new code should use the
 * PrintWriter class.</i>
 *
 * @see        java.io.PrintWriter
 *
 * @version    1.11, 07/01/98
 * @author     Frank Yellin
 * @author     Mark Reinhold
 * @since      JDK1.0
 */

public class PrintStream extends FilterOutputStream {

    private boolean autoFlush = false;
    private boolean trouble = false;

    /**
     * Track both the text- and character-output streams, so that their buffers
     * can be flushed without flushing the entire stream.
     */
    private BufferedWriter textOut;
    private OutputStreamWriter charOut;

    /**
     * Create a new print stream.
     *
     * @deprecated As of JDK&nbsp;1.1, the preferred way to print text is
     * via the PrintWriter class.  Consider replacing code of the<br>
     * form &nbsp;<code>    PrintStream p = new PrintStream(out);</code><br>
     * with &nbsp;<code>    PrintWriter p = new PrintWriter(out);</code>
     *
     * @see java.io.PrintWriter#PrintWriter(java.io.OutputStream)
     *
     * @param  out        The output stream to which values and objects will be
     *                    printed
     */
    public PrintStream(OutputStream out) {
	this(out, false);
    }

    /**
     * Create a new PrintStream.
     *
     * @deprecated As of JDK&nbsp;1.1, the preferred way to print text is
     * via the PrintWriter class.  Consider replacing code of the<br>
     * form &nbsp;<code>    PrintStream p = new PrintStream(out, autoFlush);</code><br>
     * with &nbsp;<code>    PrintWriter p = new PrintWriter(out, autoFlush);</code>
     *
     * @see java.io.PrintWriter#PrintWriter(java.io.OutputStream, boolean)
     *
     * @param  out        The output stream to which values and objects will be
     *                    printed
     * @param  autoFlush  A boolean; if true, the output buffer will be flushed
     *                    whenever a line is terminated or a newline character
     *                    (<code>'\n'</code>) is written
     */
    public PrintStream(OutputStream out, boolean autoFlush) {
	super(out);
	this.autoFlush = autoFlush;
	this.charOut = new OutputStreamWriter(this);
	this.textOut = new BufferedWriter(this.charOut);
    }

    /** Check to make sure that the stream has not been closed */
    private void ensureOpen() throws IOException {
	if (out == null)
	    throw new IOException("Stream closed");
    }

    /**
     * Flush the stream.  This is done by writing any buffered output bytes to
     * the underlying output stream and then flushing that stream.
     *
     * @see        java.io.OutputStream#flush()
     */
    public void flush() {
	synchronized (this) {
	    try {
		ensureOpen();
		out.flush();
	    }
	    catch (IOException x) {
		trouble = true;
	    }
	}
    }

    private boolean closing = false; /* To avoid recursive closing */

    /**
     * Close the stream.  This is done by flushing the stream and then closing
     * the underlying output stream.
     *
     * @see        java.io.OutputStream#close()
     */
    public void close() {
	synchronized (this) {
	    if (! closing) {
		closing = true;
		try {
		    textOut.close();
		    out.close();
		}
		catch (IOException x) {
		    trouble = true;
		}
		textOut = null;
		charOut = null;
		out = null;
	    }
	}
    }

    /**
     * Flush the stream and check its error state.  Errors are cumulative;
     * once the stream encounters an error, this routine will continue to
     * return true on all successive calls.
     *
     * @return True if the print stream has encountered an error, either on the
     * underlying output stream or during a format conversion, otherwise false.
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
     * which also implement the write() methods of OutputStream
     */

    /**
     * Write a byte, blocking if necessary.  If the character is a newline and
     * automatic flushing is enabled, the stream's <code>flush</code> method
     * will be called.
     *
     * <p> Note that the byte is written as given; to write a character that
     * will be translated according to the platform's default character
     * encoding, use the <code>print(char)</code> or <code>println(char)</code>
     * methods.
     *
     * @param  b  The byte to be written
     * @see #print(char)
     * @see #println(char)
     */
    public void write(int b) {
	try {
	    synchronized (this) {
		ensureOpen();
		out.write(b);
		if ((b == '\n') && autoFlush)
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

    /**
     * Write a portion of a byte array, blocking if necessary.
     *
     * @param  buf   A byte array
     * @param  off   Offset from which to start taking bytes
     * @param  len   Number of bytes to write
     */
    public void write(byte buf[], int off, int len) {
	try {
	    synchronized (this) {
		ensureOpen();
		out.write(buf, off, len);
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

    /*
     * The following private methods on the text- and character-output streams
     * always flush the stream buffers, so that writes to the underlying byte
     * stream occur as promptly as with the original PrintStream.
     */

    private void write(char buf[]) {
	try {
	    synchronized (this) {
		ensureOpen();
		textOut.write(buf);
		textOut.flushBuffer();
		charOut.flushBuffer();
		if (autoFlush) {
		    for (int i = 0; i < buf.length; i++)
			if (buf[i] == '\n')
			    out.flush();
		}
	    }
	}
	catch (InterruptedIOException x) {
	    Thread.currentThread().interrupt();
	}
	catch (IOException x) {
	    trouble = true;
	}
    }

    private void write(String s) {
	try {
	    synchronized (this) {
		ensureOpen();
		textOut.write(s);
		textOut.flushBuffer();
		charOut.flushBuffer();
		if (autoFlush && (s.indexOf('\n') >= 0))
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

    private void newLine() {
	try {
	    synchronized (this) {
		ensureOpen();
		textOut.newLine();
		textOut.flushBuffer();
		charOut.flushBuffer();
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

    /**
     * Print a boolean value.  If the given value is true, then the string
     * <code>"true"</code> is written to the underlying output stream;
     * otherwise, the string <code>"false"</code> is written.
     *
     * @param      b   The <code>boolean</code> to be printed
     */
    public void print(boolean b) {
	write(b ? "true" : "false");
    }

    /**
     * Print a character.  The character is translated into one or more bytes
     * according to the platform's default character encoding.
     *
     * @param      c   The <code>char</code> to be printed
     */
    public void print(char c) {
	write(String.valueOf(c));
    }

    /**
     * Print an integer.  The string printed is the same as that returned by
     * the <code>toString</code> method of the <code>Integer</code> class when
     * invoked on the given <code>int</code> value.
     *
     * @param      i   The <code>int</code> to be printed
     * @see        java.lang.Integer#toString(int)
     */
    public void print(int i) {
	write(String.valueOf(i));
    }

    /**
     * Print a long integer.  The string printed is the same as that returned
     * by the <code>toString</code> method of the <code>Long</code> class when
     * invoked on the given <code>long</code> value.
     *
     * @param      l   The <code>long</code> to be printed
     * @see        java.lang.Long#toString(long)
     */
    public void print(long l) {
	write(String.valueOf(l));
    }

    /**
     * Print a floating-point number.  The string printed is the same as that
     * returned by the <code>toString</code> method of the <code>Float</code>
     * class when invoked on the given <code>float</code> value.
     *
     * @param      f   The <code>float</code> to be printed
     * @see        java.lang.Float#toString(float)
     */
    public void print(float f) {
	write(String.valueOf(f));
    }

    /**
     * Print a double-precision floating-point number.  The string printed is
     * the same as that returned by the <code>toString</code> method of the
     * <code>Double</code> class when invoked on the given <code>double</code>
     * value.
     *
     * @param      d   The <code>double</code> to be printed
     * @see        java.lang.Double#toString(double)
     */
    public void print(double d) {
	write(String.valueOf(d));
    }

    /**
     * Print an array of characters.  The characters are converted into bytes
     * according to the platform's default character encoding.
     *
     * @param      s   The array of chars to be printed
     */
    public void print(char s[]) {
	write(s);
    }

    /**
     * Print a string.  If the argument is <code>null</code>, the string
     * <code>"null"</code> is written to the underlying output stream.
     * Otherwise, the string's characters are converted into bytes according to
     * the platform's default character encoding.
     *
     * @param      s   The <code>String</code> to be printed
     */
    public void print(String s) {
	if (s == null) {
	    s = "null";
	}
	write(s);
    }

    /**
     * Print an object.  The string printed is the same as that returned by the
     * given object's <code>toString</code> method.
     *
     * @param      obj   The <code>Object</code> to be printed
     * @see        java.lang.Object#toString()
     */
    public void print(Object obj) {
	write(String.valueOf(obj));
    }


    /* Methods that do terminate lines */

    /**
     * Finish the current line by writing a line separator.  The line
     * separator string is defined by the system property
     * <code>line.separator</code>, and is not necessarily a single newline
     * character (<code>'\n'</code>).
     */
    public void println() {
	newLine();
    }

    /**
     * Print a boolean, and then finish the line.
     *
     * @see #print(boolean)
     */
    public void println(boolean x) {
	synchronized (this) {
	    print(x);
	    newLine();
	}
    }

    /**
     * Print a character, and then finish the line.
     *
     * @see #print(char)
     */
    public void println(char x) {
	synchronized (this) {
	    print(x);
	    newLine();
	}
    }

    /**
     * Print an integer, and then finish the line.
     *
     * @see #print(int)
     */
    public void println(int x) {
	synchronized (this) {
	    print(x);
	    newLine();
	}
    }

    /**
     * Print a long, and then finish the line.
     *
     * @see #print(long)
     */
    public void println(long x) {
	synchronized (this) {
	    print(x);
	    newLine();
	}
    }

    /**
     * Print a float, and then finish the line.
     *
     * @see #print(float)
     */
    public void println(float x) {
	synchronized (this) {
	    print(x);
	    newLine();
	}
    }

    /**
     * Print a double, and then finish the line.
     *
     * @see #print(double)
     */
    public void println(double x) {
	synchronized (this) {
	    print(x);
	    newLine();
	}
    }

    /**
     * Print an array of characters, and then finish the line.
     *
     * @see #print(char[])
     */
    public void println(char x[]) {
	synchronized (this) {
	    print(x);
	    newLine();
	}
    }

    /**
     * Print a String, and then finish the line.
     *
     * @see #print(String)
     */
    public void println(String x) {
	synchronized (this) {
	    print(x);
	    newLine();
	}
    }

    /**
     * Print an Object, and then finish the line.
     *
     * @see #print(Object)
     */
    public void println(Object x) {
	synchronized (this) {
	    print(x);
	    newLine();
	}
    }

}
