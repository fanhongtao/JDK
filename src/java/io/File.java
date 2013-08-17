/*
 * @(#)File.java	1.53 98/10/06
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

import java.util.Vector;

/**
 * Instances of this class represent the name of a file or directory 
 * on the host file system. A file is specified by a pathname, which 
 * can either be an absolute pathname or a pathname relative to the 
 * current working directory. The pathname must follow the naming 
 * conventions of the host platform. 
 * <p>
 * The <code>File</code> class is intended to provide an abstraction 
 * that deals with most of the machine dependent complexities of 
 * files and pathnames in a machine-independent fashion. 
 * <p>
 * Note that whenever a filename or path is used it is
 * assumed that the host's file naming conventions are used.
 *
 * @version 1.53, 10/06/98
 * @author  Arthur van Hoff
 * @author  Jonathan Payne
 * @since   JDK1.0
 */
public
class File implements java.io.Serializable {
    /**
     * The path of the file. The host's file separator is used.
     */
    private String path;

    /**
     * The system-dependent path separator string. This field is 
     * initialized to contain the value of the system property 
     * <code>file.separator</code>. 
     *
     * @see     java.lang.System#getProperty(java.lang.String)
     * @since   JDK1.0
     */
    public static final String separator = System.getProperty("file.separator");

    /**
     * The system-dependent path separator character. This field is 
     * initialized to contain the first character of the value of the 
     * system property <code>file.separator</code>. This character 
     * separates the directory and file components in a filename. 
     *
     * @see     java.lang.System#getProperty(java.lang.String)
     * @since   JDK1.0
     */
    public static final char separatorChar = separator.charAt(0);
    
    /**
     * The system-dependent path separator string. This field is 
     * initialized to contain the value of the system property 
     * <code>path.separator</code>. 
     *
     * @see     java.lang.System#getProperty(java.lang.String)
     * @since   JDK1.0
     */
    public static final String pathSeparator = System.getProperty("path.separator");

    /**
     * The system-dependent path separator character. This field is 
     * initialized to contain the first character of the value of the 
     * system property <code>path.separator</code>. This character is 
     * often used to separate filenames in a sequence of files given as a 
     * "path list".
     *
     * @see     java.lang.System#getProperty(java.lang.String)
     * @since   JDK1.0
     */
    public static final char pathSeparatorChar = pathSeparator.charAt(0);
    
    /**
     * Creates a <code>File</code> instance that represents the file 
     * whose pathname is the given path argument. 
     *
     * @param      path   the file pathname.
     * @exception  NullPointerException  if the file path is equal to
     *               <code>null</code>.
     * @see        java.io.File#getPath()
     * @since      JDK1.0
     */
    public File(String path) {
	if (path == null) {
	    throw new NullPointerException();
	}
	this.path = path;
    }	

    /**
     * Creates a <code>File</code> instance whose pathname is the 
     * pathname of the specified directory, followed by the separator 
     * character, followed by the <code>name</code> argument. 
     *
     * @param   path   the directory pathname.
     * @param   name   the file pathname.
     * @see     java.io.File#getPath()
     * @see     java.io.File#separator
     * @since   JDK1.0
     */
    public File(String path, String name) {
	if (name == null) {
	    /* raise exception, per Java Language Spec
	     * 22.24.6 & 22.24.7
	     */
	    throw new NullPointerException();
	}
	if (path != null) {
	    if (path.endsWith(separator)) {
		this.path = path + name;
	    } else {
		this.path = path + separator + name;
	    } 
	} else {
	    this.path = name;
	}
    }

    /**
     * Creates a <code>File</code> instance that represents the file 
     * with the specified name in the specified directory. 
     * <p>
     * If the directory argument is <code>null</code>, the resulting 
     * <code>File</code> instance represents a file in the 
     * (system-dependent) current directory whose pathname is the 
     * <code>name</code> argument. Otherwise, the <code>File</code> 
     * instance represents a file whose pathname is the pathname of the 
     * directory, followed by the separator character, followed by the 
     * <code>name</code> argument. 
     *
     * @param   dir   the directory.
     * @param   name   the file pathname.
     * @see     java.io.File#getPath()
     * @see     java.io.File#separator
     * @since   JDK1.0
     */
    public File(File dir, String name) {
	this(dir == null ? (String)null : dir.getPath(), name);
    }

    /**
     * Returns the name of the file represented by this object. The name 
     * is everything in the pathame after the last occurrence of the 
     * separator character. 
     *
     * @return  the name of the file (without any directory components)
     *          represented by this <code>File</code> object.
     * @see     java.io.File#getPath()
     * @see     java.io.File#separator
     * @since   JDK1.0
     */
    public String getName() {
	int index = path.lastIndexOf(separatorChar);
	return (index < 0) ? path : path.substring(index + 1);
    }

    /**
     * Returns the pathname of the file represented by this object.
     *
     * @return  the pathname represented by this <code>File</code> object.
     * @since   JDK1.0
     */
    public String getPath() {
	return path;
    }

    /**
     * Returns the absolute pathname of the file represented by this object.
     * If this object represents an absolute pathname, then return the 
     * pathname. Otherwise, return a pathname that is a concatenation of 
     * the current user directory, the separator character, and the 
     * pathname of this file object. 
     * <p>
     * The system property <code>user.dir</code> contains the current 
     * user directory. 
     *
     * @return  a system-dependent absolute pathname for this <code>File</code>.
     * @see     java.io.File#getPath()
     * @see     java.io.File#isAbsolute()
     * @see     java.lang.System#getProperty(java.lang.String)
     * @since   JDK1.0
     */
    public String getAbsolutePath() {
	if (isAbsolute())
	    return path;

	SecurityManager security = System.getSecurityManager();
	if (security != null) {
	    security.checkPropertyAccess("user.dir");
	}
	return System.getProperty("user.dir") + separator + path;
    }

    /**
     * Returns the canonical form of this <code>File</code> object's pathname.
     * The precise definition of canonical form is system-dependent, but it
     * usually specifies an absolute pathname in which all relative references
     * and references to the current user directory have been completely
     * resolved.  The canonical form of a pathname of a nonexistent file may
     * not be defined.
     *
     * @exception IOException If an I/O error occurs, which is possible because
     * the construction of the canonical path may require filesystem queries.
     *
     * @since   JDK1.1
     */
     public String getCanonicalPath() throws IOException {
	if (isAbsolute())
	    return canonPath(path);

	SecurityManager security = System.getSecurityManager();
	if (security != null) {
	    security.checkPropertyAccess("user.dir");
	}
	return canonPath(System.getProperty("user.dir") + separator + path);
    }

    /**
     * Returns the parent part of the pathname of this <code>File</code>
     * object, or <code>null</code> if the name has no parent part.  The parent
     * part is generally everything leading up to the last occurrence of the
     * separator character, although the precise definition is system
     * dependent.  On UNIX, for example, the parent part of
     * <code>"/usr/lib"</code> is <code>"/usr"</code>, whose parent part is
     * <code>"/"</code>, which in turn has no parent.  On Windows platforms,
     * the parent part of <code>"c:\java"</code> is <code>"c:\"</code>, which
     * in turn has no parent.
     *
     * @see     java.io.File#getPath()
     * @see     java.io.File#getCanonicalPath()
     * @see     java.io.File#separator
     * @since   JDK1.0
     */
    public String getParent() {
	/* This is correct for Unix and Win32; other platforms may require a
           different algorithm */
	int index = path.lastIndexOf(separatorChar);
	if (index < 0)
	    return null;
	if (!isAbsolute() || (path.indexOf(separatorChar) != index))
	    return path.substring(0, index);
	if (index < path.length() - 1)
	    return path.substring(0, index + 1);
	return null;
    }

    private native boolean exists0();
    private native boolean canWrite0();
    private native boolean canRead0();
    private native boolean isFile0();
    private native boolean isDirectory0();
    private native long lastModified0();
    private native long length0();
    private native boolean mkdir0();
    private native boolean renameTo0(File dest);
    private native boolean delete0();
    private native boolean rmdir0(); // remove empty directory
    private native String[] list0();
    private native String canonPath(String p) throws IOException;

    /**
     * Tests if this <code>File</code> exists. 
     *
     * @return     <code>true</code> if the file specified by this object
     *             exists; <code>false</code> otherwise.
     * @exception  SecurityException  if a security manager exists, its
     *               <code>checkRead</code> method is called with the pathname
     *               of this <code>File</code> to see if the application is
     *               allowed read access to the file.
     * @see        java.io.File#getPath()
     * @see        java.lang.SecurityManager#checkRead(java.lang.String)
     * @since      JDK1.0
     */
    public boolean exists() {
	SecurityManager security = System.getSecurityManager();
	if (security != null) {
	    security.checkRead(path);
	}
	return exists0();
    }

    /**
     * Tests if the application can write to this file. 
     *
     * @return     <code>true</code> if the application is allowed to write to
     *             a file whose name is specified by this object;
     *            <code>false</code> otherwise.
     * @exception  SecurityException  if a security manager exists, its
     *               <code>checkWrite</code> method is called with the pathname
     *               of this <code>File</code> to see if the application is
     *               allowed write access to the file.
     * @see        java.io.File#getPath()
     * @see        java.lang.SecurityManager#checkWrite(java.lang.String)
     * @since      JDK1.0
     */
    public boolean canWrite() {
	SecurityManager security = System.getSecurityManager();
	if (security != null) {
	    security.checkWrite(path);
	}
	return canWrite0();
    }

    /**
     * Tests if the application can read from the specified file. 
     *
     * @return     <code>true</code> if the file specified by this object exists
     *             and the application can read the file;
     *             <code>false</code> otherwise.
     * @exception  SecurityException  if a security manager exists, its
     *               <code>checkRead</code> method is called with the pathname
     *               of this <code>File</code> to see if the application is
     *               allowed read access to the file.
     * @see        java.io.File#getPath()
     * @see        java.lang.SecurityManager#checkRead(java.lang.String)
     * @since      JDK1.0
     */
    public boolean canRead() {
	SecurityManager security = System.getSecurityManager();
	if (security != null) {
	    security.checkRead(path);
	}
	return canRead0();
    }

    /**
     * Tests if the file represented by this <code>File</code> 
     * object is a "normal" file. 
     * <p>
     * A file is "normal" if it is not a directory and, in 
     * addition, satisfies other system-dependent criteria. Any 
     * non-directory file created by a Java application is guaranteed to 
     * be a normal file. 
     *
     * @return     <code>true</code> if the file specified by this object
     *             exists and is a "normal" file; <code>false</code> otherwise.
     * @exception  SecurityException  If a security manager exists, its
     *               <code>checkRead</code> method is called with the pathname
     *               of this <code>File</code> to see if the application is
     *               allowed read access to the file.
     * @see        java.io.File#getPath()
     * @see        java.lang.SecurityManager#checkRead(java.lang.String)
     * @since      JDK1.0
     */
    public boolean isFile() {
	SecurityManager security = System.getSecurityManager();
	if (security != null) {
	    security.checkRead(path);
	}
	return isFile0();
    }

    /**
     * Tests if the file represented by this <code>File</code> 
     * object is a directory. 
     *
     * @return     <code>true</code> if this <code>File</code> exists and is a
     *             directory; <code>false</code> otherwise.
     * @exception  SecurityException  if a security manager exists, its
     *               <code>checkRead</code> method is called with the pathname
     *               of this <code>File</code> to see if the application is
     *               allowed read access to the file.
     * @see        java.io.File#getPath()
     * @see        java.lang.SecurityManager#checkRead(java.lang.String)
     * @since      JDK1.0
     */
    public boolean isDirectory() {
	SecurityManager security = System.getSecurityManager();
	if (security != null) {
	    security.checkRead(path);
	}
	return isDirectory0();
    }

    /**
     * Tests if the file represented by this <code>File</code> object is an
     * absolute pathname. The definition of an absolute pathname is system 
     * dependent. For example, on UNIX, a pathname is absolute if its 
     * first character is the separator character. On Windows platforms, 
     * a pathname is absolute if its first character is an ASCII '&#92;' or 
     * '/', or if it begins with a letter followed by a colon. 
     *
     * @return  <code>true</code> if the pathname indicated by the
     *          <code>File</code> object is an absolute pathname;
     *          <code>false</code> otherwise.
     * @see     java.io.File#getPath()
     * @see     java.io.File#separator
     * @since   JDK1.0
     */
    public native boolean isAbsolute();

    /**
     * Returns the time that the file represented by this 
     * <code>File</code> object was last modified. 
     * <p>
     * The return value is system dependent and should only be used to 
     * compare with other values returned by last modified. It should not 
     * be interpreted as an absolute time. 
     *
     * @return     the time the file specified by this object was last modified,
     *             or <code>0L</code> if the specified file does not exist.
     * @exception  SecurityException  if a security manager exists, its
     *               <code>checkRead</code> method is called with the pathname
     *               of this <code>File</code> to see if the application is
     *               allowed read access to the file.
     * @see        java.io.File#getPath()
     * @see        java.lang.SecurityManager#checkRead(java.lang.String)
     * @since      JDK1.0
     */
    public long lastModified() {
	SecurityManager security = System.getSecurityManager();
	if (security != null) {
	    security.checkRead(path);
	}
	return lastModified0();
    }

    /**
     * Returns the length of the file represented by this 
     * <code>File</code> object. 
     *
     * @return     the length, in bytes, of the file specified by this object,
     *             or <code>0L</code> if the specified file does not exist.
     * @exception  SecurityException  if a security manager exists, its
     *               <code>checkRead</code> method is called with the pathname
     *               of this <code>File</code> to see if the application is
     *               allowed read access to the file.
     * @see        java.io.File#getPath()
     * @see        java.lang.SecurityManager#checkRead(java.lang.String)
     * @since      JDK1.0
     */
    public long length() {
	SecurityManager security = System.getSecurityManager();
	if (security != null) {
	    security.checkRead(path);
	}
	return length0();
    }

    /**
     * Creates a directory whose pathname is specified by this 
     * <code>File</code> object. 
     *
     * @return     <code>true</code> if the directory could be created;
     *             <code>false</code> otherwise.
     * @exception  SecurityException  if a security manager exists, its
     *               <code>checkWrite</code> method is called with the pathname
     *               of this <code>File</code> to see if the application is
     *               allowed write access to the file.
     * @see        java.io.File#getPath()
     * @see        java.lang.SecurityManager#checkWrite(java.lang.String)
     * @since      JDK1.0
     */
    public boolean mkdir() {
	SecurityManager security = System.getSecurityManager();
	if (security != null) {
	    security.checkWrite(path);
	}
	return mkdir0();
    }

    /**
     * Renames the file specified by this <code>File</code> object to 
     * have the pathname given by the <code>File</code> argument. 
     *
     * @param      dest   the new filename.
     * @return     <code>true</code> if the renaming succeeds;
     *             <code>false</code> otherwise.
     * @exception  SecurityException  if a security manager exists, its
     *               <code>checkWrite</code> method is called both with the
     *               pathname of this file object and with the pathname of the
     *               destination target object to see if the application is
     *               allowed to write to both files.
     * @see        java.io.File#getPath()
     * @see        java.lang.SecurityManager#checkWrite(java.lang.String)
     * @since      JDK1.0
     */
    public boolean renameTo(File dest) {
	SecurityManager security = System.getSecurityManager();
	if (security != null) {
	    security.checkWrite(path);
	    security.checkWrite(dest.path);
	}
	return renameTo0(dest);
    }

    /**
     * Creates a directory whose pathname is specified by this 
     * <code>File</code> object, including any necessary parent directories.
     *
     * @return     <code>true</code> if the directory (or directories) could be
     *             created; <code>false</code> otherwise.
     * @exception  SecurityException  if a security manager exists, its
     *               <code>checkWrite</code> method is called with the pathname
     *               of each of the directories that is to be created, before
     *               any of the directories are created.
     * @see        java.io.File#getPath()
     * @see        java.lang.SecurityManager#checkWrite(java.lang.String)
     * @since      JDK1.0
     */
    public boolean mkdirs() {
	if(exists()) {
	    return false;
	}
	if (mkdir()) {
 	    return true;
 	}

	String parent = getParent();
	return (parent != null) && (new File(parent).mkdirs() && mkdir());
    }

    /**
     * Returns a list of the files in the directory specified by this
     * <code>File</code> object. 
     *
     * @return     an array of file names in the specified directory.
     *             This list does not include the current directory or the
     *             parent directory ("<code>.</code>" and "<code>..</code>"
     *             on Unix systems).
     * @exception  SecurityException  If a security manager exists, its
     *               <code>checkRead</code> method is called with the pathname
     *               of this <code>File</code> to see if the application is
     *               allowed read access to the file.
     * @see        java.io.File#getPath()
     * @see        java.lang.SecurityManager#checkRead(java.lang.String)
     * @since      JDK1.0
     */
    public String[] list() {
	SecurityManager security = System.getSecurityManager();
	if (security != null) {
	    security.checkRead(path);
	}
	return list0();
    }

    /**
     * Returns a list of the files in the directory specified by this 
     * <code>File</code> that satisfy the specified filter. 
     *
     * @param      filter   a filename filter.
     * @return     an array of file names in the specified directory.
     *             This list does not include the current directory or the
     *             parent directory ("<code>.</code>" and "<code>..</code>"
     *             on Unix systems).
     * @exception  SecurityException  If a security manager exists, its
     *               <code>checkRead</code> method is called with the pathname
     *               of this <code>File</code> to see if the application is
     *               allowed read access to the file.
     * @see        java.io.File#getPath()
     * @see        java.io.FilenameFilter
     * @see        java.lang.SecurityManager#checkRead(java.lang.String)
     * @since      JDK1.0
     */
    public String[] list(FilenameFilter filter) {
	String names[] = list();

	if (names == null) {
	    return null;
	}

	// Fill in the Vector
	Vector v = new Vector();
	for (int i = 0 ; i < names.length ; i++) {
	    if ((filter == null) || filter.accept(this, names[i])) {
		v.addElement(names[i]);
	    }
	}

	// Create the array
	String files[] = new String[v.size()];
	v.copyInto(files);

	return files;
    }

    /**
     * Deletes the file specified by this object.  If the target
     * file to be deleted is a directory, it must be empty for deletion
     * to succeed.
     *
     * @return     <code>true</code> if the file is successfully deleted;
     *             <code>false</code> otherwise.
     * @exception  SecurityException  if a security manager exists, its
     *               <code>checkDelete</code> method is called with the
     *               pathname of this <code>File</code> to see if the
     *               application is allowed to delete the file.
     * @see        java.io.File#getPath()
     * @see        java.lang.SecurityManager#checkDelete(java.lang.String)
     * @since      JDK1.0
     */
    public boolean delete() {
	SecurityManager security = System.getSecurityManager();
	if (security != null) {
	    security.checkDelete(path);
	}
	if(isDirectory())
	    return rmdir0();
	else
	    return delete0();
    }

    /**
     * Computes a hashcode for the file.
     *
     * @return  a hash code value for this <code>File</code> object.
     * @since   JDK1.0
     */
    public int hashCode() {
	return path.hashCode() ^ 1234321;
    }

    /**
     * Compares this object against the specified object.
     * Returns <code>true</code> if and only if the argument is 
     * not <code>null</code> and is a <code>File</code> object whose 
     * pathname is equal to the pathname of this object. 
     *
     * @param   obj   the object to compare with.
     * @return  <code>true</code> if the objects are the same;
     *          <code>false</code> otherwise.
     * @since   JDK1.0
     */
    public boolean equals(Object obj) {
	if ((obj != null) && (obj instanceof File)) {
	    return path.equals(((File)obj).path);
	}
	return false;
    }

    /**
     * Returns a string representation of this object. 
     *
     * @return  a string giving the pathname of this object. 
     * @see     java.io.File#getPath()
     * @since   JDK1.0
     */
    public String toString() {
	return getPath();
    }

    /**
     * WriteObject is called to save this filename.
     * The separator character is saved also so it can be replaced
     * in case the path is reconstituted on a different host type.
     */
    private synchronized void writeObject(java.io.ObjectOutputStream s)
        throws IOException
    {
	s.defaultWriteObject();
	s.writeChar(separatorChar); // Add the separator character
    }

    /**
     * readObject is called to restore this filename.
     * The original separator character is read.  If it is different
     * than the separator character on this system. The old seperator
     * is replaced by the current separator.
     */
    private synchronized void readObject(java.io.ObjectInputStream s)
         throws IOException, ClassNotFoundException
    {
	s.defaultReadObject();
	char sep = s.readChar(); // read the previous seperator char
	if (sep != separatorChar)
	    path = path.replace(sep, separatorChar);
    }

    /** use serialVersionUID from JDK 1.0.2 for interoperability */
    private static final long serialVersionUID = 301077366599181567L;
}
