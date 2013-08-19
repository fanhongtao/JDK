/*
 * @(#)JarFile.java	1.50 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.util.jar;

import java.io.*;
import java.util.*;
import java.util.zip.*;
import java.security.cert.Certificate;
import java.security.AccessController;
import sun.security.action.GetPropertyAction;
import sun.security.util.ManifestEntryVerifier;
import sun.misc.SharedSecrets;

/**
 * The <code>JarFile</code> class is used to read the contents of a JAR file
 * from any file that can be opened with <code>java.io.RandomAccessFile</code>.
 * It extends the class <code>java.util.zip.ZipFile</code> with support
 * for reading an optional <code>Manifest</code> entry. The
 * <code>Manifest</code> can be used to specify meta-information about the
 * JAR file and its entries.
 *
 * @author  David Connelly
 * @version 1.50, 01/23/03
 * @see	    Manifest
 * @see     java.util.zip.ZipFile
 * @see     java.util.jar.JarEntry
 * @since   1.2
 */
public
class JarFile extends ZipFile {
    private Manifest man;
    private JarEntry manEntry;
    private boolean manLoaded;
    private JarVerifier jv;
    private boolean jvInitialized;
    private boolean verify;
    private boolean computedHasClassPathAttribute;
    private boolean hasClassPathAttribute;

    // Set up JavaUtilJarAccess in SharedSecrets
    static {
        SharedSecrets.setJavaUtilJarAccess(new JavaUtilJarAccessImpl());
    }

    /**
     * The JAR manifest file name.
     */
    public static final String MANIFEST_NAME = "META-INF/MANIFEST.MF";

    /**
     * Creates a new <code>JarFile</code> to read from the specified
     * file <code>name</code>. The <code>JarFile</code> will be verified if
     * it is signed.
     * @param name the name of the JAR file to be opened for reading
     * @exception IOException if an I/O error has occurred
     * @exception SecurityException if access to the file is denied
     *            by the SecurityManager
     */
    public JarFile(String name) throws IOException {
	this(new File(name), true, ZipFile.OPEN_READ);
    }

    /**
     * Creates a new <code>JarFile</code> to read from the specified
     * file <code>name</code>.
     * @param name the name of the JAR file to be opened for reading
     * @param verify whether or not to verify the JarFile if
     * it is signed.
     * @exception IOException if an I/O error has occurred
     * @exception SecurityException if access to the file is denied
     *            by the SecurityManager 
     */
    public JarFile(String name, boolean verify) throws IOException {
        this(new File(name), verify, ZipFile.OPEN_READ);
    }

    /**
     * Creates a new <code>JarFile</code> to read from the specified
     * <code>File</code> object. The <code>JarFile</code> will be verified if
     * it is signed.
     * @param file the JAR file to be opened for reading
     * @exception IOException if an I/O error has occurred
     * @exception SecurityException if access to the file is denied
     *            by the SecurityManager
     */
    public JarFile(File file) throws IOException {
	this(file, true, ZipFile.OPEN_READ);
    }


    /**
     * Creates a new <code>JarFile</code> to read from the specified
     * <code>File</code> object.
     * @param file the JAR file to be opened for reading
     * @param verify whether or not to verify the JarFile if
     * it is signed.
     * @exception IOException if an I/O error has occurred
     * @exception SecurityException if access to the file is denied
     *            by the SecurityManager.
     */
    public JarFile(File file, boolean verify) throws IOException {
	this(file, verify, ZipFile.OPEN_READ);
    }


    /**
     * Creates a new <code>JarFile</code> to read from the specified
     * <code>File</code> object in the specified mode.  The mode argument
     * must be either <tt>OPEN_READ</tt> or <tt>OPEN_READ | OPEN_DELETE</tt>.
     *
     * @param file the JAR file to be opened for reading
     * @param verify whether or not to verify the JarFile if
     * it is signed.
     * @param mode the mode in which the file is to be opened
     * @exception IOException if an I/O error has occurred
     * @exception IllegalArgumentException
     *            If the <tt>mode</tt> argument is invalid
     * @exception SecurityException if access to the file is denied
     *            by the SecurityManager
     */
    public JarFile(File file, boolean verify, int mode) throws IOException {
	super(file, mode);
	this.verify = verify;
    }

    /**
     * Returns the JAR file manifest, or <code>null</code> if none.
     *
     * @return the JAR file manifest, or <code>null</code> if none
     */
    public Manifest getManifest() throws IOException {
	if (!manLoaded) {
	    // First look up manifest entry using standard name
	    manEntry = getJarEntry(MANIFEST_NAME);
	    if (manEntry == null) {
		// If not found, then iterate through all the "META-INF/"
		// entries to find a match.
		String[] names = getMetaInfEntryNames();
		if (names != null) {
		    for (int i = 0; i < names.length; i++) {
			if (MANIFEST_NAME.equals(
                            names[i].toUpperCase(Locale.ENGLISH))) {
			    manEntry = getJarEntry(names[i]);
			    break;
			}
		    }
		}
	    }
	    // If found then load the manifest
	    if (manEntry != null) {
		if (verify) {
		    byte[] b = getBytes(manEntry);
 		    man = new Manifest(new ByteArrayInputStream(b));
		    jv = new JarVerifier(man, b);
		} else {
		    man = new Manifest(super.getInputStream(manEntry));
		}
	    }
	    manLoaded = true;
	}
	return man;
    }

    private native String[] getMetaInfEntryNames();

    /**
     * Returns the <code>JarEntry</code> for the given entry name or
     * <code>null</code> if not found.
     *
     * @param name the JAR file entry name
     * @return the <code>JarEntry</code> for the given entry name or
     *         <code>null</code> if not found.
     * @see java.util.jar.JarEntry
     */
    public JarEntry getJarEntry(String name) {
	return (JarEntry)getEntry(name);
    }

    /**
     * Returns the <code>ZipEntry</code> for the given entry name or
     * <code>null</code> if not found.
     *
     * @param name the JAR file entry name
     * @return the <code>ZipEntry</code> for the given entry name or
     *         <code>null</code> if not found
     * @see java.util.zip.ZipEntry
     */
    public ZipEntry getEntry(String name) {
	ZipEntry ze = super.getEntry(name);
	if (ze != null) {
	    return new JarFileEntry(ze);
	}
	return null;
    }

    /**
     * Returns an enumeration of the ZIP file entries.
     */
    public Enumeration entries() {
	final Enumeration enum = super.entries();
	return new Enumeration() {
	    public boolean hasMoreElements() {
		return enum.hasMoreElements();
	    }
	    public Object nextElement() {
		ZipEntry ze = (ZipEntry)enum.nextElement();
		return new JarFileEntry(ze);
	    }
	};
    }

    private class JarFileEntry extends JarEntry {
	JarFileEntry(ZipEntry ze) {
	    super(ze);
	}
	public Attributes getAttributes() throws IOException {
	    Manifest man = JarFile.this.getManifest();
	    if (man != null) {
		return man.getAttributes(getName());
	    } else {
		return null;
	    }
	}
	public java.security.cert.Certificate[] getCertificates() {
            try {
                maybeInstantiateVerifier();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
	    if (certs == null && jv != null) {
		Certificate[] cs = jv.getCerts(getName());
		if (cs != null) {
		    certs = (Certificate[])cs.clone();
		}
	    }
	    return certs;
	}
    }
	    
    /*
     * Ensures that the JarVerifier has been created if one is
     * necessary (i.e., the jar appears to be signed.) This is done as
     * a quick check to avoid processing of the manifest for unsigned
     * jars.
     */
    private void maybeInstantiateVerifier() throws IOException {
        if (jv != null) {
            return;
        }

        if (verify) {
            String[] names = getMetaInfEntryNames();
            if (names != null) {
                for (int i = 0; i < names.length; i++) {
                    String name = names[i].toUpperCase(Locale.ENGLISH);
                    if (name.endsWith(".DSA") ||
                        name.endsWith(".RSA") ||
                        name.endsWith(".SF")) {
                        // Assume since we found a signature-related file
                        // that the jar is signed and that we therefore
                        // need a JarVerifier and Manifest
                        getManifest();
                        return;
                    }
                }
            }
            // No signature-related files; don't instantiate a
            // verifier
            verify = false;
        }
    }


    /*
     * Initializes the verifier object by reading all the manifest
     * entries and passing them to the verifier.
     */
    private void initializeVerifier() {
	ManifestEntryVerifier mev = null;

	// Verify "META-INF/" entries...
	try {
	    String[] names = getMetaInfEntryNames();
	    if (names != null) {
		for (int i = 0; i < names.length; i++) {
		    JarEntry e = getJarEntry(names[i]);
		    if (!e.isDirectory()) {
			if (mev == null) {
			    mev = new ManifestEntryVerifier(man);
			}
			byte[] b = getBytes(e);
			if (b != null && b.length > 0) {
			    jv.beginEntry(e, mev);
			    jv.update(b.length, b, 0, b.length, mev);
			    jv.update(-1, null, 0, 0, mev);
			}
		    }
		}
	    }
	} catch (IOException ex) {
	    // if we had an error parsing any blocks, just
	    // treat the jar file as being unsigned
	    jv = null;
            verify = false;
	}

	// if after initializing the verifier we have nothing
	// signed, we null it out.

	if (jv != null) {

	    jv.doneWithMeta();
	    if (JarVerifier.debug != null) {
		JarVerifier.debug.println("done with meta!"); 
	    }

	    if (jv.nothingToVerify()) {
		if (JarVerifier.debug != null) {
		    JarVerifier.debug.println("nothing to verify!");
		}
		jv = null;
                verify = false;
	    }
	}
    }

    /*
     * Reads all the bytes for a given entry. Used to process the
     * the META-INF files.
     */
    private byte[] getBytes(ZipEntry ze) throws IOException {
	byte[] b = new byte[(int)ze.getSize()];
	DataInputStream is = new DataInputStream(super.getInputStream(ze));
	is.readFully(b, 0, b.length);
	is.close();
	return b;
    }

    /**
     * Returns an input stream for reading the contents of the specified
     * ZIP file entry.
     * @param ze the zip file entry
     * @return an input stream for reading the contents of the specified
     *         ZIP file entry
     * @exception ZipException if a ZIP format error has occurred
     * @exception IOException if an I/O error has occurred
     * @exception SecurityException if any of the JarFile entries are incorrectly signed.
     */
    public synchronized InputStream getInputStream(ZipEntry ze) 
	throws IOException 
    {
        maybeInstantiateVerifier();
	if (jv == null) {
	    return super.getInputStream(ze);
	}
	if (!jvInitialized) {
	    initializeVerifier();
	    jvInitialized = true;
	    // could be set to null after a call to
	    // initializeVerifier if we have nothing to
	    // verify
	    if (jv == null)
		return super.getInputStream(ze);
	}

	// wrap a verifier stream around the real stream
	return new JarVerifier.VerifierStream(man, (JarEntry)ze,
					      super.getInputStream(ze), jv);
    }

    // Statics for hand-coded Boyer-Moore search in hasClassPathAttribute()
    // The bad character shift for "class-path"
    private static int[] lastOcc;
    // The good suffix shift for "class-path"
    private static int[] optoSft;
    // Initialize the shift arrays to search for "class-path"
    private static char[] src = {'c','l','a','s','s','-','p','a','t','h'};
    static {
        lastOcc = new int[128];
        optoSft = new int[10];
        lastOcc[99]=1; lastOcc[108]=2; lastOcc[97]=8; lastOcc[115]=5;
        lastOcc[116]=9; lastOcc[45]=6; lastOcc[112]=7; lastOcc[104]=10;
        for (int i=0; i<9; i++)
            optoSft[i]=10;
        optoSft[9]=1;
    }
 
    // Returns true iff this jar file has a manifest with a class path
    // attribute. Returns false if there is no manifest or the manifest
    // does not contain a "Class-Path" attribute. Currently exported to
    // core libraries via sun.misc.SharedSecrets.
    boolean hasClassPathAttribute() throws IOException {
        if (computedHasClassPathAttribute) {
            return hasClassPathAttribute;
        }

        hasClassPathAttribute = false;
        if (!isKnownToNotHaveClassPathAttribute()) {
            JarEntry manEntry = getJarEntry(MANIFEST_NAME);
            if (manEntry == null) {
                // If not found, then iterate through all the "META-INF/"
                // entries to find a match.
                String[] names = getMetaInfEntryNames();
                if (names != null) {
                    for (int i = 0; i < names.length; i++) {
                        if (MANIFEST_NAME.equals(
                                                 names[i].toUpperCase(Locale.ENGLISH))) {
                            manEntry = getJarEntry(names[i]);
                            break;
                        }
                    }
                }
            }
            if (manEntry != null) {
                byte[] b = new byte[(int)manEntry.getSize()];
                DataInputStream dis = new DataInputStream(
                                                          super.getInputStream(manEntry));
                dis.readFully(b, 0, b.length);
                dis.close();
 
                int last = b.length - src.length;
                int i = 0;
                next:       
                while (i<=last) {
                    for (int j=9; j>=0; j--) {
                        char c = (char) b[i+j];
                        c = (((c-'A')|('Z'-c)) >= 0) ? (char)(c + 32) : c;
                        if (c != src[j]) {
                            i += Math.max(j + 1 - lastOcc[c&0x7F], optoSft[j]);
                            continue next;
                        }
                    }
                    hasClassPathAttribute = true;
                    break;
                }
            }
        }
        computedHasClassPathAttribute = true;
        return hasClassPathAttribute;
    }

    private static String javaHome;
    private boolean isKnownToNotHaveClassPathAttribute() {
        // Optimize away even scanning of manifest for jar files we
        // deliver which don't have a class-path attribute. If one of
        // these jars is changed to include such an attribute this code
        // must be changed.
        if (javaHome == null) {
            javaHome = (String) AccessController.doPrivileged(
                new GetPropertyAction("java.home"));
        }
        String name = getName();
        String localJavaHome = javaHome;
        if (name.startsWith(localJavaHome)) {
            if (name.endsWith("rt.jar") ||
                name.endsWith("sunrsasign.jar") ||
                name.endsWith("jsse.jar") ||
                name.endsWith("jce.jar") ||
                name.endsWith("charsets.jar") ||
                name.endsWith("dnsns.jar") ||
                name.endsWith("ldapsec.jar") ||
                name.endsWith("localedata.jar") ||
                name.endsWith("sunjce_provider.jar")) {
                return true;
            }
        }
        return false;
    }
}
