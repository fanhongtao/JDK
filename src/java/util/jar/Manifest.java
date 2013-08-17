/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.util.jar;

import java.io.FilterInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;

/**
 * The Manifest class is used to maintain Manifest entry names and their
 * associated Attributes. There are main Manifest Attributes as well as
 * per-entry Attributes. Documentation on the Manifest format can be
 * found at :
 * <blockquote><pre>
 * http://java.sun.com/products/jdk/1.2/docs/guide/jar/manifest.html
 * </pre></blockquote>
 *
 * @author  David Connelly
 * @version 1.34, 07/01/02
 * @see	    Attributes
 * @since   1.2
 */
public class Manifest implements Cloneable {
    // manifest main attributes
    private Attributes attr = new Attributes();

    // manifest entries
    private Map entries = new HashMap();

    /**
     * Constructs a new, empty Manifest.
     */
    public Manifest() {
    }

    /**
     * Constructs a new Manifest from the specified input stream.
     *
     * @param is the input stream containing manifest data
     * @throws IOException if an I/O error has occured
     */
    public Manifest(InputStream is) throws IOException {
	read(is);
    }

    /**
     * Constructs a new Manifest that is a copy of the specified Manifest.
     *
     * @param man the Manifest to copy
     */
    public Manifest(Manifest man) {
	attr.putAll(man.getMainAttributes());
	entries.putAll(man.getEntries());
    }

    /**
     * Returns the main Attributes for the Manifest.
     * @return the main Attributes for the Manifest
     */
    public Attributes getMainAttributes() {
	return attr;
    }

    /**
     * Returns a Map of the entries contained in this Manifest. Each entry
     * is represented by a String name (key) and associated Attributes (value).
     *
     * @return a Map of the entries contained in this Manifest
     */
    public Map getEntries() {
	return entries;
    }

    /**
     * Returns the Attributes for the specified entry name.
     * This method is defined as:
     * <pre>
     *	    return (Attributes)getEntries().get(name)
     * </pre>
     * @return the Attributes for the specified entry name
     */
    public Attributes getAttributes(String name) {
	return (Attributes)getEntries().get(name);
    }

    /**
     * Clears the main Attributes as well as the entries in this Manifest.
     */
    public void clear() {
	attr.clear();
	entries.clear();
    }

    /**
     * Writes the Manifest to the specified OutputStream.
     *
     * @param out the output stream
     * @exception IOException if an I/O error has occurred
     */
    public void write(OutputStream out) throws IOException {
	DataOutputStream dos = new DataOutputStream(out);
	// Write out the main attributes for the manifest
	attr.writeMain(dos);
	// Now write out the pre-entry attributes
	Iterator it = entries.entrySet().iterator();
	while (it.hasNext()) {
	    Map.Entry e = (Map.Entry)it.next();
            StringBuffer buffer = new StringBuffer("Name: ");
	    buffer.append((String)e.getKey());
	    buffer.append("\r\n");
            make72Safe(buffer);
            dos.writeBytes(buffer.toString());
	    ((Attributes)e.getValue()).write(dos);
	}
	dos.flush();
    }

    /**
     * Adds line breaks to enforce a maximum 72 bytes per line.
     */
    static void make72Safe(StringBuffer line) {
        int length = line.length();
        if (length > 72) {
            int index = 70;
	    while (index < length - 2) {
                line.insert(index, "\r\n ");
                index += 72;
                length += 3;
            }
        }
        return;
    }

    /**
     * Reads the Manifest from the specified InputStream. The entry
     * names and attributes read will be merged in with the current
     * manifest entries.
     *
     * @param is the input stream
     * @exception IOException if an I/O error has occurred
     */
    public void read(InputStream is) throws IOException {
	// Buffered input stream for reading manifest data
	FastInputStream fis = new FastInputStream(is);
	// Line buffer
	byte[] lbuf = new byte[512];
	// Read the main attributes for the manifest
	attr.read(fis, lbuf);
	// Total number of entries, attributes read
	int ecount = 0, acount = 0;
	// Average size of entry attributes
	int asize = 2;
	// Now parse the manifest entries
	int len;
	String name = null;
        boolean skipEmptyLines = true;
	while ((len = fis.readLine(lbuf)) != -1) {
	    if (lbuf[--len] != '\n') {
		throw new IOException("manifest line too long");
	    }
	    if (len > 0 && lbuf[len-1] == '\r') {
		--len;
	    }
            if (len == 0 && skipEmptyLines) {
                continue;
            }
            skipEmptyLines = false;

	    if (name == null) {
		name = parseName(lbuf, len);
		if (name == null) {
		    throw new IOException("invalid manifest format");
		}
	    } else {
		// continuation line
		name = name + new String(lbuf, 0, 1, len-1);
	    }

	    if (fis.peek() == ' ') {
		// name is wrapped
		continue;
	    }

	    Attributes attr = getAttributes(name);
	    if (attr == null) {
		attr = new Attributes(asize);
		entries.put(name, attr);
	    }
	    attr.read(fis, lbuf);
	    ecount++;
	    acount += attr.size();
	    //XXX: Fix for when the average is 0. When it is 0, 
	    // you get an Attributes object with an initial
	    // capacity of 0, which tickles a bug in HashMap.
	    asize = Math.max(2, acount / ecount);

	    name = null;
            skipEmptyLines = true;
	}
    }

    private String parseName(byte[] lbuf, int len) {
	if (toLower(lbuf[0]) == 'n' && toLower(lbuf[1]) == 'a' &&
	    toLower(lbuf[2]) == 'm' && toLower(lbuf[3]) == 'e' &&
	    lbuf[4] == ':' && lbuf[5] == ' ') {
	    return new String(lbuf, 0, 6, len - 6);
	}
	return null;
    }

    private int toLower(int c) {
	return (c >= 'A' && c <= 'Z') ? 'a' + (c - 'A') : c;
    }

    /**
     * Returns true if the specified Object is also a Manifest and has
     * the same main Attributes and entries.
     *
     * @param o the object to be compared
     * @return true if the specified Object is also a Manifest and has
     * the same main Attributes and entries
     */
    public boolean equals(Object o) {
	if (o instanceof Manifest) {
	    Manifest m = (Manifest)o;
	    return attr.equals(m.getMainAttributes()) &&
		   entries.equals(m.getEntries());
	} else {
	    return false;
	}
    }

    /**
     * Returns the hash code for this Manifest.
     */
    public int hashCode() {
	return attr.hashCode() + entries.hashCode();
    }

    /**
     * Returns a shallow copy of this Manifest, implemented as follows:
     * <pre>
     *     public Object clone() { return new Manifest(this); }
     * </pre>
     * @return a shallow copy of this Manifest
     */
    public Object clone() {
	return new Manifest(this);
    }

    /*
     * A fast buffered input stream for parsing manifest files.
     */
    static class FastInputStream extends FilterInputStream {
	private byte buf[];
	private int count = 0;
	private int pos = 0;

	FastInputStream(InputStream in) {
	    this(in, 8192);
	}

	FastInputStream(InputStream in, int size) {
	    super(in);
	    buf = new byte[size];
	}

	public int read() throws IOException {
	    if (pos >= count) {
		fill();
		if (pos >= count) {
		    return -1;
		}
	    }
	    return buf[pos++] & 0xff;
	}

	public int read(byte[] b, int off, int len) throws IOException {
	    int avail = count - pos;
	    if (avail <= 0) {
		if (len >= buf.length) {
		    return in.read(b, off, len);
		}
		fill();
		avail = count - pos;
		if (avail <= 0) {
		    return -1;
		}
	    }
	    if (len > avail) {
		len = avail;
	    }
	    System.arraycopy(buf, pos, b, off, len);
	    pos += len;
	    return len;
	}

	/*
	 * Reads 'len' bytes from the input stream, or until an end-of-line
	 * is reached. Returns the number of bytes read.
	 */
	public int readLine(byte[] b, int off, int len) throws IOException {
	    byte[] tbuf = this.buf;
	    int total = 0;
	    while (total < len) {
		int avail = count - pos;
		if (avail <= 0) {
		    fill();
		    avail = count - pos;
		    if (avail <= 0) {
			return -1;
		    }
		}
		int n = len - total;
		if (n > avail) {
		    n = avail;
		}
		int tpos = pos;
		int maxpos = tpos + n;
		while (tpos < maxpos && tbuf[tpos++] != '\n') ;
		n = tpos - pos;
		System.arraycopy(tbuf, pos, b, off, n);
		off += n;
		total += n;
		pos = tpos;
		if (tbuf[tpos-1] == '\n') {
		    break;
		}
	    }
	    return total;
	}

	public byte peek() throws IOException {
	    if (pos == count)
		fill();
	    return buf[pos];
	}

	public int readLine(byte[] b) throws IOException {
	    return readLine(b, 0, b.length);
	}

	public long skip(long n) throws IOException {
	    if (n <= 0) {
		return 0;
	    }
	    long avail = count - pos;
	    if (avail <= 0) {
		return in.skip(n);
	    }
	    if (n > avail) {
		n = avail;
	    }
	    pos += n;
	    return n;
	}

	public int available() throws IOException {
	    return (count - pos) + in.available();
	}

	public void close() throws IOException {
	    if (in != null) {
		in.close();
		in = null;
		buf = null;
	    }
	}

	private void fill() throws IOException {
	    count = pos = 0;
	    int n = in.read(buf, 0, buf.length);
	    if (n > 0) {
		count = n;
	    }
	}
    }
}
