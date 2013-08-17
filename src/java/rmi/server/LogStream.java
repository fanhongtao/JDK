/*
 * @(#)LogStream.java	1.8 98/07/01
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
package java.rmi.server;

import java.io.*;
import java.util.*;

/**
 * <code>LogStream</code> presents a mechanism for logging errors that are
 * of possible interest to those monitoring the system.
 *
 * @author Ann Wollrath (lots of code stolen from Ken Arnold)
 */
public class LogStream extends PrintStream {

    /** table mapping known log names to log stream objects */
    private static Hashtable	known = new Hashtable(5);
    /** default output stream for new logs */
    private static PrintStream	defaultStream = System.err;

    /** log name for this log */
    private String name;

    /** stream where output of this log is sent to */
    private OutputStream logOut;

    /** string writer for writing message prefixes to log stream */
    private OutputStreamWriter logWriter;

    /** string buffer used for constructing log message prefixes */
    private StringBuffer buffer = new StringBuffer();

    /** stream used for buffering lines */
    private ByteArrayOutputStream bufOut;

    /**
     * Create a new LogStream object.  Since this only constructor is
     * private, users must have a LogStream created through the "log"
     * method.
     * @param name string identifying messages from this log
     * @out output stream that log messages will be sent to
     */
    private LogStream(String name, OutputStream out)
    {
	super(new ByteArrayOutputStream());
	bufOut = (ByteArrayOutputStream) super.out;

	this.name = name;
	setOutputStream(out);
    }

    /**
     * Return the LogStream identified by the given name.  If
     * a log corresponding to "name" does not exist, a log using
     * the default stream is created.
     */
    public static LogStream log(String name) {
	LogStream stream;
	synchronized (known) {
	    stream = (LogStream)known.get(name);
	    if (stream == null) {
		stream = new LogStream(name, defaultStream);
	    }
	    known.put(name, stream);
	}
	return stream;
    }

    /**
     * Return the current default stream for new logs.
     */
    public static synchronized PrintStream getDefaultStream() {
	return defaultStream;
    }

    /**
     * Set the default stream for new logs.
     */
    public static synchronized void setDefaultStream(PrintStream newDefault) {
	defaultStream = newDefault;
    }

    /**
     * Return the current stream to which output from this log is sent.
     */
    public synchronized OutputStream getOutputStream()
    {
	return logOut;
    }
    
    /**
     * Set the stream to which output from this log is sent.
     */
    public synchronized void setOutputStream(OutputStream out)
    {
	logOut = out;
	// Maintain an OutputStreamWriter with default CharToByteConvertor
	// (just like new PrintStream) for writing log message prefixes.
	logWriter = new OutputStreamWriter(logOut);
    }
    
    /**
     * Write a byte of data to the stream.  If it is not a newline, then
     * the byte is appended to the internal buffer.  If it is a newline,
     * then the currently buffered line is sent to the log's output
     * stream, prefixed with the appropriate logging information.
     */
    public void write(int b)
    {
	if (b == '\n') {
	    // synchronize on "this" first to avoid potential deadlock
	    synchronized (this) {
		synchronized (logOut) {
		    // construct prefix for log messages:
		    buffer.setLength(0);;
		    buffer.append(		// date/time stamp...
			(new Date()).toString());
		    buffer.append(':');
		    buffer.append(name);	// ...log name...
		    buffer.append(':');
		    buffer.append(Thread.currentThread().getName());
		    buffer.append(':');	// ...and thread name

		    try {
			// write prefix through to underlying byte stream
			logWriter.write(buffer.toString());
			logWriter.flush();

			// finally, write the already converted bytes of
			// the log message
			bufOut.writeTo(logOut);
			logOut.write(b);
			logOut.flush();
		    } catch (IOException e) {
			setError();
		    } finally {
			bufOut.reset();
		    }
		}
	    }
	}
	else
	    super.write(b);
    }

    /**
     * Write a subarray of bytes.  Pass each through write byte method.
     */
    public void write(byte b[], int off, int len)
    {
	if (len < 0)
	    throw new ArrayIndexOutOfBoundsException(len);
	for (int i = 0; i < len; ++ i)
	    write(b[off + i]);
    }

    /**
     * Return log name as string representation
     */
    public String toString()
    {
	return name;
    }

    /** constants for logging levels */
    public static final int SILENT  = 0;
    public static final int BRIEF   = 10;
    public static final int VERBOSE = 20;

    /**
     * Convert a string name of a logging level to its internal
     * integer representation.
     */
    public static int parseLevel(String s)
    {
	if ((s == null) || (s.length() < 1))
	    return -1;

	try {
	    return Integer.parseInt(s);
	} catch (NumberFormatException e) {
	}
	if (s.length() < 1)
	    return -1;

	if ("SILENT".startsWith(s.toUpperCase()))
	    return SILENT;
	else if ("BRIEF".startsWith(s.toUpperCase()))
	    return BRIEF;
	else if ("VERBOSE".startsWith(s.toUpperCase()))
	    return VERBOSE;

	return -1;
    }
}
