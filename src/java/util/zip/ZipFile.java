/*
 * @(#)ZipFile.java	1.58 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
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
 * @version   1.58, 01/23/03 
 * @author	David Connelly
 */
public
class ZipFile implements ZipConstants {
    private long jzfile;  // address of jzfile data
    private String name;  // zip file name
    private int total;	  // total number of entries

    private static final int STORED = ZipEntry.STORED;
    private static final int DEFLATED = ZipEntry.DEFLATED;

    /**
     * Mode flag to open a zip file for reading.
     */
    public static final int OPEN_READ = 0x1;
        
    /**
     * Mode flag to open a zip file and mark it for deletion.  The file will be
     * deleted some time between the moment that it is opened and the moment
     * that it is closed, but its contents will remain accessible via the
     * <tt>ZipFile</tt> object until either the close method is invoked or the 
     * virtual machine exits.
     */
    public static final int OPEN_DELETE = 0x4;
    
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
	this(new File(name), OPEN_READ);
    }



    /**
     * Opens a new <code>ZipFile</code> to read from the specified
     * <code>File</code> object in the specified mode.  The mode argument
     * must be either <tt>OPEN_READ</tt> or <tt>OPEN_READ | OPEN_DELETE</tt>.
     * 
     * <p>First, if there is a security manager, its <code>checkRead</code>
     * method is called with the <code>name</code> argument as its argument to
     * ensure the read is allowed.
     *
     * @param file the ZIP file to be opened for reading
     * @param mode the mode in which the file is to be opened
     * @exception ZipException if a ZIP format error has occurred
     * @exception IOException if an I/O error has occurred
     * @exception  SecurityException  if a security manager exists and its  
     *             <code>checkRead</code> method doesn't allow read access to the file,
     *             or <code>checkDelete</code> method doesn't allow deleting the file
     *             when <tt>OPEN_DELETE</tt> flag is set.
     * @exception IllegalArgumentException
     *            If the <tt>mode</tt> argument is invalid
     * @see SecurityManager#checkRead(java.lang.String)
     */
    public ZipFile(File file, int mode) throws IOException {
        if (((mode & OPEN_READ) == 0) || 
            ((mode & ~(OPEN_READ | OPEN_DELETE)) != 0)) {
            throw new IllegalArgumentException("Illegal mode: 0x"+ 
                                               Integer.toHexString(mode));
        }
        String name = file.getPath();
	SecurityManager sm = System.getSecurityManager();
	if (sm != null) {
	    sm.checkRead(name);
	    if ((mode & OPEN_DELETE) != 0) {
		sm.checkDelete(name);
	    }
	}
	long jzfileCopy = open(name, mode, file.lastModified());
	this.name = name;
	this.total = getTotal(jzfileCopy);
        jzfile = jzfileCopy;
    }

    private static native long open(String name, int mode, long lastModified);
    private static native int getTotal(long jzfile);

    /**
     * Opens a ZIP file for reading given the specified File object.
     * @param file the ZIP file to be opened for reading
     * @exception ZipException if a ZIP error has occurred
     * @exception IOException if an I/O error has occurred
     */
    public ZipFile(File file) throws ZipException, IOException {
	this(file, OPEN_READ);
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
        long jzentry = 0;
        synchronized (this) {
            ensureOpen(jzfile);
            jzentry = getEntry(jzfile, name);
            if (jzentry == 0 && !name.endsWith("/")) {
                // try a directory name
                jzentry = getEntry(jzfile, name + "/");
            }

            if (jzentry != 0) {
                ZipEntry ze = new ZipEntry(name, jzentry);
                freeEntry(jzfile, jzentry);
                return ze;
            }
        }
        return null;
    }

    private static native long getEntry(long jzfile, String name);

    // freeEntry releases the C jzentry struct.  
    private static native void freeEntry(long jzfile, long jzentry);

    /**
     * Returns an input stream for reading the contents of the specified
     * zip file entry.
     *
     * Returns an input stream for reading the contents of the specified
     * zip file entry.
     *
     * <p> Closing this ZIP file will, in turn, close all input 
     * streams that have been returned by invocations of this method.
     *
     * @param entry the zip file entry
     * @return the input stream for reading the contents of the specified
     * zip file entry.
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
        long jzentry = 0;
        InputStream in = null;
        synchronized (this) {
            ensureOpen(jzfile);
            jzentry = getEntry(jzfile, name);
            if (jzentry == 0) {
                return null;
            }
            in = new ZipFileInputStream(jzentry, this);
        }
	switch (getMethod(jzentry)) {
	case STORED:
	    return in;
	case DEFLATED:
	    return new InflaterInputStream(in, getInflater()) {
                private boolean isClosed = false;
                
		public void close() throws IOException {
                    if (!isClosed) {
                         releaseInflater(inf);
                        this.in.close();
                        isClosed = true;
                    }
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

                public int available() throws IOException {
                    if (super.available() != 0) {
                        return this.in.available();
                    } else {
                        return 0;
                    }
                }
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
     * @return the path name of the ZIP file
     */
    public String getName() {
        return name;
    }

    /**
     * Returns an enumeration of the ZIP file entries.
     * @return an enumeration of the ZIP file entries
     * @exception IllegalStateException if the zip file has been closed
     */
    public Enumeration entries() {
        ensureOpen(jzfile);
        return new Enumeration() {
                private int i = 0;
                public boolean hasMoreElements() {
                    synchronized (ZipFile.this) {
                        if (ZipFile.this.jzfile == 0) {
                            throw new IllegalStateException("zip file closed");
                        }
                        ensureOpen(ZipFile.this.jzfile);
                    }
                    return i < total;
                }
                public Object nextElement() throws NoSuchElementException {
                    synchronized (ZipFile.this) {
                        ensureOpen(ZipFile.this.jzfile);
                        if (i >= total) {
                            throw new NoSuchElementException();
                        }
                        long jzentry = getNextEntry(jzfile, i++);
                        if (jzentry == 0) {
                            String message;
                            if (ZipFile.this.jzfile == 0) {
                                message = "ZipFile concurrently closed";
                            } else {
                                message = getZipMessage(ZipFile.this.jzfile);
                            }
                            throw new InternalError("jzentry == 0" +
                                                    ",\n jzfile = " + ZipFile.this.jzfile +
                                                    ",\n total = " + ZipFile.this.total +
                                                    ",\n name = " + ZipFile.this.name +
                                                    ",\n i = " + i +
                                                    ",\n message = " + message
                                );
                        }
                        ZipEntry ze = new ZipEntry(jzentry);
                        freeEntry(jzfile, jzentry);
                        return ze;
                    }
                }
            };
    }

    private static native long getNextEntry(long jzfile, int i);

    /**
     * Returns the number of entries in the ZIP file.
     * @return the number of entries in the ZIP file
     * @exception IllegalStateException if the zip file has been closed
     */
    public int size() {
        ensureOpen(jzfile);
	return total;
    }

    /**
     * Closes the ZIP file.
     * <p> Closing this ZIP file will close all of the input streams
     * previously returned by invocations of the {@link #getInputStream
     * getInputStream} method.
     *
     * @throws IOException if an I/O error has occured
     */
    public void close() throws IOException {
        synchronized (this) {
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
    }

    /**
     * Ensures that the <code>close</code> method of this ZIP file is
     * called when there are no more references to it.
     *
     * <p>
     * Since the time when GC would invoke this method is undetermined, 
     * it is strongly recommanded that applications invoke the <code>close</code> 
     * method as soon they have finished accessing this <code>ZipFile</code>.
     * This will prevent holding up system resources for an undetermined 
     * length of time.
     * 
     * @exception  IOException  if an I/O error occurs.
     * @see        java.util.zip.ZipFile#close()
     */
    protected void finalize() throws IOException {
        close();
    }

    private static native void close(long jzfile);

    private void ensureOpen(long fd) {
	if (fd == 0) {
	    throw new IllegalStateException("zip file closed");
	}
    }
	
    /*
     * Inner class implementing the input stream used to read a zip file entry.
     */
   private class ZipFileInputStream extends InputStream {
	private long jzentry;	// address of jzentry data
	private int pos;	// current position within entry data
	private int rem;	// number of remaining bytes within entry
        private int size;       // uncompressed size of this entry
        private ZipFile handle; // this would prevent the zip file from being GCed
       
	ZipFileInputStream(long jzentry, ZipFile zf) {
	    pos = 0;
	    rem = getCSize(jzentry);
            size = getSize(jzentry);
	    this.handle = zf;
	    this.jzentry = jzentry;
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
            synchronized (ZipFile.this) {
                if (ZipFile.this.jzfile == 0)
                    throw new ZipException("ZipFile closed.");
                len = ZipFile.read(ZipFile.this.jzfile, jzentry, pos, b,
                                   off, len);
            }
	    if (len > 0) {
		pos += len;
		rem -= len;
	    }
	    if (rem == 0) {
		close();
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
	    if (rem == 0) {
		close();
	    }
	    return len;
	}

	public int available() {
	    return size;
	}

       public void close() {
           rem = 0;
           synchronized (ZipFile.this) {
               if (jzentry != 0 && ZipFile.this.jzfile != 0) {
                   freeEntry(ZipFile.this.jzfile, jzentry);
                   jzentry = 0;
               }
           }
       }

    }

    private static native int read(long jzfile, long jzentry,
				   int pos, byte[] b, int off, int len);

    private static native int getCSize(long jzentry);

    private static native int getSize(long jzentry);
    
    // Temporary add on for bug troubleshooting
    private static native String getZipMessage(long jzfile);
}
