/*
 * @(#)PrintWriter.java	1.29 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
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
 * @version 	1.29, 01/23/03
 * @author	Frank Yellin
 * @author	Mark Reinhold
 * @since	JDK1.1
 */

public class PrintWriter extends Writer {

    /**
     * The underlying character-output stream of this
     * <code>PrintWriter</code>.
     *
     * @since 1.2
     */
    protected Writer out;

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
	lineSeparator = (String) java.security.AccessController.doPrivileged(
               new sun.security.action.GetPropertyAction("line.separator"));
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

    /** 
     * Flush the stream. 
     * @see #checkError()
     */
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

    /** 
     * Close the stream. 
     * @see #checkError()
     */
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
     * Flush the stream if it's not closed and check its error state.  
     * Errors are cumulative; once the stream encounters an error, this 
     * routine will return true on all successive calls.
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

    /** 
     * Write a single character.
     * @param c int specifying a character to be written.
     */
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

    /** 
     * Write a portion of an array of characters. 
     * @param buf Array of characters
     * @param off Offset from which to start writing characters
     * @param len Number of characters to write
     */
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
     * @param buf Array of characters to be written
     */
    public void write(char buf[]) {
	write(buf, 0, buf.length);
    }

    /** 
     * Write a portion of a string. 
     * @param s A String
     * @param off Offset from which to start writing characters
     * @param len Number of characters to write
     */
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
     * @param s String to be written
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

    /**
     * Print a boolean value.  The string produced by <code>{@link
     * java.lang.String#valueOf(boolean)}</code> is translated into bytes
     * according to the platform's default character encoding, and these bytes
     * are written in exactly the manner of the <code>{@link
     * #write(int)}</code> method.
     *
     * @param      b   The <code>boolean</code> to be printed
     */
    public void print(boolean b) {
	write(b ? "true" : "false");
    }

    /**
     * Print a character.  The character is translated into one or more bytes
     * according to the platform's default character encoding, and these bytes
     * are written in exactly the manner of the <code>{@link
     * #write(int)}</code> method.
     *
     * @param      c   The <code>char</code> to be printed
     */
    public void print(char c) {
	write(c);
    }

    /**
     * Print an integer.  The string produced by <code>{@link
     * java.lang.String#valueOf(int)}</code> is translated into bytes according
     * to the platform's default character encoding, and these bytes are
     * written in exactly the manner of the <code>{@link #write(int)}</code>
     * method.
     *
     * @param      i   The <code>int</code> to be printed
     * @see        java.lang.Integer#toString(int)
     */
    public void print(int i) {
	write(String.valueOf(i));
    }

    /**
     * Print a long integer.  The string produced by <code>{@link
     * java.lang.String#valueOf(long)}</code> is translated into bytes
     * according to the platform's default character encoding, and these bytes
     * are written in exactly the manner of the <code>{@link #write(int)}</code>
     * method.
     *
     * @param      l   The <code>long</code> to be printed
     * @see        java.lang.Long#toString(long)
     */
    public void print(long l) {
	write(String.valueOf(l));
    }

    /**
     * Print a floating-point number.  The string produced by <code>{@link
     * java.lang.String#valueOf(float)}</code> is translated into bytes
     * according to the platform's default character encoding, and these bytes
     * are written in exactly the manner of the <code>{@link #write(int)}</code>
     * method.
     *
     * @param      f   The <code>float</code> to be printed
     * @see        java.lang.Float#toString(float)
     */
    public void print(float f) {
	write(String.valueOf(f));
    }

    /**
     * Print a double-precision floating-point number.  The string produced by
     * <code>{@link java.lang.String#valueOf(double)}</code> is translated into
     * bytes according to the platform's default character encoding, and these
     * bytes are written in exactly the manner of the <code>{@link
     * #write(int)}</code> method.
     *
     * @param      d   The <code>double</code> to be printed
     * @see        java.lang.Double#toString(double)
     */
    public void print(double d) {
	write(String.valueOf(d));
    }

    /**
     * Print an array of characters.  The characters are converted into bytes
     * according to the platform's default character encoding, and these bytes
     * are written in exactly the manner of the <code>{@link #write(int)}</code>
     * method.
     *
     * @param      s   The array of chars to be printed
     *
     * @throws  NullPointerException  If <code>s</code> is <code>null</code>
     */
    public void print(char s[]) {
	write(s);
    }

    /**
     * Print a string.  If the argument is <code>null</code> then the string
     * <code>"null"</code> is printed.  Otherwise, the string's characters are
     * converted into bytes according to the platform's default character
     * encoding, and these bytes are written in exactly the manner of the
     * <code>{@link #write(int)}</code> method.
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
     * Print an object.  The string produced by the <code>{@link
     * java.lang.String#valueOf(Object)}</code> method is translated into bytes
     * according to the platform's default character encoding, and these bytes
     * are written in exactly the manner of the <code>{@link #write(int)}</code>
     * method.
     *
     * @param      obj   The <code>Object</code> to be printed
     * @see        java.lang.Object#toString()
     */
    public void print(Object obj) {
	write(String.valueOf(obj));
    }


    /* Methods that do terminate lines */

    /**
     * Terminate the current line by writing the line separator string.  The
     * line separator string is defined by the system property
     * <code>line.separator</code>, and is not necessarily a single newline
     * character (<code>'\n'</code>).
     */
    public void println() {
	newLine();
    }

    /**
     * Print a boolean value and then terminate the line.  This method behaves
     * as though it invokes <code>{@link #print(boolean)}</code> and then
     * <code>{@link #println()}</code>.
     *
     * @param x the <code>boolean</code> value to be printed
     */
    public void println(boolean x) {
	synchronized (lock) {
	    print(x);
	    println();
	}
    }

    /**
     * Print a character and then terminate the line.  This method behaves as
     * though it invokes <code>{@link #print(char)}</code> and then <code>{@link
     * #println()}</code>.
     *
     * @param x the <code>char</code> value to be printed
     */
    public void println(char x) {
	synchronized (lock) {
	    print(x);
	    println();
	}
    }

    /**
     * Print an integer and then terminate the line.  This method behaves as
     * though it invokes <code>{@link #print(int)}</code> and then <code>{@link
     * #println()}</code>.
     *
     * @param x the <code>int</code> value to be printed
     */
    public void println(int x) {
	synchronized (lock) {
	    print(x);
	    println();
	}
    }

    /**
     * Print a long integer and then terminate the line.  This method behaves
     * as though it invokes <code>{@link #print(long)}</code> and then
     * <code>{@link #println()}</code>.
     *
     * @param x the <code>long</code> value to be printed
     */
    public void println(long x) {
	synchronized (lock) {
	    print(x);
	    println();
	}
    }

    /**
     * Print a floating-point number and then terminate the line.  This method
     * behaves as though it invokes <code>{@link #print(float)}</code> and then
     * <code>{@link #println()}</code>.
     *
     * @param x the <code>float</code> value to be printed
     */
    public void println(float x) {
	synchronized (lock) {
	    print(x);
	    println();
	}
    }

    /**
     * Print a double-precision floating-point number and then terminate the
     * line.  This method behaves as though it invokes <code>{@link
     * #print(double)}</code> and then <code>{@link #println()}</code>.
     *
     * @param x the <code>double</code> value to be printed
     */
    public void println(double x) {
	synchronized (lock) {
	    print(x);
	    println();
	}
    }

    /**
     * Print an array of characters and then terminate the line.  This method
     * behaves as though it invokes <code>{@link #print(char[])}</code> and then
     * <code>{@link #println()}</code>.
     *
     * @param x the array of <code>char</code> values to be printed
     */
    public void println(char x[]) {
	synchronized (lock) {
	    print(x);
	    println();
	}
    }

    /**
     * Print a String and then terminate the line.  This method behaves as
     * though it invokes <code>{@link #print(String)}</code> and then
     * <code>{@link #println()}</code>.
     *
     * @param x the <code>String</code> value to be printed
     */
    public void println(String x) {
	synchronized (lock) {
	    print(x);
	    println();
	}
    }

    /**
     * Print an Object and then terminate the line.  This method behaves as
     * though it invokes <code>{@link #print(Object)}</code> and then
     * <code>{@link #println()}</code>.
     *
     * @param x the <code>Object</code> value to be printed
     */
    public void println(Object x) {
	synchronized (lock) {
	    print(x);
	    println();
	}
    }

}
