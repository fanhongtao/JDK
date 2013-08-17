/*
 * @(#)ZipFile.java	1.37 98/09/24
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

package java.util.zip;

import java.io.InputStream;
import java.io.IOException;
import java.io.EOFException;
import java.io.File;
import java.util.Vector;
import java.util.Enumeration;
import java.util.NoSuchElementException;
import java.security.AccessController;

/**
 * This class is used to read entries from a zip file.
 *
 * @version	1.37, 09/24/98
 * @author	David Connelly
 */
public
class ZipFile implements ZipConstants {
    private long jzfile;  // address of jzfile data
    private String name;  // zip file name
    private int total;	  // total number of entries

    private static final int STORED = ZipEntry.STORED;
    private static final int DEFLATED = ZipEntry.DEFLATED;

    static {
	AccessController.doPrivileged(
			  new sun.security.action.LoadLibraryAction("zip"));
	initIDs();
    }

    private static native void initIDs();

    /**
     * Opens a zip file for reading.
     * 
     * <p>First, if there is a security
     * manager, its <code>checkRead</code> method
     * is called with the <code>name</code> argument
     * as its argument to ensure the read is allowed.
     *
     * @param name the name of the zip file
     * @exception ZipException if a ZIP format error has occurred
     * @exception IOException if an I/O error has occurred
     * @exception  SecurityException  if a security manager exists and its  
     *             <code>checkRead</code> method doesn't allow read access to the file.
     * @see SecurityManager#checkRead(java.lang.String)
     */
    public ZipFile(String name) throws IOException {
	SecurityManager sm = System.getSecurityManager();
	if (sm != null) {
	    sm.checkRead(name);
	}
	jzfile = open(name);
	this.name = name;
	this.total = getTotal(jzfile);
    }

    private static native long open(String name);
    private static native int getTotal(long jzfile);

    /**
     * Opens a ZIP file for reading given the specified File object.
     * @param file the ZIP file to be opened for reading
     * @exception ZipException if a ZIP error has occurred
     * @exception IOException if an I/O error has occurred
     */
    public ZipFile(File file) throws ZipException, IOException {
	this(file.getPath());
    }

    /**
     * Returns the zip file entry for the specified name, or null
     * if not found.
     *
     * @param name the name of the entry
     * @return the zip file entry, or null if not found
     * @exception IllegalStateException if the zip file has been closed
     */
    public ZipEntry getEntry(String name) {
	if (name == null) {
	    throw new NullPointerException("name");
	}
	if (jzfile == 0) {
	    throw new IllegalStateException("zip file closed");
	}
	long jzentry = getEntry(jzfile, name);
	if (jzentry != 0) {
	    return new ZipEntry(name, jzentry);
	}
	return null;
    }

    private static native long getEntry(long jzfile, String name);

    /**
     * Returns an input stream for reading the contents of the specified
     * zip file entry.
     *
     * @param entry the zip file entry
     * @exception ZipException if a ZIP format error has occurred
     * @exception IOException if an I/O error has occurred
     * @exception IllegalStateException if the zip file has been closed
     */
    public InputStream getInputStream(ZipEntry entry) throws IOException {
	return getInputStream(entry.name);
    }

    /**
     * Returns an input stream for reading the contents of the specified
     * entry, or null if the entry was not found.
     */
    private InputStream getInputStream(String name) throws IOException {
	if (name == null) {
	    throw new NullPointerException("name");
	}
	if (jzfile == 0) {
	    throw new IllegalStateException("zip file closed");
	}
	long jzentry = getEntry(jzfile, name);
	if (jzentry == 0) {
	    return null;
	}
	InputStream in = new ZipFileInputStream(jzfile, jzentry);
	switch (getMethod(jzentry)) {
	case STORED:
	    return in;
	case DEFLATED:
	    return new InflaterInputStream(in, getInflater()) {
		public void close() {
		    releaseInflater(inf);
		}
		// Override fill() method to provide an extra "dummy" byte
		// at the end of the input stream. This is required when
		// using the "nowrap" Inflater option.
		protected void fill() throws IOException {
		    if (eof) {
			throw new EOFException(
			    "Unexpected end of ZLIB input stream");
		    }
		    len = this.in.read(buf, 0, buf.length);
		    if (len == -1) {
			buf[0] = 0;
			len = 1;
			eof = true;
		    }
		    inf.setInput(buf, 0, len);
		}
		private boolean eof;
	    };
	default:
	    throw new ZipException("invalid compression method");
	}
    }

    private static native int getMethod(long jzentry);

    /*
     * Gets an inflater from the list of available inflaters or allocates
     * a new one.
     */
    private Inflater getInflater() {
	synchronized (inflaters) {
	    int size = inflaters.size();
	    if (size > 0) {
		Inflater inf = (Inflater)inflaters.remove(size - 1);
		inf.reset();
		return inf;
	    } else {
		return new Inflater(true);
	    }
	}
    }

    /*
     * Releases the specified inflater to the list of available inflaters.
     */
    private void releaseInflater(Inflater inf) {
	synchronized (inflaters) {
	    inflaters.add(inf);
	}
    }

    // List of available Inflater objects for decompression
    private Vector inflaters = new Vector();

    /**
     * Returns the path name of the ZIP file.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns an enumeration of the ZIP file entries.
     * @exception IllegalStateException if the zip file has been closed
     */
    public Enumeration entries() {
	if (jzfile == 0) {
	    throw new IllegalStateException("zip file closed");
	}
	return new Enumeration() {
	    private int i = 0;
	    public boolean hasMoreElements() {
		return i < total;
	    }
	    public Object nextElement() throws NoSuchElementException {
		if (i >= total) {
		    throw new NoSuchElementException();
		}
		long jzentry = getNextEntry(jzfile, i++);
		if (jzentry == 0) {
		    throw new InternalError("jzentry == 0");
		}
		return new ZipEntry(jzentry);
	    }
	};
    }

    private static native long getNextEntry(long jzfile, int i);

    /**
     * Returns the number of entries in the ZIP file.
     * @exception IllegalStateException if the zip file has been closed
     */
    public int size() {
	if (jzfile == 0) {
	    throw new IllegalStateException("zip file closed");
	}
	return total;
    }

    /**
     * Closes the ZIP file.
     */
    public void close() throws IOException {
	if (jzfile != 0) {
	    // Close the zip file
	    long zf = this.jzfile;
	    jzfile = 0;
	    close(zf);
	    // Release inflaters
	    synchronized (inflaters) {
		int size = inflaters.size();
		for (int i = 0; i < size; i++) {
		    Inflater inf = (Inflater)inflaters.get(i);
		    inf.end();
		}
	    }
	}
    }

    private static native void close(long jzfile);

    /*
     * Inner class implementing the input stream used to read a zip file entry.
     */
   private static class ZipFileInputStream extends InputStream {
	private long jzfile;	// address of jzfile data
	private long jzentry;	// address of jzentry data
	private int pos;	// current position within entry data
	private int rem;	// number of remaining bytes within entry

	ZipFileInputStream(long jzfile, long jzentry) {
	    this.jzfile = jzfile;
	    this.jzentry = jzentry;
	    pos = 0;
	    rem = getCSize(jzentry);
	}

	public int read(byte b[], int off, int len) throws IOException {
	    if (rem == 0) {
		return -1;
	    }
	    if (len <= 0) {
		return 0;
	    }
	    if (len > rem) {
		len = rem;
	    }
	    len = ZipFile.read(jzfile, jzentry, pos, b, off, len);
	    if (len > 0) {
		pos += len;
		rem -= len;
	    }
	    return len;
	}

	public int read() throws IOException {
	    byte[] b = new byte[1];
	    if (read(b, 0, 1) == 1) {
		return b[0] & 0xff;
	    } else {
		return -1;
	    }
	}

	public long skip(long n) {
	    int len = n > rem ? rem : (int)n;
	    pos += len;
	    rem -= len;
	    return len;
	}

	public int available() {
	    return rem;
	}
    }

    private static native int read(long jzfile, long jzentry,
				   int pos, byte[] b, int off, int len);

    private static native int getCSize(long jzentry);
}
