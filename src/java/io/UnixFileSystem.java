/*
 * @(#)UnixFileSystem.java	1.10 01/12/03
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.io;

import java.security.AccessController;
import sun.security.action.GetPropertyAction;


class UnixFileSystem extends FileSystem {

    private final char slash;
    private final char colon;

    public UnixFileSystem() {
        slash = 
	    ((String) AccessController.doPrivileged(
              new GetPropertyAction("file.separator"))).charAt(0);
	colon = 
	    ((String) AccessController.doPrivileged(
              new GetPropertyAction("path.separator"))).charAt(0);
    }


    /* -- Normalization and construction -- */

    public char getSeparator() {
	return slash;
    }

    public char getPathSeparator() {
	return colon;
    }

    /* A normal Unix pathname contains no duplicate slashes and does not end
       with a slash.  It may be the empty string. */

    /* Normalize the given pathname, whose length is len, starting at the given
       offset; everything before this offset is already normal. */
    private String normalize(String pathname, int len, int off) {
	if (len == 0) return pathname;
	int n = len;
	while ((n > 0) && (pathname.charAt(n - 1) == '/')) n--;
	if (n == 0) return "/";
	StringBuffer sb = new StringBuffer(pathname.length());
	if (off > 0) sb.append(pathname.substring(0, off));
	char prevChar = 0;
	for (int i = off; i < n; i++) {
	    char c = pathname.charAt(i);
	    if ((prevChar == '/') && (c == '/')) continue;
	    sb.append(c);
	    prevChar = c;
	}
	return sb.toString();
    }

    /* Check that the given pathname is normal.  If not, invoke the real
       normalizer on the part of the pathname that requires normalization.
       This way we iterate through the whole pathname string only once. */
    public String normalize(String pathname) {
	int n = pathname.length();
	char prevChar = 0;
	for (int i = 0; i < n; i++) {
	    char c = pathname.charAt(i);
	    if ((prevChar == '/') && (c == '/'))
		return normalize(pathname, n, i - 1);
	    prevChar = c;
	}
	if (prevChar == '/') return normalize(pathname, n, n - 1);
	return pathname;
    }

    public int prefixLength(String pathname) {
	if (pathname.length() == 0) return 0;
	return (pathname.charAt(0) == '/') ? 1 : 0;
    }

    public String resolve(String parent, String child) {
	if (child.equals("")) return parent;
	if (child.charAt(0) == '/') {
	    if (parent.equals("/")) return child;
	    return parent + child;
	}
	if (parent.equals("/")) return parent + child;
	return parent + '/' + child;
    }

    public String getDefaultParent() {
	return "/";
    }

    public String fromURIPath(String path) {
	String p = path;
	if (p.endsWith("/") && (p.length() > 1)) {
	    // "/foo/" --> "/foo", but "/" --> "/"
	    p = p.substring(0, p.length() - 1);
	}
	return p;
    }


    /* -- Path operations -- */

    public boolean isAbsolute(File f) {
	return (f.getPrefixLength() != 0);
    }

    public String resolve(File f) {
	if (isAbsolute(f)) return f.getPath();
	return resolve(System.getProperty("user.dir"), f.getPath());
    }

    public native String canonicalize(String path) throws IOException;


    /* -- Attribute accessors -- */

    public native int getBooleanAttributes0(File f);

    public int getBooleanAttributes(File f) {
	int rv = getBooleanAttributes0(f);
	String name = f.getName();
	boolean hidden = (name.length() > 0) && (name.charAt(0) == '.');
	return rv | (hidden ? BA_HIDDEN : 0);
    }

    public native boolean checkAccess(File f, boolean write);
    public native long getLastModifiedTime(File f);
    public native long getLength(File f);


    /* -- File operations -- */

    public native boolean createFileExclusively(String path)
	throws IOException;
    public native boolean delete(File f);
    public synchronized native boolean deleteOnExit(File f);
    public native String[] list(File f);
    public native boolean createDirectory(File f);
    public native boolean rename(File f1, File f2);
    public native boolean setLastModifiedTime(File f, long time);
    public native boolean setReadOnly(File f);


    /* -- Filesystem interface -- */

    public File[] listRoots() {
	try {
	    SecurityManager security = System.getSecurityManager();
	    if (security != null) {
		security.checkRead("/");
	    }
	    return new File[] { new File("/") };
	} catch (SecurityException x) {
	    return new File[0];
	}
    }


    /* -- Basic infrastructure -- */

    public int compare(File f1, File f2) {
	return f1.getPath().compareTo(f2.getPath());
    }

    public int hashCode(File f) {
	return f.getPath().hashCode() ^ 1234321;
    }

    
    private static native void initIDs();

    static {
	initIDs();
    }

}
