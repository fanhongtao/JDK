/*
 * @(#)ZipFile.java	1.71 05/11/21
 *
 * Copyright 2005 Sun Microsystems, Inc. All rights reserved.
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
import java.security.PrivilegedAction;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import sun.nio.ByteBuffered;
import java.lang.reflect.*;

/**
 * This class is used to read entries from a zip file.
 *
 * <p> Unless otherwise noted, passing a <tt>null</tt> argument to a constructor
 * or method in this class will cause a {@link NullPointerException} to be
 * thrown.
 *
 * @version   1.71, 11/21/05 
 * @author	David Connelly
 */
public
class ZipFile implements ZipConstants {
    private long jzfile;  // address of jzfile data
    private String name;  // zip file name
    private int total;	  // total number of entries
    private MappedByteBuffer mappedBuffer; // if ZipFile.c uses file mapping.
    private ZipCloser closer; // cleans up after mappedBuffer.
    private boolean mbUsed;      // if caller used mappedBuffer
    private boolean closeRequested;

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
	/* Zip library is loaded from System.initializeSystemClass */
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
     * @throws ZipException if a ZIP format error has occurred
     * @throws IOException if an I/O error has occurred
     * @throws SecurityException if a security manager exists and its
     *         <code>checkRead</code> method doesn't allow read access to the file.
     * @see SecurityManager#checkRead(java.lang.String)
     */
    public ZipFile(String name) throws IOException {
	this(new File(name), OPEN_READ);
    }

    /**
     * Handles cleanup after mappedBuffer is no longer referenced.
     *
     * The DirectByteBuffer code creates a phantom reference to mappedBuffer
     * that will call ZipCloser.run() when mappedBuffer is no longer 
     * (strongly) referenced.
     * If it was safe to do so, ZipFile.close() (and finalize()) will have 
     * already cleaned up.
     *
     * Note: since ZipFile references MappedByteBuffer, we can be sure that 
     * the ZipFile has already been finalized by the time ZipCloser.run() 
     * is called.
     */
    private static class ZipCloser
	implements Runnable
    {
	private long mappedFileID;
	
	private ZipCloser(long jzFile) {
	    mappedFileID = jzFile;
	}

	public synchronized void setClosed() {
	    mappedFileID = 0;
	}
	
	public synchronized void run() {
	    if (mappedFileID != 0) {
		ZipFile.close(mappedFileID);
		mappedFileID = 0;
	    }
	}
    } /* ZipCloser */

    private static Constructor directByteBufferConstructor = null;

    private static void initDBBConstructor() {
	AccessController.doPrivileged(new PrivilegedAction() {
		public Object run() {
		    try {
			Class th = Class.forName("java.nio.DirectByteBuffer");
			directByteBufferConstructor
			    = th.getDeclaredConstructor(
					new Class[] { int.class,
                                                      long.class,
						      Runnable.class });
			directByteBufferConstructor.setAccessible(true);
		    } catch (ClassNotFoundException x) {
			throw new InternalError();
		    } catch (NoSuchMethodException x) {
			throw new InternalError();
		    } catch (IllegalArgumentException x) {
			throw new InternalError();
		    } catch (ClassCastException x) {
			throw new InternalError();
		    }
                    return null;
		}});
    }

    private static MappedByteBuffer newMappedByteBuffer(int size, long addr,
							Runnable unmapper)
    {
        MappedByteBuffer dbb;
        if (directByteBufferConstructor == null)
            initDBBConstructor();
        try {
            dbb = (MappedByteBuffer)directByteBufferConstructor.newInstance(
              new Object[] { new Integer(size),
                             new Long(addr),
			     unmapper });
        } catch (InstantiationException e) {
            throw new InternalError();
        } catch (IllegalAccessException e) {
            throw new InternalError();
        } catch (InvocationTargetException e) {
            throw new InternalError();
        }
        return dbb;
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
     * @throws ZipException if a ZIP format error has occurred
     * @throws IOException if an I/O error has occurred
     * @throws SecurityException if a security manager exists and
     *         its <code>checkRead</code> method
     *         doesn't allow read access to the file,
     *         or its <code>checkDelete</code> method doesn't allow deleting
     *         the file when the <tt>OPEN_DELETE</tt> flag is set.
     * @throws IllegalArgumentException if the <tt>mode</tt> argument is invalid
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
	this.mbUsed = false;
	long mappedAddr = getMappedAddr(jzfileCopy);
	long len = getMappedLen(jzfileCopy);

	if (mappedAddr != 0 && len < Integer.MAX_VALUE) {
	    // Zip's native code may be able to handle files up to 4GB, but
	    // ByteBuffers can only handle 2GB. So fallback on Zip files >= 2GB.
	    this.closer = new ZipCloser(jzfileCopy); 
	    this.mappedBuffer = newMappedByteBuffer((int)len, mappedAddr, 
						    this.closer);
	}

        jzfile = jzfileCopy;
    }

    private static native long open(String name, int mode, long lastModified);
    private static native int getTotal(long jzfile);
    private static native long getMappedAddr(long jzfile);
    private static native long getMappedLen(long jzfile);


    /**
     * Opens a ZIP file for reading given the specified File object.
     * @param file the ZIP file to be opened for reading
     * @throws ZipException if a ZIP error has occurred
     * @throws IOException if an I/O error has occurred
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
     * @throws IllegalStateException if the zip file has been closed
     */
    public ZipEntry getEntry(String name) {
        if (name == null) {
            throw new NullPointerException("name");
        }
        long jzentry = 0;
        synchronized (this) {
            ensureOpen();
            jzentry = getEntry(jzfile, name, true);
            if (jzentry != 0) {
                ZipEntry ze = new ZipEntry(name, jzentry);
                freeEntry(jzfile, jzentry);
                return ze;
            }
        }
        return null;
    }

    private static native long getEntry(long jzfile, String name, 
                                        boolean addSlash); 

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
     * @throws ZipException if a ZIP format error has occurred
     * @throws IOException if an I/O error has occurred
     * @throws IllegalStateException if the zip file has been closed
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
        ZipFileInputStream in = null;
        synchronized (this) {
            ensureOpen();
            jzentry = getEntry(jzfile, name, false);
            if (jzentry == 0) {
                return null;
            }
	    if (mappedBuffer != null) {
		in = new MappedZipFileInputStream(jzentry, name);
	    } else {
		in = new ZipFileInputStream(jzentry);
	    }
        }
        final ZipFileInputStream zfin = in;
	switch (getMethod(jzentry)) {
	case STORED:
	    return zfin;
	case DEFLATED:
	    // MORE: Compute good size for inflater stream:
            long size = getSize(jzentry) + 2; // Inflater likes a bit of slack
            if (size > 65536) size = 8192;
            if (size <= 0) size = 4096;
            return new InflaterInputStream(zfin, getInflater(), (int)size) {
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
                    if (isClosed)
                        return 0;
		    long avail = zfin.size() - inf.getBytesWritten();
		    return avail > (long) Integer.MAX_VALUE ?
			Integer.MAX_VALUE : (int) avail;
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
     * @throws IllegalStateException if the zip file has been closed
     */
    public Enumeration<? extends ZipEntry> entries() {
        ensureOpen();
        return new Enumeration<ZipEntry>() {
                private int i = 0;
                public boolean hasMoreElements() {
                    synchronized (ZipFile.this) {
                        ensureOpen();
                        return i < total;
                    }
                }
                public ZipEntry nextElement() throws NoSuchElementException {
                    synchronized (ZipFile.this) {
                        ensureOpen();
                        if (i >= total) {
                            throw new NoSuchElementException();
                        }
                        long jzentry = getNextEntry(jzfile, i++);
                        if (jzentry == 0) {
                            String message;
                            if (closeRequested) {
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
     * @throws IllegalStateException if the zip file has been closed
     */
    public int size() {
        ensureOpen();
	return total;
    }

    /**
     * Closes the ZIP file.
     * <p> Closing this ZIP file will close all of the input streams
     * previously returned by invocations of the {@link #getInputStream
     * getInputStream} method.
     *
     * @throws IOException if an I/O error has occurred
     */
    public void close() throws IOException {
        synchronized (this) {
	    closeRequested = true;
	    
	    if (jzfile != 0) {
		// Close the zip file
		long zf = this.jzfile;
		jzfile = 0;
		if (closer != null) {
		    if (!mbUsed) { // no one is looking; we can close early
			closer.setClosed(); // tell closer not to bother
			close(zf);
		    }
		    // Some caller may have ref to MappedByteBuffer,
		    // so let phantom processing (ZipCloser) close the ZipFile.
		} else {
		    close(zf);
		}
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
     * it is strongly recommended that applications invoke the <code>close</code> 
     * method as soon they have finished accessing this <code>ZipFile</code>.
     * This will prevent holding up system resources for an undetermined 
     * length of time.
     * 
     * @throws IOException if an I/O error has occurred
     * @see    java.util.zip.ZipFile#close()
     */
    protected void finalize() throws IOException {
        close();
    }

    private static native void close(long jzfile);

    private void ensureOpen() {
	if (closeRequested) {
	    throw new IllegalStateException("zip file closed");
	}
    }

    private void ensureOpenOrZipException() throws IOException {
	if (closeRequested) {
	    throw new ZipException("ZipFile closed");
	}
    }
	
    /*
     * Inner class implementing the input stream used to read a
     * (possibly compressed) zip file entry.
     */
   private class ZipFileInputStream extends InputStream {
	protected long jzentry;	// address of jzentry data
	private   long pos;	// current position within entry data
	protected long rem;	// number of remaining bytes within entry
        protected long size;    // uncompressed size of this entry

	ZipFileInputStream(long jzentry) {
	    pos = 0;
	    rem = getCSize(jzentry);
            size = getSize(jzentry);
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
		len = (int) rem;
	    }
            synchronized (ZipFile.this) {
		ensureOpenOrZipException();

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
	    if (n > rem)
		n = rem;
	    pos += n;
	    rem -= n;
	    if (rem == 0) {
		close();
	    }
	    return n;
	}

        public int available() {
	    return rem > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) rem;
        }

        public long size() {
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

    /*
     * Inner class implementing the input stream used to read a
     * mapped (possibly compressed) zip file entry. Overrides
     * all methods of ZipFileInputStream.
     */
   private class MappedZipFileInputStream extends ZipFileInputStream
       implements ByteBuffered {

       private ByteBuffer directBuffer = null;
       private String name;
       
	MappedZipFileInputStream(long jzentry, String name) {
	    super(jzentry);
	    this.name = name;
	    int offset = (int)getEntryOffset(jzentry);
	    MappedByteBuffer bb = ZipFile.this.mappedBuffer;
	    synchronized (bb) {
		bb.position(offset);
		bb.limit((int)(offset + rem)); // won't use this code if file > 2GB

		this.directBuffer = bb.slice();
		
		bb.position(0); // reset, but doesn't matter
		bb.limit(bb.capacity()); // reset limit
	    }
	}

       /* getByteBuffer returns a ByteBuffer if the jar file has been mapped in.
	  If this method is called, the zip code won't close the ZipFile until 
	  the last reference to file's mapped buffer is collected. */
       public ByteBuffer getByteBuffer() throws IOException {
	   synchronized (ZipFile.this) {
	       ensureOpenOrZipException();
	       // have to defer ZipFile.close() until all the buffers are garbage
	       ZipFile.this.mbUsed = true;
	       return directBuffer;
	   }
       }

	public int read(byte b[], int off, int len) throws IOException {
	    int rem = directBuffer.remaining();
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
                ensureOpenOrZipException();

		directBuffer.get(b, off, len);
            }
	   
	    if (len == rem) {
		close();
	    }
	    return len;
	}

	public int read() throws IOException {
	    synchronized (ZipFile.this) {
               ensureOpenOrZipException();

		if (directBuffer.remaining() == 0) {
		    return -1;
		} else {
		    return directBuffer.get() & 0xff;
		}
	    }
	}

	public long skip(long n) {
	    int rem = directBuffer.remaining();
	    int len = n > rem ? rem : (int)n;
	    directBuffer.position(directBuffer.position() + len);
	    if (len == rem) {
		close();
	    }
	    return len;
	}

        public int available() {
            return directBuffer.remaining();
        }

        public long size() {
            return size;
        }

        public void close() {
	    directBuffer.position(directBuffer.limit());
            synchronized (ZipFile.this) {
                if (jzentry != 0 && ZipFile.this.jzfile != 0) {
                    freeEntry(ZipFile.this.jzfile, jzentry);
                    jzentry = 0;
                }
            }
        }

    } /* MappedZipFileInputStream */

    private static native int read(long jzfile, long jzentry,
				   long pos, byte[] b, int off, int len);

    private static native long getCSize(long jzentry);

    private static native long getSize(long jzentry);

    /* If the zip file is mapped, return the offset from the beginning of the zip 
       file to this entry. Return 0 otherwise. */
    private static native long getEntryOffset(long jzentry);

    // Temporary add on for bug troubleshooting
    private static native String getZipMessage(long jzfile);
}
